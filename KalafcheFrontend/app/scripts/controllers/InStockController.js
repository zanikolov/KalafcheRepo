'use strict';

angular.module('kalafcheFrontendApp')
	.controller('InStockController', function ($scope, $element, $mdDialog, ModelService, BrandService, ProductService, InStockService, SessionService, StoreService) {

		init();

		function init() {
            $scope.currentPage = 1; 
            $scope.inStockPerPage = 15;
			$scope.inStockList = [];
            $scope.brands = [];
            $scope.products = [];
            $scope.models = [];
            $scope.stores = [];
            $scope.productCode = "";
            $scope.selectedStore = null;
            $scope.showZeroInStocks = false;

            $scope.currentSale = {};
            $scope.currentSale.selectedStocks = [];

            getAllBrands();
            getAllProducts();
            getAllDeviceModels();         
            getAllStores();
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

        $scope.getInStock = function() {
            var userStoreId = SessionService.currentUser.employeeStoreId ? SessionService.currentUser.employeeStoreId : 0;
            InStockService.getInStock(userStoreId, $scope.selectedStore.id, $scope.selectedBrand.id, $scope.selectedModel.id, $scope.productCode, $scope.selectedBarcode, $scope.showZeroInStocks).then(function(response) {
                $scope.inStockList = response;
                $scope.resetCurrentPage();
            });
        }

        function getAllStores() {
            StoreService.getAllEntities().then(function(response) {
                $scope.stores = response;
                $scope.selectedStore = {"id": SessionService.currentUser.employeeStoreId};
            });

        };

        $scope.barcodeScanned = function(barcode) {                             
            $scope.selectedBarcode = barcode;      
        }

        $scope.filterByProductCode = function() {
            var productCodesString = $scope.productCode;
            var productCodes = productCodesString.split(" ");
            return function predicateFunc(inStock) {
                return productCodes.indexOf(inStock.productCode) !== -1 ;
            };
        };

        // $scope.filterByBarcode = function() {
        //     console.log("<><><>");
        //     return function predicateFunc(inStock) {
        //         return inStock.barcode == $scope.barcode;
        //     };
        // };

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

	    $scope.openRelocationModal = function(stock){
            $scope.selectedStock = stock;
            $mdDialog.show({
              locals:{selectedStock: $scope.selectedStock, selectedStore: $scope.selectedStore},
              controller: 'RelocationModalController',
              templateUrl: 'views/modals/relocation-modal.html',
              parent: angular.element(document.body)
            })
            .then(function(answer) {
              $scope.status = 'You said the information was "".';
            }, function() {
              $scope.status = 'You cancelled the dialog.';
            });
	    };

        $scope.openSaleModal = function (stock) {
            if (stock) {
                $scope.currentSale.selectedStocks.push(angular.copy(stock));
                stock.quantity -= 1;
            }

            $mdDialog.show({
              locals:{currentSale: $scope.currentSale},
              controller: 'SaleModalController',
              templateUrl: 'views/modals/sale-modal.html',
              parent: angular.element(document.body)
            })
            .then(function(answer) {
              console.log('>>> first function');
            }, function() {
              console.log('>>> second function');
            });

        };

        $scope.openWasteModal = function(stock){
            $scope.selectedStock = stock;
            $mdDialog.show({
              locals:{selectedStock: $scope.selectedStock},
              controller: 'WasteModalController',
              templateUrl: 'views/modals/waste-modal.html',
              parent: angular.element(document.body)
            })
            .then(function(answer) {
              $scope.status = 'You said the information was "".';
            }, function() {
              $scope.status = 'You cancelled the dialog.';
            });
        };


        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };

        $scope.resetDeviceModel = function() {
            $scope.currentPage = 1;
            $scope.selectedModel = {};
        }

        $scope.isEmployeeStoreSelected = function(stock) { 
            return stock.storeId === SessionService.currentUser.employeeStoreId;
        };


        $scope.clearModelSearchTerm= function() {
            $scope.modelSearchTerm = "";
        }

        $scope.isTotalSumRowVisible = function() {
            if ($scope.isAdmin() && $scope.inStockPerPage * $scope.currentPage >= $scope.inStockList.length) {
                return true;
            } else {
                return false;
            }
        };
        $scope.getTotalSum = function() {
            var totalSum = 0;

            for (var i = 0; i < $scope.inStockList.length; i++) {
                var currStock = $scope.inStockList[i];
                    totalSum += currStock.productPrice * currStock.quantity;               
            }

            return Math.round(totalSum * 100) / 100;
        }; 

        $scope.printStickersStocks = function() {
            InStockService.printStickersForStocks($scope.selectedStore.id).then(
                    function(response) {
                    }
                );     
        }; 

        $scope.printStickersStocksV2 = function() {
            InStockService.printStickersForStocksV2($scope.selectedStore.id).then(
                    function(response) {
                    }
                );     
        };     
        
	});