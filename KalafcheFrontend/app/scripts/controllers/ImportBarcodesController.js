'use strict';

angular.module('kalafcheFrontendApp')
    .controller('ImportBarcodesController', function($scope, $element, ModelService, BrandService, ProductService, ImportBarcodesService, ServerValidationService) {
        init();

        function init() {
            $scope.selecModelDisabled = true;

            $scope.rawItems = [];
            $scope.brands = [];
            $scope.products = [];
            $scope.models = [];

            $scope.rawItemsPerPage = 20;
            $scope.currentPage = 1;

            getAllBrands();
            getAllProducts();
            getAllDeviceModels();
        };

        $scope.saveRawItem = function(rawItem, $index) {
            if (rawItem.productId && rawItem.deviceModelId) {
                ImportBarcodesService.updateItemBarcode(rawItem).then(function(response) {
                    console.log(rawItem);
                });
            }
        };

        $scope.deleteRawItem = function(index) {
            $scope.rawItems.splice(index, 1);    
        };

        function getAllBrands() {
            BrandService.getAllDeviceBrands().then(function(response) {
                $scope.brands = response;
            });
        };

        function getAllProducts() {
            ProductService.getAllProducts().then(function(response) {
                $scope.products = response;
            });

        };

        function getAllDeviceModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response; 
            });
        };

        $scope.getProductProperties = function(rawItem) {
            for (var i = 0; i < $scope.products.length; i++) {
                var curr = $scope.products[i];

                if (rawItem.productCode === curr.code) {
                    rawItem.productId = curr.id;
                    rawItem.productName = curr.name;

                    break;
                }
            }
        };

        $scope.resetProductProperties = function(rawItem) {
            rawItem.productId = null;
            rawItem.productName = null;
            rawItem.productCode = null;
        };

        $scope.importFile = function () {
            var file = $scope.file;
            ImportBarcodesService.importFile(file).then(function(response) {
                $scope.rawItems = response;
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.fileForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };
  });