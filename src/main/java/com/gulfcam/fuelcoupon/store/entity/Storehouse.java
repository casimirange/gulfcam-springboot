package com.gulfcam.fuelcoupon.store.entity;
import com.gulfcam.fuelcoupon.store.dto.ResponseStoreHouseGroupDTO;
import com.gulfcam.fuelcoupon.utilities.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NamedNativeQuery(name = "Storehouse.groupeNoteBookByInternalReference",
        query = "SELECT i.id_type_voucher as typeVoucher,SUM(i.quantity_carton) as quantityCarton, SUM(i.quantity_notebook) as quantityNoteBook, tv.amount as amount FROM item i JOIN type_voucher tv on i.id_type_voucher = tv.internal_reference WHERE i.id_store_house = :reference GROUP BY i.id_type_voucher",
        resultSetMapping = "Mapping.ResponseStoreHouseGroupDTO")
@SqlResultSetMapping(name = "Mapping.ResponseStoreHouseGroupDTO",
        classes = @ConstructorResult(targetClass = ResponseStoreHouseGroupDTO.class,
                columns = {@ColumnResult(name = "typeVoucher", type=Long.class),
                        @ColumnResult(name = "amount", type=Float.class),
                        @ColumnResult(name = "quantityNoteBook", type=Integer.class),
                        @ColumnResult(name = "quantityCarton", type=Integer.class)}))
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Storehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Reférence interne", example = "0987698")
    @NotNull
    @Column(unique = true)
    private Long internalReference;

    @NotNull
    @Column(nullable = true, name = "id_store")
    private Long idStore;

    private String type;

    private String name;

    private LocalDate updateAt;

    private LocalDate createAt;

    @ManyToOne
    private Status status;

}
