package com.interview.assignment.taskmanagementapi.Exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.interview.assignment.taskmanagementapi.constant.DataFormatConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Calendar;

@Data
@NoArgsConstructor
public class UsernameAlreadyExistsException extends RuntimeException {

    @JsonFormat(pattern = DataFormatConstants.TIMESTAMP_FORMAT)
    private Calendar timestamp;
    private HttpStatus status;
    private String errorCode;
    public UsernameAlreadyExistsException(final HttpStatus status, final String errorCode) {
        super();
        timestamp = Calendar.getInstance();
        this.status = status;
        this.errorCode = errorCode;
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }

}
