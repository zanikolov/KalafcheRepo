package com.kalafche.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping({ "/service/logout" })
public class LogoutController {

	@GetMapping(value="/logout")
	public void logoutPage (HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.getWriter().write("Logout Success");
	}
}
