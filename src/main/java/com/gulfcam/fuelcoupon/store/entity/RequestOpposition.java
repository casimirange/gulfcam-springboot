package com.gulfcam.fuelcoupon.store.entity;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
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
public class RequestOpposition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    private String reason;

    @NotNull
    @Column(nullable = true, name = "id_commerci_attache")
    private Long idCommercialAttache;

    @Column(nullable = true, name = "id_sales_manager")
    private Long idSalesManager;

    @Column(nullable = true, name = "id_client")
    private Long idClient;

//    @NotNull
//    @Column(nullable = true, name = "id_service_client")
//    private Long idServiceClient;

//    @Column(nullable = true, name = "id_manager_coupon")
//    private Long idManagerCoupon;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private Status status;

}
