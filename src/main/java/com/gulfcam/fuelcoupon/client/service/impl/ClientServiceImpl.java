package com.gulfcam.fuelcoupon.client.service.impl;

import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.client.repository.IClientRepo;
import com.gulfcam.fuelcoupon.client.service.IClientService;
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
public class ClientServiceImpl implements IClientService {

    @Autowired
    IClientRepo iClientRepo;

    @Autowired
    ResourceBundleMessageSource messageSource;

    @Override
    public Page<Client> getAllClients(int page, int size, String sort, String order) {
        return iClientRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
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
    public boolean existsByGulfCamAccountNumber(String gulfcamaccountnumber) {
        return iClientRepo.existsByGulfcamAccountNumber(gulfcamaccountnumber);
    }
}
