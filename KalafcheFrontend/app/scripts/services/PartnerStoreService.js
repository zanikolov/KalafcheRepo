'use strict';

angular.module('kalafcheFrontendApp')
	.service('PartnerStoreService', function($http, Environment) {
		angular.extend(this, {
			submitPartnerStore: submitPartnerStore,
            getAllPartnerStores: getAllPartnerStores
		});

        function getAllPartnerStores() {   
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/partnerStore')
                .then(
                    function(response) {
                        return response.data
                    }
                ) ;
        };

        function submitPartnerStore(partnerStore) { 
            if (partnerStore.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/partnerStore', partnerStore)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/partnerStore', partnerStore)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        };
	});