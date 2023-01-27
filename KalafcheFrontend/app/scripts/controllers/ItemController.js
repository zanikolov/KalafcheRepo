'use strict';

angular.module('kalafcheFrontendApp')

    .directive('item', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/assortment/item.html',
            controller: ItemController
        }
    });

    function ItemController ($scope, $element, ItemService, BrandService, ModelService, ProductService, ApplicationService) {

    	init();

        function init() {
            $scope.items = [];
            $scope.currentPage = 1; 
            $scope.itemsPerPage = 20;
            $scope.selectedItem = {};

            getAllBrands();
            getAllProducts();
            getAllModels(); 
            getAllItems();
        }

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

        function getAllModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response; 
            });
        };

        $scope.resetSelectedItem = function () {
            $scope.selectedItem = {};
        };

        function getAllItems() {
            ItemService.getAllItems().then(function(response) {
                $scope.items = response;
            });
        };

        $scope.editItem = function (item) {
            $scope.selectedItem = angular.copy(item);
        };

        $scope.filterByProductCode = function() {
            var productCodesString = $scope.productCode;
            var productCodes = productCodesString.split(" ");
            return function predicateFunc(item) {
                return productCodes.indexOf(item.productCode) !== -1 ;
            };
        };

        $scope.clearModelSearchTerm= function() {
            $scope.modelSearchTerm = "";
        }

        $element.find('#modelSearchTerm').on('keydown', function(ev) {
            ev.stopPropagation();
        });

        $scope.$on('cancelEdit', function (event, data) {
            $scope.selectedItem = {};
        });

        $scope.$on('submitSuccess', function (event, data) {
            $scope.selectedItem = {};
            getAllItems();
        });

    };