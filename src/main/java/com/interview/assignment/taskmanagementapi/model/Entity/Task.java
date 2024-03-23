package com.interview.assignment.taskmanagementapi.model.Entity;

import io.micrometer.core.lang.Nullable;
import lombok.*;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "title must be not empty")
    private String title;
    @NotNull(message = "description must be not empty")
    private String description;
    @NotNull(message = "dueDate must be not empty")
    private Calendar dueDate;
    @NotNull(message = "completed status must be not empty")
    private boolean completed;
    @Nullable
    private Calendar completedDate;
}

