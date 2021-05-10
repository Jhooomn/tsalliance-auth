import { Account } from './account'
import { Table, Model, Column, DataType, BelongsTo, ForeignKey, PrimaryKey } from 'sequelize-typescript'
import config from '../config/config'
import { Role } from './role';
import { randomBytes } from 'crypto';
import { TrustedError } from '../error/trustedError';
import bcrypt from "bcrypt"
import jwt, { TokenExpiredError, JsonWebTokenError } from "jsonwebtoken"
import CryptUtil from '../utils/crypt';
import { Endpoint } from '../endpoint/endpoint';
import { Validator } from '../models/validator';
import { Op } from 'sequelize'
import { Router } from '../router';

@Table({
    modelName: 'member',
    tableName: config.mysql.prefix + "members",
    timestamps: true
})
export class Member extends Model implements Account {
    public accountType: Account.AccountType = Account.AccountType.ACCOUNT_MEMBER;

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
            name: "email", msg: ""
        }
    })
    email: string

    @Column({
        type: DataType.STRING,
        allowNull: false
    })
    password: string

    @Column({
        type: DataType.UUID,
        allowNull: true
    })
    @BelongsTo(() => Role, { as: "role", onDelete: "SET NULL" })
    @ForeignKey(() => Role)
    roleId: string

    @Column({
        type: DataType.STRING(16),
        allowNull: true
    })
    avatar: string

    @Column({
        type: DataType.STRING(16),
        defaultValue: () => {
            return randomBytes(8).toString('hex')
        }
    })
    credentialHash: string

    role?: Role

    /**
     * Check if account is from type member
     * Returns always true for member classes
     */
    isMember() {
        return this.accountType == Account.AccountType.ACCOUNT_MEMBER
    }

    /**
     * Check if account has a specific permission
     * @param permission Permission string
     * @returns {Boolean} True or False
     */
    hasPermission(permission: String, route?: Router.Route): Boolean {
        if(!permission) return true
        if(this.role?.uuid === "*") return true

        if(route) {
            return route.isOwnResource || this.role?.permissions.includes(permission)
        } else {
            return this.role?.permissions.includes(permission)
        }
    }

    /**
     * Login a member with username and password
     * @param username Member's username
     * @param password Member's password
     */
    static async signInWithCredentials(username: string, password: string): Promise<Endpoint.Result> {
        if(!username || !password) {
            return TrustedError.get(TrustedError.Errors.INVALID_CREDENTIALS)
        }

        let member: Member = await Member.findOne({ where: {name: username}, attributes: ['uuid', 'password', 'credentialHash']})

        if(!member) {
            return TrustedError.get(TrustedError.Errors.INVALID_ACCOUNT)
        }

        if(bcrypt.compareSync(password, member.password)) {
            return new Endpoint.ResultSingleton(200, { 
                token: jwt.sign({ client: { uuid: member.uuid, credentialHash: member.credentialHash }}, CryptUtil.getInstance().getJwtSecret(), {})
            })
        } else {
            return TrustedError.get(TrustedError.Errors.INVALID_CREDENTIALS)
        }
    }

    /**
     * Sign in a member account using jsonwebtoken.
     * @param token jsonwebtoken
     * @returns {Member} Member account object or TrustedError
     */
    static async signInWithToken(token: String): Promise<Account | TrustedError> {
        try {
            let decodedToken = jwt.verify(token, CryptUtil.getInstance().getJwtSecret())
            let account = await this.findOne({ where: { uuid: decodedToken.client.uuid }, include: [
                { model: Role, as: 'role' }
            ]})

            if(!account) {
                return TrustedError.get(TrustedError.Errors.INVALID_ACCOUNT)
            }

            // Credentials have changed -> Invalidate token
            if(account.credentialHash != decodedToken.client.credentialHash) {
                return TrustedError.get(TrustedError.Errors.SESSION_EXPIRED)
            }

            return account
        } catch (error) {
            if(error instanceof TokenExpiredError || error instanceof JsonWebTokenError) {
                return TrustedError.get(TrustedError.Errors.SESSION_EXPIRED)
            } else {
                console.log(error)
                return TrustedError.get(TrustedError.Errors.INTERNAL_ERROR)
            }
        }
    }

    static async registerMember(name: string, password: string, email: string, role: string = undefined): Promise<Member | TrustedError> {
        // Validate updated data
        let validationResult = await Validator.validateMemberCreate({
            name: name, 
            role: role, 
            email: email,
            password: password
        })

        if(!validationResult.hasPassed()) {
            return validationResult.getError()
        }

        // Validate password complexity
        validationResult = await Validator.validatePassword(password)

        if(!validationResult.hasPassed()) {
            return validationResult.getError()
        }

        // Check if username or email exists
        let exists = await Member.findOne({ 
            where: {
                [Op.or]: [
                    { name: name || "" },
                    { email: email || "" }
                ]
            }
        })
        if(exists) {
            return TrustedError.get(TrustedError.Errors.ACCOUNT_NAME_EXISTS)
        }

        // Create account
        let member = await Member.create({
            name,
            password: bcrypt.hashSync(password, config.app.salt_rounds),
            roleID: role || undefined,
            email
        })

        // Do not send sensitive data back
        member.credentialHash = undefined
        member.password = undefined

        return member
    }
}