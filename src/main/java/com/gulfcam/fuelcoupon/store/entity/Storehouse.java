package com.gulfcam.fuelcoupon.store.entity;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Storehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = true, name = "id_store")
    private Long idStore;

    private String type;

    private LocalDate updateAt;

    private LocalDate createAt;

    @ManyToOne
    private Status status;

}
