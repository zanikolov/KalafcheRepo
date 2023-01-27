'use strict';

angular.module('kalafcheFrontendApp')
    .controller('RefundReportController', function ($scope, $rootScope, ApplicationService, RefundService, AuthService,  StoreService, BrandService, ModelService, SessionService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.refundsPerPage = 15;
            $scope.refunds = []; 
            $scope.stores = [];
            $scope.selectedStore = {};
            
            $scope.dateFormat = 'dd-MMMM-yyyy';
            $scope.startDate = {};
            $scope.endDate = {};
            $scope.startDateMilliseconds = {};
            $scope.endDateMilliseconds = {};
            $scope.startDatePopup = {opened: false};
            $scope.endDatePopup = {opened: false};

            getCurrentDate();
            getAllBrands();
            getAllModels();  
            getAllStores(); 
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

        function getAllModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response; 
            });
        };

        $scope.searchRefunds = function() {
            RefundService.searchRefunds($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedStore.id, $scope.selectedBrand.id, $scope.selectedModel.id, $scope.productCode).then(function(response) {
                $scope.refunds = response;
            });        
        }

        $scope.clear = function() {
            $scope.startDate = null;
            $scope.endDate = null;
        };

        $scope.changeStartDate = function() {
            $scope.startDate.setHours(0);
            $scope.startDate.setMinutes(1);
            $scope.startDateMilliseconds = $scope.startDate.getTime();
        };

        $scope.changeEndDate = function() {
            $scope.endDate.setHours(23);
            $scope.endDate.setMinutes(59);
            $scope.endDateMilliseconds = $scope.endDate.getTime();
        };

        $scope.changeStore = function() {
            $scope.searchRefunds();
        };

        function getAllStores() {
            StoreService.getAllStoresForSaleReport().then(function(response) {
                $scope.stores = response;
                $scope.selectedStore =  {"id": SessionService.currentUser.employeeStoreId};
                $scope.searchRefunds();
            });

        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };

        $scope.expand = function(refund) {
            refund.expanded = !refund.expanded;
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

    });

