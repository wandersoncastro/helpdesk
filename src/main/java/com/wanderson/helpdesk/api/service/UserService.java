package com.wanderson.helpdesk.api.service;

import com.wanderson.helpdesk.api.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {
    User findByEmail(String email);

    User createOrUpdate(User user);

    Optional<User> findById(String id);

    void delete(User user);

    Page<User> findAll(int page, int count);
}
