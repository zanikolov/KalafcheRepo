package com.kalafche.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.filter.CorsFilter;

import com.kalafche.security.RestAuthenticationEntryPoint;
import com.kalafche.security.RestAuthenticationSuccessHandler;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	DataSource dataSource;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
				.authorizeRequests()
					//.anyRequest().permitAll();
					.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.antMatchers("/logout-success").permitAll()
					.anyRequest().authenticated()
					.and()
//					.addFilterBefore(corsFilter, CorsFilter.class)
					.httpBasic().authenticationEntryPoint(new RestAuthenticationEntryPoint())
					.and()
//					.formLogin().successHandler(new RestAuthenticationSuccessHandler())
	                .logout().logoutUrl("/logout").deleteCookies("JSESSIONID", "currentUser").invalidateHttpSession(true)
                    .logoutSuccessHandler(new LogoutSuccessHandler() {
                    		@Override
							public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, 
									Authentication authentication) throws IOException, ServletException {
                    			SecurityContextHolder.getContext().setAuthentication(null);
                    			SecurityContextHolder.clearContext();
                    			
                    			HttpSession session = request.getSession();
                    			session.invalidate();
                    			
                    			System.out.println(">>>> Logout Success");
							}
                    	}
                    );

		
		
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery(
				"select username, password, enabled from employee where username = ?")
			.authoritiesByUsernameQuery(
				"select e.username, r.name from employee e join employee_role er on e.id = er.employee_id join auth_role r on er.auth_role_id = r.id where e.username = ?");
	}
}
