package com.gulfcam.fuelcoupon.store.entity;

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
public class Carton {

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

    @Column(nullable = true, name = "id_store_keeper")
    private Long idStoreKeeper;

    @Column(nullable = true, name = "id_store_house")
    private Long idStoreHouse;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private EStatus status;
}
