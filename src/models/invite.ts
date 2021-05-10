import { Table, Model, Column, DataType, BelongsTo, ForeignKey, PrimaryKey } from 'sequelize-typescript'
import { Member } from '../account/member'
import config from '../config/config'
import { TrustedError } from '../error/trustedError'

@Table({
    modelName: 'invite',
    tableName: config.mysql.prefix + "invites",
    timestamps: true
})
export class Invite extends Model {
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
        type: DataType.DATE,
        allowNull: false,
        defaultValue: () => {
            let expiresAtMillis = Date.now() + (1000*60*60*24*7)
            return new Date(expiresAtMillis)
        }
    })
    expiresAt: string

    @Column({
        type: DataType.INTEGER,
        allowNull: true,
        defaultValue: 0
    })
    uses: number

    @Column({
        type: DataType.UUID,
        allowNull: true
    })
    @BelongsTo(() => Member, { as: "member", onDelete: "CASCADE" })
    @ForeignKey(() => Member)
    memberId: string

    /**
     * Check if invite is expired
     */
    public isExpired(): Boolean {
        let expiresAt = new Date(this.expiresAt).getTime()
        let currentTimeMillis = Date.now()
        return expiresAt <= currentTimeMillis
    }

    /**
     * Static method for checking integrity of invite code
     * @param inviteUUID 
     */
    static async isInviteValid(inviteUUID: string): Promise<Boolean> {

        let invite = await Invite.findOne({ where: {uuid: inviteUUID }})
        if(!invite) {
            return false
        }

        let isExpired = invite.isExpired()

        // Delete invite asynchronously
        if(isExpired) {
            Invite.destroy({ where: { uuid: inviteUUID }})
        }

        return !isExpired
    }

    /**
     * Increase the uses count for an invite
     * @param inviteUUID Invite id
     */
    static async increaseUse(inviteUUID: string) {
        let invite = await Invite.findOne({ where: {uuid: inviteUUID }})
        if(!invite) {
            return
        }

        if(!invite.isExpired()) {
            await invite.update({ uses: invite.uses+1 })
        } else {
            invite.destroy()
            return
        }
    }
}