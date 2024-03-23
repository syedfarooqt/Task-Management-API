package com.interview.assignment.taskmanagementapi.model;

import io.micrometer.core.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSearchCriteria {
  @Nullable
  private boolean Completed;

  @Nullable
  private Calendar dueDateFrom;

  @Nullable
  private Calendar dueDateTo;

  @Nullable
  private Calendar completedDateFrom;

  @Nullable
  private Calendar completedDateTo;

  @Nullable
  private String title;

}