package com.gulfcam.fuelcoupon.utilities.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TermesContratMembre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String terme;

    public TermesContratMembre(String terme) {
        this.terme = terme;
    }
}
