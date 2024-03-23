package com.interview.assignment.taskmanagementapi.util;

import com.interview.assignment.taskmanagementapi.Exception.InvalidDueDateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;


@Component
@Slf4j
public class ValidationUtils {

    public static void validateDueDate(Calendar dueDate) {
        Calendar currentDate = Calendar.getInstance();
        if (!dueDate.after(currentDate)) {
            throw new InvalidDueDateException("Due date must be in the future");
        }
        log.info("due date validated : "+dueDate);
    }
}
