package com.gulfcam.fuelcoupon.utilities.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "setting_properties")
public class SettingProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = true, unique = true)
    private String key;

    private String value;


}
