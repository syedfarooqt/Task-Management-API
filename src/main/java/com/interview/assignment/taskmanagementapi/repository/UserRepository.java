package com.interview.assignment.taskmanagementapi.repository;
import com.interview.assignment.taskmanagementapi.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUserName(String username);

    void  deleteUserByUserName(String userName);


}

