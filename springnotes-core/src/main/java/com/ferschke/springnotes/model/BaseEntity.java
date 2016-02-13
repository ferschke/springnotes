package com.ferschke.springnotes.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.rest.core.annotation.Description;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Adds basic common fields for type entities (Version, CreationDate, Type identifier) 
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class BaseEntity{

	@JsonIgnore
	@Version
	@Setter(AccessLevel.PRIVATE) 
	@Description("The version of this entity. Only used for auditing purposes and changes whenever the entity is modified.")
	private Long version;	
	
	@JsonIgnore
	@CreationTimestamp
	@Column(name = "system_created")
	@Setter(AccessLevel.PRIVATE) 
	@Description("The date this entity was first stored in the database. Only used for auditing purposes.")
	private Date creationTime;

	@JsonIgnore
	@LastModifiedDate
	@Column(name = "system_modified")
	@Setter(AccessLevel.PRIVATE) 
	@Description("The date this entity was last modified. Only used for auditing purposes.")
	private Date modificationTime;

	@PreUpdate
    public void preUpdate() {
        this.modificationTime = new Date();
    }
}
