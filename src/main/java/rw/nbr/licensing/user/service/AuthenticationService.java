package rw.nbr.licensing.user.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rw.nbr.licensing.exception.ApiException;
import rw.nbr.licensing.user.entity.InstitutionAccessAccount;
import rw.nbr.licensing.user.repository.InstitutionAccountRepository;
import rw.nbr.licensing.user.request.AuthenticationRequest;
import rw.nbr.licensing.user.request.InstitutionAccountRegisterRequest;
import rw.nbr.licensing.user.response.AuthenticationResponse;
import rw.nbr.licensing.user.token.JwtService;
import rw.nbr.licensing.user.token.TokenRepository;
import rw.nbr.licensing.user.token.TokenType;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final InstitutionAccountRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(InstitutionAccountRepository repository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(InstitutionAccountRegisterRequest request) {

        logger.info("The Authentication service, received info" +
                "to be processed for registering the account of {}", request);


        // Validate entry request for required fields.
        ValidateAccountRegistrationRequest(request)
        ;

        var user = InstitutionAccessAccount.builder()
                .institutionAbbr(request.getInstitutionAbbr())
                .institutionName(request.getInstitutionName())
                .tin(request.getTin())
                .fullname(request.getFullname())
                .email(request.getEmail())
                .idNumber(request.getIdNumber())
                .contactPhoneNumber(request.getContactPhoneNumber())
                .role(request.getRole())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();


        // Save user information {} into database ::TABLE EL_INST_AUTH_ACCESS_ACCOUNT
        /*
         *var savedUser = Optional.ofNullable(repository.save(user))
                .orElseThrow(() -> new ApiException(500, "Internal Server Error", "USER_SAVE_FAILED", "Failed to save the user."));
                *
                * the code below are the same as this in comment.
                * */

        // Retrieve email to check if existing from database.
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.name(), "USER_SAVE_FAILED", "Email is already Exist");
        }

        var savedUser = repository.save(user);

        // Check if user saved in database is null
        if (savedUser == null || savedUser.getId() == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(), "USER_SAVE_FAILED", "Failed to save the user.");
        }

        logger.info("User successfully saved with ID: {}", savedUser.getId());


        // Generate token and refreshToken and validate if it's generated
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        if (jwtToken.isBlank() || jwtToken == null) {
            logger.warn("Token is null, failed to be generated.");
        }

        //Save token information and userId into token table in database.
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        logger.info("The Authentication service is received  credentials " +
                "  and it's about to be processed");

        //Check if the email or password is null or empty
        validateEmailAndPassword(request.getEmail(), request.getPassword());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);

        saveUserToken(user, jwtToken);

        logger.info(" The Authentication service for login is done. ");

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(InstitutionAccessAccount institutionAccessAccount, String jwtToken) {
        var token = Token.builder()
                .institutionAccessAccount(institutionAccessAccount)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        Optional.ofNullable(tokenRepository.save(token)).
                orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.name(), "TOKEN_SAVED_FAILED", "Failed to save token"));

        logger.info("Token successfully generated and saved in database with value{ }", token);
    }

    private void revokeAllUserTokens(InstitutionAccessAccount user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        logger.info("The refresh token request is received and it's about to be processed");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            logger.warn("Auth header is null or not started with Bear");

            return;
        }

        refreshToken = authHeader.substring(7);

        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {

            logger.info("The email  found for refreshing token is:  " + userEmail);

            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }


    private void validateEmailAndPassword(String email, String password) {

        if (email == null || email.isEmpty()) {

            throw new ApiException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(),
                    "MISSING_EMAIL", "The email is required.");
        }
        if (password == null || password.isEmpty()) {

            throw new ApiException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(),
                    "MISSING_PASSWORD", "The Password is required.");
        }
    }
/*

        private void ValidateAccountRegistrationRequest(InstitutionAccountRegisterRequest request){

            if (request.getInstitutionAbbr() == null || request.getInstitutionAbbr().isEmpty()) {
                throw new ApiException(400, "Bad Request", "MISSING_INSTITUTION_ABBR", "The institution abbreviation is required.");
            }

            if (request.getInstitutionName() == null || request.getInstitutionName().isEmpty()) {
                throw new ApiException(400, "Bad Request", "MISSING_INSTITUTION_NAME", "The institution name is required.");
            }

            if (request.getTin() == null || request.getTin().isEmpty()) {
                throw new ApiException(400, "Bad Request", "MISSING_TIN", "The TIN (Tax Identification Number) is required.");
            }

            if (request.getFullname() == null || request.getFullname().isEmpty()) {
                throw new ApiException(400, "Bad Request", "MISSING_FULLNAME", "The fullname is required.");
            }

            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new ApiException(400, "Bad Request", "MISSING_EMAIL", "The email is required.");
            }

            if (request.getIdNumber() == null || request.getIdNumber().isEmpty()) {
                throw new ApiException(400, "Bad Request", "MISSING_ID_NUMBER", "The ID number is required.");
            }

            if (request.getContactPhoneNumber() == null || request.getContactPhoneNumber().isEmpty()) {
                throw new ApiException(400, "Bad Request", "MISSING_CONTACT_PHONE", "The contact phone number is required.");
            }

            if (request.getRole() == null || request.getRole().getPermissions().isEmpty()){
                throw new ApiException(400, "Bad Request", "MISSING_ROLE", "The user role is required.");
            }

            if (request == null || request.getPassword().isEmpty()) {
                throw new ApiException(400, "Bad Request", "MISSING_PASSWORD", "The password is required.");
            }


        }
*/

    // This code replace above method because it's short and conside
    private void ValidateAccountRegistrationRequest(InstitutionAccountRegisterRequest request) {

        if (request.getRole() == null) {

            throw new ApiException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(),
                    "MISSING_ROLE", "The user role is required.");
        }

        Map<String, String> fields = new HashMap<>();

        fields.put("institutionAbbr", request.getInstitutionAbbr());
        fields.put("institutionName", request.getInstitutionName());
        fields.put("tin", request.getTin());
        fields.put("fullname", request.getFullname());
        fields.put("email", request.getEmail());
        fields.put("idNumber", request.getIdNumber());
        fields.put("contactPhoneNumber", request.getContactPhoneNumber());
        fields.put("password", request.getPassword());

        for (Map.Entry<String, String> entry : fields.entrySet()) {

            if (entry.getValue() == null || entry.getValue().isEmpty()) {

                throw new ApiException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(),
                        "MISSING_" + entry.getKey().toUpperCase(),
                        "The " + entry.getKey() + " is required");
            }
        }
    }
}
