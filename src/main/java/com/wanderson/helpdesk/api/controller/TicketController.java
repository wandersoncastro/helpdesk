package com.wanderson.helpdesk.api.controller;

import com.wanderson.helpdesk.api.entity.ChangeStatus;
import com.wanderson.helpdesk.api.entity.Ticket;
import com.wanderson.helpdesk.api.entity.User;
import com.wanderson.helpdesk.api.enums.ProfileEnum;
import com.wanderson.helpdesk.api.enums.StatusEnum;
import com.wanderson.helpdesk.api.response.Response;
import com.wanderson.helpdesk.api.security.jwt.JwtTokenUtil;
import com.wanderson.helpdesk.api.service.TicketService;
import com.wanderson.helpdesk.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.awt.print.Pageable;
import java.util.*;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> createOrUpdate(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result){
        Response<Ticket> response = new Response<Ticket>();
        try{
            validateCreateTicket(ticket, result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            ticket.setStatus(StatusEnum.getStatus("New"));
            ticket.setUser(userFromRequest(request));
            ticket.setDate(new Date());
            ticket.setNumber(generateNumber());
            Ticket ticketPersisted = (Ticket) ticketService.createOrUpdate(ticket);
            response.setData(java.util.Optional.ofNullable(ticketPersisted));
        }catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return  ResponseEntity.ok(response);
    }

    private void validateCreateTicket(Ticket ticket, BindingResult result){
        if(ticket.getTitle() == null){
            result.addError(new ObjectError("Ticket", "Title no information"));
            return;
        }
    }

    public User userFromRequest (HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUsernameFromToken(token);
        return userService.findByEmail(email);
    }

    private Integer generateNumber(){
        Random random = new Random();
        return random.nextInt(9999);
    }

    @PutMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result) {
        Response<Ticket> response = new Response<Ticket>();
        try{
            validateUpdateTicket(ticket, result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            Optional<Ticket> ticketCurrent = ticketService.findById(ticket.getId());
            ticket.setStatus(ticketCurrent.get().getStatus());
            ticket.setUser(ticketCurrent.get().getUser());
            ticket.setDate(ticketCurrent.get().getDate());
            ticket.setNumber(ticketCurrent.get().getNumber());
            if(ticketCurrent.get().getAssignedUser() != null){
                ticket.setAssignedUser(ticketCurrent.get().getAssignedUser());
            }
            Ticket ticketPersisted = (Ticket) ticketService.createOrUpdate(ticket);
            response.setData(Optional.ofNullable(ticketPersisted));
        } catch (Exception e ){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateUpdateTicket(Ticket ticket, BindingResult result){
        if(ticket.getId() == null){
            result.addError(new ObjectError("Ticket", "Id no information"));
            return;
        }
        if(ticket.getTitle() == null){
            result.addError(new ObjectError("Ticket", "Title no information"));
            return;
        }
    }

    @GetMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> findById(@PathVariable("id") String id) {
        Response<Ticket> response = new Response<Ticket>();
        Optional<Ticket> ticket = ticketService.findById(id);
        if(ticket == null){
            response.getErrors().add("Resister not found id: "+id);
            return ResponseEntity.badRequest().body(response);
        }
        List<ChangeStatus> changes =  new ArrayList<ChangeStatus>();
        Iterable<ChangeStatus> changesCurrent = ticketService.listChangeStatus(ticket.get().getId());
        for (Iterator<ChangeStatus> iterator = changesCurrent.iterator(); iterator.hasNext();){
            ChangeStatus changeStatus = (ChangeStatus) iterator.next();
            changeStatus.setTicket(null);
            changes.add(changeStatus);
        }
        ticket.get().setChanges(changes);
        response.setData(ticket);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id) {
        Response<String> response = new Response<String>();
        Optional<Ticket> ticket = ticketService.findById(id);
        if(ticket == null){
            response.getErrors().add("Resister not found id: "+id);
            return ResponseEntity.badRequest().body(response);
        }
        ticketService.delete(ticket.get());
        return ResponseEntity.ok(new Response<String>());
    }

    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("count") int count) {
        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        User userRequest = userFromRequest(request);
        if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)){
            tickets = ticketService.listTicket(page, count);
        } else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)){
            tickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
        }
        response.setData(Optional.ofNullable(tickets));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request,
                                                               @PathVariable("page") int page,
                                                               @PathVariable("count") int count,
                                                               @PathVariable("number") Integer number,
                                                               @PathVariable("title") String title,
                                                               @PathVariable("status") String status,
                                                               @PathVariable("priority") String priority,
                                                               @PathVariable("assigned") boolean assigned) {
        title = title.equals("uninformed")? "" : title;
        status = status.equals("uninformed")? "" : status;
        priority = priority.equals("uninformed")? "" : priority;
        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        if(number > 0){
            tickets = ticketService.findByNumber(page, count, number);
        } else {
            User userRequest = userFromRequest(request);
            if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)){
                if(assigned){
                    tickets = ticketService.findByParameterAndAssignedUser(page, count, title, status, priority, userRequest.getId());
                }else{
                    tickets = ticketService.findByParameters(page, count, title, status, priority);
                }
            }else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)){
                tickets = ticketService.findByParametersAndCurrentUser(page, count, title, status, priority, userRequest.getId());
            }
        }
        response.setData(Optional.ofNullable(tickets));
        return ResponseEntity.ok(response);
    }
}
