'use strict';

angular.module('kalafcheFrontendApp')
    .controller('WasteReportController', function ($scope, $rootScope, $mdDialog, ApplicationService, WasteService, AuthService, SessionService, StoreService, BrandService, ModelService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.wastesPerPage = 15;
            $scope.wastes = []; 
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

        $scope.searchWastes = function() {
            WasteService.searchWastes($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedStore.id, $scope.selectedBrand.id, $scope.selectedModel.id, $scope.productCode).then(function(response) {
                $scope.wastes = response.wastes;
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
            $scope.searchWastes();
        };

        function getAllStores() {
            StoreService.getAllStoresForSaleReport().then(function(response) {
                $scope.stores = response;
                $scope.selectedStore =  {"id": SessionService.currentUser.employeeStoreId};
                $scope.searchWastes();
            });

        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };

        $scope.expand = function(waste) {
            waste.expanded = !waste.expanded;
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

        $scope.showImage = function(waste){
            $mdDialog.show({
                locals:{imgSrc:"https://drive.google.com/uc?export=view&id=" + waste.fileId},
                controller: function($scope, imgSrc) { $scope.imgSrc = imgSrc; },
                templateUrl: 'views/modals/image-modal.html',
                clickOutsideToClose:true,
                parent: angular.element(document.body)
            })
            .then(function(answer) {
                $scope.status = 'You said the information was "".';
            }, function() {
                $scope.status = 'You cancelled the dialog.';
            });
        };

    });

