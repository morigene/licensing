package rw.nbr.licensing.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.nbr.licensing.user.request.ChangePasswordRequest;
import rw.nbr.licensing.user.service.ChangePasswordService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "Change a Password",
        description = "This is helpful during user forget the password and need to reset or just wanting " +
                "to change the password")
public class ChangePasswordController {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordController.class);
    private final ChangePasswordService service;

    public ChangePasswordController(ChangePasswordService service) {
        this.service = service;
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        logger.info("The change password request is  received and it's about to be processed.");

        service.changePassword(request, connectedUser);

        return ResponseEntity.ok("The password has been changed");
    }
}
