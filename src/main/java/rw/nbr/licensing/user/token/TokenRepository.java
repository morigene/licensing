package rw.nbr.licensing.user.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {

    @Query(value = """
            select t from Token t inner join InstitutionAccessAccount u\s
            on t.institutionAccessAccount.id = u.id\s
            where u.id = :id and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTokenByUser(String id);

    Optional<Token> findByToken(String token);
}
