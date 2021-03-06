'use strict';

angular.module('kalafcheFrontendApp')
	.controller('StockModalController', function ($scope, $uibModalInstance, selectedStock, PartnerService, SaleService, SessionService, ApplicationService, StockRelocationService, OrderedStockService) {

        init();

        function init() {
            $scope.selectedStock = selectedStock;
            $scope.partner = null;
            $scope.isSubmitButonDisabled = false;
            $scope.invalidPartnerCodeErrorText = "Несъществуващ код!";
            $scope.invalidSalePriceErrorText = "Въведете намалената цена!";

            $scope.showPartnerCodeErrorText = false;
            $scope.showSalePriceErrorText = false;

            //Stock order
            console.log(selectedStock);
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
        				var sale = {"stockId": stock.id, "partnerId": partner.id, "employeeId": SessionService.currentUser.employeeId, "cost": stock.itemPrice, "salePrice": $scope.salePrice, "saleTimestamp": ApplicationService.getCurrentTimestamp()};
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
                var sale = {"stockId": stock.id, "employeeId": SessionService.currentUser.employeeId, "cost": stock.itemPrice, "salePrice": $scope.salePrice, "saleTimestamp": ApplicationService.getCurrentTimestamp()};
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
                var stock = $scope.selectedStock;
                var relocation = {"stockId": stock.id, "fromKalafcheStoreId": stock.kalafcheStoreId, "toKalafcheStoreId": SessionService.currentUser.employeeKalafcheStoreId, "employeeId": SessionService.currentUser.employeeId, "relocationRequestTimestamp": ApplicationService.getCurrentTimestamp(), "quantity": $scope.quantityForRelocation};
                StockRelocationService.submitRelocation(relocation).then(
                    function(response) {
                        $scope.selectedStock.quantity -= $scope.quantityForRelocation;
                        $scope.selectedStock.orderedQuantity += $scope.quantityForRelocation;
                        $uibModalInstance.close($scope.selectedStock);
                    }
                );
        };

        $scope.submitOrderedStock = function() {
            var stock = $scope.stockForOrder;
            var orderedStock = {"itemId": stock.itemId, "deviceModelId": stock.deviceModelId, "stockOrderId": stock.stockOrderId, "quantity": $scope.quantityForOrder, "deviceModelName": stock.deviceModelName, "deviceBrandName": stock.deviceBrandName, "itemName": stock.itemName, "itemProductCode": stock.itemProductCode};
            OrderedStockService.submitOrderedStock(orderedStock).then(
                function(response) {
                    console.log(">>>>>");
                    console.log(response);
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