import { Account } from './account'
import { Table, Model, Column, DataType, PrimaryKey } from 'sequelize-typescript'
import config from '../config/config'
import { randomBytes } from 'crypto'
import { TrustedError } from '../error/trustedError';
import { Router } from '../router';

@Table({
    modelName: 'system',
    tableName: config.mysql.prefix + "systems",
    timestamps: true
})
export class System extends Model implements Account {
    public readonly accountType: Account.AccountType = Account.AccountType.ACCOUNT_SYSTEM;

    @PrimaryKey
    @Column({
        type: DataType.UUID,
        allowNull: false,
        defaultValue: DataType.UUIDV4,
        unique: {
            name: "uuid", msg: ""
        }
    })
    uuid: string

    @Column({
        type: DataType.STRING(16),
        unique: {
            name: "clientId", msg: ""
        },
        defaultValue: () => {
            return randomBytes(8).toString('hex')
        }
    })
    clientId: string

    @Column({
        type: DataType.STRING(16),
        unique: {
            name: "clientSecret", msg: ""
        },
        defaultValue: () => {
            return randomBytes(8).toString('hex')
        }
    })
    clientSecret: string

    @Column({
        type: DataType.STRING(16),
        unique: {
            name: "clientToken", msg: ""
        },
        defaultValue: () => {
            return randomBytes(8).toString('hex')
        }
    })
    clientToken: string

    @Column({
        type: DataType.STRING(16),
        allowNull: false,
        unique: {
            name: "name", msg: ""
        }
    })
    name: string


    @Column({
        type: DataType.STRING,
        allowNull: false,
        unique: {
            name: "url", msg: ""
        }
    })
    url: string

    @Column({
        type: DataType.STRING(16),
        allowNull: true
    })
    avatar: string

    /**
     * Check if account is from type member
     * Returns always false for system classes
     */
    isMember() {
        return this.accountType == Account.AccountType.ACCOUNT_MEMBER
    }

    /**
     * Check if account has a specific permission
     * @param permission Permission string
     * @returns {Boolean} Always returns True, because System apps have full access
     */
    hasPermission(permission: String = "", route?: Router.Route): Boolean {
        return true
    }

    /**
     * Sign in a system account using jsonwebtoken.
     * @param token jsonwebtoken
     * @returns {System} System account object or TrustedError
     */
    static async signInWithCredentials(clientId: string, clientSecret: string, clientToken: string): Promise<Account | TrustedError> {
        if(!clientId || !clientSecret || !clientToken) {
            return TrustedError.get(TrustedError.Errors.INVALID_CREDENTIALS)
        }

        let systemAccount = await System.findOne({ where: { clientId, clientSecret, clientToken }})
        if(!systemAccount) {
            return TrustedError.get(TrustedError.Errors.INVALID_CREDENTIALS)
        }

        return systemAccount
    }
}