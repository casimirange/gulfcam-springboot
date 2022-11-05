package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.store.entity.Ticket;
import com.gulfcam.fuelcoupon.store.repository.ITicketRepo;
import com.gulfcam.fuelcoupon.store.service.ITicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TicketServiceImpl implements ITicketService {

    @Autowired
    ITicketRepo iTicketRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<Ticket> getAllTickets(int page, int size, String sort, String order) {
        return iTicketRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Ticket> getTicketsByIdRequestOpposition(Long idRequestOpposition, int page, int size, String sort, String order) {
        return iTicketRepo.getTicketsByIdRequestOpposition(idRequestOpposition,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Page<Ticket> getTicketsByIdCoupon(Long idCoupon, int page, int size, String sort, String order) {
        return iTicketRepo.getTicketsByIdCoupon(idCoupon,(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort))));
    }

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return iTicketRepo.findById(id);
    }

    @Override
    public Optional<Ticket> getByInternalReference(Long internelReference) {
        return iTicketRepo.getTicketByInternalReference(internelReference);
    }

    @Override
    public void createTicket(Ticket ticket) {
        iTicketRepo.save(ticket);
    }

    @Override
    public void deleteTicket(Ticket ticket) {
        iTicketRepo.delete(ticket);
    }
}
