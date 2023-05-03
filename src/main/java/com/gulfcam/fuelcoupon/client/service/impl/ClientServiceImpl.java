package com.gulfcam.fuelcoupon.client.service.impl;

import com.gulfcam.fuelcoupon.client.entity.Client;
<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.client.entity.ETypeClient;
import com.gulfcam.fuelcoupon.client.entity.TypeClient;
import com.gulfcam.fuelcoupon.client.repository.IClientRepo;
import com.gulfcam.fuelcoupon.client.repository.ITypeClientRepo;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Station;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
=======
import com.gulfcam.fuelcoupon.client.repository.IClientRepo;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
import com.gulfcam.fuelcoupon.client.repository.IClientRepo;
import com.gulfcam.fuelcoupon.client.service.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientServiceImpl implements IClientService {

    @Autowired
    IClientRepo iClientRepo;

    @Autowired
<<<<<<< HEAD
<<<<<<< HEAD
    ITypeClientRepo iTypeClientRepo;

    @Autowired
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<Client> getAllClients(int page, int size, String sort, String order) {
        return iClientRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
<<<<<<< HEAD
<<<<<<< HEAD
    public Page<Client> filtres(String name, String company, String type, LocalDate date, int page, int size, String sort, String order) {
        Specification<Client> specification = ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("completeName")), "%" + name + "%"));
            }

            if (company != null && !company.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), "%" + company + "%"));
            }

            if (type != null && !type.isEmpty()){
                Optional<TypeClient> typeClient = iTypeClientRepo.findByName(ETypeClient.valueOf(type));
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("typeClient")), typeClient.map(TypeClient::getId).orElse(null)));
            }

            if (date != null){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("createdAt").as(String.class)), date.toString() + '%'));
            }
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });

        return iClientRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    public Optional<Client> getClientByEmail(String email) {
        return iClientRepo.getClientByEmail(email);
    }

    @Override
    public Optional<Client> getClientByGulfCamAccountNumber(String gulfcamaccountnumber) {
        return iClientRepo.getClientByGulfcamAccountNumber(gulfcamaccountnumber);
    }

    @Override
    public Optional<Client> getClientById(Long id) {
        return iClientRepo.findById(id);
    }

    @Override
    public List<Client> getClientsByCompleteNameContains(String completeName) {
        return iClientRepo.getClientsByCompleteNameContains(completeName);
    }

    @Override
    public Optional<Client> getClientByInternalReference(Long internalReference) {
        return iClientRepo.getClientByInternalReference(internalReference);
    }

    @Override
    public void createClient(Client client) {
        iClientRepo.save(client);
    }

    @Override
    public void deleteClient(Client client) {
        iClientRepo.delete(client);
    }

    @Override
    public boolean existsByEmail(String email) {
        return iClientRepo.existsByEmail(email);
    }

    @Override
<<<<<<< HEAD
<<<<<<< HEAD
    public boolean existsByNiu(String niu) {
        return iClientRepo.existsByNiu(niu);
    }

    @Override
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    public boolean existsByGulfCamAccountNumber(String gulfcamaccountnumber) {
        return iClientRepo.existsByGulfcamAccountNumber(gulfcamaccountnumber);
    }
}
