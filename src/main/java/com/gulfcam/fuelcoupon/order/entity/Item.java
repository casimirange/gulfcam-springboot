package com.gulfcam.fuelcoupon.order.entity;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
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
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @Column(nullable = true, name = "id_type_voucher")
    private Long idTypeVoucher;

    @Column(nullable = true, name = "id_store_house")
    private Long idStoreHouse;

    private int quantityNotebook;

    private int quantityCarton;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private EStatus status;

}
