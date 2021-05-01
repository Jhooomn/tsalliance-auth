package eu.tsalliance.auth.model;

import eu.tsalliance.auth.model.image.Image;
import eu.tsalliance.auth.utils.RandomUtil;

import javax.persistence.*;
import java.net.URL;
import java.util.UUID;

@Entity
@Table(name = "ts_apps")
public class Application {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(unique = true, nullable = false)
    private String name;
    private String description;

    @Column(unique = true, nullable = false)
    private URL url;

    @Column(unique = true, nullable = false)
    private int code = RandomUtil.generateRandomNumber(4);

    @Column(unique = true, nullable = false)
    private String clientId = RandomUtil.generateRandomString(64);

    @Column(unique = true, nullable = false)
    private String clientSecret = RandomUtil.generateRandomString(64);

    @Column(unique = true, nullable = false)
    private String accessToken = RandomUtil.generateRandomString(64);

    @OneToOne
    private Image logo;

    public Application() { }
    public Application(String id) {
        this.id = id;
    }

    public Image getLogo() {
        return logo;
    }

    public void setLogo(Image logo) {
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
