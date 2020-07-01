package com.wanderson.helpdesk.api.controller;

import com.wanderson.helpdesk.api.entity.User;
import com.wanderson.helpdesk.api.response.Response;
import com.wanderson.helpdesk.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*" )
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> create(HttpServletRequest request, @RequestBody User user, BindingResult result){
        Response<User> response = new Response<User>();
        try{
            validateCreateUser(user, result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userPersisted = (User) userService.createOrUpdate(user);
            response.setData(Optional.ofNullable(userPersisted));
        } catch (DuplicateKeyException duplicateKeyException){
            response.getErrors().add("E-mail already registered !");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception exception){
            response.getErrors().add(exception.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateCreateUser(User user, BindingResult result){
        if(user.getEmail() == null){
            result.addError(new ObjectError("User", "Email no information"));
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> update (HttpServletRequest request, @RequestBody User user, BindingResult result){
        Response<User> response = new Response<User>();
        try{
            validateUpdateUser(user, result);
            if(result.hasErrors()){
                result.getAllErrors().forEach((error -> response.getErrors().add(error.getDefaultMessage())));
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userPersisted = (User) userService.createOrUpdate(user);
            response.setData(Optional.ofNullable(userPersisted));
        } catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateUpdateUser(User user, BindingResult result){
        if(user.getId() == null){
            result.addError(new ObjectError("User", "Id no information"));
        }
        if(user.getEmail() == null){
            result.addError(new ObjectError("User", "Email no information"));
        }
    }

    @GetMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> findById(@PathVariable("id") String id){
        Response<User> response = new Response<User>();
        Optional<User> user = userService.findById(id);
        if(user == null){
            response.getErrors().add("Register not found id: "+id);
            return ResponseEntity.badRequest().body(response);
        }
        response.setData(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id){
        Response<String> response = new Response<String>();
        Optional<User> user = userService.findById(id);
        if(user == null){
            response.getErrors().add("Register not found id: "+id);
            return ResponseEntity.badRequest().body(response);
        }
        userService.delete(user.get());
        return ResponseEntity.ok(new Response<String>());
    }

    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<Page<User>>> findAll(@PathVariable int page, @PathVariable int count){
        Response<Page<User>> response = new Response<Page<User>>();
        Page<User> users = userService.findAll(page, count);
        response.setData(Optional.ofNullable(users));
        return ResponseEntity.ok(response);
    }

}
