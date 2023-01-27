'use strict';

angular.module('kalafcheFrontendApp')
	.controller('RefundModalController', function ($scope, $mdDialog, selectedSaleItem, ApplicationService, RefundService) {

        init();

        function init() {
            $scope.selectedSaleItem = selectedSaleItem;
            $scope.refund = {};
        };

        $scope.submitRefund = function() {
            $scope.refund.saleItemId = $scope.selectedSaleItem.id;
            RefundService.submitRefund($scope.refund).then(
                function(response) {
                    $scope.selectedSaleItem.isRefunded = true;
                    $mdDialog.hide($scope.selectedSaleItem);
                }
            );
        };

		$scope.closeModal = function () {
			$mdDialog.cancel();
		};

	});