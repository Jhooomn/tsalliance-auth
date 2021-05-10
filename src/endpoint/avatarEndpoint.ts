import { Endpoint } from "./endpoint";
import { Router } from "../router";
import { TrustedError } from "../error/trustedError";

import config from '../config/config'
import fs from "fs"
import { randomBytes } from "crypto";
import { Member } from "../account/member";
import { MediaUtil } from "../utils/mediaUtil";

export default class AvatarEndpoint extends Endpoint {

    constructor() {
        super(
            Endpoint.AuthenticationFlag.FLAG_NO_AUTH,
            [
                { endpointAction: "setOne", permission: "permission.avatars.set" },
                { endpointAction: "deleteOne", permission: "permission.avatars.delete" }
            ])

        MediaUtil.setupUploadDirectory()
    }

    /**
     * @api {get} /avatars/:hash Get avatar
     */
    async actionGetOne(route: Router.Route): Promise<Endpoint.Result> {
        let avatarHash = route.params?.["hash"]
        let file = config.app.rootDir+"/uploads/avatars/"+avatarHash+".jpeg"

        if(!fs.existsSync(file)) {
            file = config.app.rootDir+"/assets/images/ts_logo_background.jpeg"
        }

        // Send avatar
        let readStream = fs.createReadStream(file)
        route.response.setHeader('Content-Type', "image/jpeg")
        readStream.on("open", () => {
            readStream.pipe(route.response)
        })
        readStream.on("close", () => {
            route.response.end()
        })

        // Return response
        let response = new Endpoint.ResultEmpty(200)
        response.renderFormat = "IMAGE"
        return response   
    }

    /**
     * @api {post} /avatars/:uuid Set avatar
     */
    async actionSetOne(route: Router.Route): Promise<Endpoint.Result> {
        return new Promise<Endpoint.Result>((resolve) => {
            let targetUUID = route.params?.["uuid"]

            let busboy = route.request["busboy"]
            if(!busboy) {
                return TrustedError.get(TrustedError.Errors.MISSING_ATTACHMENTS)
            }

            Member.findOne({ where: { uuid: targetUUID }}).then((member) => {
                if(!member) {
                    return TrustedError.get(TrustedError.Errors.RESOURCE_NOT_FOUND)
                }

                route.request.pipe(busboy)
                busboy.on('file', (fieldname, file, filename, encoding, mimetype) => {
                    MediaUtil.createAvatar(member, file, mimetype)
                        .then((result) => resolve(result))
                        .catch((error) => resolve(error))
                })
            }).catch((error) => {
                console.log(error)
                resolve(TrustedError.get(TrustedError.Errors.INTERNAL_ERROR))
            })
        })
    }

    /**
     * @api {delete} /avatars/:uuid Delete avatar
     */
    async actionDeleteOne(route: Router.Route): Promise<Endpoint.Result> {
        let targetUUID = route.params?.["uuid"]

        let member = await Member.findOne({ where: { uuid: targetUUID }})
        if(!member) {
            return TrustedError.get(TrustedError.Errors.RESOURCE_NOT_FOUND)
        }

        return MediaUtil.deleteAvatar(member)
    }

}