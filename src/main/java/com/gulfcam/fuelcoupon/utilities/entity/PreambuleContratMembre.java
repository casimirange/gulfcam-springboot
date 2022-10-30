package com.gulfcam.fuelcoupon.utilities.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PreambuleContratMembre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String preambule;
    @Column(columnDefinition = "TEXT")
    private String valeur;

    public PreambuleContratMembre(String preambule, String valeur) {
        this.preambule = preambule;
        this.valeur = valeur;
    }
}
