package com.gulfcam.fuelcoupon.client.service;

import com.gulfcam.fuelcoupon.client.entity.Client;
<<<<<<< HEAD
<<<<<<< HEAD
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
=======
import org.springframework.data.domain.Page;

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
import org.springframework.data.domain.Page;

>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
import java.util.List;
import java.util.Optional;

public interface IClientService {

    Page<Client> getAllClients(int page, int size, String sort, String order);
<<<<<<< HEAD
<<<<<<< HEAD
    Page<Client> filtres(String name, String company, String type, LocalDate date, int page, int size, String sort, String order);
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    Optional<Client> getClientByEmail(String email);
    Optional<Client> getClientByGulfCamAccountNumber(String gulfcamaccountnumber);
    Optional<Client> getClientById(Long id);
    List<Client> getClientsByCompleteNameContains(String completeName);
    Optional<Client> getClientByInternalReference(Long internalReference);
    void createClient(Client client);
    void deleteClient(Client client);
    boolean existsByEmail(String email);
<<<<<<< HEAD
<<<<<<< HEAD
    boolean existsByNiu(String niu);
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    boolean existsByGulfCamAccountNumber(String gulfcamaccountnumber);

}
