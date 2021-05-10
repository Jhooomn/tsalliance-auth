import sequelize from "sequelize"
import { Member } from "../account/member"
import { TrustedError } from "../error/trustedError"
import { Invite } from "../models/invite"
import { Router } from "../router"
import { Endpoint } from "./endpoint"

export default class InviteEndpoint extends Endpoint {

    constructor() {
        super(
            Endpoint.AuthenticationFlag.FLAG_NO_AUTH,
            [
                new Endpoint.Permission("getMultiple", "permission.invites.get"),
                new Endpoint.Permission("createOne", "permission.invites.create"),
                new Endpoint.Permission("deleteOne", "permission.invites.delete")
            ]
        )
    }

    /**
     * @api {get} /invites/:uuid Get Invite
     */
    async actionGetOne(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]
        let attributes = ['uuid', 'expiresAt']

        if(!(route.account instanceof TrustedError) && route.account.hasPermission("permission.invites.read")) {
            attributes.push('uses')
            attributes.push('createdAt')
        }

        let invite = await Invite.findOne({ where: { uuid: targetUUID }, })
        if(!invite) {
            return TrustedError.get(TrustedError.Errors.RESOURCE_NOT_FOUND)
        }

        return new Endpoint.ResultSingleton(200, invite)
    }

    /**
     * @api {get} /invites Get multiple
     */
    async actionGetMultiple(route: Router.Route): Promise<Endpoint.Result> {
        let offset = parseInt(route.query?.["offset"]) || 0
        let limit = parseInt(route.query?.["limit"]) || 30

        if(offset < 0) offset = 0
        if(limit > 30 || limit < 1) limit = 30

        // Specify what to return
        let options = {
            offset,
            limit,
            order: sequelize.literal("createdAt DESC"),
            attributes: ['uuid', 'expiresAt', 'createdAt', 'uses'],
            include: [
                { model: Member, as: 'member', attributes: ['uuid', 'name']}
            ]
        }

        // Define where clause
        let where = {}

        let invites = await Invite.findAll({ where, ...options})
        let availableCount = await (await Invite.findAndCountAll({ where: {}})).count

        return new Endpoint.ResultSet(200, invites, availableCount)
    }

    /**
     * @api {delete} /invites/:uuid Delete Invite
     */
    async actionDeleteOne(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]

        let affectedRows = await Invite.destroy({ 
            where: { 
                uuid: targetUUID
            }
        })

        if(affectedRows == 0) {
            return TrustedError.get(TrustedError.Errors.RESOURCE_NOT_FOUND)
        } else {
            return new Endpoint.ResultEmpty(200)
        }
    }

    /**
     * @api {post} /invites Create Invite
     */
    async actionCreateOne(route: Router.Route): Promise<Endpoint.Result> {
        let expiresAt = route.body?.["expiresAt"]

        if(expiresAt && !Date.parse(expiresAt)) {
            return TrustedError.get(TrustedError.Errors.UNSUPPORTED_FORMAT)
        }

        // Create invite
        let invite = await Invite.create({
            expiresAt: !!expiresAt ? expiresAt : undefined,
            memberId: route.account?.["uuid"]
        })

        return new Endpoint.ResultSingleton(200, invite)
    }
}