package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.client.entity.Client;
<<<<<<< HEAD
=======
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import com.gulfcam.fuelcoupon.store.entity.Station;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
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

<<<<<<< HEAD
    private String nameSaleManager;

    private String nameCommercialAttache;
=======
    private String nameManagerCoupon;

    private String nzmeServiceClient;
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6

    private List<Ticket> tickets;

    private Users idCommercialAttache;

    private Users idSalesManager;

    private Client idClient;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private Status status;

}
