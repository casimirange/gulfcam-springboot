package com.gulfcam.fuelcoupon.user.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Audited(withModifiedFlag = true)
@JsonInclude(value = Include.NON_NULL)
public class StatusUser {
	
	@Schema(description = "identifiant unique du statut", example = "1", required = true, accessMode = AccessMode.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long Id;

	@Schema(description = "nom du statut", example = "STATUS_ENABLED")
	@Enumerated(EnumType.STRING)
	private EStatusUser name;

	private String description;

}
