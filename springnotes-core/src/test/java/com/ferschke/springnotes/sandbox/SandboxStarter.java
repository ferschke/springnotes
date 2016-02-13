package com.ferschke.springnotes.sandbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.ferschke.springnotes.model.Note;
import com.ferschke.springnotes.repository.NoteRepository;

@SpringBootApplication
@ComponentScan(value = { "com.ferschke.springnotes" })
public class SandboxStarter implements CommandLineRunner{
	
	@Autowired private NoteRepository noteRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(SandboxStarter.class);
	}

	@Override
	public void run(String... arg0) throws Exception {
//		Note n = new Note();
//		n.setBody("This is a Text");
//		noteRepo.save(n);

		Note n = noteRepo.findOne(1L);
		n.setTitle("New");
		noteRepo.save(n);
	}

}
