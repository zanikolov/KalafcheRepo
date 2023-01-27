'use strict';

angular.module('kalafcheFrontendApp')
	.controller('RelocationModalController', function ($scope, $mdDialog, selectedStock, selectedStore, SessionService, ApplicationService, RelocationService) {

        init();

        function init() {
            $scope.selectedStock = selectedStock;
            $scope.selectedStore = selectedStore;
            $scope.partner = null;
            $scope.isSubmitButonDisabled = false;

            //Stock order
            console.log($scope.selectedStore);
            $scope.stockForOrder = selectedStock.selectedStock;
        };

        $scope.submitRelocation = function() {
                var itemId = $scope.selectedStock.itemId;
                var sourceStoreId = $scope.selectedStore.id
                var relocation = {"itemId": itemId, "sourceStoreId": sourceStoreId, "destStoreId": SessionService.currentUser.employeeStoreId, "quantity": $scope.quantityForRelocation};
                RelocationService.submitRelocation(relocation).then(
                    function(response) {
                        $scope.selectedStock.quantity -= $scope.quantityForRelocation;
                        $scope.selectedStock.orderedQuantity += $scope.quantityForRelocation;
                        $mdDialog.hide($scope.selectedStock);
                    }
                );
        };

		$scope.closeModal = function () {
			$mdDialog.cancel();
		};

	});