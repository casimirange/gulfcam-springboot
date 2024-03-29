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
    @Column(unique = false)
    private String clientReference;

    @Column(nullable = true, name = "id_client")
    private Long idClient;

    @Column(nullable = true, name = "id_fund")
    private Long idFund;

    @Column(nullable = true, name = "id_commercial_attache")
    private Long idCommercialAttache;

    @Column(nullable = true, name = "id_manager_coupon")
    private Long idManagerCoupon;

    @Column(nullable = true, name = "id_sales_manager")
    private Long idSalesManager;

    @Column(nullable = true, name = "id_space_manager2")
    private Long idSpaceManager2;

    @Column(nullable = true, name = "id_space_manager1")
    private Long idSpaceManager1;

    @Column(nullable = true, name = "net_aggregate_amount")
    private int NetAggregateAmount;

    @Column(nullable = true, name = "ttc_aggregateA_amount")
    private int TTCAggregateAmount;

//    @Column(nullable = true, name = "id_storekeeper")
//    private Long idStorekeeper;

    @Column(nullable = true, name = "link_invoice")
    private String linkInvoice;

    @Column(nullable = true, name = "link_delivery")
    private String linkDelivery;

    @Column(nullable = true, name = "tax")
    private String tax;

    @NotNull
    @Column(nullable = true, name = "id_store")
    private Long idStore;

    @ManyToOne
    private StatusOrder status;

    private String channel;

    private String deliveryTime;

    @Column(nullable = true, name = "id_payment_method")
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
