package rw.nbr.licensing.exception;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"status", "message", "error"})
public class MessageExceptionHandler {

    @JsonProperty("status")
    private int status;

    @JsonProperty("message")
    private String message;


    private ErrorDetails error;
}
