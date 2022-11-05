package com.gulfcam.fuelcoupon.client.repository;
import com.gulfcam.fuelcoupon.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IClientRepo extends JpaRepository<Client, Long> {

    Optional<Client> getClientByEmail(String email);
    Optional<Client> getClientByGulfcamAccountNumber(String gulfcamaccountnumber);
    Optional<Client> getClientByInternalReference(Long internalReference);

    boolean existsByEmail(String email);
    boolean existsByGulfcamAccountNumber(String gulfcamaccountnumber);
}
