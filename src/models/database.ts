import {Sequelize} from 'sequelize-typescript';
import { Member } from '../account/member';
import { Role } from '../account/role';
import { Invite } from './invite'
import config from '../config/config'
import bcrypt from 'bcrypt'
import { DiscordConnection } from '../account/appConnection';
import { System } from '../account/system';
import { ResetToken } from './resetToken';

export class Database {
    private static instance: Database = undefined
    private sequelize: Sequelize

    /**
     * Connect to database asynchronously
     */
    async connect() {
        console.info("Connecting to mysql database '"+config.mysql.user+"@"+config.mysql.host+":"+config.mysql.port+"/"+config.mysql.dbname+"'")
        this.sequelize = new Sequelize({
            database: config.mysql.dbname,
            dialect: "mysql",
            username: config.mysql.user,
            password: config.mysql.pass,
            host: config.mysql.host,
            port: config.mysql.port,
            logging: false
        })

        await this.sequelize.authenticate().then(async() => {
            console.info("Successfully connected to database '"+config.mysql.user+"@"+config.mysql.host+":"+config.mysql.port+"/"+config.mysql.dbname+"'")
            console.info("Setting up database")

            await this.sequelize.addModels([Member, Role, DiscordConnection, Invite, System, ResetToken])

            await this.setupRoles()
            await this.setupMembers()
            await this.setupNonDefaultTables()
        }).catch((error) => {
            console.error("Could not connect to database '"+config.mysql.user+"@"+config.mysql.host+":"+config.mysql.port+"/"+config.mysql.dbname+"': "+error.message)
        })
    }

    /**
     * Get Database instance
     */
    static getInstance() {
        if(!this.instance) this.instance = new Database()
        return this.instance 
    }

    /**
     * Starting database setup for roles
     * Creates a default role if it does not exists
     */
    private async setupRoles() {
        await Role.sync({alter: true})
        await Role.findOrCreate({ where: { name: "root" }, defaults: {
            uuid: "*",
            name: "root",
            permissions: ['*'],
            hierarchy: 1001
        }})
    }

    /**
     * Starting database setup for members.
     * Creates a default member account if it does not exist
     */
    private async setupMembers() {
        await Member.sync({ alter: true })
        await Member.findOrCreate({ where: { roleId: "*" }, defaults: {
            name: "root",
            email: "webmaster@localhost",
            password: bcrypt.hashSync('hackme', config.app.salt_rounds),
            roleId: "*"
        }})
    }

    /**
     * Starting database setup for invites.
     */
    private async setupNonDefaultTables() {
        await Invite.sync({ alter: true })
        await System.sync({ alter: true })
        await DiscordConnection.sync({ alter: true })
        await ResetToken.sync({ alter: true })
    }
}