package com.gulfcam.fuelcoupon.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "documentId", "fileName", "documentType", "documentFormat", "user" })
@Table(name = "user_documents")
public class DocumentStorageProperties {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long documentId;
	
	private String fileName;
	
	private String documentType;
	
	private String documentFormat;

	private String uploadDir;

	private LocalDate createdAt;

	@Lob
	private String commentError;
	
	@ManyToOne
	@JsonIgnore
	private Users user;

	@ManyToOne
	DocumentCategorie categories;
}
