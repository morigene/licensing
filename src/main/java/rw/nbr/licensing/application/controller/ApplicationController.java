package rw.nbr.licensing.application.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.nbr.licensing.application.request.ApplicationRequest;
import rw.nbr.licensing.application.response.ApplicationResponse;
import rw.nbr.licensing.application.service.ApplicationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('APPLICATION_CREATE')")
    public ResponseEntity<ApplicationResponse> createApplication(
            @RequestBody ApplicationRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.createApplication(request, authentication));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('APPLICATION_APPROVE')")
    public ResponseEntity<Void> approveApplication(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        service.approveApplication(id, authentication);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('APPLICATION_REJECT')")
    public ResponseEntity<Void> approveApplication(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        service.approveApplication(id, authentication);
        return ResponseEntity.ok().build();
    }

    //TODO
    //1.Do review for submitted license and application controller and service logic
    //2.Do the same for save draft and resubmit
    //3. Trail and Auding
    //4. Front End
    //5. Units tests
}