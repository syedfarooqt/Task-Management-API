package com.interview.assignment.taskmanagementapi.service;

import com.interview.assignment.taskmanagementapi.model.Entity.Task;
import com.interview.assignment.taskmanagementapi.model.Request.TaskRequest;
import com.interview.assignment.taskmanagementapi.model.TaskSearchCriteria;
import com.interview.assignment.taskmanagementapi.repository.TaskCriteriaRepository;
import com.interview.assignment.taskmanagementapi.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class TaskService {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskCriteriaRepository taskCriteriaRepository;


    public void TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        Task newTask = Task.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .completed(task.isCompleted())
                .build();
        if(task.isCompleted()){
            Calendar date = Calendar.getInstance();
            newTask.setCompletedDate(date);
        }
        log.info("Task created: {}", newTask);
        log.info("Initiating task storage in the database...");
        taskRepository.save(newTask);
        log.info("Task saved to database successfully");
        return newTask;
    }

//    public Page<Task> getAllTasks(PageRequest pageRequest) {
//        return (Page<Task>) taskRepository.findAll(pageRequest);
//    }
public Page<Task> getAllTasks(int page, int size, String sortBy) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
    return taskRepository.findAll(pageRequest);
}
    public void deleteTask(Long id) {
        try {
            taskRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            log.error("Task with ID {} not found for deletion", id);
            throw ex;
        } catch (Exception ex) {
            log.error("An error occurred while deleting task with ID {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<Task> getTaskByStatus(boolean statusType,int page,int size) {
        Pageable pageable=PageRequest.of(page,size);
        try {
            return taskRepository.findByCompleted(statusType, pageable);
        }catch (Exception ex){
            log.error("An error occurred while retrieving task : {} ", ex.getMessage(), ex);
            throw ex;
        }
    }
    public void updateTaskByStatus(boolean statusType,Long id) {
        try {
        Optional<Task>  optionalTask=taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setCompleted(statusType);
            if(task.isCompleted()){
                Calendar date = Calendar.getInstance();
                task.setCompletedDate(date);
            }else{
                task.setCompletedDate(null);
            }
            taskRepository.save(task);
        }
        else {
            throw new RuntimeException("Task not found with ID: " + id);
        }
    }catch (Exception ex){
        log.error("An error occurred while retrieving task : {} ", ex.getMessage(), ex);
        throw ex;
    }
    }
    public List<Task> getTaskByDueDate(Calendar from, Calendar to) {
        try {
        return taskRepository.findByDueDateBetween(from,to);
    }catch (Exception ex){
        log.error("An error occurred while retrieving task : {} ", ex.getMessage(), ex);
        throw ex;
    }

    }
    public List<Task> getTaskByDueDateBefore(Calendar to,int page,int size) {
        Pageable pageable=PageRequest.of(page,size);
        try{
        return  taskRepository.findByDueDateBefore(to,pageable);
    }catch (Exception ex){
        log.error("An error occurred while retrieving task : {} ", ex.getMessage(), ex);
        throw ex;
    }
    }
    public List<Task> getTaskByDueDateAfter(Calendar from,Sort sort) {try {
        return taskRepository.findByDueDateAfter(from,sort);
    }catch (Exception ex){
        log.error("An error occurred while retrieving task : {} ", ex.getMessage(), ex);
        throw ex;
    }
    }

    public Task getTaskById(Long id) {
        try {
            Optional<Task> optionalTask = taskRepository.findById(id);
            log.info("Task for this id : {} is task -> {}", id, optionalTask);
            return optionalTask.orElse(null);
        }  catch (Exception ex){
        log.error("An error occurred while retrieving task : {} ", ex.getMessage(), ex);
        throw ex;
    }
    }
    public Page<Task> getTasksBasedOnCriteria(int pageNo,int pageSize,String sortBy,Sort.Direction direction,TaskSearchCriteria taskSearchCriteria )
    {
        try {
        return taskCriteriaRepository.findAllWithFilter(pageNo,pageSize,sortBy,direction, taskSearchCriteria);
        }catch (Exception ex){
            log.error("An error occurred while retrieving task : {} ", ex.getMessage(), ex);
            throw ex;
        }
    }

    public Task updateTask(Long id, @Valid Task updatedTaskRequest) {
        try {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            log.info("Task for this id : {} ,Before updating Task -> {}",id,optionalTask);
            Task task = optionalTask.get();
            task.setTitle(updatedTaskRequest.getTitle());
            task.setDescription(updatedTaskRequest.getDescription());
            task.setDueDate(updatedTaskRequest.getDueDate());
            task.setCompleted(updatedTaskRequest.isCompleted());
            taskRepository.save(task);
            log.info("Task for this id : {} ,After updating Task -> {}",id,task);
            return task;
        }
        log.info("task is empty .. not found !!");
        return null;
        }catch (Exception ex){
            log.error("An error occurred while retrieving task : {} ", ex.getMessage(), ex);
            throw ex;
        }
    }

}
