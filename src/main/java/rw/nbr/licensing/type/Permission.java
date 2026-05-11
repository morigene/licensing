package rw.nbr.licensing.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {


    // Application (Applicant)
    APPLICATION_CREATE,
    APPLICATION_READ_OWN,
    APPLICATION_UPDATE_OWN,
    APPLICATION_SUBMIT,
    APPLICATION_RESUBMIT,

    // Application (Internal staff)
    APPLICATION_READ_ALL,
    APPLICATION_REVIEW,
    APPLICATION_COMPLIANCE_CHECK,
    APPLICATION_APPROVE,
    APPLICATION_REJECT,
    APPLICATION_REQUEST_INFO,

    // Documents
    DOCUMENT_UPLOAD,
    DOCUMENT_READ_OWN,
    DOCUMENT_READ_ALL,

    // Audit
    AUDIT_READ_OWN,
    AUDIT_READ_ALL,

    // User management
    USER_CREATE,
    USER_READ,
    USER_UPDATE,
    USER_DISABLE


}
