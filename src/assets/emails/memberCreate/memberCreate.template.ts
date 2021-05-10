import fs from 'fs'
import config from '../../../config/config'

export default {
    from: "TSAlliance <noreply@tsalliance.eu>",
    subject: "Willkommen in der Allianz!",
    html: fs.readFileSync(config.app.rootDir + "/assets/emails/memberCreate/memberCreate.html").toString('utf-8')
}