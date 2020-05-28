package com.wanderson.helpdesk.api.service;

import com.wanderson.helpdesk.api.entity.ChangeStatus;
import com.wanderson.helpdesk.api.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface TicketService {
    Ticket createOrUpdate(Ticket ticket);

    Optional<Ticket> findById(String id);

    void delete(Ticket ticket);

    Page<Ticket> listTicket(int page, int count);

    ChangeStatus createChangeStatus(ChangeStatus changesStatus);

    Iterable<ChangeStatus> listChangeStatus(String ticketId);

    Page<Ticket> findByCurrentUser(int page, int count, String userId);

    Page<Ticket> findByParameters(int page, int count, String title, String status, String priority);

    Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status, String priority);

    Page<Ticket> findByNumber(int page, int count, Integer number);

    Iterable<Ticket> findAll();

    Page<Ticket> findByParameterAndAssignedUser(int page, int count, String title, String status, String priority, String assignedUser);

}
