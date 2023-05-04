package com.gulfcam.fuelcoupon.utilities.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@Table(name = "settingproperties")
public class SettingProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = true, unique = true)
    private String key;

    private String value;


}
