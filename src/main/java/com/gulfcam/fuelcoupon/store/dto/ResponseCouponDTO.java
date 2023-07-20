package com.gulfcam.fuelcoupon.store.dto;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.order.entity.TypeVoucher;
import com.gulfcam.fuelcoupon.store.entity.*;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseCouponDTO {

    private Long id;

    private Long internalReference;

    private String serialNumber;

    private String nameClient;

    private String nameStation;

    private Long idClient;

    private Client client;

    private Long idPompiste;

    private String pompisteName;

    private Long idRequestOpposition;

    private RequestOpposition requestOpposition;

    private Long idCreditNote;

    private CreditNote creditNote;

    private Long idTicket;

    private Ticket ticket;

    private Long idNotebook;

    private Notebook notebook;

    private Long idStation;

    private Station station;

    private Long idTypeVoucher;

    private TypeVoucher typeVoucher;

    private LocalDateTime createdAt;

    private LocalDate productionDate;

    private int modulo;

    private float amount;

    private LocalDateTime updateAt;

    private Status status;


}
