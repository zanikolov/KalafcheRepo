'use strict';

angular.module('kalafcheFrontendApp')
	.service('ModelService', function($http, Environment) {
		angular.extend(this, {
			submitModel: submitModel,
            getAllDeviceModels: getAllDeviceModels
		});

    	function submitModel(model) {	
			return $http.put(Environment.apiEndpoint + '/KalafcheBackend/deviceModel', model)
            	.then(
                	function(response) {
                    	console.log(response);
                	}
            	) 
    	}

        function getAllDeviceModels() {   
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/deviceModel')
                .then(
                    function(response) {
                        return response.data
                    }
                ) ;
        }

        function submitModel(model) { 
            if (model.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/deviceModel', model)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/deviceModel', model)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

	});