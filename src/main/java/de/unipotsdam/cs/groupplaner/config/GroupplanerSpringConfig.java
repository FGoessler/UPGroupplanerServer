package de.unipotsdam.cs.groupplaner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.util.logging.Logger;

@EnableWebMvc
@Configuration
@ComponentScan("de.unipotsdam.cs.groupplaner")
@Import({ GroupplanerSecurityConfig.class })
public class GroupplanerSpringConfig {
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/groupplaner");
		dataSource.setUsername("uni");
		dataSource.setPassword("infopw");

		return dataSource;
	}
	
	@Bean
	public Logger logger() {
		return Logger.getAnonymousLogger();
	}
	
}
