'use strict';

angular.module('kalafcheFrontendApp')

    .directive('productType', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/assortment/product-type.html',
            controller: ProductTypeController
        }
    });

    function ProductTypeController ($scope, ProductService, AuthService, ServerValidationService) {

        init();

        function init() {
            $scope.productType = {}; 
            $scope.productTypes = [];
            $scope.productMasterTypes = [];
            $scope.currentPage = 1; 
            $scope.productTypesPerPage = 20;
            $scope.serverErrorMessages = {};

            getAllProductTypes();
            getAllProductMasterTypes();
        }

        $scope.resetProductType = function () {
            $scope.productType = null;
        };

        function getAllProductTypes() {
            ProductService.getAllTypes().then(function(response) {
                $scope.productTypes = response;
            });
        };

        function getAllProductMasterTypes() {
            ProductService.getAllProductMasterTypes().then(function(response) {
                $scope.productMasterTypes = response;
            });
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };

        $scope.resetProductTypeForm = function() {
            $scope.resetProductType();
            $scope.resetServerErrorMessages()
            $scope.productTypeForm.$setPristine();
            $scope.productTypeForm.$setUntouched();
        };

        $scope.editProductType = function (productType) {
            $scope.productType = angular.copy(productType);
        };

        $scope.submitProductType = function() {
            console.log($scope.productType);
            ProductService.submitProductType($scope.productType).then(function(response) {
                $scope.resetProductTypeForm();
                getAllProductTypes();
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.productTypeForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

    };