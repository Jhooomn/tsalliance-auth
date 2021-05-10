import { Account } from '../account/account'
import { Member } from '../account/member'
import { Role } from '../account/role'
import { TrustedError } from '../error/trustedError'
import { Router } from '../router'
import { Endpoint } from './endpoint'
import config from '../config/config'

import { Op } from 'sequelize'
import bcrypt from 'bcrypt'

import Joi from 'joi'
import { Validator } from '../models/validator'
import { randomBytes } from 'crypto'
import { ValidationError } from '../error/validationError'
import { MailUtil } from '../utils/mailUtil'

export default class MemberEndpoint extends Endpoint {

    constructor() {
        super(
            Endpoint.AuthenticationFlag.FLAG_NO_AUTH,
            [
                new Endpoint.Permission("updateOne", "permission.members.update"),
                new Endpoint.Permission("deleteOne", "permission.members.delete"),
                new Endpoint.Permission("createOne", "permission.members.create"),
                new Endpoint.Permission("changePassword", "permission.members.updatePassword")
            ]
        )
    }

    /**
     * @api {get} /accounts/:uuid Get account
     */
    async actionGetOne(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]

        let roleAttributes = ['uuid', 'name']
        let memberAttributes = ['uuid', 'name', 'createdAt', 'avatar']

        if(route.isOwnResource || route.account instanceof Member && route.account.hasPermission("permission.members.read") ) {
            roleAttributes.push("hierarchy", "permissions")
            memberAttributes.push("email")
        }

        let member = await Member.findOne({ 
            where: { 
                uuid: targetUUID 
            }, 
            attributes: memberAttributes,
            include: { model: Role, as: 'role', attributes: roleAttributes}
        })

        if(!member) {
            return TrustedError.get(TrustedError.Errors.RESOURCE_NOT_FOUND)
        }

        return new Endpoint.ResultSingleton(200, member)
    }

    /**
     * @api {get} /accounts Get multiple accounts
     */
    async actionGetMultiple(route: Router.Route): Promise<Endpoint.Result> {
        let offset = parseInt(route.query?.["offset"]) || 0
        let limit = parseInt(route.query?.["limit"]) || 30

        if(offset < 0) offset = 0
        if(limit > 30 || limit < 1) limit = 30

        let roleAttributes = ['uuid', 'name']
        let memberAttributes = ['uuid', 'name', 'createdAt', 'avatar']

        if(route.account instanceof Member && route.account?.hasPermission("permission.members.read") ) {
            roleAttributes.push("hierarchy", "permissions")
            memberAttributes.push("email")
        }

        // Specify what to return
        let options = {
            offset,
            limit,
            attributes: memberAttributes,
            include: { model: Role, as: 'role', attributes: roleAttributes}
        }

        // Define where clause
        let where = {}

        let members = await Member.findAll({ where, ...options})
        let availableCount = await (await Member.findAndCountAll({ where: {}})).count
        return new Endpoint.ResultSet(200, members, availableCount)
    }

    /**
     * @api {delete} /accounts/:uuid Delete account
     */
    async actionDeleteOne(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]

        if(await Account.canModify(route.account as Account, targetUUID)) {
            let account = await Member.findOne({ where: { uuid: targetUUID }})

            // Do not delete default user
            if(account && account.roleId == '*') {
                return TrustedError.get(TrustedError.Errors.PERMISSION_DENIED)
            }

            let affectedRows = await Member.destroy({ 
                where: { uuid: targetUUID }
            })
    
            if(affectedRows == 0) {
                return TrustedError.get(TrustedError.Errors.RESOURCE_NOT_FOUND)
            } else {
                return new Endpoint.ResultSingleton(200, undefined)
            }
        } else {
            return TrustedError.get(TrustedError.Errors.PERMISSION_DENIED)
        }
    }

    /**
     * @api {post} /accounts Create account
     */
    async actionCreateOne(route: Router.Route): Promise<Endpoint.Result> {
        let name = route.body?.["name"]
        let password = route.body?.["password"]
        let role = route.body?.["role"]
        let email = route.body?.["email"]

        let member = await Member.registerMember(name, password, email, role)

        if(member instanceof TrustedError) {
            return (member as TrustedError)
        } else if(member instanceof ValidationError) {
            return (member as ValidationError)
        } else {
            MailUtil.getInstance().sendMemberCreatedMail(member, password)
            return new Endpoint.ResultSingleton(200, member)
        }
    }

    /**
     * @api {put} /accounts/:uuid Update account
     */
    async actionUpdateOne(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]

        if(await Account.canModify(route.account as Account, targetUUID)) {
            let updated = {
                name: route.body?.["name"],
                role: route.body?.["role"],
                email: route.body?.["email"]
            }

            // Remove undefined values
            Object.keys(updated).forEach(key => updated[key] === undefined ? delete updated[key] : {});

            // Count properties, if no properties, skip update
            if(Object.keys(updated).length === 0 && updated.constructor === Object) {
                return new Endpoint.ResultEmpty(200)
            } else {
                // Validate updated data
                let validationResult = await Validator.validateMemberUpdate({
                    name: updated.name, 
                    role: updated.role, 
                    email: updated.email
                })

                if(!validationResult.hasPassed()) {
                    return validationResult.getError()
                }

                // Check if username or email exists
                let exists = await Member.findOne({ 
                    where: {
                        [Op.or]: [
                            { name: updated.name || "" },
                            { email: updated.email || "" }
                        ],
                        [Op.not]: [
                            { uuid: targetUUID }
                        ]
                    }
                })        
                
                if(exists) {
                    return TrustedError.get(TrustedError.Errors.RESOURCE_EXISTS)
                }

                // Update account
                await Member.update(updated, { where: { uuid: targetUUID }})[0]
                return new Endpoint.ResultEmpty(200)
            }
        } else {
            return TrustedError.get(TrustedError.Errors.PERMISSION_DENIED)
        }
    }

    /**
     * @api {put} /members/:uuid/password Change password
     */
    async actionChangePassword(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]

        let currentPassword = route.body?.["currentPassword"] || " "
        let updatedPassword = route.body?.["updatedPassword"] || ""

        // Validate inputs
        const validationSchema = Joi.object({
            currentPassword: Joi.string().required(),
            updatedPassword: Joi.string().required()
        })

        let validation = await Validator.validate(validationSchema, {currentPassword, updatedPassword})

        if(!validation.hasPassed()) {
            return validation.getError()
        }

        // Verify current password
        let userPasswordData = await Member.findOne({ where: { uuid: targetUUID }, attributes: ['password']})
        if(userPasswordData) {
            if(!bcrypt.compareSync(currentPassword, userPasswordData.password)) {
                return TrustedError.get(TrustedError.Errors.INVALID_CREDENTIALS)
            }
        } else {
            return TrustedError.get(TrustedError.Errors.RESOURCE_NOT_FOUND)
        }

        // Validate password complexity
        validation = await Validator.validatePassword(updatedPassword)

        if(!validation.hasPassed()) {
            return validation.getError()
        }

        if(await Account.canModify(route.account as Member, targetUUID)) {
            await Member.update({
                    password: bcrypt.hashSync(updatedPassword, config.app.salt_rounds),
                    credentialHash: randomBytes(8).toString('hex')
                },{ 
                where: { 
                    uuid: targetUUID
                }
            })
    
            return new Endpoint.ResultEmpty(200)
        } else {
            return TrustedError.get(TrustedError.Errors.PERMISSION_DENIED)
        }
    }

}