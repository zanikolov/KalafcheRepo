package com.kalafche.rest;

import java.util.List;

import com.kalafche.rest.model.NagerDateResponse;

import feign.Param;
import feign.RequestLine;

public interface NagerDateRestClient {
	
	 @RequestLine("GET /api/v3/PublicHolidays/{year}/{countryCode}")
	  List<NagerDateResponse> getPublicHolidays(@Param("year") Integer year, @Param("countryCode") String countryCode);

}
