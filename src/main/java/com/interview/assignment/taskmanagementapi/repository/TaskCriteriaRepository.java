package com.interview.assignment.taskmanagementapi.repository;

import com.interview.assignment.taskmanagementapi.model.Entity.Task;
import com.interview.assignment.taskmanagementapi.model.TaskSearchCriteria;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.List;

@Repository
public class TaskCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    public TaskCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }
    public Page<Task> findAllWithFilter(
            int  pageNo,
            int  pageSize,
            String sortBy,
            Sort.Direction sortDirection,
            TaskSearchCriteria taskSearchCriteria) {

        CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
        Root<Task> taskRoot = criteriaQuery.from(Task.class);
        Predicate predicate = getPredicate(taskSearchCriteria, taskRoot);
        criteriaQuery.where(predicate);
        setOrder(sortBy,sortDirection, criteriaQuery, taskRoot);
        Sort sort=Sort.by(sortDirection,sortBy);
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);

        TypedQuery<Task> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(pageNo * pageSize);
        typedQuery.setMaxResults(pageSize);

        long consentCount = getTaskCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, consentCount);

    }
    private long getTaskCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Task> countRoot = countQuery.from(Task.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
    private void setOrder(
            String sortBy,
            Sort.Direction sortDirection,
            CriteriaQuery<Task> criteriaQuery,
            Root<Task> taskRoot) {
        if (sortDirection.equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(taskRoot.get(sortBy)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(taskRoot.get(sortBy)));
        }
    }
    private Predicate getPredicate(TaskSearchCriteria taskSearchCriteria, Root<Task> taskRoot) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        if (taskSearchCriteria.isCompleted()) {
            predicates.add(criteriaBuilder.isTrue(taskRoot.get("completed")));
        } else {
            predicates.add(criteriaBuilder.isFalse(taskRoot.get("completed")));
        }

        if (taskSearchCriteria.getDueDateFrom() != null && taskSearchCriteria.getDueDateTo() != null ) {
            Calendar fromDueDate = taskSearchCriteria.getDueDateFrom();
            Calendar toDueDate = taskSearchCriteria.getDueDateTo();
            predicates.add(criteriaBuilder.between(taskRoot.get("dueDate"), fromDueDate, toDueDate));
        }

        if (taskSearchCriteria.getCompletedDateFrom() != null && taskSearchCriteria.getCompletedDateTo() != null) {
            Calendar fromCompletedDate = taskSearchCriteria.getCompletedDateFrom();
            Calendar toCompletedDate = taskSearchCriteria.getCompletedDateTo();
            predicates.add(criteriaBuilder.between(taskRoot.get("completedDate"), fromCompletedDate, toCompletedDate));
        }

        if (taskSearchCriteria.getTitle() != null && !taskSearchCriteria.getTitle().isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(taskRoot.get("title")), "%" + taskSearchCriteria.getTitle().toLowerCase() + "%"));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
