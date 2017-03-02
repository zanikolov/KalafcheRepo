package com.kalafche.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.kalafche.config" })
@PropertySource(value = { "classpath:hibernate.properties" })
public class HibernateConfig {

   @Autowired
   private Environment environment;

   @Bean
   public LocalSessionFactoryBean sessionFactory() {
       LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
       sessionFactory.setDataSource(dataSource());
       sessionFactory.setPackagesToScan(new String[] { "com.kalafche.model" });
       sessionFactory.setHibernateProperties(hibernateProperties());
       
       return sessionFactory;
    }
    
	@Bean
	public DataSource dataSource() {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource("jdbc/KalafcheDB");

		return dataSource;
	}
    
   private Properties hibernateProperties() {
       Properties properties = new Properties();
       properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
       properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
       properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
       
       return properties;        
   }
    
   @Bean
   @Autowired
   public HibernateTransactionManager transactionManager(SessionFactory s) {
      HibernateTransactionManager txManager = new HibernateTransactionManager();
      txManager.setSessionFactory(s);
      
      return txManager;
   }
}