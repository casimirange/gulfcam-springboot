package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.store.entity.Ticket;
import com.gulfcam.fuelcoupon.user.entity.Users;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResponseRequestOppositionDTO {

    private Long id;

    private Long internalReference;

    private String description;

    private String reason;

    private String nameClient;

    private String nameSaleManager;

    private String nameCommercialAttache;

    private List<Ticket> tickets;

    private Users idCommercialAttache;

    private Users idSalesManager;

    private Client idClient;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private Status status;

}
