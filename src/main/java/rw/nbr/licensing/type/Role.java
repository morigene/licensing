package rw.nbr.licensing.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



@RequiredArgsConstructor
public enum Role {



    APPLICANT(
            Set.of(
                    Permission.APPLICATION_CREATE,
                    Permission.APPLICATION_READ_OWN,
                    Permission.APPLICATION_UPDATE_OWN,
                    Permission.APPLICATION_SUBMIT,
                    Permission.APPLICATION_RESUBMIT,

                    Permission.DOCUMENT_UPLOAD,
                    Permission.DOCUMENT_READ_OWN,

                    Permission.AUDIT_READ_OWN
            )
    ),

    COMPLIANCE_OFFICER(
            Set.of(

                    Permission.APPLICATION_READ_ALL,
                    Permission.APPLICATION_COMPLIANCE_CHECK,
                    Permission.APPLICATION_REQUEST_INFO,

                    Permission.DOCUMENT_READ_ALL,

                    Permission.AUDIT_READ_ALL
            )
    ),

    APPROVER(
            Set.of(

                    Permission.APPLICATION_READ_ALL,
                    Permission.APPLICATION_APPROVE,
                    Permission.APPLICATION_REJECT,

                    Permission.DOCUMENT_READ_ALL,

                    Permission.AUDIT_READ_ALL
            )
    ),

    ADMIN(
            Set.of(

                    Permission.USER_CREATE,
                    Permission.USER_READ,
                    Permission.USER_UPDATE,
                    Permission.USER_DISABLE,

                    Permission.APPLICATION_READ_ALL,
                    Permission.DOCUMENT_READ_ALL,
                    Permission.AUDIT_READ_ALL
            )
    ),


    SUPER_ADMIN(
            EnumSet.allOf(Permission.class)
    );




    private final Set<Permission> permissions;

  /*  public List<SimpleGrantedAuthority> getAuthorities() {

        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }*/

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {

        var authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.name()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return authorities;
    }
}
