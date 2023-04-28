package com.gulfcam.fuelcoupon.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.gulfcam.fuelcoupon.audit.Auditable;
import com.gulfcam.fuelcoupon.store.entity.Store;
import com.gulfcam.fuelcoupon.user.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ResponseUsersDTO extends Auditable<String> {

	private Long userId;

	private Long internalReference;

	private String email;

	private String telephone;

	private int pinCode;

	private Set<RoleUser> roles;

	private StatusUser status;

	private TypeAccount typeAccount;

	private List<ERole> roleNames;

	private String otpCode;

	private String firstName;

	private String lastName;

	private String position;

	private Long idStore;

	private Store store;

	private String nameStore;

	private LocalDateTime dateLastLogin;

   private LocalDateTime createdDate;

}
