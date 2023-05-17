package com.gulfcam.fuelcoupon.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.gulfcam.fuelcoupon.audit.Auditable;
import com.gulfcam.fuelcoupon.store.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@ToString
@Table(name = "users")
@Audited(withModifiedFlag = true)
public class Users extends Auditable<String> {

	@Schema(description = "identifiant unique de l'utilisateur", example = "1", required = true, accessMode = AccessMode.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long userId;

	@Schema(description = "Reférence interne", example = "0987698")
	@NotNull
	@Column(unique = true)
	private Long internalReference;

	@Column(unique = true,nullable = false)
	@Email()
	private String email;

	@Schema(description = "mot de passe de l'utilisateur", example = "ebkn46Ai?")
	@NotNull
	@JsonIgnore
	private String password;

	@Schema(description = "Téléphone de l'utilisateur", example = "690362808?")
	@Column(nullable = true, unique = true)
	private String telephone;

	@Schema(description = "Code pin de l'utilisateur", example = "0000?")
	@Column(nullable = true, unique = true, name = "pin_code")
	private int pinCode;

	@Column(name = "USING_2FA")
	private boolean using2FA ;
	
	@JsonIgnore
	private String tokenAuth;	// utilisé pour vérifier l'activation du compte d'une part et la non revocation du refesh tokenAuth d'autre part
	
	@JsonIgnore
	private String notificationKey;

	@ManyToMany
	private Set<RoleUser> roles;

	@ManyToOne
	private StatusUser status;

	@ManyToOne
	private TypeAccount typeAccount;

	@ManyToMany
	@JsonIgnore
	@NotAudited
	private List<OldPassword> oldPasswords = new ArrayList<>();
	
	@Transient
	@Schema(hidden = true)
	@JsonIgnore
	private List<ERole> roleNames;

	private String otpCode;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Schema(description = "Poste occupé par l'utilisateur", example = "Développeur")
	@Column(name = "position")
	private String position;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime otpCodeCreatedAT;

	private boolean emailverify;

	private boolean phoneverify;

	@Schema(description = "ID du Magasin", example = "Développeur")
	@Column(name = "id_store")
	private Long idStore;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime dateLastLogin;

	private boolean isFirstConnection;

   private LocalDateTime createdDate;

	private boolean isDelete = false;
	public Users(Long internalReference, String email, String password) {
		this.internalReference = internalReference;
		this.email = email;
		this.password = password;
	}
	
	public Users(Long internalReference, String email, String telephone, String password) {
		this.internalReference = internalReference;
		this.email = email;
		this.password = password;
		this.telephone = telephone;
	}

	public Users(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public Users(int pinCode, String password) {
		super();
		this.pinCode = pinCode;
		this.password = password;
	}
}
