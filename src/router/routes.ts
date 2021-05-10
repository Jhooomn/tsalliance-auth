import { Endpoint } from "../endpoint/endpoint"

import AuthEndpoint from "../endpoint/authEndpoint"
import AvatarEndpoint from "../endpoint/avatarEndpoint"
import MemberEndpoint from "../endpoint/memberEndpoint"
import AppConnectionEndpoint from "../endpoint/appconnectionEndpoint"
import InviteEndpoint from "../endpoint/invitesEndpoint"
import SystemsEndpoint from "../endpoint/systemsEndpoint"


export namespace Routes {
    export const list: Array<RouteGroup> =  [
        {
            handler: MemberEndpoint,
            groupname: 'members',
            routes: [
                { name: 'MemberGetMultiple', path: '/members', action: 'getMultiple', method: 'get'},
                { name: 'MemberGet', path: '/members/:uuid', action: 'getOne', method: 'get' },
                { name: 'MemberDelete', path: '/members/:uuid', action: 'deleteOne', method: 'delete'},
                { name: 'MemberCreate', path: '/members', action: 'createOne', method: 'post'},
                { name: 'MemberUpdate', path: '/members/:uuid', action: 'updateOne', method: 'put'},
                { name: 'MemberUpdatePassword', path: '/members/:uuid/password', action: 'changePassword', method: 'put'},
            ]
        },
        {
            handler: AuthEndpoint,
            groupname: 'authentication',
            routes: [
                { name: 'AuthLogin', path: '/auth/signin', action: 'loginOne', method: 'post'},
                { name: 'AuthRegister', path: '/auth/signup', action: 'registerOne', method: 'post'},
                { name: 'AuthRequestReset', path: '/auth/reset', action: 'requestReset', method: 'post'},
                { name: 'AuthResetPassword', path: '/auth/reset', action: 'resetPassword', method: 'put'}
            ]
        },
        {
            handler: AvatarEndpoint,
            groupname: 'avatars',
            routes: [
                { name: 'GetAccountAvatar', path: '/avatars/:hash', action: 'getOne', method: 'get'},
                { name: 'SetAccountAvatar', path: '/avatars/:uuid', action: 'setOne', method: 'post'},
                { name: 'DeleteAccountAvatar', path: '/avatars/:uuid', action: 'deleteOne', method: 'delete'}
            ]
        },
        {
            handler: AppConnectionEndpoint,
            groupname: 'appconnections',
            routes: [
                { name: 'ConnectionGetAll', path: '/connections/:uuid', action: 'getAll', method: 'get'},
                { name: 'ConnectionCreate', path: '/connections/:uuid/:app', action: 'createOne', method: 'post'},
                { name: 'ConnectionDelete', path: '/connections/:uuid/:app', action: 'deleteOne', method: 'delete'},
            ]
        },
        {
            handler: InviteEndpoint,
            groupname: 'invites',
            routes: [
                {name: 'InviteGetMultiple', path: '/invites', action: 'getMultiple', method: 'get' },
                {name: 'InviteGet', path: '/invites/:uuid', action: 'getOne', method: 'get'},
                {name: 'InviteDelete', path: '/invites/:uuid', action: 'deleteOne', method: 'delete'},
                {name: 'InviteCreate', path: '/invites', action: 'createOne', method: 'post'},
            ]
        },
        {
            handler: SystemsEndpoint,
            groupname: 'modules',
            routes: [
                {name: 'ModuleGetAll', path: '/modules', action: 'getAll', method: 'get'},
                {name: 'ModuleGet', path: '/modules/:uuid', action: 'getOne', method: 'get'},
                {name: 'ModuleUpdate', path: '/modules/:uuid', action: 'updateOne', method: 'put'},
                {name: 'ModuleDelete', path: '/modules/:uuid', action: 'deleteOne', method: 'delete'},
                {name: 'ModuleCreate', path: '/modules', action: 'createOne', method: 'post'},
            ]
        }
    ]

    /**
     * Only used to define routing groups
     */
    export class RouteGroup {
        public readonly handler: { new(): Endpoint }
        public readonly groupname: String
        public readonly routes: Array<Route> = []

        constructor(handler: { new(): Endpoint }, groupname: String) {
            this.handler = handler
            this.groupname = groupname
        }
    }

    /**
     * Only used to define routes
     */
    export class Route {
        public readonly name: String
        public readonly path: String
        public readonly action: String
        public readonly method: String
        public readonly meta?: String

        constructor(name: String, path: String, action: String, method: String) {
            this.name = name
            this.path = path
            this.action = action
            this.method = method
        }
    }
}
