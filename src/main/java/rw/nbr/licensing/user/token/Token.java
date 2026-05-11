package rw.nbr.licensing.user.token;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.nbr.licensing.user.entity.InstitutionAccessAccount;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EL_TOKEN")
public class Token {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTITUTION_ID")
    public InstitutionAccessAccount institutionAccessAccount;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TOKEN_ID")
    private String id;
    @Column(name = "TOKEN", unique = true)
    private String token;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TokenType tokenType = TokenType.BEARER;
    private boolean revoked;

    private boolean expired;
}
