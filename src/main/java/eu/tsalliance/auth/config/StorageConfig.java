package eu.tsalliance.auth.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class StorageConfig {

    public final String MIME_IMAGE_JPG = "image/jpg";
    public final String MIME_IMAGE_JPEG = "image/jpeg";
    public final String MIME_IMAGE_PNG = "image/png";

    @PostConstruct
    private void createDirectories() throws IOException {
        File avatarsDir = new File(this.getAvatarsUploadDirPath().toAbsolutePath().toString());
        File logosDir = new File(this.getLogosUploadDirPath().toAbsolutePath().toString());

        if(!avatarsDir.exists()) {
            Files.createDirectories(avatarsDir.toPath());
        }

        if(!logosDir.exists()) {
            Files.createDirectories(logosDir.toPath());
        }
    }

    public Path getUploadDirPath(){
        return new File(System.getProperty("user.dir") + File.separator + "uploads").toPath();
    }
    public Path getAvatarsUploadDirPath(){
        return new File(System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatars").toPath();
    }
    public Path getLogosUploadDirPath(){
        return new File(System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "logos").toPath();
    }

    public Path buildAvatarTargetPath(String id){
        return new File(this.getAvatarsUploadDirPath().toAbsolutePath().toString(), id + ".png").toPath();
    }
    public Path buildLogoTargetPath(String id){
        return new File(this.getLogosUploadDirPath().toAbsolutePath().toString(), id + ".png").toPath();
    }

    public List<String> getSupportedMimeTypes(){
        return new ArrayList<>(Arrays.asList(
                MIME_IMAGE_JPG,
                MIME_IMAGE_JPEG,
                MIME_IMAGE_PNG
        ));
    }

    public boolean isSupportedMimeType(String mimeType) {
        return this.getSupportedMimeTypes().contains(mimeType.toLowerCase());
    }

}
