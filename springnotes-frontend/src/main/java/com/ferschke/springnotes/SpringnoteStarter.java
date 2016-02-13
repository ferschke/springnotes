package com.ferschke.springnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = { "com.ferschke.springnotes" })
public class SpringnoteStarter {

	public static void main(String[] args) {
		SpringApplication.run(SpringnoteStarter.class, args);
	}

}