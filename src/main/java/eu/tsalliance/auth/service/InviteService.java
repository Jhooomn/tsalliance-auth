package eu.tsalliance.auth.service;

import eu.tsalliance.apiutils.validator.Validator;
import eu.tsalliance.auth.model.Invite;
import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.repository.InviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class InviteService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private Validator validator;

    /**
     * Get an invite from the database by its id
     * @param id Invite's id
     * @return Optional of type Invite
     */
    public Optional<Invite> findById(String id) {
        return this.inviteRepository.findById(id);
    }

    /**
     * Get a page of invites
     * @param pageable Paging options
     * @return Page of type Invite
     */
    public Page<Invite> findAll(Pageable pageable) {
        return this.inviteRepository.findAll(pageable);
    }

    /**
     * Create a new invite.
     * @param invite Invite's data
     * @return Invite
     */
    public Invite createInvite(Invite invite) throws Exception {
        invite.setCreatedAt(new Date());
        invite.setUses(0);

        if(invite.canExpire()) {
            this.validator.date(invite.getExpiresAt(), "createdAt", false).after(new Date()).check();
        } else {
            invite.setExpiresAt(null);
        }

        // Set access to all apps if not specified
        if(invite.getAccessableApps() == null) {
            invite.setAccessableApps(this.applicationService.findAllIdOnly());
        }

        this.validator.number(invite.getMaxUses(), "maxUses", false).max(Integer.MAX_VALUE).min(1).check();
        this.validator.throwErrors();

        return this.inviteRepository.saveAndFlush(invite);
    }

    /**
     * Check if an invite is valid. Normally an invite is
     * invalid if it expired or has reached the maximum
     * amount of uses.
     * @param id Invite's id
     * @return True or False
     */
    public boolean isInviteValidById(String id) {
        if(id == null) return false;
        Optional<Invite> invite = this.findById(id);
        return invite.isPresent() && this.isInviteValid(invite.get());
    }

    /**
     * Check if an invite is valid. Normally an invite is
     * invalid if it expired or has reached the maximum
     * amount of uses.
     * @param invite Invite's data
     * @return True or False
     */
    public boolean isInviteValid(Invite invite) {
        if(invite == null) {
            return false;
        }

        int maxUses = invite.getMaxUses() == -1 ? Integer.MAX_VALUE : invite.getMaxUses();
        return (invite.canExpire() && invite.getExpiresAt().getTime() <= System.currentTimeMillis()) || (invite.getUses() < maxUses);
    }

    /**
     * Check if an invite is valid. Normally an invite is invalid
     * if it expired or has reached the maximum amount of uses.
     * Using this method, the invite gets deleted if it is invalid.
     * @param id Invite's id
     * @return True or False
     */
    public boolean isInviteValidAndDeleteById(String id) {
        if(this.isInviteValidById(id)) {
            return true;
        } else {
            this.deleteInviteById(id);
            return false;
        }
    }

    /**
     * Check if an invite is valid. Normally an invite is invalid
     * if it expired or has reached the maximum amount of uses.
     * Using this method, the invite gets deleted if it is invalid.
     * @param invite Invite data
     * @return True or False
     */
    public boolean isInviteValidAndDelete(Invite invite) {
        if(this.isInviteValid(invite)) {
            return true;
        } else {
            this.deleteInviteById(invite.getId());
            return false;
        }
    }

    /**
     * Delete an invite by its id
     * @param id Invite's id
     */
    public void deleteInviteById(String id) {
        if(id == null) return;
        this.inviteRepository.deleteById(id);
    }

    /**
     * Invite a new potential user by sending an invite via email
     * @param inviter Inviter's instance
     * @param email Recipient of the email
     * @throws Exception Exception
     */
    public Invite inviteEmail(User inviter, String email) throws Exception {

        this.validator.email(email, "email", true).check();
        this.validator.throwErrors();

        Invite oneTimeInvite = new Invite();
        oneTimeInvite.setCanExpire(false);
        oneTimeInvite.setMaxUses(1);
        oneTimeInvite.setInviter(inviter);

        oneTimeInvite = this.createInvite(oneTimeInvite);
        this.emailService.sendInviteMail(email, inviter.getUsername(), oneTimeInvite.getId());
        return oneTimeInvite;
    }

}
