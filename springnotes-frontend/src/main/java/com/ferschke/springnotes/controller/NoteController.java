package com.ferschke.springnotes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class NoteController {
	
	/**
	 * Spring Boot’s autoconfigured view resolver will map this to the
	 * corresponding html template (index.html)
	 * 
	 * @return
	 */
	@RequestMapping(value = "/")
	public String index(){
		return "index";
	}
}
