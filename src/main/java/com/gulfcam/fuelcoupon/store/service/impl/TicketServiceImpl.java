package com.gulfcam.fuelcoupon.store.service.impl;

import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.order.service.ITypeVoucherService;
import com.gulfcam.fuelcoupon.store.dto.ResponseTicketDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Ticket;
import com.gulfcam.fuelcoupon.store.repository.ITicketRepo;
import com.gulfcam.fuelcoupon.store.service.ICouponService;
import com.gulfcam.fuelcoupon.store.service.IStationService;
import com.gulfcam.fuelcoupon.store.service.ITicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketServiceImpl implements ITicketService {

    @Autowired
    ITicketRepo iTicketRepo;
    @Autowired
    ICouponService iCouponService;
    @Autowired
    ITypeVoucherService iTypeVoucherService;
    @Autowired
    IClientService iClientService;
    @Autowired
    IStationService iStationService;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<Ticket> getAllTickets(int page, int size, String sort, String order) {
        return iTicketRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<ResponseTicketDTO> getTicketsByIdRequestOpposition(Long idRequestOpposition, int page, int size, String sort, String order) {
        Page<Ticket> tickets = iTicketRepo.getTicketsByIdRequestOpposition(idRequestOpposition, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        ResponseTicketDTO responseTicketDTO;
        List<ResponseTicketDTO> responseTicketDTOList = new ArrayList<>();

        for (Ticket ticket: tickets){
            responseTicketDTO = new ResponseTicketDTO();
            Coupon coupon = iCouponService.getByInternalReference(ticket.getIdCoupon()).get();
            TypeVoucher typeVoucher = iTypeVoucherService.getByInternalReference((coupon.getIdTypeVoucher() == null)? null: coupon.getIdTypeVoucher()).get();
            responseTicketDTO.setStatus(coupon.getStatus());
            responseTicketDTO.setSerialNumber(coupon.getSerialNumber());
            responseTicketDTO.setNameClient((coupon.getIdClient() == null)? null: iClientService.getClientByInternalReference(coupon.getIdClient()).get().getCompleteName());
            responseTicketDTO.setNotebookNumber(coupon.getIdNotebook());
            responseTicketDTO.setAmount(typeVoucher.getAmount());
            responseTicketDTO.setInternalReference(ticket.getInternalReference());
            responseTicketDTO.setCreatedAt(ticket.getCreatedAt());
            responseTicketDTOList.add(responseTicketDTO);

        }

        return new PageImpl<>(responseTicketDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)),  tickets.getTotalElements());
    }

    @Override
    public List<Ticket> getTicketsByIdRequestOpposition(Long idRequestOpposition) {
        return iTicketRepo.getTicketsByIdRequestOpposition(idRequestOpposition);
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
