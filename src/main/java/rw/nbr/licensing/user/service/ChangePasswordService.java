package rw.nbr.licensing.user.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rw.nbr.licensing.user.entity.InstitutionAccessAccount;
import rw.nbr.licensing.user.repository.InstitutionAccountRepository;
import rw.nbr.licensing.user.request.ChangePasswordRequest;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordService.class);
    private final PasswordEncoder passwordEncoder;
    private final InstitutionAccountRepository repository;

    public ChangePasswordService(PasswordEncoder passwordEncoder, InstitutionAccountRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        logger.info("The service  is reached to perform action..");

        var user = (InstitutionAccessAccount) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {

            logger.warn("The Current password provided is wrong: " + request.getCurrentPassword());

            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {

            logger.warn("Password is not same, new password and confirmed should be the same");

            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);

        logger.info("The service is done its job!");
    }
}
