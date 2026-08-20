'use strict';

angular.module('kalafcheFrontendApp')
	.service('InStockService', function($http, Environment, FileSaver) {
		angular.extend(this, {
            getInStock: getInStock,
            getAllInStockForReport: getAllInStockForReport,
            printStickersForStocks: printStickersForStocks,
            printStickersForStocksV2: printStickersForStocksV2,
            printEuroOnlyStickersForStocks: printEuroOnlyStickersForStocks,
            printEuroOnlyStickersForStocksV2: printEuroOnlyStickersForStocksV2,
            printInStocks: printInStocks

		});

    function getInStock(userStoreId, selectedStoreId, deviceBrandId, deviceModelId, productCode, barcode, showZeroInStocks) {
        return $http.get(Environment.apiEndpoint + '/KalafcheBackend/stock',
          {"params": {
            "userStoreId": userStoreId,
            "selectedStoreId": selectedStoreId,
            "deviceBrandId": deviceBrandId,
            "deviceModelId": deviceModelId,
            "productCodes": productCode,
            "barcode": barcode,
            "showZeroInStocks": showZeroInStocks}})
            .then(
                function(response) {
                    return response.data
                }
            );
    }

    function getAllInStockForReport() {
        return $http.get(Environment.apiEndpoint + '/KalafcheBackend/stock/getAllStocksForReport')
            .then(
                function(response) {
                    return response.data
                }
            );
    }

    function printStickersForStocks(storeId) {
      return $http.get(Environment.apiEndpoint + '/KalafcheBackend/stock/printStickers/' + storeId, {responseType: "blob"})
          .then(
              function(response) {
                console.log(response.data);
                  var blob = new Blob([response.data], {type: "application/pdf"});
                  FileSaver.saveAs(blob, 'Етикети наличност.pdf')
              }
          );
    }

    function printEuroOnlyStickersForStocks(storeId) {
      return $http.get(Environment.apiEndpoint + '/KalafcheBackend/stock/printStickers/' + storeId, {
          responseType: "blob",
          params: {
            euroOnly: true
          }
      })
          .then(
              function(response) {
                console.log(response.data);
                  var blob = new Blob([response.data], {type: "application/pdf"});
                  FileSaver.saveAs(blob, 'Етикети наличност EUR.pdf')
              }
          );
    }

    function printStickersForStocksV2(storeId) {
      return $http.get(Environment.apiEndpoint + '/KalafcheBackend/stock/printStickers/v2/' + storeId, {responseType: "blob"})
          .then(
              function(response) {
                console.log(response.data);
                  var blob = new Blob([response.data], {type: "application/pdf"});
                  FileSaver.saveAs(blob, 'Етикети наличност V2.pdf')
              }
          );
    }

    function printEuroOnlyStickersForStocksV2(storeId) {
      return $http.get(Environment.apiEndpoint + '/KalafcheBackend/stock/printStickers/v2/' + storeId, {
          responseType: "blob",
          params: {
            euroOnly: true
          }
      })
          .then(
              function(response) {
                console.log(response.data);
                  var blob = new Blob([response.data], {type: "application/pdf"});
                  FileSaver.saveAs(blob, 'Етикети наличност V2 EUR.pdf')
              }
          );
    }

    function printInStocks(inStocks) {
      return $http.post(Environment.apiEndpoint + '/KalafcheBackend/stock/excel', inStocks, {responseType: "blob"})
          .then(
              function(response) {
                  var blob = new Blob([response.data], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
                  FileSaver.saveAs(blob, 'Наличности.xlsx')
              }
          );
    }

	});
