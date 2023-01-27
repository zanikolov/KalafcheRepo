'use strict';

angular.module('kalafcheFrontendApp')
	.service('WasteService', function($http, Environment) {
		angular.extend(this, {
			submitWaste: submitWaste,
			searchWastes: searchWastes
		});

        function submitWaste(waste, file) {
            var fileFormData = new FormData();
            fileFormData.append('wasteImage', file);
            fileFormData.append('itemId', waste.itemId);
            fileFormData.append('description', waste.description);

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/waste', fileFormData, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
 
            }).then(function (response) {
                return response.data;
            })
        }

        function searchWastes(startDateMilliseconds, endDateMilliseconds, storeIds,
        	selectedBrandId, selectedModelId, productCode) { 
            var params = {"params" : 
            	{"startDateMilliseconds": startDateMilliseconds, 
            	"endDateMilliseconds": endDateMilliseconds, 
                "storeIds": storeIds, 
                "deviceBrandId": selectedBrandId, 
                "deviceModelId": selectedModelId, 
                "productCode": productCode}};
            console.log(params);

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/waste', params)
                .then(
                    function(response) {
                        console.log(response.data);
                        return response.data
                    }
                );
        }

	});