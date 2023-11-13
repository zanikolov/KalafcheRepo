'use strict';

angular.module('kalafcheFrontendApp')
	.service('CompanyService', function($http, Environment) {
		angular.extend(this, {
			submitCompany: submitCompany,
            getAllCompanies: getAllCompanies
		});

        function submitCompany(company) { 
            if (company.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/company', company)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/company', company)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        };

        function getAllCompanies() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/company')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }
	});