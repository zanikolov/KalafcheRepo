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
            console.log($scope.sale.selectedStocks);
            console.log($scope.code);

            SaleService.getTotalSum($scope.sale.selectedStocks, $scope.code).then(
                function(response){
                    console.log(response);
                    $scope.totalSumReport = response;
                })
        };

        $scope.submitSale = function() {
            $scope.loading = true;
            var requestBody = {};
            if ($scope.discountCode) {
                requestBody.discountCodeCode = $scope.code;
            }
            requestBody.isCashPayment = $scope.sale.isCashPayment;
            requestBody.saleItems = [];
            angular.forEach($scope.sale.selectedStocks, function(stock){
                requestBody.saleItems.push({"itemId": stock.itemId});
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

        $scope.onChangeDiscountCode = function () {
            if ($scope.code) {
                DiscountService.getDiscountCode($scope.code).then(
                    function(discountCode) {
                        $scope.discountCode = discountCode;
                        $scope.serverErrorMessages = {};
                        calculateTotalSum();
                    },
                    function(errorResponse) {
                        $scope.discountCode = null
                        ServerValidationService.processServerErrors(errorResponse, $scope.saleForm);
                        $scope.serverErrorMessages = errorResponse.data.errors;
                        calculateTotalSum();
                    }                );
            } else {
                $scope.serverErrorMessages = {};
                $scope.discountCode = null;
                calculateTotalSum();
            }
        }

	});