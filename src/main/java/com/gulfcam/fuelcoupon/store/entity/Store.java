package com.gulfcam.fuelcoupon.store.entity;
import com.gulfcam.fuelcoupon.store.dto.ResponseStoreGroupDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseStoreHouseGroupDTO;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NamedNativeQuery(name = "Store.groupNoteBootByInternalReference",
        query = "SELECT u.id_type_voucher as typeVoucher, tv.amount as amount, SUM(u.quantity_notebook) as quantityNoteBook \n" +
                "FROM unit u \n" +
                "JOIN type_voucher tv on u.id_type_voucher = tv.internal_reference  \n" +
                "WHERE u.id_store = :reference \n" +
                "GROUP BY u.id_type_voucher",
        resultSetMapping = "Mapping.ResponseStoreGroupDTO")
@SqlResultSetMapping(name = "Mapping.ResponseStoreGroupDTO",
        classes = @ConstructorResult(targetClass = ResponseStoreGroupDTO.class,
                columns = {@ColumnResult(name = "typeVoucher", type=Long.class),
                        @ColumnResult(name = "amount", type=Float.class),
                        @ColumnResult(name = "quantityNoteBook", type=Integer.class)}))
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @Column(nullable = true, name = "Localization")
    private String localization;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    @ManyToOne
    private Status status;


}
