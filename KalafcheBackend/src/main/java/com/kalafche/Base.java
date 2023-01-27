package com.kalafche;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class Base {

	protected boolean userHasRole(String role) {
		@SuppressWarnings("unchecked")
		List<GrantedAuthority> authorities = (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for (GrantedAuthority auth : authorities) {
			if (auth.getAuthority().equals(role)) {
				return true;
			}
		}
		
		return false;
	}
	
}
