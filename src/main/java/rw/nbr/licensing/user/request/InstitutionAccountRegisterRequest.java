package rw.nbr.licensing.user.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.nbr.licensing.type.Role;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionAccountRegisterRequest {

    // INSTITUTION REPRESENT COMMERCIAL BANK OR FINANCIAL
    @NotEmpty
    @NotNull
    @Size(max = 5)
    private String institutionAbbr;

    @NotEmpty(message = "Institution name can not be empty")
    @NotNull(message = "")
    @Size(max = 100)
    private String institutionName;

    @NotNull
    @NotEmpty
    @Size(max = 9)
    private String tin;


    @NotNull
    @NotBlank(message = "The fullname is required")
    @Size(max = 50)
    private String fullname;


    @NotEmpty
    @NotNull
    @Size(max = 16)
    private String idNumber;

    @NotEmpty
    @NotNull
    @Size(max = 10)
    private String contactPhoneNumber;

    @NotEmpty
    @NotNull
    @Size(max = 35)
    private String email;

    @NotEmpty
    @NotNull
    private String password;

    @NotNull
    private Role role;

}
