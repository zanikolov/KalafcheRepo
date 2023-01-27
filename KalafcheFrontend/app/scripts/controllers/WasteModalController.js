'use strict';

angular.module('kalafcheFrontendApp')
	.controller('WasteModalController', function ($scope, $mdDialog, selectedStock, WasteService, ServerValidationService) {

        init();

        function init() {
            $scope.selectedStock = selectedStock;
            $scope.waste = {};
            $scope.submitInProcess = false;
        };

        $scope.submitWaste = function() {
            $scope.submitInProcess = true;
            var image = $scope.image;
            $scope.waste.itemId = $scope.selectedStock.itemId;
            WasteService.submitWaste($scope.waste, image).then(
                function(response) {
                    $scope.submitInProcess = false;
                    $scope.selectedStock.quantity -= 1;
                    $mdDialog.hide($scope.selectedStock);
                }, function(errorResponse) {
                    $scope.submitInProcess = false;
                    ServerValidationService.processServerErrors(errorResponse, $scope.wasteForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                }
            );
        };

		$scope.closeModal = function () {
			$mdDialog.cancel();
		};

	});