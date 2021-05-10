package eu.tsalliance.auth.service;

import eu.tsalliance.apiutils.exception.MimeTypeNotSupported;
import eu.tsalliance.apiutils.exception.NotFoundException;
import eu.tsalliance.auth.config.StorageConfig;
import eu.tsalliance.auth.model.Application;
import eu.tsalliance.auth.model.image.Image;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.ImageRepository;
import eu.tsalliance.auth.service.account.UserService;
import eu.tsalliance.auth.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.Optional;

@Service
@Slf4j
public class FileStorageService {

    @Autowired
    private StorageConfig storageConfig;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    /**
     * Serve an image to the given response object
     * @param id Id of the image
     * @param imageType Whether it is an avatar or a logo
     * @param response HttpServletResponse
     * @throws IOException Read failed
     */
    public void serveImage(String id, String imageType, HttpServletResponse response) throws IOException, NotFoundException {
        FileSystemResource file;

        if(imageType.equalsIgnoreCase("avatar")) {
            file = new FileSystemResource(this.storageConfig.buildAvatarTargetPath(id));
        } else {
            file = new FileSystemResource(this.storageConfig.buildLogoTargetPath(id));
        }

        if(!file.getFile().exists()) {
            throw new NotFoundException();
        }

        log.error(file.getFile().getAbsolutePath());

        response.setContentType(this.storageConfig.MIME_IMAGE_PNG);
        StreamUtils.copy(file.getInputStream(), response.getOutputStream());
    }

    /**
     * Store logo and bind to application
     * @param file Multipart file
     * @param appId Application's id to set logo for
     * @return Image
     * @throws MimeTypeNotSupported Mime not supported
     * @throws IOException Write failed
     */
    public Image storeAppLogo(MultipartFile file, String appId) throws MimeTypeNotSupported, IOException, NotFoundException {
        Optional<Application> application = this.applicationService.findById(appId);
        if(application.isEmpty()) {
            throw new NotFoundException();
        }

        Image image = Optional.ofNullable(application.get().getLogo()).orElse(new Image());
        image.setUri("alliance:logo:" + image.getId());
        image.setEtag(RandomUtil.generateRandomString(8));

        Path targetFile = this.storageConfig.buildLogoTargetPath(image.getId());
        this.storeImage(file, targetFile, image);

        image = this.imageRepository.saveAndFlush(image);
        application.get().setLogo(image);
        this.applicationService.saveApp(application.get());

        return image;
    }

    /**
     * Store avatar and bind to user
     * @param file Multipart file
     * @param userId User's id to set avatar for
     * @return Image
     * @throws MimeTypeNotSupported Mime not supported
     * @throws IOException Write failed
     */
    public Image storeAvatar(MultipartFile file, String userId) throws MimeTypeNotSupported, IOException, NotFoundException {
        Optional<User> user = this.userService.findUserById(userId);
        if(user.isEmpty()) {
            throw new NotFoundException();
        }

        Image image = Optional.ofNullable(user.get().getAvatar()).orElse(new Image());
        image.setUri("alliance:avatar:" + image.getId());
        image.setEtag(RandomUtil.generateRandomString(8));

        Path targetFile = this.storageConfig.buildAvatarTargetPath(image.getId());
        this.storeImage(file, targetFile, image);

        image = this.imageRepository.saveAndFlush(image);
        user.get().setAvatar(image);
        this.userService.saveUser(user.get());

        return image;
    }

    /**
     * Store a multipart file on local filesystem
     * @param file Multipart File
     * @return Image
     * @throws MimeTypeNotSupported Mime not supported
     * @throws IOException Write failed
     */
    private Image storeImage(MultipartFile file, Path target, Image image) throws MimeTypeNotSupported, IOException {
        if (!this.storageConfig.isSupportedMimeType(file.getContentType())) {
            throw new MimeTypeNotSupported(file.getContentType(), this.storageConfig.getSupportedMimeTypes());
        }

        // Create new image data object
        image.setMimeType(this.storageConfig.MIME_IMAGE_PNG);

        // Write file
        this.resizeImage(file.getInputStream(), target, 256, 256);

        return image;
    }

    public void resizeImage(InputStream input, Path target, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(input);
        java.awt.Image newResizedImage = originalImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);

        String s = target.getFileName().toString();
        String fileExtension = s.substring(s.lastIndexOf(".") + 1);

        // we want image in png format
        ImageIO.write(this.convertToBufferedImage(newResizedImage), fileExtension, target.toFile());
    }

    private BufferedImage convertToBufferedImage(java.awt.Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.dispose();

        return bi;
    }


}
