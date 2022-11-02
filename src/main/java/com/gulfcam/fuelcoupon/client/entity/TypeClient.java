package com.gulfcam.fuelcoupon.client.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@ToString
@Table(name = "TypeClient")
@Audited(withModifiedFlag = true)
public class TypeClient {

    @Schema(description = "identifiant unique du type de compte", example = "1", required = true, accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Schema(description = "nom du type de compte", example = "PARTICULAR, ENTREPRISE, INSTITUTION")
    @Enumerated(EnumType.STRING)
    private ETypeClient name;

    public TypeClient(ETypeClient name) {
        this.name = name;
    }
}
