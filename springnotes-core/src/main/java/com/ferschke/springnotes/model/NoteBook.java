package com.ferschke.springnotes.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.Description;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
@Entity
@Table(name="notebook")
@Description("A notebook represents a collection of note entities")
public class NoteBook extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_notebook", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("The primary key of a notebook")
	private Long id;
	
	@Column(columnDefinition="LONGTEXT")
	@Description("The title or name of the notebook")
	private String title;

	@Column(columnDefinition="LONGTEXT")
	@Description("A description for the notebook")
	private String description;

	@OneToMany(mappedBy = "notebook")
	@Description("A list of notes that belong to this notebook")
	private List<Note> notes;
}
