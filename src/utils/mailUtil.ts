import nodemailer from 'nodemailer'
import { Member } from '../account/member'
import { TrustedError } from '../error/trustedError'
import config from '../config/config'

import resetPasswordTemplate from '../assets/emails/reset/reset.template'
import memberCreatedTemplate from '../assets/emails/memberCreate/memberCreate.template'
import memberWelcomeTemplate from '../assets/emails/memberWelcome/memberWelcome.template'

export class MailUtil {
    private static instance: MailUtil = undefined
    private transporter: any

    constructor() {
        this.transporter = nodemailer.createTransport({
            pool: true,
            host: config.smtp.host,
            port: config.smtp.port,
            auth: {
                user: config.smtp.user,
                pass: config.smtp.pass
            }
        })

    }

    /**
     * Check if client can connect to smtp server
     */
    async canConnect(): Promise<Boolean> {
        return new Promise<Boolean>((resolve) => {
            this.transporter.verify((error, success) => {
                if(error) {
                    console.error(error)
                    resolve(false)
                } else {
                    resolve(true)
                }
            })
        })
    }

    /**
     * Send a password reset email to member's mail account
     * @param member The member who requested the password reset
     * @param resetToken Generated reset token
     * @returns {Boolean | TrustedError} True/False or TrustedError
     */
    async sendMemberResetMail(member: Member, resetToken: string): Promise<Boolean | TrustedError> {
        return new Promise<Boolean | TrustedError>((resolve) => {
            resetPasswordTemplate.html = resetPasswordTemplate.html.replace("%reset_password_url%", "https://tsalliance.eu/auth/reset?token="+resetToken)

            this.transporter.sendMail({
                ...resetPasswordTemplate,
                to: member.email,
            }, (err) => {
                if(err) {
                    console.error(err)
                    resolve(TrustedError.get(TrustedError.Errors.SEND_MAIL_FAILED))
                } else {
                    resolve(true)
                }
            })
        })
    }

    /**
     * Send an email to a created member (not when registered)
     * @param member The member who was created
     * @param password Password of account
     * @returns {Boolean | TrustedError} True/False or TrustedError
     */
    async sendMemberCreatedMail(member: Member, password: string): Promise<Boolean | TrustedError> {
        return new Promise<Boolean | TrustedError>((resolve) => {
            memberCreatedTemplate.html = memberCreatedTemplate.html
                                                .replace("%alliance_url%", "https://tsalliance.eu/auth/login")
                                                .replace("%member_name%", member.name)
                                                .replace("%member_password%", password)

            this.transporter.sendMail({
                ...memberCreatedTemplate,
                to: member.email,
            }, (err) => {
                if(err) {
                    console.error(err)
                    resolve(TrustedError.get(TrustedError.Errors.SEND_MAIL_FAILED))
                } else {
                    resolve(true)
                }
            })
        })
    }

    /**
     * Send an email to a newly registered member
     * @param member The member who was created
     * @returns {Boolean | TrustedError} True/False or TrustedError
     */
    async sendMemberRegisteredMail(member: Member): Promise<Boolean | TrustedError> {
        return new Promise<Boolean | TrustedError>((resolve) => {
            memberWelcomeTemplate.html = memberWelcomeTemplate.html.replace("%alliance_url%", "https://tsalliance.eu")

            this.transporter.sendMail({
                ...memberWelcomeTemplate,
                to: member.email,
            }, (err) => {
                if(err) {
                    console.error(err)
                    resolve(TrustedError.get(TrustedError.Errors.SEND_MAIL_FAILED))
                } else {
                    resolve(true)
                }
            })
        })
    }

    /**
     * Get instance of MailUtil
     */
    static getInstance(): MailUtil {
        if(!this.instance) this.instance = new MailUtil()
        return this.instance
    }

}