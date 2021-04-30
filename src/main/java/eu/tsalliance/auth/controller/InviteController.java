package eu.tsalliance.auth.controller;

import eu.tsalliance.auth.model.Invite;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.InviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invites")
public class InviteController {

    @Autowired
    private InviteService inviteService;

    @GetMapping
    public Page<Invite> listAll(Pageable pageable) {
        return this.inviteService.findAll(pageable);
    }

    @GetMapping("{id}")
    public ResponseEntity<Invite> findInvite(@PathVariable("id") String id) {
        return ResponseEntity.of(this.inviteService.findById(id));
    }

    @PostMapping
    public Invite createInvite(@RequestBody Invite invite) throws Exception {
        return this.inviteService.createInvite(invite);
    }

    @PostMapping("invite")
    public Invite inviteMail(@RequestParam("email") String email) throws Exception {
        // TODO: Authenticate user and get username
        return this.inviteService.inviteEmail(new User(), email);
    }

    @DeleteMapping("{id}")
    public void deleteInvite(@PathVariable("id") String id) {
        this.inviteService.deleteInviteById(id);
    }

}
