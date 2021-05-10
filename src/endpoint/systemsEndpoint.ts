import { Member } from "../account/member"
import { System } from "../account/system"
import { TrustedError } from "../error/trustedError"
import { Router } from "../router"
import { Endpoint } from "./endpoint"

import { Op } from 'sequelize'
import { Validator } from "../models/validator"

export default class SystemsEndpoint extends Endpoint {

    constructor() {
        super(Endpoint.AuthenticationFlag.FLAG_NO_AUTH, [
            { endpointAction: "createOne", permission: "permission.system.create" },
            { endpointAction: "deleteOne", permission: "permission.system.delete" },
            { endpointAction: "updateOne", permission: "permission.system.update" },
            { endpointAction: "createOne", permission: "permission.system.create" },
        ])
    }

    /**
     * @api {get} /modules/:uuid Get Module
     */
    async actionGetOne(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]

        let attributes = ['uuid', 'name', 'url', 'avatar']

        if(route.account instanceof Member && route.account.hasPermission("permission.system.read")) {
            attributes.push('clientId')
            attributes.push('clientToken')
            attributes.push('clientSecret')
        }

        let systemAccount = await System.findOne({ where: { uuid: targetUUID }, attributes})
        
        if(!systemAccount) {
            return TrustedError.get(TrustedError.Errors.RESOURCE_NOT_FOUND)
        }

        return new Endpoint.ResultSingleton(200, systemAccount)
    }

    /**
     * @api {get} /modules/all Get all
     */
    async actionGetAll(route: Router.Route): Promise<Endpoint.Result> {
        let attributes = ['uuid', 'name', 'url', 'avatar']

        if(route.account instanceof Member && route.account.hasPermission("permission.system.read")) {
            attributes.push('clientId')
            attributes.push('clientToken')
            attributes.push('clientSecret')
        }

        let systemAccounts = await System.findAll({attributes})
        return new Endpoint.ResultSet(200, systemAccounts, systemAccounts.length)
    }

    /**
     * @api {delete} /modules/:uuid Delete Module
     */
    async actionDeleteOne(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]

        let affectedRows = await System.destroy({ 
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
     * @api {post} /modules/:uuid Create Module
     */
    async actionCreateOne(route: Router.Route): Promise<Endpoint.Result> {
        let name = route.body?.["name"]
        let url = route.body?.["url"]

        // Validate updated data
        let validationResult = await Validator.validateSystemCreate({
            name, 
            url
        })

        if(!validationResult.hasPassed()) {
            return validationResult.getError()
        }

        // Check if modulename or url exists
        let exists = await System.findOne({ 
            where: {
                [Op.or]: [
                    { name },
                    { url }
                ]
            }
        })
        if(exists) {
            return TrustedError.get(TrustedError.Errors.RESOURCE_EXISTS)
        }

        // Create module
        let module = await System.create({
            name,
            url
        })

        return new Endpoint.ResultSingleton(200, module)
    }

    /**
     * @api {put} /modules/:uuid Update Module
     */
    async actionUpdateOne(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]
        let updated = {
            name: route.body?.["name"],
            url: route.body?.["url"]
        }

        // Remove undefined values
        Object.keys(updated).forEach(key => updated[key] === undefined ? delete updated[key] : {});

        // Count properties, if no properties, skip update
        if(Object.keys(updated).length === 0 && updated.constructor === Object) {
            return new Endpoint.ResultEmpty(200)
        } else {
            // Validate updated data
            let validationResult = await Validator.validateSystemUpdate({
                name: updated.name, 
                url: updated.url
            })

            if(!validationResult.hasPassed()) {
                return validationResult.getError()
            }

            // Check if username or email exists
            let exists = await System.findOne({ 
                where: {
                    [Op.or]: [
                        { name: updated.name || "" },
                        { url: updated.url || "" }
                    ],
                    [Op.not]: [
                        { uuid: targetUUID }
                    ]
                }
            })        
            
            if(exists) {
                return TrustedError.get(TrustedError.Errors.RESOURCE_EXISTS)
            }

            // Update module
            await System.update(updated, { where: { uuid: targetUUID }})
            return new Endpoint.ResultEmpty(200)
        }
    }

}