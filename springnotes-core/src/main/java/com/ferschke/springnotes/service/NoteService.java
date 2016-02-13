package com.ferschke.springnotes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ferschke.springnotes.repository.NoteRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class NoteService {

	private final @NonNull NoteRepository noteRepo;

	
}
