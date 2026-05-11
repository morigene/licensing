package rw.nbr.licensing.user.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rw.nbr.licensing.type.Role;
import rw.nbr.licensing.user.token.Token;


import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "INST_AUTH_ACCESS_ACCOUNT")
public class InstitutionAccessAccount implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "INSTITUTION_ID")
    private String id;


    @NotEmpty
    @NotNull
    @Size(max = 5)
    @Column(name = "INSTITUTION_ABBR", length = 5)
    private String institutionAbbr;


    @NotNull
    @NotEmpty
    @Size(max = 100)
    @Column(name = "INSTITUTION_NAME", length = 255)
    private String institutionName;


    @NotNull
    @NotEmpty
    @Size(max = 9)
    @Column(name = "INSTITUTION_TIN", length = 9)
    private String tin;


    @NotNull
    @NotEmpty
    @Size(max = 100)
    @Column(name = "REPRESENTATIVE_NAME", length = 255)
    private String fullname;

    @NotNull
    @NotEmpty
    @Size(max = 16)
    @Column(name = "REPRESENTATIVE_ID", length = 16)
    private String idNumber;

    @NotNull
    @NotEmpty
    @Size(max = 10)
    @Column(name = "CONTACT_PHONE", length = 10)
    private String contactPhoneNumber;

    @NotNull
    @NotEmpty
    @Size(max = 35)
    @Column(name = "EMAIL", length = 255, unique = true)
    private String email;

    @NotNull
    @NotEmpty
    @Column(name = "PASSWORD")
    private String password;


    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "institutionAccessAccount")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
