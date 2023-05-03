package com.gulfcam.fuelcoupon.utilities.entity;

import lombok.*;

import javax.persistence.*;

@Entity
<<<<<<< HEAD
<<<<<<< HEAD
@Data
=======
@Getter
@Setter
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
@Getter
@Setter
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@Table(name = "settingproperties")
public class SettingProperties {

    @Id
<<<<<<< HEAD
<<<<<<< HEAD
    @GeneratedValue(strategy = GenerationType.AUTO)
=======
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    private Long id;

    @Column(nullable = true, unique = true)
    private String key;

    private String value;


}
