package com.interview.assignment.taskmanagementapi.model.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotNull(message = "title is required")
    private String title;

    @NotNull(message = "description must be not empty")
    private String description;

    @NotNull(message = "dueDate must be not empty")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Calendar dueDate;

    private boolean completed;


}
