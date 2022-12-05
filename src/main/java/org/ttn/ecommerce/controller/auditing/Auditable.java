package org.ttn.ecommerce.controller.auditing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable<U> {

    @CreatedBy
    @Column(name = "created_by")
    private U createdBy;

    @JsonIgnore
    @CreatedDate
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @JsonIgnore
    @LastModifiedBy
    @Column(name = "last_modified_by")
    private U lastModifiedBy;

    @JsonIgnore
    @LastModifiedDate
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;
}
