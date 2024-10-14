package com.event.repository;

import com.event.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<User, String>, QuerydslPredicateExecutor<User> {

    User findByUsername(String username);

    User findByJmbgOrUsername(String jmbg, String username);

    @Modifying
    @Query("UPDATE users u SET u.active = :active WHERE u.jmbg = :jmbg")
    void changeActiveStatus(String jmbg, boolean active);

    @Query("SELECT COUNT(u) > 0 FROM users u WHERE u.username = :username AND u.jmbg <> :jmbg")
    boolean existsByUsernameAndDifferentJmbg(String username, String jmbg);


}
