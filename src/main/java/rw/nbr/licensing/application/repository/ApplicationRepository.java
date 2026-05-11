package rw.nbr.licensing.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.nbr.licensing.application.entity.Application;
import rw.nbr.licensing.application.type.ApplicationState;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    List<Application> findByApplicantUsername(String username);

    List<Application> findByState(ApplicationState state);
}