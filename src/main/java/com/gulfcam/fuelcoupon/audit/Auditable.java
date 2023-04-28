package com.gulfcam.fuelcoupon.audit;


import com.gulfcam.fuelcoupon.user.entity.Users;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter(value = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PROTECTED)
public abstract class Auditable<T> {
	
	@CreatedDate
	protected LocalDateTime createdAt;
	
	@LastModifiedDate
	protected LocalDateTime updateAt;
	
	@CreatedBy
	@ManyToOne
	protected Users createdBy;
	
	@LastModifiedBy
	@ManyToOne
	protected Users updateBy;
}
