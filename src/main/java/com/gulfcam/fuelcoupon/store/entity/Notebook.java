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
public class Notebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @Column(unique = false)
    private String serialNumber;

    @Column(unique = false)
    private String plage_coupon;

    @Column(nullable = true, name = "id_store_house")
    private Long idStoreHouse;

    @Column(nullable = true, name = "id_store_keeper")
    private Long idStoreKeeper;

    @Column(nullable = true, name = "id_carton")
    private Long idCarton;

    @Column(nullable = true, name = "id_type_voucher")
    private Long idTypeVoucher;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private Status status;
}
