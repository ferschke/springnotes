package com.ferschke.springnotes.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.ferschke.springnotes")
@Import({PersistenceContext.class})
public class ApplicationContext {


}
