package com.ferschke.springnotes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.ferschke.springnotes.model.Note;
import com.ferschke.springnotes.model.Notebook;
import com.ferschke.springnotes.repository.NoteRepository;
import com.ferschke.springnotes.repository.NotebookRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class NoteService {

	private final @NonNull NoteRepository noteRepo;
	private final @NonNull NotebookRepository notebookRepo;

	public Note createNote(String title, Notebook notebook){
		Assert.hasText(title);
		Assert.notNull(notebook);
		return noteRepo.save(new Note(title, notebook));
	}

	public Note createNote(String title, String body, String notebooktitle){
		Assert.hasText(title);
		Assert.hasText(body);
		Assert.hasText(notebooktitle);
		
		Notebook curNotebook = notebookRepo.findByTitle(notebooktitle)
				.orElse(notebookRepo.save(new Notebook(notebooktitle)));		
		
		Note newNote = new Note(title, body, curNotebook);
		return noteRepo.save(newNote);
	}

	public void deleteNote(Note note){
		noteRepo.delete(note);
	}

	public void deleteNotes(Iterable<Note> notes){
		noteRepo.delete(notes);
	}

	public void deleteNotebook(Notebook notebook){
		notebookRepo.delete(notebook);
	}

	public void deleteNotebooks(Iterable<Notebook> notebooks){
		notebookRepo.delete(notebooks);
	}

}
