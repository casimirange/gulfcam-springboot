package com.gulfcam.fuelcoupon.order.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @Schema(description = "Reférence client", example = "0987698")
    private Long clientReference;

    @Column(nullable = true, name = "id_client")
    private Long idClient;

    @Column(nullable = true, name = "id_fund")
    private Long idFund;

    @Column(nullable = true, name = "id_manager_store")
    private Long idManagerStore;

    @Column(nullable = true, name = "id_manager_coupon")
    private Long idManagerCoupon;

    @Column(nullable = true, name = "net_aggregate_amount")
    private int NetAggregateAmount;

    @Column(nullable = true, name = "ttc_aggregateA_amount")
    private int TTCAggregateAmount;

    @Column(nullable = true, name = "id_storekeeper")
    private Long idStorekeeper;

    @Column(nullable = true, name = "tax")
    private String tax;

    @NotNull
    @Column(nullable = true, name = "id_store")
    private Long idStore;

    @ManyToOne
    private StatusOrder status;

    private String channel;

    private String deliveryTime;

    private Long IdPaymentMethod;

    private String paymentReference;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String reasonForCancellation;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

}
