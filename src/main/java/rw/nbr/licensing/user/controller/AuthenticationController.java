package rw.nbr.licensing.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.nbr.licensing.exception.ApiException;
import rw.nbr.licensing.exception.ErrorDetails;
import rw.nbr.licensing.exception.MessageExceptionHandler;
import rw.nbr.licensing.user.request.AuthenticationRequest;
import rw.nbr.licensing.user.service.AuthenticationService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/account-access")
@RequiredArgsConstructor
@Tag(name = "Authentication",
        description = "The API for User Authentication Management")
public class AuthenticationController {


    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @Operation(
            summary = "The EndPoint for Authentication",
            description = "This EndPoint allows User to provide email and password  for " +
                    "being authenticated in our resources from the system, He provide this " +
                    "request in JSON body  and S/He get response from server as JSON format." +
                    "The EndPoint returns access token and refresh token  and Access token are used" +
                    "in header of each request to access other APIs such Awarded Tenders(Contracts) API.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PostMapping("/authenticate")
    public ResponseEntity</*AuthenticationResponse*/ ?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        logger.info(" The Authentication Login request is about to be processed ");

        try {

            var response = service.authenticate(request);

            logger.info("The authentication for user " + request.getEmail() +
                    " is successfully logged in ");

            return ResponseEntity.ok(response);

        } catch (ApiException ex) {

            var error = ErrorDetails.builder()
                    .code(ex.getCode())
                    .details(ex.getDetails())
                    .build();

            var message = MessageExceptionHandler.builder()
                    .status(ex.getStatus())   //HTTP Status code
                    .message(ex.getMessage())    // HTT Status Message
                    .error(error)
                    .build();
            return ResponseEntity.status(ex.getStatus())
                    .header("Email-Password", "Required")
                    .body(message);

        } catch (BadCredentialsException ex) {

            logger.warn("Login failed for email {}: Invalid credentials", request.getEmail());

            var error = ErrorDetails.builder()
                    .code("LOGIN_ERROR")
                    .details("Invalid email or password")
                    .build();
            var message = MessageExceptionHandler.builder()
                    .status(HttpStatus.FORBIDDEN.value())   //HTTP Status code
                    .message(ex.getMessage())    // HTT Status Message
                    .error(error)
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(message);

        } catch (HttpMessageNotReadableException ex) {
            var error = ErrorDetails.builder()
                    .code("EMPTY_REQUEST_BODY")
                    .details("Request body cannot be empty")
                    .build();
            var message = MessageExceptionHandler.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .error(error)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(message);
        } catch (DisabledException ex) {

            logger.warn("Login failed for email {}: Account is disabled", request.getEmail());

            var error = ErrorDetails.builder()
                    .code("LOGIN_ERROR")
                    .details("Your account has expired. Contact admin.")
                    .build();
            var message = MessageExceptionHandler.builder()
                    .status(HttpStatus.FORBIDDEN.value())   //HTTP Status code
                    .message(ex.getMessage())    // HTT Status Message
                    .error(error)
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(message);


        } catch (LockedException ex) {

            logger.warn("Login failed for email {}: Account is locked", request.getEmail());

            var error = ErrorDetails.builder()
                    .code("LOGIN_ERROR")
                    .details("Your account has expired. Contact admin.")
                    .build();
            var message = MessageExceptionHandler.builder()
                    .status(HttpStatus.FORBIDDEN.value())   //HTTP Status code
                    .message(ex.getMessage())    // HTT Status Message
                    .error(error)
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(message);

        } catch (AccountExpiredException ex) {

            logger.warn("Login failed for email {}: Account expired", request.getEmail());

            var error = ErrorDetails.builder()
                    .code("LOGIN_ERROR")
                    .details("Your account has expired. Contact admin.")
                    .build();
            var message = MessageExceptionHandler.builder()
                    .status(HttpStatus.FORBIDDEN.value())   //HTTP Status code
                    .message(ex.getMessage())    // HTT Status Message
                    .error(error)
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(message);

        } catch (AuthenticationException ex) {

            logger.error("Unexpected authentication error for email {}: {}", request.getEmail(), ex.getMessage());

            var error = ErrorDetails.builder()
                    .code("LOGIN_ERROR")
                    .details("Authentication failed. Please try again.")
                    .build();
            var message = MessageExceptionHandler.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())   //HTTP Status code
                    .message(ex.getMessage())    // HTT Status Message
                    .error(error)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(message);

        } catch (Exception ex) {

            logger.error("Unexpected server error: {}", ex.getMessage(), ex);

            var error = ErrorDetails.builder()
                    .code("SERVER_ERROR")
                    .details("The Error Occurred on server, Please contact System Administrator")
                    .build();

            var message = MessageExceptionHandler.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())   //HTTP Status code
                    .message(ex.getMessage())    // HTT Status Message
                    .error(error)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("SERVER-ERROR", "Server ADMIN")
                    .body(message);
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
}
