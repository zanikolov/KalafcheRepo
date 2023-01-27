'use strict';

angular.module('kalafcheFrontendApp')
    .controller('ItemFormController', function($scope, ItemService, ApplicationService, BrandService, ProductService, ModelService, ServerValidationService) {

        init();

        function init() {
            $scope.brands = [];
            $scope.models = [];
            $scope.serverErrorMessages = {};

            getAllBrands();
            getAllModels();
        };

        function getAllBrands() {
            BrandService.getAllDeviceBrands().then(function(response) {
                $scope.brands = response;
            });
        };

        function getAllModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response; 
            });
        };

        $scope.getProductProperties = function () {
	        ProductService.getProductByCode($scope.item.productCode).then(
                function(response) {
                    if (response) {  
                    	$scope.item.productId = response.id;
            			$scope.item.productName = response.name;
                    } else {
                        $scope.item.productName = "Несъществуващ продуктов код.";
                        $scope.itemForm.$invalid = true;
                    }
                }
            );
        };

        $scope.resetProductProperties = function () {
            $scope.item.productId = null;
            $scope.item.productName = null;
        };

    	$scope.cancelEditItem = function() {
            $scope.$emit('cancelEdit', '');
            $scope.serverErrorMessages = {};
            $scope.itemForm.$setPristine();
            $scope.itemForm.$setUntouched();
        };

        $scope.submitItem = function() {
            ItemService.submitItem($scope.item).then(function(response) {
                $scope.serverErrorMessages = {};
                $scope.$emit('submitSuccess', '');
                $scope.itemForm.$setPristine();
                $scope.itemForm.$setUntouched();
            },
            function(response) {
                ServerValidationService.processServerErrors(response, $scope.itemForm);
                $scope.serverErrorMessages = response.data.errors;
            }); 
        };

    });