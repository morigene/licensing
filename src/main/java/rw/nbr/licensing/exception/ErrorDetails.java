package rw.nbr.licensing.exception;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorDetails {


    private String code;
    private String details;
}
