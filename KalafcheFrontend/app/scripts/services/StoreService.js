'use strict';

angular.module('kalafcheFrontendApp')
	.service('StoreService', function($http, Environment, SessionService) {
		angular.extend(this, {
            getAllStores: getAllStores,
            getAllEntities: getAllEntities,
            getSelectedStore: getSelectedStore,
            submitStore: submitStore,
            getRealSelectedStore: getRealSelectedStore,
            getAllStoresForSaleReport: getAllStoresForSaleReport
		});

        function getAllStores(includingWarehouse) {   
            var params = {"params" : {"includingWarehouse": includingWarehouse}}
            console.log(params);
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/store', params)
                .then(
                    function(response) {
                        return response.data
                    }
                ) ;
        };

        function getAllStoresForSaleReport() {   
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/store/report')
                .then(
                    function(response) {
                        return response.data
                    }
                ) ;
        };

        function getAllEntities() {   
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/store/entities')
                .then(
                    function(response) {
                        return response.data
                    }
                ) ;
        };

        function submitStore(store) { 
            if (store.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/store', store)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/store', store)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        };

        function getSelectedStore(stores, isAdmin) {
            if (isAdmin) {
                return stores[0];
            } else {
                for (var i = 0; i < stores.length; i++) {
                    var store = stores[i];

                    if (store.id == SessionService.currentUser.employeeStoreId) {
                        return store;
                    }
                }
            }
        };

        function getRealSelectedStore(stores, isAdmin) {
            if (isAdmin) {
                return stores[0];
            } else {
                for (var i = 0; i < stores.length; i++) {
                    var store = stores[i];

                    if (store.identifiers == SessionService.currentUser.employeeStoreId) {
                        return store;
                    }
                }
            }
        };

	});

