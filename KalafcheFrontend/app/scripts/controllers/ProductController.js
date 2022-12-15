'use strict';

angular.module('kalafcheFrontendApp')

    .directive('product', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/assortment/product.html',
            controller: ProductController
        }
    });

    function ProductController ($scope, ProductService, AuthService, ServerValidationService) {

        init();

        function init() {
            $scope.product = {}; 
            $scope.products = [];
            $scope.currentPage = 1; 
            $scope.productsPerPage = 50;
            $scope.serverErrorMessages = {};

            getAllTypes();
            getAllProducts();
        }

        $scope.resetProduct = function () {
            $scope.product = null;
        };

        function getAllTypes() {
            ProductService.getAllTypes().then(function(response) {
                $scope.types = response;
            });
        };

        function getAllProducts() {
            ProductService.getAllProducts().then(function(response) {
                $scope.products = response;
            });
        };

        function getProductSpecificPrice(productId) {
            ProductService.getProductSpecificPrice(productId).then(function(response) {
                return response;
            });
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };

        $scope.editProduct = function (product) {
            ProductService.getProductSpecificPrice(product.id).then(function(response) {
                $scope.product = angular.copy(product);
                $scope.product.specificPrices = response;
                console.log($scope.product);
            });
        };

        $scope.resetProductForm = function() {
            $scope.resetProduct();
            $scope.resetServerErrorMessages();
            $scope.productForm.$setPristine();
            $scope.productForm.$setUntouched();
        };

        $scope.submitProduct = function() {
            ProductService.submitProduct($scope.product).then(function(response) {
                $scope.resetProductForm();
                getAllProducts();
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.productForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.isSuperAdmin = function() {
            return AuthService.isSuperAdmin();
        }

        $scope.filterByProductCode = function() {
            var productCodesString = $scope.productCode;
            var productCodes = productCodesString.split(" ");
            return function predicateFunc(product) {
                return productCodes.indexOf(product.code) !== -1 ;
            };
        };

    };