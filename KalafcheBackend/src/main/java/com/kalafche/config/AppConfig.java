package com.kalafche.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan("com.kalafche")
@EnableWebMvc
@EnableTransactionManagement
public class AppConfig extends WebMvcConfigurerAdapter  {
	
	@Bean
	@Autowired
	public DataSource getDataSource() throws SQLException {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource("jdbc/KalafcheDB");
		dataSource.getConnection().setAutoCommit(false);
		
		return dataSource;
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}
	
	@Bean(name = "transactionManager")
	@Autowired
	public DataSourceTransactionManager getTransactionManager(DataSource dataSource) throws SQLException {
		DataSourceTransactionManager txManager = new DataSourceTransactionManager();
		dataSource.getConnection().setAutoCommit(false);
		txManager.setDataSource(dataSource);
		
		return txManager;
	}
	
	@Bean
	public CommonsMultipartResolver multipartResolver(){
	    CommonsMultipartResolver resolver = new CommonsMultipartResolver();
	    resolver.setMaxUploadSize(10485760); // set the size limit to 10MiB
	    resolver.setDefaultEncoding("UTF-8");
	    return resolver;
	}
	
//	@Bean
//	public CorsFilter corsFilter() {
//
//	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	    CorsConfiguration config = new CorsConfiguration();
//	    config.setAllowCredentials(true); // you USUALLY want this
//	    // likely you should limit this to specific origins
//	    config.addAllowedOrigin("*"); 
//	    config.addAllowedHeader("*");
//	    config.addAllowedMethod("GET");
//	    config.addAllowedMethod("POST");
//	    config.addAllowedMethod("PUT");
//	    source.registerCorsConfiguration("/logout", config);
//	    return new CorsFilter(source);
//	}
	
//	@Bean
//	public LogoutFilter logoutFilter() {
//		LogoutFilter filter = new LogoutFilter("/logout/success", new SecurityContextLogoutHandler());
//		filter.setFilterProcessesUrl("/j_spring_security_logout");
//		return filter;
//	}
	
//	@Bean(name = "transactionManager")
//	@Autowired
//	public DataSourceTransactionManager getTransactionManager(DataSource dataSource) {
//		DataSourceTransactionManager txManager = new DataSourceTransactionManager();
//		txManager.setDataSource(dataSource);
//		
//		return txManager;
//	}
}
