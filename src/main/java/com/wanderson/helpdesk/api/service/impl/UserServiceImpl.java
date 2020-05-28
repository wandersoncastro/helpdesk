package com.wanderson.helpdesk.api.service.impl;


import com.wanderson.helpdesk.api.entity.User;
import com.wanderson.helpdesk.api.repository.UserRepository;
import com.wanderson.helpdesk.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public User createOrUpdate(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return this.userRepository.findById(id);
    }

    @Override
    public void delete(User user) {
        this.userRepository.delete(user);
    }

    @Override
    public Page<User> findAll(int page, int count) {
        Pageable pages =  PageRequest.of(page, count);
        return this.userRepository.findAll(pages);
    }
}
