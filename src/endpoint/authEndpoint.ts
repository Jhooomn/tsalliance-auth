import { Member } from "../account/member"
import { TrustedError } from "../error/trustedError"
import { Validator } from "../models/validator"
import { Router } from "../router"
import { Endpoint } from "./endpoint"
import { Invite } from "../models/invite"
import { ValidationError } from "../error/validationError"
import { ResetToken } from "../models/resetToken"
import { MailUtil } from "../utils/mailUtil"
import { randomBytes } from "crypto"
import bcrypt from 'bcrypt'
import config from '../config/config'

export default class AuthEndpoint extends Endpoint {

    constructor() {
        super(Endpoint.AuthenticationFlag.FLAG_NO_AUTH, [])
    }

    /**
     * @api {post} /auth Login member
     */
    async actionLoginOne(route: Router.Route) {
        let username = route.body?.["username"]
        let password = route.body?.["password"]

        return Member.signInWithCredentials(username, password)
    }

    /**
     * @api {post} /auth/signup Register member
     */
    async actionRegisterOne(route: Router.Route): Promise<Endpoint.Result> {
        let name = route.body?.["name"]
        let password = route.body?.["password"]
        let email = route.body?.["email"]
        let inviteCode = route.body?.["invite"]

        // Validate inputs
        if(!inviteCode || !await Invite.isInviteValid(inviteCode)) {
            return TrustedError.get(TrustedError.Errors.INVALID_INVITE)
        }

        let member = await Member.registerMember(name, password, email)

        if(member instanceof TrustedError) {
            return (member as TrustedError)
        } else if(member instanceof ValidationError) {
            return (member as ValidationError)
        } else {
            Invite.increaseUse(inviteCode)
            MailUtil.getInstance().sendMemberRegisteredMail(member)
            return new Endpoint.ResultEmpty(200)
        }
    }

    /**
     * @api {post} /auth/reset Request password reset
     */
    async actionRequestReset(route: Router.Route): Promise<Endpoint.Result> {
        let name = route.body?.["name"] || " "

        let member = await Member.findOne({where: { name }})
        if(!member) {
            return TrustedError.get(TrustedError.Errors.RESOURCE_NOT_FOUND)
        }

        let token = await ResetToken.deleteAndCreate(member)
        let mailResult = await MailUtil.getInstance().sendMemberResetMail(member, token)

        if(mailResult instanceof TrustedError) {
            return mailResult as TrustedError
        }

        return new Endpoint.ResultEmpty(200)
    }

    /**
     * @api {put} /auth/reset Reset password
     */
    async actionResetPassword(route: Router.Route): Promise<Endpoint.Result> {
        let tokenValue = route.body?.["token"] || " "
        let password = route.body?.["password"]

        let token = await ResetToken.findOne({ where: { tokenValue }, include: [
            {model: Member, as: 'member', attributes: ['uuid']}
        ]})
        
        // Check token
        if(!token || !token.isTokenValid()) {
            return TrustedError.get(TrustedError.Errors.INVALID_CREDENTIALS)
        }

        // Validate password complexity
        let validation = await Validator.validatePassword(password)

        if(!validation.hasPassed()) {
            return validation.getError()
        }

        // Update member
        await token.member?.update({ 
            password: bcrypt.hashSync(password, config.app.salt_rounds),
            credentialHash: randomBytes(8).toString('hex')
        })

        // Destroy token and send response
        token.destroy()
        return new Endpoint.ResultEmpty(200)
    }
}