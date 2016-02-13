package com.ferschke.springnotes.controller;

import static com.ferschke.springnotes.config.WebSocketConfiguration.MESSAGE_PREFIX;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.ferschke.springnotes.model.Note;

import lombok.RequiredArgsConstructor;

@Component
@RepositoryEventHandler(Note.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class EventHandler {

	private final SimpMessagingTemplate websocket;
	private final EntityLinks entityLinks;

	@HandleAfterCreate
	public void newNote(Note note) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/newNote", getPath(note));
	}

	@HandleAfterDelete
	public void deleteNote(Note note) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/deleteNote", getPath(note));
	}

	@HandleAfterSave
	public void updateNote(Note note) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/updateNote", getPath(note));
	}

	/**
	 * Take a {@link Contribution} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param contribution
	 */
	private String getPath(Note note) {
		return this.entityLinks.linkForSingleResource(note.getClass(),
				note.getId()).toUri().getPath();
	}

}
