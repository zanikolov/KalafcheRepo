'use strict';

angular.module('kalafcheFrontendApp')
	.service('SaleService', function($http, Environment, FileSaver) {
		angular.extend(this, {
			submitSale: submitSale,
            getSaleItems: getSaleItems,
            getSalesByStores: getSalesByStores,
            searchSales: searchSales,
            searchSaleItems: searchSaleItems,
            getTotalSum: getTotalSum,
            generateExcel: generateExcel,
            getMonthlyTurnover: getMonthlyTurnover,
            getProductTypeSplitReport: getProductTypeSplitReport,
            getTransactionSplitReport: getTransactionSplitReport
		});

        function getTotalSum(items) {
            var selectedSaleItems = [];
            angular.forEach(items, function(item) {
                var selectedSaleItem = {};
                selectedSaleItem.itemPrice = item.productPrice;
                if (item.discountCode) {
                    selectedSaleItem.discountValue = item.discountCode.discountValue;
                    selectedSaleItem.discountType = item.discountCode.discountTypeCode;
                    selectedSaleItem.discountCode = item.discountCode.code;
                }
                selectedSaleItems.push(selectedSaleItem);
            })

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/sale/totalSum', selectedSaleItems)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        };

        function submitSale(sale) { 
            return $http.put(Environment.apiEndpoint + '/KalafcheBackend/sale', sale)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getSaleItems(saleId) {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/sale/' + saleId)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function searchSales(startDateMilliseconds, endDateMilliseconds, storeIds, selectedBrandId, selectedModelId, productCode) { 
            var params = {"params" : {"startDateMilliseconds": startDateMilliseconds, "endDateMilliseconds": endDateMilliseconds, 
                "storeIds": storeIds, "deviceBrandId": selectedBrandId, "deviceModelId": selectedModelId, "productCode": productCode}};

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/sale', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function searchSaleItems(startDateMilliseconds, endDateMilliseconds, storeIds, selectedBrandId, selectedModelId, productCode, productTypeId, masterProductTypeId, priceFrom, priceTo, discountCampaignCode) { 
            var params = {"params" : {"startDateMilliseconds": startDateMilliseconds, "endDateMilliseconds": endDateMilliseconds, 
                "storeIds": storeIds, "deviceBrandId": selectedBrandId, "deviceModelId": selectedModelId, "productCode": productCode, "productTypeId": productTypeId, "masterProductTypeId": masterProductTypeId,
                "priceFrom": priceFrom, "priceTo": priceTo, "discountCampaignCode": discountCampaignCode}};

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/sale/saleItem', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getSalesByStores(startDateMilliseconds, endDateMilliseconds, selectedBrandId, selectedModelId, productCode, productTypeId) { 
            var params = {"params" : {"startDateMilliseconds": startDateMilliseconds, "endDateMilliseconds": endDateMilliseconds,
             "deviceBrandId": selectedBrandId, "deviceModelId": selectedModelId, "productCode": productCode, "productTypeId": productTypeId}};

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/sale/store', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getMonthlyTurnover(month) {
            var params = {"params" : {"month": month}};

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/sale/pastPeriods', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getProductTypeSplitReport(startDateMilliseconds, endDateMilliseconds, storeId) { 
            var request = {};
            request.startDate = startDateMilliseconds;
            request.endDate = endDateMilliseconds;

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/sale/productTypeSplit', request, {responseType: "arraybuffer"})
                .then(
                    function(response) {
                        var blob = new Blob([response.data], {type: "application/vnd.openxmlformat-officedocument.spreadsheetml.sheet;"});
                        FileSaver.saveAs(blob, 'split-report.xlsx')
                    }
                );
        }

        function getTransactionSplitReport(startDateMilliseconds, endDateMilliseconds, storeId) { 
            var request = {};
            request.startDate = startDateMilliseconds;
            request.endDate = endDateMilliseconds;

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/sale/transactionSplit', request, {responseType: "arraybuffer"})
                .then(
                    function(response) {
                        var blob = new Blob([response.data], {type: "application/vnd.openxmlformat-officedocument.spreadsheetml.sheet;"});
                        FileSaver.saveAs(blob, 'split-report.xlsx')
                    }
                );
        }

        function generateExcel(saleItems, startDateMilliseconds, endDateMilliseconds) { 
            var request = {};
            request.saleItems = saleItems;
            request.startDate = startDateMilliseconds;
            request.endDate = endDateMilliseconds;

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/sale/excel', request, {responseType: "arraybuffer"})
                .then(
                    function(response) {
                        var blob = new Blob([response.data], {type: "application/vnd.openxmlformat-officedocument.spreadsheetml.sheet;"});
                        FileSaver.saveAs(blob, 'Справка продажби артикули.xlsx')
                    }
                );
        }

	});