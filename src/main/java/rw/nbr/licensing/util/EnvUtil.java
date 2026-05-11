package rw.nbr.licensing.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.application.super-admin")
public class EnvUtil {

    private final Environment environment;

    @Value("${spring.application.super-admin.institution_Abbr}")
    private String institutionAbbr;

    @Value("${spring.application.super-admin.institution_name}")
    private String institutionName;

    @Value("${spring.application.super-admin.TIN}")
    private String tin;

    @Value("${spring.application.super-admin.full_name}")
    private String fullname;

    @Value("${spring.application.super-admin.ID}")
    private String idNumber;

    /**
     * when annotated class with @ConfigurationProperties
     * you can directly get this value as field variable however you should always have setter
     * This is good for when you have grouped setting instead of single value.
     * Again drawback of @ConfigurationProperties does not allow Snake_Case but only camelCase and kebab-case
     * However the best practice .yml file should have camelCase or Snake_Case
     */
    // :: this was the test purpose
    private String phone;

    @Value("${spring.application.super-admin.email}")
    private String email;

    private String password;


    public EnvUtil(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getInstitutionAbbr() {
        return institutionAbbr;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public String getTin() {
        return tin;
    }

    public String getFullname() {
        return fullname;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    // Environment property:: useful to read complex value/security and so on.
    // Otherwise,numeric or combined characters will not full read in @Value except you make it string.
    private String getProperty(String key) {
        return environment.getProperty(key);
    }
}
