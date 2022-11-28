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
public class Carton {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @NotNull
    @Column(unique = false)
    private int serialTo;

    @NotNull
    @Column(unique = false)
    private int number;

    @NotNull
    @Column(unique = false)
    private int serialFrom;

    @NotNull
    @Column(unique = false)
    private int typeVoucher;

    @NotNull
    @Column(unique = true, name = "carton_from")
    private int from;

    @NotNull
    @Column(unique = true, name = "carton_to")
    private int to;

    @Column(nullable = true, name = "id_store_keeper")
    private Long idStoreKeeper;

    @Column(nullable = true, name = "id_store_house")
    private Long idStoreHouse;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private Status status;
}
