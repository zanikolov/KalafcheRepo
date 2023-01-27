'use strict';

angular.module('kalafcheFrontendApp')
	.service('ImportBarcodesService', function($http, SessionService, Environment, FileSaver) {
		angular.extend(this, {
			updateItemBarcode: updateItemBarcode,
            importFile: importFile
		});

    	function updateItemBarcode(rawItem) {	
            var params = {"params" : {"productId": rawItem.productId, "deviceModelId": rawItem.deviceModelId, "barcode": rawItem.barcode}};
            
			return $http.put(Environment.apiEndpoint + '/KalafcheBackend/rawItem', rawItem)
            	.then(
                	function(response) {
                    	return response.data;
                	}
            	) 
    	}

        function importFile(file) {
            var uploadUrl = Environment.apiEndpoint + '/KalafcheBackend/rawItem/import';
            var fileFormData = new FormData();
            fileFormData.append('rawItemFile', file);

            return $http.post(uploadUrl, fileFormData, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
 
            }).then(function (response) {
                return response.data;
            })
        }

	});