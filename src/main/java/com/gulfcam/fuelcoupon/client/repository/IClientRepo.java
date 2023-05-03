package com.gulfcam.fuelcoupon.client.repository;
import com.gulfcam.fuelcoupon.client.entity.Client;
<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IClientRepo extends JpaRepository<Client, Long> {

    List<Client> getClientsByCompleteNameContains(String completeName);
    Optional<Client> getClientByEmail(String email);
    Optional<Client> getClientByGulfcamAccountNumber(String gulfcamaccountnumber);
    Optional<Client> getClientByInternalReference(Long internalReference);

    boolean existsByEmail(String email);
<<<<<<< HEAD
<<<<<<< HEAD
    boolean existsByNiu(String niu);
    boolean existsByGulfcamAccountNumber(String gulfcamaccountnumber);
    Page<Client> findAll(Specification<Client> specification, Pageable pageable);
=======
    boolean existsByGulfcamAccountNumber(String gulfcamaccountnumber);
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
    boolean existsByGulfcamAccountNumber(String gulfcamaccountnumber);
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
}
