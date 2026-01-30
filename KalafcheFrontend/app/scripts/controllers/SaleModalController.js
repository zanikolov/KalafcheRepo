'use strict';

angular.module('kalafcheFrontendApp')
	.controller('SaleModalController', function ($scope, currentSale, $mdDialog, DiscountService, SaleService, EmployeeService, ServerValidationService, SessionService, ApplicationService, FiscalMiddlewareService) {

        init();

        function init() {
            $scope.sale = currentSale;
            $scope.replacementSale = null;
            $scope.replacementSaleHasRefundedItem = false;
            $scope.discountCode = null;
            $scope.serverErrorMessages = {};
            $scope.totalSumReport = 0;

            getEmployeesByStore();
            calculateTotalSum();
        };

        function getEmployeesByStore() {
            var userStoreId = SessionService.currentUser.employeeStoreId ? SessionService.currentUser.employeeStoreId : 0;
            EmployeeService.getAllActiveEmployeesByStore(userStoreId).then(
                function(response){
                    console.log(response);
                    $scope.employees = response;
                })
        }

        function calculateTotalSum() {
            SaleService.getTotalSum($scope.sale).then(
                function(response){
                    console.log(response);
                    $scope.totalSumReport = response;
                })
        };

        $scope.submitSale = function() {
            $scope.loading = true;
            var requestBody = {};

            requestBody.isCashPayment = $scope.sale.isCashPayment;
            requestBody.employeeId = $scope.sale.employeeId;
            requestBody.replacementSaleUSI = $scope.sale.replacementSaleUSI;
            requestBody.storeId = SessionService.currentUser.employeeStoreId ? SessionService.currentUser.employeeStoreId : 0;
            requestBody.saleItems = [];
            angular.forEach($scope.sale.selectedStocks, function(stock){
                var item = {};
                item.itemId = stock.itemId;
                item.discountCode = stock.discountCode ? stock.discountCode.code : null;
                requestBody.saleItems.push(item);
            });
            SaleService.submitSale(requestBody).then(
                function(response) {
                    // console.log("Call Fiscal Middleware!");
                    // console.log(response);
                    // FiscalMiddlewareService.printFiscalReceipt(response).then(
                    //     function(fiscalResponse) {
                    //         console.log("Success!");
                    //     },
                    //     function(error) {
                    //         console.log("Error!");
                    //     }
                    // )
                    $scope.sale.selectedStocks = [];
                    $scope.sale.isCashPayment = null;
                    $scope.sale.employeeId = null;
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
            $scope.sale.paid = null;
            $scope.sale.currency = null;
            $mdDialog.cancel();
        }

        $scope.onChangePaidAmount = function () {
            calculateTotalSum();
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

        $scope.onChangeReplacementSaleUSI = function () {
            if ($scope.sale.replacementSaleUSI && $scope.sale.replacementSaleUSI.length == 20) {
                SaleService.getSaleByUSI($scope.sale.replacementSaleUSI).then(
                    function(sale) {
                        $scope.replacementSale = sale;
                        $scope.serverErrorMessages = {};
                    },
                    function(errorResponse) {
                        $scope.replacementSale = null;
                        ServerValidationService.processServerErrors(errorResponse, $scope.saleForm);
                        $scope.serverErrorMessages = errorResponse.data.errors;
                    }                );
            } else {
                $scope.serverErrorMessages = {};
                $scope.replacementSale = null;
            }
        }

        $scope.onIsReplacementChanged = function () {
            $scope.sale.replacementSaleUSI = null;
            $scope.replacementSale = null;
            $scope.serverErrorMessages = {};
        }

        $scope.getSaleTimestamp = function(saleTimestamp) {
            return ApplicationService.convertEpochToTimestamp(saleTimestamp)
        };

	});