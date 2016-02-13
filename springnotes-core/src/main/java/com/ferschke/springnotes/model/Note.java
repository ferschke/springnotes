package com.ferschke.springnotes.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.Description;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * Content entities represent the content of Contribution and Context entities.
 * The main payload of a Content entity resides in its text and data field. The
 * content of Contributions usually textual, thus the text field will hold the
 * content of a Contribution. The data field is able to hold arbitrary blobs of
 * data. This is most likely necessary when used to represent the content of
 * Context entities but will rarely be the case for content of Contribution
 * entities. Content entities formally represent nodes in a linked list by
 * pointing to a previous and a next content revision. This way, revision
 * histories of Contribution and Context entities can be represented. A Content
 * entity is related to a User indicating that this user is the author of the
 * content instance. Other relationships between Users and Content or
 * Contributions can be represented with ContributionUserInteraction entities.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
@Entity
@Table(name="note")
@Description("A note entity")
public class Note extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_note", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("The primary key of a note")
	private Long id;
	
	@Column(columnDefinition="LONGTEXT")
	@Description("The title of a note")
	private String title;

	@Column(columnDefinition="LONGTEXT")
	@Description("The text body of a note")
	private String body;
}
