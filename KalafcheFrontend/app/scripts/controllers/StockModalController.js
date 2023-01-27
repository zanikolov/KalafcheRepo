'use strict';

angular.module('kalafcheFrontendApp')
	.controller('StockModalController', function ($scope, $uibModalInstance, selectedStock, selectedStore, PartnerService, SaleService, SessionService, ApplicationService, RelocationService, OrderedStockService) {

        init();

        function init() {
            $scope.selectedStock = selectedStock;
            $scope.selectedStore = selectedStore;
            $scope.partner = null;
            $scope.isSubmitButonDisabled = false;
            $scope.invalidPartnerCodeErrorText = "Несъществуващ код!";
            $scope.invalidSalePriceErrorText = "Въведете намалената цена!";

            $scope.showPartnerCodeErrorText = false;
            $scope.showSalePriceErrorText = false;

            //Stock order
            console.log($scope.selectedStore);
            $scope.stockForOrder = selectedStock.selectedStock;
            $scope.orderedStockList = selectedStock.orderedStockList;
        };

        $scope.submitSale = function() {
            $scope.isSubmitButonDisabled = true;
            if ($scope.partnerCode) {
                if (!$scope.salePrice) {
                    $scope.showSalePriceErrorText = true;
                    console.log("Incorrect discount percentage!!!");
                    $scope.isSubmitButonDisabled = false;
                } else {
        			if ($scope.partner) {  
        				var stock = $scope.selectedStock;
                        var partner = $scope.partner;
        				var sale = {"stockId": stock.id, "partnerId": partner.id, "employeeId": SessionService.currentUser.employeeId, "cost": stock.productPrice, "salePrice": $scope.salePrice, "saleTimestamp": ApplicationService.getCurrentTimestamp()};
    		        	SaleService.submitSale(sale).then(
    			            function(response) {
    			            	$scope.selectedStock.quantity -= 1;
    							$uibModalInstance.close($scope.selectedStock);
                                $scope.isSubmitButonDisabled = false;
    			            }
    			        );
    				}
                }
            } else {
                var stock = $scope.selectedStock;
                var sale = {"stockId": stock.id, "employeeId": SessionService.currentUser.employeeId, "cost": stock.productPrice, "salePrice": $scope.salePrice, "saleTimestamp": ApplicationService.getCurrentTimestamp()};
                SaleService.submitSale(sale).then(
                    function(response) {
                        $scope.selectedStock.quantity -= 1;
                        $uibModalInstance.close($scope.selectedStock);
                        $scope.isSubmitButonDisabled = false;
                    }
                );
            }
            
        };

        $scope.submitRelocation = function() {
                var itemId = $scope.selectedStock.itemId;
                var sourceStoreId = $scope.selectedStore.id
                var relocation = {"itemId": itemId, "sourceStoreId": sourceStoreId, "destStoreId": SessionService.currentUser.employeeStoreId, "quantity": $scope.quantityForRelocation};
                RelocationService.submitRelocation(relocation).then(
                    function(response) {
                        $scope.selectedStock.quantity -= $scope.quantityForRelocation;
                        $scope.selectedStock.orderedQuantity += $scope.quantityForRelocation;
                        $uibModalInstance.close($scope.selectedStock);
                    }
                );
        };

        $scope.submitOrderedStock = function() {
            var stock = $scope.stockForOrder;
            var orderedStock = {"productId": stock.productId, "deviceModelId": stock.deviceModelId, "stockOrderId": stock.stockOrderId, "quantity": $scope.quantityForOrder, "deviceModelName": stock.deviceModelName, "deviceBrandName": stock.deviceBrandName, "productName": stock.productName, "productCode": stock.productCode};
            OrderedStockService.submitOrderedStock(orderedStock).then(
                function(response) {
                    orderedStock.id = response.data;
                    $scope.orderedStockList.push(orderedStock);
                    $uibModalInstance.close($scope.orderedStockList);
                }
            );
        };

		$scope.closeModal = function () {
			$uibModalInstance.dismiss('cancel');
		};

        $scope.onChangePartnerCode = function () {
            $scope.isSubmitButonDisabled = true;

            if (!$scope.partnerCode || $scope.partnerCode === '') {
                $scope.isSubmitButonDisabled = false;
                $scope.showPartnerCodeErrorText = false;
                $scope.showSalePriceErrorText = false;
                $scope.partner = null;
            } else {
                PartnerService.getPartnerByCode($scope.partnerCode).then(
                            function(partner) {
                                if (partner) {  
                                    $scope.partner = partner;
                                    $scope.isSubmitButonDisabled = false;
                                    $scope.showPartnerCodeErrorText = false;
                                } else{
                                    $scope.showPartnerCodeErrorText = true;
                                    $scope.partner = null;
                                    console.log("Incorrect partner's code!!!");
                                }
                            }
                        );
            }
        };

	});