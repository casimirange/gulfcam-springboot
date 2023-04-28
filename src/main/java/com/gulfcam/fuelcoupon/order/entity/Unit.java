package com.gulfcam.fuelcoupon.order.entity;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @Column(nullable = true, name = "id_type_voucher")
    private Long idTypeVoucher;

    @Column(nullable = true, name = "id_store")
    private Long idStore;

    private int quantityNotebook;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private Status status;

}
