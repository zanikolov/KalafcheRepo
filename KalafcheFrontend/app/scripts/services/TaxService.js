'use strict';

angular.module('kalafcheFrontendApp')
	.service('TaxService', function($http, Environment) {
		angular.extend(this, {
            getTaxes: getTaxes
		});


        function getTaxes() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/tax/expense')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }


	});