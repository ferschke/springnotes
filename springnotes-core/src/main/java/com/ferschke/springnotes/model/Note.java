package com.ferschke.springnotes.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.Description;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
@RequiredArgsConstructor
@Entity
@Table(name="note")
@Description("A note entity")
public class Note extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Note(Notebook notebook){
		setNotebook(notebook);
	}
	
	public Note(String title, Notebook notebook){
		setTitle(title);
		setNotebook(notebook);
	}

	public Note(String title, String body, Notebook notebook){
		setTitle(title);
		setBody(body);
		setNotebook(notebook);
	}
	
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

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_notebook")
	@Description("The notebook to which this note belongs")
	private Notebook notebook;
}
