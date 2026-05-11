package rw.nbr.licensing.application.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import rw.nbr.licensing.application.entity.Application;
import rw.nbr.licensing.application.repository.ApplicationRepository;
import rw.nbr.licensing.application.request.ApplicationRequest;
import rw.nbr.licensing.application.response.ApplicationResponse;
import rw.nbr.licensing.application.type.ApplicationState;

import java.util.UUID;

public class ApplicationService {

    private final ApplicationRepository repository;

    public ApplicationService(ApplicationRepository repository) {
        this.repository = repository;
    }

    public ApplicationResponse createApplication(ApplicationRequest request,
                                                 Authentication auth) {

        Application app = new Application();
        app.setApplicantUsername(auth.getName());
        app.setState(ApplicationState.DRAFT);
        app.setCreatedAt(LocalDateTime.now());

        Application saved = repository.save(app);

        return new ApplicationResponse(saved.getId(), saved.getState());
    }


    public void approveApplication(UUID id, Authentication auth) {

        Application app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        User user = (User) auth.getPrincipal();

        // 🔐 Segregation of Duties rule
        if (app.getReviewedBy() != null &&
                app.getReviewedBy().equals(user.getId())) {

            throw new AccessDeniedException("Reviewer cannot approve this application");
        }

        if (app.getState() != ApplicationState.COMPLIANCE_CHECK) {
            throw new IllegalStateException("Invalid state for approval");
        }

        app.setApprovedBy(user.getId());
        app.setState(ApplicationState.APPROVED);
        app.setUpdatedAt(LocalDateTime.now());

        repository.save(app);
    }

}
