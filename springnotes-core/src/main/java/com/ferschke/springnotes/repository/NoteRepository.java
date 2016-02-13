package com.ferschke.springnotes.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.ferschke.springnotes.model.Note;

public interface NoteRepository extends PagingAndSortingRepository<Note, Long>{

	
	
}
