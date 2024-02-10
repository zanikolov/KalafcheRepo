'use strict';

angular.module('kalafcheFrontendApp')
    .directive('newStockImport', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/new-stock/import.html',
            controller: NewStockImportController
        }
    });

    function NewStockImportController($http, $scope, $element, AuthService, SessionService, ModelService, BrandService, ProductService, NewStockService, StoreService, ServerValidationService) {

        init();

        function init() {
            $scope.selecModelDisabled = true;
            $scope.newStockFormVisible = false;
            $scope.addNewStockButtonVisible = true;

            $scope.newNewStock = {};
            $scope.newStocks = [];
            $scope.brands = [];
            $scope.products = [];
            $scope.models = [];
            $scope.unexistingItems = [];

            $scope.newStocksPerPage = 20;
            $scope.currentPage = 1;

            getAllBrands();
            getAllProducts();
            getAllDeviceModels();
            getAllStores();
        };

        $scope.isAdmin = function() {
            return AuthService.isAdmin();
        };

        $scope.isManager = function() {
            return AuthService.isManager();
        }

        $scope.isUser = function() {
            return AuthService.isUser();
        }
        
        $scope.addNewStock = function() {
            $scope.newStockFormVisible = true;
            $scope.addNewStockButtonVisible = false;
        };

        $scope.cancelAdditionOfNewStock = function() {
            $scope.newStockFormVisible = false;
            $scope.addNewStockButtonVisible = true;
            $scope.newNewStock = {};
        };

        $scope.submitNewAddedStock = function() {
            if ($scope.newStockForm.$valid && $scope.selectedStore) {
                NewStockService.submitNewStock($scope.newNewStock, $scope.selectedStore.id).then(function(response) {
                    NewStockService.getNewStocks($scope.selectedStore.id).then(function(response) {
                        $scope.newStockFormVisible = false;
                        $scope.newNewStock = {};
                        $scope.newStocks = response;  
                        $scope.addNewStockButtonVisible = true;               
                    });
                });
            }
        };

        $scope.deleteNewStock = function(newStockId, index) {
            NewStockService.deleteNewStock(newStockId).then(
                    function(response) {
                        $scope.newStocks.splice(index, 1);
                    }
                );
            
        };

        $scope.approveNewStock = function(newStock, index) {
            newStock.isApproved = true;
            NewStockService.approveNewStock(newStock).then(
                    function(response) {
                        $scope.newStocks.splice(index, 1);
                    }
                );     
        };

        $scope.approveAllNewStocks = function() {
            NewStockService.approveAllNewStocks($scope.newStocks).then(
                    function(response) {
                        $scope.newStocks = [];
                    }
                );     
        };

        $scope.printStickersForNewStocks = function() {
            NewStockService.printStickersForNewStocks($scope.selectedStore.id).then(
                    function(response) {
                    }
                );     
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

        function getNewStocks() {
            if ($scope.selectedStore) {
                NewStockService.getNewStocks($scope.selectedStore.id).then(function(response) {
                    $scope.newStocks = response; 
                });
            }
        };

        $scope.getNewStocks = function() {
            getNewStocks();
        }

        function getAllStores() {
            StoreService.getAllStores().then(function(response) {
                $scope.stores = response;
                $scope.selectedStore = {"id": SessionService.currentUser.employeeStoreId};
                getNewStocks();
            });
        };

        $scope.getNameById = function(list, id) {
            if (list) {
                for (var i = 0; i < list.length; i++) {
                    var current = list[i];
                    if (current.id === id) {
                        return current.name;
                    }
                }
            }

            return null;
        };

        $scope.isApproveButtonVisible = function() {
            return  $scope.isAdmin() && $scope.stocksWaitingForApprovalByStore[$scope.selectedStore.id].length > 0;
        };

        $scope.getProductProperties = function () {
            for (var i = 0; i < $scope.products.length; i++) {
                var curr = $scope.products[i];

                if ($scope.newNewStock.productCode === curr.code) {
                    $scope.newNewStock.productId = curr.id;
                    $scope.newNewStock.productName = curr.name;
                    $scope.newNewStock.productPrice = curr.price;

                    break;
                }
            }
        };

        $scope.resetProductProperties = function () {
            $scope.newNewStock.productId = null;
            $scope.newNewStock.productName = null;
        };

        $scope.importFile = function (file) {          
            if ($scope.selectedStore && $scope.selectedStore.id != 0 && file) {
                NewStockService.importFile(file, $scope.selectedStore.id).then(function(response) {
                    getNewStocks($scope.selectedStore.id); 
                },
                function(errorResponse) {
                    ServerValidationService.processServerErrors(errorResponse, $scope.fileForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                });
            }
        };

        $scope.clearModelSearchTerm= function() {
            $scope.modelSearchTerm = "";
        }
        
  };