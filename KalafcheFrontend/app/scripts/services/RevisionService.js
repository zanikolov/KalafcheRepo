'use strict';

angular.module('kalafcheFrontendApp')
	.service('RevisionService', function($http, Environment, SessionService) {
		angular.extend(this, {
			initiateRevision: initiateRevision,
            getCurrentRevision: getCurrentRevision,
            getRevisionTypes: getRevisionTypes,
            getRevisionItemByBarcode: getRevisionItemByBarcode,
            plusRevisionItem: plusRevisionItem,
            minusRevisionItem: minusRevisionItem,
            finalizeRevision: finalizeRevision,
            searchRevisions: searchRevisions,
            getRevisionDetailedData: getRevisionDetailedData,
            getRevisionItems: getRevisionItems
		});

        function initiateRevision(revision) { 
            return $http.put(Environment.apiEndpoint + '/KalafcheBackend/revision', revision)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        };

        function getCurrentRevision(storeId) {
            var params = {"params" : {"storeId": storeId}};
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/revision/current', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getRevisionTypes() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/revision/type')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getRevisionItemByBarcode(revisionId, barcode) {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/revision/' + revisionId + '/item/' + barcode)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getRevisionItems(revisionId) {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/revision/item/' + revisionId)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function plusRevisionItem(revisionItem) { 
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/revision/item/plus', revisionItem)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        };

        function minusRevisionItem(revisionItem) { 
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/revision/item/minus', revisionItem)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        };

        function syncRevisionItemQuantity(revisionItem) { 
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/revision/sync', revisionItem)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        };

        function finalizeRevision(revision) { 
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/revision', revision)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        };

        function getRevisionDetailedData(revisionId) {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/revision/' + revisionId)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function searchRevisions(startDateMilliseconds, endDateMilliseconds, storeId, typeId) { 
            var params = {"params" : {"startDateMilliseconds": startDateMilliseconds, "endDateMilliseconds": endDateMilliseconds, "storeId": storeId, "typeId": typeId}};
            console.log(params);
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/revision', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

	});