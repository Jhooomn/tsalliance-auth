package eu.tsalliance.auth.controller;

import eu.tsalliance.apiutils.exception.MimeTypeNotSupported;
import eu.tsalliance.apiutils.exception.NotFoundException;
import eu.tsalliance.auth.model.image.Image;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("logos/{id}")
    public void getLogo(@PathVariable("id") String id, HttpServletResponse response) throws IOException, NotFoundException {
        this.fileStorageService.serveImage(id, "logo", response);
    }

    @PostMapping("logos/{appId}")
    @PreAuthorize("hasPermission('alliance.logos.write')")
    public Image uploadLogo(@PathVariable("appId") String id, MultipartFile file) throws IOException, NotFoundException, MimeTypeNotSupported {
        return this.fileStorageService.storeAppLogo(file, id);
    }

    @GetMapping("avatars/{id}")
    public void getAvatar(@PathVariable("id") String id, HttpServletResponse response) throws IOException, NotFoundException {
        this.fileStorageService.serveImage(id, "avatar", response);
    }

    @PostMapping("avatars")
    public Image uploadAvatar(MultipartFile file, Authentication authentication) throws IOException, MimeTypeNotSupported, NotFoundException {
        return this.fileStorageService.storeAvatar(file, ((User) authentication.getPrincipal()).getId());
    }

}
