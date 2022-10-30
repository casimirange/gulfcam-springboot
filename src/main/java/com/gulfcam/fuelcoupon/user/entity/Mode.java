package com.gulfcam.fuelcoupon.user.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"modeName", "isDelete"}))
@Audited(withModifiedFlag = true)
@JsonInclude(value = Include.NON_NULL)
public class Mode {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long modeId;
	
	@NotNull
	private String modeName;
	
	@NotNull
	private String modePrice;
	
	private String modeDescription;
	
	private LocalDateTime isDelete = null;
}
