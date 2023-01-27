'use strict';

angular.module('kalafcheFrontendApp')
    .directive('saleReportByStore', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/sale-report/by-store.html',
            controller: SalesByStoresReportController
        }
    });

    function SalesByStoresReportController($scope, ApplicationService, AuthService, SaleService, StoreService, BrandService, ModelService, ProductService, SessionService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.salesByStorePerPage = 25;
            $scope.saleItems = []; 
            $scope.stores = [];
            $scope.brands = [];
            $scope.models = [];
            $scope.selectedBrand = {};
            $scope.selectedModel = {};
            $scope.selectedStore = {};
            $scope.selectedProductType = {};
            $scope.productCode = "";
            
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

            getAllBrands();
            getAllDeviceModels();
            getAllProductTypes();
            getSalesByStores();
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

        function getAllProductTypes() {
            ProductService.getAllTypes().then(function(response) {
                $scope.productTypes = response;
            });
        };

        $scope.searchSalesByStores = function() {
            getSalesByStores();         
        }

        function getSalesByStores() {
            SaleService.getSalesByStores($scope.startDateMilliseconds, $scope.endDateMilliseconds,
                $scope.selectedBrand.id, $scope.selectedModel.id, $scope.productCode, $scope.selectedProductType.id).then(function(response) {
                $scope.report = response;

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
        };

        $scope.changeEndDate = function() {
            $scope.endDate.setHours(23);
            $scope.endDate.setMinutes(59);
            $scope.endDateMilliseconds = $scope.endDate.getTime();
        };

        $scope.openStartDatePopup = function() {
            $scope.startDatePopup.opened = true;
        };

        $scope.openEndDatePopup = function() {
            $scope.endDatePopup.opened = true;
        };

        $scope.getReportDate = function(reportTimestamp) {
            return ApplicationService.convertEpochToDate(reportTimestamp)
        };

        $scope.filterByProductCode = function() {
            var productCodesString = $scope.productCode;
            var productCodes = productCodesString.split(" ");
            return function predicateFunc(sale) {
                return productCodes.indexOf(sale.productCode) !== -1 ;
            };
        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };
        
        $scope.isAdmin = function() {
            return AuthService.isAdmin();
        }
        
        $scope.isManager = function() {
            return AuthService.isManager();
        }

        $scope.isUser = function() {
            return AuthService.isUser();
        }

        // $scope.generateExcel = function() {
        //     SaleService.generateExcel($scope.report.saleItems, $scope.startDateMilliseconds, $scope.endDateMilliseconds).then(function(response) {
        //         console.log(">>> Success!");
        //     });   
        // }

    };

