package eu.tsalliance.auth.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.net.URL;
import java.util.UUID;

@Entity
@Table(name = "ts_apps")
public class Application {

    @Id
    private String id = UUID.randomUUID().toString();
    private String applicationName;
    private String applicationCode;
    private URL applicationUrl;

    private String applicationClientId;
    private String applicationClientSecret;
    private String applicationAccessToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public URL getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(URL applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getApplicationClientId() {
        return applicationClientId;
    }

    public void setApplicationClientId(String applicationClientId) {
        this.applicationClientId = applicationClientId;
    }

    public String getApplicationClientSecret() {
        return applicationClientSecret;
    }

    public void setApplicationClientSecret(String applicationClientSecret) {
        this.applicationClientSecret = applicationClientSecret;
    }

    public String getApplicationAccessToken() {
        return applicationAccessToken;
    }

    public void setApplicationAccessToken(String applicationAccessToken) {
        this.applicationAccessToken = applicationAccessToken;
    }
}
