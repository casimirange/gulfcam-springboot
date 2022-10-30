package com.gulfcam.fuelcoupon.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.gulfcam.fuelcoupon.audit.Auditable;
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

	@Schema(description = "nom d'utilisateur", example = "Arnold")
	@NotNull
	@Column(unique = true)
	private String idGulfcam;

	@Column(unique = true,nullable = false)
	@Email
	private String email;

	@Schema(description = "mot de passe de l'utilisateur", example = "ebkn46Ai?")
	@NotNull
	@JsonIgnore
	private String password;

	@Column(nullable = true, unique = true)
	private String telephone;

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

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime otpCodeCreatedAT;

	private boolean emailverify;

	private boolean phoneverify;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime dateLastLogin;

	private String imageUrl;

	private boolean isFirstConnection;

   private LocalDate createdDate;

	private boolean isDelete = false;
	public Users(String idGulfcam, String email, String password) {
		this.idGulfcam = idGulfcam;
		this.email = email;
		this.password = password;
	}
	
	public Users(String idGulfcam, String email, String telephone, String password) {
		this.idGulfcam = idGulfcam;
		this.email = email;
		this.password = password;
		this.telephone = telephone;
	}

	public Users(String idGulfcam, String password) {
		super();
		this.idGulfcam = idGulfcam;
		this.password = password;
	}
}
