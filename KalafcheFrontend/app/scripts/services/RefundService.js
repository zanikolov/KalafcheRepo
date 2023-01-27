'use strict';

angular.module('kalafcheFrontendApp')
	.service('RefundService', function($http, Environment) {
		angular.extend(this, {
			submitRefund: submitRefund,
			searchRefunds: searchRefunds
		});

        function submitRefund(refund) {
            return $http.put(Environment.apiEndpoint + '/KalafcheBackend/refund', refund)
                .then(
                    function(response) {
                        console.log(response);
                    });
        }

        function searchRefunds(startDateMilliseconds, endDateMilliseconds, storeIds,
        	selectedBrandId, selectedModelId, productCode) { 
            var params = {"params" : 
            	{"startDateMilliseconds": startDateMilliseconds, 
            	"endDateMilliseconds": endDateMilliseconds, 
                "storeIds": storeIds, 
                "deviceBrandId": selectedBrandId, 
                "deviceModelId": selectedModelId, 
                "productCode": productCode}};

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/refund', params)
                .then(
                    function(response) {
                        console.log(response.data);
                        return response.data
                    }
                );
        }

	});