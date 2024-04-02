'use strict';

angular.module('kalafcheFrontendApp')
	.service('NewStockService', function($http, SessionService, Environment, FileSaver) {
		angular.extend(this, {
			submitNewStock: submitNewStock,
            deleteNewStock: deleteNewStock,
            getAllNewStocks: getAllNewStocks,
            getNewStocks: getNewStocks,
            validateFile: validateFile,
            importFile: importFile,
            approveNewStock: approveNewStock,
            approveAllNewStocks: approveAllNewStocks,
            printStickersForNewStocks: printStickersForNewStocks,
            printStickersForNewStocksV2: printStickersForNewStocksV2,
            relocateNewStocks: relocateNewStocks
		});

    	function submitNewStock(newStock, storeId) {	
            newStock.storeId = storeId;
			return $http.put(Environment.apiEndpoint + '/KalafcheBackend/newStock', newStock)
            	.then(
                	function(response) {
                    	return response.data;
                	}
            	) 
    	}

        function getAllNewStocks() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/newStock')
                .then(
                    function(response) {
                        return response.data;
                    }
                ); 
        }

        function getNewStocks(storeId) {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/newStock/' + storeId)
                .then(
                    function(response) {
                        return response.data;
                    }
                ); 
        }

        function deleteNewStock(newStockId) {
            return $http.delete(Environment.apiEndpoint + '/KalafcheBackend/newStock', {"params": {"newStockId": newStockId}})
                .then(
                    function(response) {
                        return response.data;
                    }
                ); 
        }

        function approveNewStock(newStock) {
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/newStock/approve', newStock)
                .then(
                    function(response) {
                        return response.data;
                    }
                ); 
        }

        function approveAllNewStocks(newStocks) {
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/newStock/approveAll', newStocks)
                .then(
                    function(response) {
                        return response.data;
                    }
                ); 
        }

        function printStickersForNewStocks(storeId) {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/newStock/print/' + storeId, {responseType: "arraybuffer"})
                .then(
                    function(response) {
                        var blob = new Blob([response.data], {type: "application/pdf"});
                        FileSaver.saveAs(blob, 'Етикети за нова стока.pdf')
                    }
                ); 
        }

        function printStickersForNewStocksV2(storeId) {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/newStock/print/v2/' + storeId, {responseType: "arraybuffer"})
                .then(
                    function(response) {
                        var blob = new Blob([response.data], {type: "application/pdf"});
                        FileSaver.saveAs(blob, 'Етикети за нова стока V2.pdf')
                    }
                ); 
        }

        function relocateNewStocks(storeId) {
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/newStock/relocate/' + storeId)
                .then(
                    function(response) {
                        return response.data;
                    }
                );  
        }

        function validateFile(file) {
            var fileFormData = new FormData();
            fileFormData.append('newStockFile', file);

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/newStock/validate', fileFormData, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
 
            }).then(function (response) {
                return response.data;
            })
        }

        function importFile(file, storeId) {
            var fileFormData = new FormData();
            fileFormData.append('newStockFile', file);
            fileFormData.append('storeId', storeId);

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/newStock/import', fileFormData, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
 
            }).then(function (response) {
                return response.data;
            })
        }

	});