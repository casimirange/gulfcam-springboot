package com.gulfcam.fuelcoupon.order.entity;
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
public class TypeVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    private String designation;

    @Column(nullable = true, name = "amount")
    private float amount;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private Status status;


}
