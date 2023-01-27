package com.kalafche.security.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.kalafche.security.entity.CustomerDetails;

/**
 * Customer details exposing as a service. This is secured by spring role base
 * security. This service is only for ROLE_ADMIN
 * 
 * @author malalanayake
 * 
 */
@Controller
@RequestMapping(value = "/customer")
@Secured("ROLE_ADMIN")
public class CustomerDetailService {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	public CustomerDetailService() {
		super();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public CustomerDetails findById(@PathVariable("id") final Long id,
			final UriComponentsBuilder uriBuilder, final HttpServletResponse response) {
		return new CustomerDetails("Pesho");
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<CustomerDetails> findAll() {
		List<CustomerDetails> list = new ArrayList<CustomerDetails>();
		list.add(new CustomerDetails("Gosho"));
		
		return list;
	}

}
