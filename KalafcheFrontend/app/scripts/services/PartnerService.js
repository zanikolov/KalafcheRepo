'use strict';

angular.module('kalafcheFrontendApp')
	.service('PartnerService', function($http, Environment) {
		angular.extend(this, {
			getPartnerByCode: getPartnerByCode,
            submitPartner: submitPartner,
            getAllPartners: getAllPartners
		});

        function getPartnerByCode(discountCode) { 
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/partner/' + discountCode)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function submitPartner(partner) { 
            if (partner.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/partner', partner)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/partner', partner)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

        function getAllPartners() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/partner')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

	});