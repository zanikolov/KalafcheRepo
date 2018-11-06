'use strict';

angular.module('kalafcheFrontendApp')
	.controller('SaleModalController', function ($scope, currentSale, $mdDialog, PartnerService, SaleService, ServerValidationService) {

        init();

        function init() {
            $scope.sale = currentSale;
            $scope.partner = null;
            $scope.serverErrorMessages = {};
        };

        $scope.submitSale = function() {
            var requestBody = {};
            if ($scope.partner) {
                requestBody.partnerId = $scope.partner.id;
            }
            requestBody.isCashPayment = $scope.sale.isCashPayment;
            requestBody.saleItems = [];
            angular.forEach($scope.sale.selectedStocks, function(stock){
                requestBody.saleItems.push({"itemId": stock.itemId});
            });
            SaleService.submitSale(requestBody).then(
                function(response) {
                    $scope.sale = {};
                    $scope.sale.selectedStocks = [];
                    $mdDialog.hide($scope.sale);
                }
            );
            
        };

        $scope.removeStock = function(index, stock) {
            $scope.sale.selectedStocks.splice(index, 1);
            stock.quantity += 1;

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

        $scope.onChangePartnerCode = function () {
            if ($scope.partnerCode) {
                PartnerService.getPartnerByCode($scope.partnerCode).then(
                    function(partner) {
                        $scope.partner = partner;
                        $scope.serverErrorMessages = {};
                    },
                    function(errorResponse) {
                        ServerValidationService.processServerErrors(errorResponse, $scope.saleForm);
                        $scope.serverErrorMessages = errorResponse.data.errors;
                    }
                );
            } else {
                $scope.serverErrorMessages = {};
                $scope.partner = null;
            }
        }

	});