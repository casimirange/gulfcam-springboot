package com.gulfcam.fuelcoupon.client.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @Column(nullable = true, name = "complete_name")
    private String completeName;

    @Column(nullable = true, name = "company_name")
    private String companyName;

    private String address;

    private String phone;

<<<<<<< HEAD
<<<<<<< HEAD
    @Column(unique = false,nullable = true)
    @Email
    private String email;

    @Column(unique = true,nullable = true)
    private String niu;

=======
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    @Column(unique = true,nullable = false)
    @Email
    private String email;

<<<<<<< HEAD
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
=======
>>>>>>> 0e0546e1a1696567d3c70419c5fcf4c1501b95d6
    private String RCCM;

    @Column(nullable = true, unique = true)
    private String gulfcamAccountNumber;

    @ManyToOne
    private TypeClient typeClient;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;
}
