package rw.nbr.licensing.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.nbr.licensing.user.entity.InstitutionAccessAccount;

import java.util.Optional;


@Repository
public interface InstitutionAccountRepository extends JpaRepository<InstitutionAccessAccount, String> {

    Optional<InstitutionAccessAccount> findByEmail(String email);

}
