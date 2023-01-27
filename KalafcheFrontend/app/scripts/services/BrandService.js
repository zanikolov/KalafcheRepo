'use strict';

angular.module('kalafcheFrontendApp')
	.service('BrandService', function($http, Environment) {
		angular.extend(this, {
			submitBrand: submitBrand,
            getAllDeviceBrands: getAllDeviceBrands
		});

    	function submitBrand(brand) {	
			return $http.put(Environment.apiEndpoint + '/KalafcheBackend/deviceBrand', brand)
            	.then(
                	function(response) {
                    	console.log(response);
                	}
            	)
    	}

        function getAllDeviceBrands() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/deviceBrand')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }
	});