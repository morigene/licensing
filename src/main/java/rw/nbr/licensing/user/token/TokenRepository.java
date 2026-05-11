package rw.nbr.licensing.user.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {



    @Query("select t from Token t " +
            "inner join InstitutionAccessAccount u on t.institutionAccessAccount.id = u.id " +
            "where u.id = :id and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokenByUser(String id);

    Optional<Token> findByToken(String token);
}
