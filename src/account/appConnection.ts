import { Table, Model, Column, DataType, BelongsTo, ForeignKey } from 'sequelize-typescript'
import config from '../config/config'
import { Member } from './member'
import axios from 'axios'
import querystring from 'querystring'
import { TrustedError } from '../error/trustedError'
import { Endpoint } from '../endpoint/endpoint'

@Table({
    modelName: 'DCConnection',
    tableName: config.mysql.prefix + "connections_dc",
    timestamps: true
})
export class DiscordConnection extends Model {
    @Column({
        type: DataType.UUID,
        allowNull: false,
        primaryKey: true,
        defaultValue: DataType.UUIDV4
    })
    uuid: string

    @Column({
        type: DataType.STRING,
        allowNull: true,
    })
    accessToken: string

    @Column({
        type: DataType.STRING,
        allowNull: true,
    })
    refreshToken: string

    @Column({
        type: DataType.STRING,
        allowNull: false,
        unique: {
            name: "foreignUserId", msg: ""
        }
    })
    foreignUserId: string

    @Column({
        type: DataType.UUID,
        allowNull: false
    })
    @BelongsTo(() => Member, { as: "member", onDelete: "CASCADE" })
    @ForeignKey(() => Member)
    memberId: string

    member?: Member

    /**
     * Sends a POST request to discord api to obtain an access_token
     * @param {String} grant_code Token obtained after authorize
     * @param {String} redirect_uri URL for redirection
     * @returns {Endpoint.Result} Endpoint result
     */
    static obtainTokenFromDiscord(grant_code: string, redirect_uri: string): Promise<Endpoint.Result> {
        return new Promise<Endpoint.Result>((resolve) => {
            let data = {
                'client_id': config.connections.discord.clientId,
                'client_secret': config.connections.discord.clientSecret,
                'grant_type': 'authorization_code',
                'code': grant_code,
                'redirect_uri': redirect_uri,
                'scope': config.connections.discord.scopes.join(" ")
            }

            axios.post(config.connections.discord.authEndpoint+"/token", querystring.stringify(data), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then((response) => {
                this.obtainUserIDFromDiscord(response.data.access_token).then((result) => {
                    resolve(new Endpoint.ResultSingleton(response.status, {
                        token: response.data,
                        user: result?.["data"]
                    }))
                }).catch((error) => {
                    console.error(error)
                    resolve(TrustedError.get(TrustedError.Errors.DISCORD_ERROR))
                })
            }).catch((error) => {
                console.error(error)
                resolve(TrustedError.get(TrustedError.Errors.DISCORD_ERROR))
            })
        })
    }

    /**
     * Sends a get request to discord to obtain user's id
     * @param access_token Access token of discord user
     * @returns {Endpoint.Result} Endpoint result
     */
    static obtainUserIDFromDiscord(access_token: string): Promise<Endpoint.Result> {
        return new Promise<Endpoint.Result>((resolve) => {
            axios.get(config.connections.discord.endpoint+"/users/@me", {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer '+access_token
                }
            }).then((response) => {
                resolve(new Endpoint.ResultSingleton(response.status, response.data))
            }).catch((error) => {
                console.error(error)
                resolve(TrustedError.get(TrustedError.Errors.DISCORD_ERROR))
            })
        })
    }
}