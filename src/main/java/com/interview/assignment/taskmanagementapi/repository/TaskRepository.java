package com.interview.assignment.taskmanagementapi.repository;//package com.interview.assignment.taskmanagementapi.repository;

import com.interview.assignment.taskmanagementapi.model.Entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
 //   Page<Task> findByTitleContaining(String title, Pageable pageable);

    List<Task> findByCompleted(boolean completed, Pageable pageable);


    List<Task> findByDueDateAfter(Calendar dueDate, Sort sort);

    //Optional<Task> findByCompleted(boolean completed);

    List<Task> findByDueDateBetween(Calendar dueDateStart, Calendar dueDateEnd);

    List<Task> findByDueDateBefore(Calendar dueDate, Pageable pageable);


}
