import fs from 'fs'
import config from '../../../config/config'

export default {
    from: "TSAlliance <noreply@tsalliance.eu>",
    subject: "Password reset request",
    html: fs.readFileSync(config.app.rootDir + "/assets/emails/reset/reset.html").toString('utf-8')
}