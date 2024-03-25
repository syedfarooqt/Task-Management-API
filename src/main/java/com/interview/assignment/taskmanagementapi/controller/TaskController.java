package com.interview.assignment.taskmanagementapi.controller;

import com.interview.assignment.taskmanagementapi.Exception.AppException;
import com.interview.assignment.taskmanagementapi.Exception.InvalidDueDateException;
import com.interview.assignment.taskmanagementapi.constant.DataFormatConstants;
import com.interview.assignment.taskmanagementapi.model.Entity.Task;
import com.interview.assignment.taskmanagementapi.model.TaskSearchCriteria;
import com.interview.assignment.taskmanagementapi.service.TaskService;
import com.interview.assignment.taskmanagementapi.util.ValidationUtils;
import io.micrometer.core.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.annotation.Secured;


import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@Slf4j
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private  TaskService taskService;
    @Autowired
    private  ValidationUtils validationUtils;

    // CRUD operations for tasks
    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
    public Task createTask(@RequestBody @Valid Task task) {
        log.info("Received request to create task: {}", task);
        try {
        validationUtils.validateDueDate(task.getDueDate());
        return taskService.createTask(task);
        } catch (InvalidDueDateException e) {
            log.error("Failed to create task due to invalid due date: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while creating task", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable("id") Long id) {
        log.info("requested id to get task : "+id);
        try {
            return taskService.getTaskById(id);
        }catch (Exception ex){
            log.error("An error occurred while retrieve Task : {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable("id") Long id, @RequestBody @Valid Task task) {
        log.info("requested id to update task : "+id);
        log.info("requested task detail to update task : "+task);
        try {
            return taskService.updateTask(id, task);
        } catch (Exception e) {
            log.error("An error occurred while retrieve Task : {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable("id") Long id) {
        log.info("requested id - {} to delete task ",id);
        try {
            taskService.deleteTask(id);
            log.info("requested id - {} is deleted successfully ", id);
        }
        catch (Exception ex) {
            log.error("An error occurred while deleting tasks : {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping("/status")
    public List<Task> getTaskByStatusType(@RequestParam("statusType") boolean statusType,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        try {
            return  taskService.getTaskByStatus(statusType,page,size);
        } catch (Exception ex) {
            log.error("An error occurred while retrieving tasks by status type: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PutMapping("/status")
    public ResponseEntity<String> updateTaskByStatusTypeAndCurrentDate(@RequestParam("id")  @Valid  Long id,
            @RequestParam(defaultValue = "true")  @Valid boolean statusType) {
        try {
            taskService.updateTaskByStatus(statusType, id);
            return ResponseEntity.ok("updated the Task");
        }catch (Exception ex){
            log.error("An error occurred while updating tasks: {}", ex.getMessage(), ex);
            throw ex;
        }

    }

    @GetMapping("/dueDateRange")
    public List<Task> getTaskByDueDateRange(@RequestParam("reportFrom") String reportFrom,
                                            @RequestParam("reportTo") String reportTo) {
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        try {
            from.setTime(new SimpleDateFormat(DataFormatConstants.TIMESTAMP_FORMAT1).parse(reportFrom));
            to.setTime(new SimpleDateFormat(DataFormatConstants.TIMESTAMP_FORMAT1).parse(reportTo));
        } catch (ParseException e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Date Criteria is not Able to Parse");
        }
        try {
            return taskService.getTaskByDueDate(from, to);
        }catch (Exception ex){
            log.error("An error occurred while retrieving tasks: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
    @GetMapping("/dueDateBefore")
    public List<Task> getTaskByDueDateBefore(@RequestParam("reportTo") String reportTo,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Calendar to = Calendar.getInstance();
        try {
            to.setTime(new SimpleDateFormat(DataFormatConstants.TIMESTAMP_FORMAT).parse(reportTo));
        } catch (ParseException e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Date Criteria is not Able to Parse");
        }
        try {
       return taskService.getTaskByDueDateBefore(to,page,size);
        }catch (Exception ex){
            log.error("An error occurred while retrieving tasks: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
    @GetMapping("/dueDateAfter")
    public List<Task> getTaskByDueDateAfter(@RequestParam("reportFrom") String reportFrom,
                                       @RequestParam(defaultValue = "id") String sortBy,
                                       @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Calendar from = Calendar.getInstance();
        try {
            from.setTime(new SimpleDateFormat(DataFormatConstants.TIMESTAMP_FORMAT).parse(reportFrom));
        } catch (ParseException e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Date Criteria is not Able to Parse");
        }
        Sort sort=Sort.by(direction,sortBy);
        try {
       return taskService.getTaskByDueDateAfter(from,sort);
        }catch (Exception ex){
            log.error("An error occurred while retrieving tasks: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    // Pagination, sorting, and filtering
    @GetMapping("/allTask")
    public Page<Task> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        try {
            return taskService.getAllTasks(page, size, sortBy);
        }catch (Exception ex){
        log.error("An error occurred while retrieving tasks: {}", ex.getMessage(), ex);
        throw ex;
    }
    }

    //for getting the task based on certain criteria
    @GetMapping("/task")
    public Page<Task> getTaskByFilters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
            @RequestParam @Nullable boolean completed,
            @RequestParam @Nullable String dueDateRangeFrom,
            @RequestParam @Nullable String dueDateRangeTo,
            @RequestParam @Nullable String completedDateFrom,
            @RequestParam @Nullable String completedDateTo,
            @RequestParam @Nullable String title

    ) {
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        Calendar completedFrom = null;
        Calendar completedTo=null;
        if (completedDateFrom != null && completedDateTo != null) {
             completedFrom = Calendar.getInstance();
             completedTo = Calendar.getInstance();
        }


        try {
            from.setTime(new SimpleDateFormat(DataFormatConstants.TIMESTAMP_FORMAT).parse(dueDateRangeFrom));
            to.setTime(new SimpleDateFormat(DataFormatConstants.TIMESTAMP_FORMAT).parse(dueDateRangeTo));
            if (completedDateFrom != null && completedDateTo != null) {
                completedFrom.setTime(new SimpleDateFormat(DataFormatConstants.TIMESTAMP_FORMAT).parse(completedDateFrom));
                completedTo.setTime(new SimpleDateFormat(DataFormatConstants.TIMESTAMP_FORMAT).parse(completedDateTo));
            }
           } catch (ParseException e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Date Criteria is not Able to Parse");
        }
        TaskSearchCriteria taskSearchCriteria=new TaskSearchCriteria();
        taskSearchCriteria.setCompleted(completed);
        taskSearchCriteria.setDueDateFrom(from);
        taskSearchCriteria.setDueDateTo(to);
        taskSearchCriteria.setCompletedDateFrom(completedFrom);
        taskSearchCriteria.setCompletedDateTo(completedTo);
        taskSearchCriteria.setTitle(title);
        try {
        return taskService.getTasksBasedOnCriteria(page, size, sortBy,sortDirection, taskSearchCriteria);
        }catch (Exception ex){
            log.error("An error occurred while retrieving tasks: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
