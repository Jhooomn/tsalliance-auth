package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.model.Invite;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.InviteService;
import eu.tsalliance.exception.BadOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invites")
public class InviteController {

    @Autowired
    private InviteService inviteService;

    @GetMapping
    @PreAuthorize("hasAuthority('alliance.invites.read')")
    public Page<Invite> listAll(Pageable pageable) {
        return this.inviteService.findAll(pageable);
    }

    @GetMapping("{id}")
    public ResponseEntity<Invite> findInvite(@PathVariable("id") String id) {
        return ResponseEntity.of(this.inviteService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('alliance.invites.write')")
    public Invite createInvite(@RequestBody Invite invite) throws Exception {
        return this.inviteService.createInvite(invite);
    }

    @PostMapping("invite")
    //@PreAuthorize("hasAuthority('alliance.invites.write')")
    public Invite inviteMail(@RequestParam("email") String email, Authentication authentication) throws Exception {
        User user = (User) authentication.getPrincipal();

        if(user.getEmail().equalsIgnoreCase(email)) {
            throw new BadOperationException();
        }

        return this.inviteService.inviteEmail(user, email);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('alliance.invites.write')")
    public void deleteInvite(@PathVariable("id") String id) {
        this.inviteService.deleteInviteById(id);
    }

}
