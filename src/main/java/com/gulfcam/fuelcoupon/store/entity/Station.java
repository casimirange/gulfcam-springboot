package com.gulfcam.fuelcoupon.store.entity;
import com.gulfcam.fuelcoupon.utilities.entity.EStatus;
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
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    private String localization;

    private String designation;

    @NotNull
    @Column(unique = true)
    private int pinCode;

    private float balance;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private EStatus status;

}
