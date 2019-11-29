package com.geekShirt.orderservice.entities;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@Data
@EntityListeners(AuditingEntityListener.class)
/*informa que sera una clase base, para que transmita los campos a las clases heredadas*/
@MappedSuperclass
public class CommonEntity implements Serializable  {

    @Column (name = "CREATED_DATE")
    @CreatedDate
    private Date createdDate;

    @Column (name = "LAST_UPDATE")
    @LastModifiedDate
    private Date lastUpdateDate;
}
