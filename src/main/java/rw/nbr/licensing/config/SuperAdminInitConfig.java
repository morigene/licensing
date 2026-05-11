package rw.nbr.licensing.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import rw.nbr.licensing.type.Role;
import rw.nbr.licensing.user.entity.InstitutionAccessAccount;
import rw.nbr.licensing.user.repository.InstitutionAccountRepository;
import rw.nbr.licensing.user.request.InstitutionAccountRegisterRequest;
import rw.nbr.licensing.user.service.AuthenticationService;
import rw.nbr.licensing.util.EnvUtil;

import java.util.Optional;

import static rw.nbr.licensing.type.Role.SUPER_ADMIN;


@Configuration
public class SuperAdminInitConfig {
    private static final Logger logger = LoggerFactory.getLogger(SuperAdminInitConfig.class);

    @Value("${spring.application.super-admin.init:false}")  // Read flag from application.properties
    private boolean isSuperAdminInitEnabled;

    @Value("${spring.application.super-admin.password}")
    private String superAdminPassword;

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service,
            InstitutionAccountRepository repository,
            EnvUtil env
    ) {
        return args -> {
            if (!isSuperAdminInitEnabled) {
                logger.info("Super Admin initialization is disabled.");
                return;
            }

            logger.info("Checking if Super Admin needs to be created...");

            String superAdminEmail = env.getEmail();
            Optional<InstitutionAccessAccount> existingSuperAdmin = repository.findByEmail(superAdminEmail);

            if (existingSuperAdmin.isPresent()) {
                logger.warn("Super Admin already exists with email: {}", superAdminEmail);
                return;
            }

            var superAdmin = InstitutionAccountRegisterRequest.builder()
                    .institutionName(env.getInstitutionName())
                    .institutionAbbr(env.getInstitutionAbbr())
                    .tin(env.getTin())
                    .fullname(env.getFullname())
                    .email(superAdminEmail)
                    .idNumber(env.getIdNumber())
                    .contactPhoneNumber(env.getPhone())
                    .role(Role.SUPER_ADMIN)
                    .password(superAdminPassword)
                    .build();

            try {
                service.register(superAdmin);
                logger.info("Super Admin {} registered successfully.", superAdminEmail);
            } catch (DataIntegrityViolationException e) {
                logger.error("Error while registering Super Admin: {}", e.getMessage());
            }
        };
    }
}