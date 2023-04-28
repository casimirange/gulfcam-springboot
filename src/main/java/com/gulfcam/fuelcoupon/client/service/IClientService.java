package com.gulfcam.fuelcoupon.client.service;

import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IClientService {

    Page<Client> getAllClients(int page, int size, String sort, String order);
    Page<Client> filtres(String name, String company, String type, LocalDate date, int page, int size, String sort, String order);
    Optional<Client> getClientByEmail(String email);
    Optional<Client> getClientByGulfCamAccountNumber(String gulfcamaccountnumber);
    Optional<Client> getClientById(Long id);
    List<Client> getClientsByCompleteNameContains(String completeName);
    Optional<Client> getClientByInternalReference(Long internalReference);
    void createClient(Client client);
    void deleteClient(Client client);
    boolean existsByEmail(String email);
    boolean existsByNiu(String niu);
    boolean existsByGulfCamAccountNumber(String gulfcamaccountnumber);

}
