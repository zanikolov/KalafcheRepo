package com.kalafche.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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
	public DataSource getDataSource() {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource("jdbc/KalafcheDB");
		
		return dataSource;
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}
	
	@Bean(name = "transactionManager")
	@Autowired
	public DataSourceTransactionManager getTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager txManager = new DataSourceTransactionManager();
		txManager.setDataSource(dataSource);
		
		return txManager;
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
