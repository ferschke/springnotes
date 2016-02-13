package com.ferschke.springnotes.repository;

import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.ferschke.springnotes.model.Note;

public interface NoteRepository extends BaseRepository<Note, Long>{

	Optional<Note> findByTitle(@Param("title")String title);	
	
}
