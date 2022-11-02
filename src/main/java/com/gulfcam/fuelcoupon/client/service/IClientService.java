package com.gulfcam.fuelcoupon.client.service;

import com.gulfcam.fuelcoupon.client.entity.Client;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IClientService {

    Page<Client> getAllClients(int page, int size, String sort, String order);
    Optional<Client> getClientByEmail(String email);
    Optional<Client> getClientByGulfCamAccountNumber(String gulfcamaccountnumber);
    Optional<Client> getClientById(Long id);
    void createClient(Client client);
    void deleteClient(Client client);
    boolean existsByEmail(String email);
    boolean existsByGulfCamAccountNumber(String gulfcamaccountnumber);

}
