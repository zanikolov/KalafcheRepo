'use strict';

angular.module('kalafcheFrontendApp')
	.controller('SaleModalController', function ($scope, currentSale, $mdDialog, DiscountService, SaleService, ServerValidationService) {

        init();

        function init() {
            $scope.sale = currentSale;
            $scope.discountCode = null;
            $scope.serverErrorMessages = {};
            $scope.totalSumReport = 0;

            calculateTotalSum();
        };

        function calculateTotalSum() {
            SaleService.getTotalSum($scope.sale.selectedStocks).then(
                function(response){
                    console.log(response);
                    $scope.totalSumReport = response;
                })
        };

        $scope.submitSale = function() {
            $scope.loading = true;
            var requestBody = {};

            requestBody.isCashPayment = $scope.sale.isCashPayment;
            requestBody.saleItems = [];
            angular.forEach($scope.sale.selectedStocks, function(stock){
                var item = {};
                item.itemId = stock.itemId;
                item.discountCode = stock.discountCode ? stock.discountCode.code : null;
                requestBody.saleItems.push(item);
            });
            SaleService.submitSale(requestBody).then(
                function(response) {
                    $scope.sale.selectedStocks = [];
                    $scope.sale.isCashPayment = null;
                    $mdDialog.cancel();
                    $scope.loading = false;
                },
                function(error) {
                    $scope.loading = false;
                }
            );
            
        };

        $scope.removeStock = function(index, stock) {
            $scope.sale.selectedStocks.splice(index, 1);
            stock.quantity += 1;
            calculateTotalSum();

            if ($scope.sale.selectedStocks.length < 1) {
                $mdDialog.cancel();
            }
        }

		$scope.closeModal = function () {
			//$uibModalInstance.dismiss('cancel');
            $mdDialog.cancel();
		};

        $scope.resetSale = function() {
            angular.forEach($scope.sale.selectedStocks, function(stock){
                stock.quantity += 1;
            });
            $scope.sale.selectedStocks = [];
            $mdDialog.cancel();
        }

        $scope.onChangeDiscountCode = function (stock) {
            if (stock.code) {
                DiscountService.getDiscountCode(stock.code).then(
                    function(discountCode) {
                        stock.discountCode = discountCode;
                        $scope.serverErrorMessages = {};
                        calculateTotalSum();
                    },
                    function(errorResponse) {
                        stock.discountCode = null
                        ServerValidationService.processServerErrors(errorResponse, $scope.saleForm);
                        $scope.serverErrorMessages = errorResponse.data.errors;
                        calculateTotalSum();
                    }                );
            } else {
                $scope.serverErrorMessages = {};
                stock.discountCode = null;
                calculateTotalSum();
            }
        }

	});