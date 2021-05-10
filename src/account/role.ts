import { Table, Model, Column, DataType, HasMany } from 'sequelize-typescript'
import config from '../config/config'
import { Member } from './member'

@Table({
    modelName: 'role',
    tableName: config.mysql.prefix + "roles",
    timestamps: true
})
export class Role extends Model {

    @Column({
        type: DataType.UUID,
        allowNull: false,
        primaryKey: true,
        defaultValue: DataType.UUIDV4
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
        type: DataType.JSON,
        allowNull: false,
        defaultValue: []
    })
    permissions: Array<String>

    @Column({
        type: DataType.INTEGER,
        allowNull: false,
        defaultValue: 0
    })
    hierarchy: Number

    @HasMany(() => Member, { as: "member" })
    member: Array<Member>
}