'use strict';

angular.module('kalafcheFrontendApp')
    .controller('SaleReportController', function($scope, ApplicationService, SaleService, KalafcheStoreService, BrandService, ModelService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.salesPerPage = 15;
            $scope.sales = []; 
            $scope.kalafcheStores = [];
            $scope.brands = [];
            $scope.models = [];
            $scope.selectedBrand = {};
            $scope.selectedModel = {};
            $scope.selectedKalafcheStore = {};
            $scope.productCode = "";
            //$scope.displayCostWithDiscounts = true;
            
            $scope.dateFormat = 'dd-MMMM-yyyy';
            $scope.startDate = {};
            $scope.endDate = {};
            $scope.startDateMilliseconds = {};
            $scope.endDateMilliseconds = {};
            $scope.startDatePopup = {opened: false};
            $scope.endDatePopup = {opened: false};

            $scope.warehouseQuantity = 0;
            $scope.companyQuantity = 0;
            $scope.isQuantitiesVisible = false;

            getCurrentDate();
            $scope.startDateOptions = {
                formatYear: 'yy',
                maxDate: new Date(),
                minDate: new Date(2015, 5, 22),
                startingDay: 1,
                showWeeks: false
            };
            $scope.endDateOptions = {
                formatYear: 'yy',
                maxDate: new Date(2025, 5, 22),
                minDate: $scope.startDate,
                startingDay: 1,
                showWeeks: false
            };

            getAllRealStores();
            getAllBrands();
            getAllDeviceModels();   
        }

        function getCurrentDate() {
            $scope.startDate = new Date();
            $scope.startDate.setHours(0);
            $scope.startDate.setMinutes(0);
            $scope.startDateMilliseconds = $scope.startDate.getTime();
            $scope.endDate = new Date();
            $scope.endDate.setHours(23);
            $scope.endDate.setMinutes(59);
            $scope.endDateMilliseconds = $scope.endDate.getTime();

        };

        function getAllBrands() {
            BrandService.getAllDeviceBrands().then(function(response) {
                $scope.brands = response;
            });
        };

        function getAllDeviceModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response; 
            });
        };


        $scope.searchSales = function() {
            getSales();         
        }

        function getSales() {
            console.log($scope.selectedKalafcheStore.idendtifiers);
            SaleService.searchSales($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedKalafcheStore.identifiers,
                $scope.selectedBrand.id, $scope.selectedModel.id, $scope.productCode).then(function(response) {
                $scope.sales = response.sales;
                $scope.warehouseQuantity = response.warehouseQuantity;
                $scope.companyQuantity = response.companyQuantity;

                if ($scope.selectedModel.id && $scope.productCode) {
                    $scope.isQuantitiesVisible = true;
                } else {
                    $scope.isQuantitiesVisible = false;
                }
            });           
        }

        $scope.clear = function() {
            $scope.startDate = null;
            $scope.endDate = null;
        };

        $scope.changeStartDate = function() {
            $scope.endDateOptions.minDate = $scope.startDate;
            $scope.startDate.setHours(0);
            $scope.startDate.setMinutes(1);
            $scope.startDateMilliseconds = $scope.startDate.getTime();

            //$scope.searchSales();
        };

        $scope.changeEndDate = function() {
            $scope.endDate.setHours(23);
            $scope.endDate.setMinutes(59);
            $scope.endDateMilliseconds = $scope.endDate.getTime();

            //$scope.searchSales();
        };

        $scope.changeKalafcheStore = function() {
            $scope.searchSales();
        };

        // $scope.changeDisplayCostWithDiscounts = function() {
        //     console.log(">>>>> " + $scope.displayCostWithDiscounts);
        // }

        $scope.openStartDatePopup = function() {
            $scope.startDatePopup.opened = true;
        };

        $scope.openEndDatePopup = function() {
            $scope.endDatePopup.opened = true;
        };

        $scope.filterByPeriod = function () {
            return function predicateFunc(sale) {
                return sale.saleTimestamp >= $scope.startDateMilliseconds && sale.saleTimestamp <= $scope.endDateMilliseconds;
            };
        };

        function getAllRealStores() {
            KalafcheStoreService.getAllRealStores().then(function(response) {
                $scope.kalafcheStores = response;
                $scope.selectedKalafcheStore = KalafcheStoreService.getRealSelectedStore($scope.kalafcheStores, $scope.isAdmin());
                getSales();
            });

        };

        // function getAllSales() {
        //     SaleService.getAllSales().then(function(response) {
        //         $scope.sales = response;
        //     });
        // };



        $scope.getSaleTimestamp = function(sale) {
            var timeStamp = new Date(sale.saleTimestamp);

            var minutes = ApplicationService.getTwoDigitNumber(timeStamp.getMinutes());
            var hh = ApplicationService.getTwoDigitNumber(timeStamp.getHours())
            var dd = ApplicationService.getTwoDigitNumber(timeStamp.getDate());
            var mm = ApplicationService.getTwoDigitNumber(timeStamp.getMonth() + 1); //January is 0!
            var yyyy = timeStamp.getFullYear();

            return dd + "-" + mm + "-" + yyyy + " " + hh + ":" + minutes;
        };

        $scope.getTotalSum = function() {
            var totalSum = 0;

            for (var i = 0; i < $scope.sales.length; i++) {
                var currSale = $scope.sales[i];

                if ((!$scope.selectedKalafcheStore.id || currSale.kalafcheStoreId === $scope.selectedKalafcheStore.id) && currSale.saleTimestamp >= $scope.startDateMilliseconds && currSale.saleTimestamp <= $scope.endDateMilliseconds) {
                    totalSum += currSale.salePrice;
                }
            }

            return Math.round(totalSum * 100) / 100;
        };

        $scope.isTotalSumRowVisible = function() {
            if ($scope.salesPerPage * $scope.currentPage >= $scope.sales.length) {
                return true;
            } else {
                return false;
            }
        };

        $scope.filterByProductCode = function() {
            var productCodesString = $scope.productCode;
            var productCodes = productCodesString.split(" ");
            return function predicateFunc(sale) {
                return productCodes.indexOf(sale.itemProductCode) !== -1 ;
            };
        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };
    });