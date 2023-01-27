'use strict';

angular.module('kalafcheFrontendApp')
    .controller('StockOrderController', function ($scope, $uibModal, ModelService, BrandService, ProductService, ColorService, InStockService, SessionService, StoreService, SaleService, PartnerService, ApplicationService, StockOrderService, OrderedStockService) {

        init();

        function init() {
            $scope.orderedStockCurrentPage = 1; 
            $scope.orderedStockPerPage = 15;
            $scope.stockOrder;
            $scope.orderedStockList = [];
            $scope.brands = [];
            $scope.products = [];
            $scope.models = [];
            $scope.productCode = "";
            $scope.selectedBrand = {};
            $scope.selectedModel = {};

            getAllBrands();
            getAllProducts();
            getAllDeviceModels();        
            getStockOrder();


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

        function getAllDeviceModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response; 
            });
        };

        $scope.filterByProductCode = function() {
            var productCodesString = $scope.productCode;
            var productCodes = productCodesString.split(" ");
            return function predicateFunc(inStock) {
                return productCodes.indexOf(inStock.productCode) !== -1 ;
            };
        };

        $scope.resetCurrentPage = function() {
            $scope.inStockCurrentPage = 1;
            $scope.orderedStockCurrentPage = 1;
        };

        function getStockOrder() {
            StockOrderService.getCurrentStockOrder().then(function(stockOrder) {
                $scope.stockOrder = stockOrder.data;

                OrderedStockService.getAllOrderedStocks($scope.stockOrder.id).then(function(orderedStockList) { 
                    $scope.orderedStockList = orderedStockList;
                });
            });
        };

        $scope.openOrderModal = function(orderedStock){

            orderedStock.stockOrderId = $scope.stockOrder.id;

            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'stockOrderModal',
                controller: 'OrderModalController',
                size: "sm",
                resolve: {
                    orderedStock: function () {
                            return orderedStock;
                        }
                    }
                });

            modalInstance.result.then(function (orderedStock) {
                    $scope.orderedStockList.push(orderedStock);
                }, function () {
                    console.log('Modal dismissed at: ' + new Date());
                }
            );
        };
        
    });