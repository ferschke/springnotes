package com.ferschke.springnotes.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * SpringNote base configuration class.
 * Parameters that are most likely to be changed (i.e. for the databse connection) are read from the hibernate.properties file.
 * 
 * Fore more information about the Spring JavaConfig, see <a href="http://docs.spring.io/spring-data/jpa/docs/1.4.3.RELEASE/reference/html/jpa.repositories.html">the Spring Data docs</a>.
 * <br/>
 * The configuration class be replaced by a custom configuration.
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.ferschke.springnotes.model","com.ferschke.springnotes.repository","com.ferschke.springnotes.service"})
@PropertySources({
    @PropertySource("classpath:hibernate.properties"), //default hibernate configuration
    @PropertySource("classpath:jdbc.properties"), //default database configuration
    @PropertySource("classpath:c3p0.properties"), //default connection pool configuration
    @PropertySource(value = "classpath:custom.properties", ignoreResourceNotFound = true) //optional custom config. keys specified here override defaults 
})
@EntityScan(basePackages = { "com.ferschke.springnotes.model" })
@EnableJpaRepositories(basePackages = { "com.ferschke.springnotes.repository" })
public class BaseConfiguration {

	@Autowired 
	private Environment environment;

	@Bean
	public DataSource dataSource() {
		try {
			ComboPooledDataSource ds = new ComboPooledDataSource();
			ds.setDriverClass(environment.getRequiredProperty("jdbc.driverClassName"));
			String host = environment.getRequiredProperty("jdbc.host");
			String port = environment.getRequiredProperty("jdbc.port");
			String database = environment.getRequiredProperty("jdbc.database");
			ds.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database+ "?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8");
			ds.setUser(environment.getRequiredProperty("jdbc.username"));
			ds.setPassword(environment.getRequiredProperty("jdbc.password"));
			ds.setAcquireIncrement(Integer.parseInt(environment.getRequiredProperty("c3p0.acquireIncrement").trim()));
			ds.setIdleConnectionTestPeriod(
					Integer.parseInt(environment.getRequiredProperty("c3p0.idleConnectionTestPeriod").trim()));
			ds.setMaxStatements(Integer.parseInt(environment.getRequiredProperty("c3p0.maxStatements").trim()));
			ds.setMinPoolSize(Integer.parseInt(environment.getRequiredProperty("c3p0.minPoolSize").trim()));
			ds.setMaxPoolSize(Integer.parseInt(environment.getRequiredProperty("c3p0.maxPoolSize").trim()));
			return ds;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Environment env) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("com.ferschke.springnotes");

		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
		jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.put("hibernate.connection.useUnicode", true);
		jpaProperties.put("hibernate.connection.characterEncoding", "UTF-8");
		jpaProperties.put("hibernate.ejb.naming_strategy", env.getRequiredProperty("hibernate.ejb.naming_strategy"));
		jpaProperties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
		jpaProperties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
		jpaProperties.put("hibernate.jdbc.batch_size", env.getRequiredProperty("hibernate.jdbc.batch_size"));
		jpaProperties.put("hibernate.order_inserts", true);
		jpaProperties.put("hibernate.order_updates", true);
		jpaProperties.put("hibernate.id.new_generator_mappings", Boolean.parseBoolean(environment.getRequiredProperty("hibernate.id.new_generator_mappings").trim()));
		factory.setJpaProperties(jpaProperties);

		return factory;
	}


	@Bean
	PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

}