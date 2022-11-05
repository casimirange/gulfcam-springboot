package com.gulfcam.fuelcoupon.store.repository;

import com.gulfcam.fuelcoupon.store.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITicketRepo extends JpaRepository<Ticket, Long> {

    Page<Ticket> getTicketsByIdRequestOpposition(Long idRequestOpposition, Pageable pageable);
    Page<Ticket> getTicketsByIdCoupon(Long idCoupon, Pageable pageable);
    Optional<Ticket> getTicketByInternalReference(Long internalReference);
}
