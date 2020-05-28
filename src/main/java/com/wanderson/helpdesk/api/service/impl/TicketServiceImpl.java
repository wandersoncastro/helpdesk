package com.wanderson.helpdesk.api.service.impl;

import com.wanderson.helpdesk.api.entity.ChangeStatus;
import com.wanderson.helpdesk.api.entity.Ticket;
import com.wanderson.helpdesk.api.repository.ChangeStatusRepository;
import com.wanderson.helpdesk.api.repository.TicketRepository;
import com.wanderson.helpdesk.api.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ChangeStatusRepository changeStatusRepository;

    @Override
    public Ticket createOrUpdate(Ticket ticket) {
        return this.ticketRepository.save(ticket);
    }

    @Override
    public Optional<Ticket> findById(String id) {
        return this.ticketRepository.findById(id);
    }

    @Override
    public void delete(Ticket ticket) {
        this.ticketRepository.delete(ticket);
    }

    @Override
    public Page<Ticket> listTicket(int page, int count) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findAll(pages);
    }

    @Override
    public ChangeStatus createChangeStatus(ChangeStatus changesStatus) {
        return this.changeStatusRepository.save(changesStatus);
    }

    @Override
    public Iterable<ChangeStatus> listChangeStatus(String ticketId) {
        return this.changeStatusRepository.findByTicketIdOrderByDateChangeStatusDesc(ticketId);
    }

    @Override
    public Page<Ticket> findByCurrentUser(int page, int count, String userId) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByUserIdOrderByDateDesc(pages,userId);
    }

    @Override
    public Page<Ticket> findByParameters(int page, int count, String title, String status, String priority) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(title, status, priority, pages);
    }

    @Override
    public Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status, String priority) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDateDesc(title, status, priority, pages);
    }

    @Override
    public Page<Ticket> findByNumber(int page, int count, Integer number) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByNumber(number, pages);
    }

    @Override
    public Iterable<Ticket> findAll() {
        return this.ticketRepository.findAll();
    }

    @Override
    public Page<Ticket> findByParameterAndAssignedUser(int page, int count, String title, String status, String priority, String assignedUser) {
        Pageable pages = PageRequest.of(page, count);
        return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDateDesc(title, status, priority, pages);
    }
}
