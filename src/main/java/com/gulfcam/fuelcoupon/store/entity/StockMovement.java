package com.gulfcam.fuelcoupon.store.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @Column(nullable = true, name = "id_store1")
    private Long idStore1;

    @Column(nullable = true, name = "id_store_house1")
    private Long idStoreHouse1;

    @Column(nullable = true, name = "id_store2")
    private Long idStore2;

    @Column(nullable = true, name = "id_store_house2")
    private Long idStoreHouse2;

    private String type;

}
