package com.kalafche.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {
	
	public void onStartup(ServletContext servletContext) throws ServletException {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(AppConfig.class);
		ctx.setServletContext(servletContext);
		
		Dynamic dynamic = servletContext.addServlet("dispatcher",
				new DispatcherServlet(ctx));
		dynamic.addMapping("/");
		dynamic.setLoadOnStartup(1);
		
//		String profile = System.getProperty("spring.profile", "dev");
//		servletContext.setInitParameter("spring.profiles.active", profile);
//        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();  
//        context.register(AppConfig.class);  
//        context.setServletContext(servletContext);    
//        Dynamic dynamic = servletContext.addServlet("dispatcher", new DispatcherServlet(context));  
//        dynamic.addMapping("/");  
//        dynamic.setLoadOnStartup(1);
	}
}
