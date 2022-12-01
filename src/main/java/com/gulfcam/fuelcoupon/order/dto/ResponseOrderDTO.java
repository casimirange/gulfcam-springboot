package com.gulfcam.fuelcoupon.order.dto;
import com.gulfcam.fuelcoupon.client.entity.Client;
import com.gulfcam.fuelcoupon.order.entity.PaymentMethod;
import com.gulfcam.fuelcoupon.order.entity.StatusOrder;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseOrderDTO {

    private Long id;

    private Long internalReference;

    private String clientReference;

    private Client client;

    private String completeName;

    private String localisation;

    private Users fund;

    private Users managerOrder;

    private Users managerCoupon;

    private int NetAggregateAmount;

    private int TTCAggregateAmount;

    private Users storekeeper;

    private String linkInvoice;

    private String linkDelivery;

    private String tax;

    private Store store;

    private StatusOrder status;

    private String channel;

    private String deliveryTime;

    private PaymentMethod paymentMethod;

    private String paymentReference;

    private String reasonForCancellation;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

}
