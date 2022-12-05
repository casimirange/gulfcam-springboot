package com.gulfcam.fuelcoupon.store.entity;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @NotNull
    @Column(unique = true)
    private String serialNumber;

    @Column(nullable = true, name = "id_client")
    private Long idClient;

    @Column(nullable = true, name = "id_request_opposition")
    private Long idRequestOpposition;

    @Column(nullable = true, name = "id_credit_note")
    private Long idCreditNote;

    @Column(nullable = true, name = "id_ticket")
    private Long idTicket;

    @Column(nullable = true, name = "id_notebook")
    private Long idNotebook;

    @Column(nullable = true, name = "id_station")
    private Long idStation;

    @Column(nullable = true, name = "id_type_voucher")
    private Long idTypeVoucher;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private Status status;


}
