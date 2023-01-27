'use strict';

angular.module('kalafcheFrontendApp')
	.service('ItemService', function($http, Environment, ApplicationService) {
		angular.extend(this, {
			submitItem: submitItem,
            updateItem: updateItem,
            getAllItems: getAllItems,
            insertItemOrUpdateBarcode: insertItemOrUpdateBarcode
		});

    	function submitItem(item) {	
            if (item.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/item', item)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
    			return $http.put(Environment.apiEndpoint + '/KalafcheBackend/item/upsert', item)
                	.then(
                    	function(response) {
                        	return response.data;
                    	}
                	)
            }
    	}

        function updateItem(item) { 
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/item', item)
                .then(
                    function(response) {
                        console.log(response);
                    }
                ) 
        }

        function getAllItems() {   
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/item')
                .then(
                    function(response) {
                        return response.data;
                    }
                ) ;
        }

        function insertItemOrUpdateBarcode(item) { 
            return $http.put(Environment.apiEndpoint + '/KalafcheBackend/item/upsert', item)
                .then(
                    function(response) {
                        console.log(response);
                    }
                ) 
        }

	});