import { TrustedError } from '../error/trustedError'
import { Router } from '../router'
import { Member } from './member'
import { Role } from './role'
import { System } from './system'

export interface Account {
    accountType: Account.AccountType
    isMember()
    hasPermission(permission: String, route?: Router.Route)
}

export namespace Account {
    export const enum AccountType {
        ACCOUNT_SYSTEM = 1,
        ACCOUNT_MEMBER
    }

    /**
     * Extract session token from request and authenticate requester
     * @param request Express request
     * @returns {Account} Account data or TrustedError
     */
    export async function authenticateRequest(request): Promise<Account | TrustedError> {
        let authorizationHeader = request.headers["authorization"]

        if(authorizationHeader?.startsWith("Bearer")) {
            let token = authorizationHeader.slice(7)
            return Member.signInWithToken(token)
        } else if(authorizationHeader?.startsWith("System")) {
            let clientId = request.headers["client_id"]
            let clientSecret = request.headers["client_secret"]
            let clientToken = authorizationHeader.slice(7)

            return System.signInWithCredentials(clientId, clientSecret, clientToken)
        } else {
            return TrustedError.get(TrustedError.Errors.UNKNOWN_AUTH_METHOD)
        }
    }
    
    /**
     * Check if an account can modify another one
     * @param account Account who tries to modify
     * @param targetUUID Target which should be modified
     */
    export async function canModify(account: Account, targetUUID: string): Promise<Boolean> {
        if(!account.isMember()) return true
        
        if(account instanceof Member) {
            if(account.uuid == targetUUID && account.roleId != '*' || account.roleId == '*') {
                return true
            }

            let targetAccount = await Member.findOne({ where: { uuid: targetUUID }, include: [{model: Role, as: 'role'}]})
            if(!targetAccount) return false

            return account.role?.hierarchy >= targetAccount.role?.hierarchy
        } else {
            return false
        }
    }
}