package com.gulfcam.fuelcoupon.user.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@ToString
@Table(name = "TypeAccount")
public class TypeAccount {

    @Schema(description = "identifiant unique du type de compte", example = "1", required = true, accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Schema(description = "nom du type de compte", example = "NORMALE, ENTREPRISE")
    @Enumerated(EnumType.STRING)
    private ETypeAccount name;

    public TypeAccount(ETypeAccount name) {
        this.name = name;
    }
}
