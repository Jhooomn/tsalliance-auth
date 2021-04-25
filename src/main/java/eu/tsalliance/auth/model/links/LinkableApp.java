package eu.tsalliance.auth.model.links;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.net.URL;
import java.util.UUID;

@Entity
@Table(name = "ts_linkableApps")
public class LinkableApp {

    @Id
    private String id = UUID.randomUUID().toString();

    private String appName;
    private URL appUrl;
    private URL authorizeUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public URL getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(URL appUrl) {
        this.appUrl = appUrl;
    }

    public URL getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(URL authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
    }
}
