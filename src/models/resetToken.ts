import { randomBytes } from 'crypto'
import { Table, Model, Column, DataType, BelongsTo, ForeignKey, PrimaryKey } from 'sequelize-typescript'
import { Member } from '../account/member'
import config from '../config/config'
import { TrustedError } from '../error/trustedError'

@Table({
    modelName: 'reset',
    tableName: config.mysql.prefix + "reset_tokens",
    timestamps: true
})
export class ResetToken extends Model {
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
            let expiresAtMillis = Date.now() + (1000*60*10)
            return new Date(expiresAtMillis)
        }
    })
    expiresAt: string

    @Column({
        type: DataType.UUID,
        allowNull: true
    })
    @BelongsTo(() => Member, { as: "member", onDelete: "CASCADE" })
    @ForeignKey(() => Member)
    memberId: string
    member?: Member

    @Column({
        type: DataType.STRING(16),
        defaultValue: () => {
            return randomBytes(8).toString('hex')
        }
    })
    tokenValue: string

    /**
     * Check if invite is expired
     */
    public isExpired(): Boolean {
        let expiresAt = new Date(this.expiresAt).getTime()
        let currentTimeMillis = Date.now()
        return expiresAt <= currentTimeMillis
    }

    /**
     * Method for checking integrity of invite code
     * @param tokenValue Token value
     */
    public isTokenValid(): Boolean {
        let isExpired = this.isExpired()

        // Delete invite asynchronously
        if(isExpired) {
            this.destroy()
        }

        return !isExpired
    }

    /**
     * Check if a token already exists, then delete and create new one. Otherwise only create a new token
     * @param member Member to create token for
     * @returns {String} Token value
     */
    static async deleteAndCreate(member: Member): Promise<string> {
        let tokenExists = await ResetToken.findOne({ where: {memberId: member.uuid}})
        if(tokenExists) {
            tokenExists.destroy()
        }

        let generatedToken = await ResetToken.create({
            memberId: member.uuid
        })

        return generatedToken.tokenValue
    }
}