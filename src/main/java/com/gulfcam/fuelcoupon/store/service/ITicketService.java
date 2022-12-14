package com.gulfcam.fuelcoupon.store.service;

import com.gulfcam.fuelcoupon.store.entity.Ticket;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ITicketService {

    Page<Ticket> getAllTickets(int page, int size, String sort, String order);
    Page<Ticket> getTicketsByIdRequestOpposition(Long idRequestOpposition, int page, int size, String sort, String order);
    List<Ticket> getTicketsByIdRequestOpposition(Long idRequestOpposition);
    Page<Ticket> getTicketsByIdCoupon(Long idCoupon, int page, int size, String sort, String order);
    Optional<Ticket> getTicketById(Long id);
    Optional<Ticket> getByInternalReference(Long internelReference);
    void createTicket(Ticket ticket);
    void deleteTicket(Ticket ticket);

}
