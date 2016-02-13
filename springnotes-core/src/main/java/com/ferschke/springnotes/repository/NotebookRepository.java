package com.ferschke.springnotes.repository;

import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.ferschke.springnotes.model.Notebook;

public interface NotebookRepository extends BaseRepository<Notebook, Long>{

	Optional<Notebook> findByTitle(@Param("title")String title);
	
}
