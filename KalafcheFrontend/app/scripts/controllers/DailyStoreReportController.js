'use strict';

angular.module('kalafcheFrontendApp')
    .controller('DailyStoreReportController', function DailyStoreReportController($scope, $rootScope, $mdDialog, ApplicationService, DailyStoreReportService, AuthService, StoreService, SessionService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.dailyStoreReportsPerPage = 15;
            $scope.dailyStoreReports = []; 
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

        $scope.searchDailyStoreReports = function() {
            DailyStoreReportService.searchDailyStoreReports($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedStore.id).then(function(response) {
                $scope.dailyStoreReports = response;
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

        $scope.changeStore = function() {
            $scope.searchSales();
        };

        function getAllStores() {
            StoreService.getAllStoresForSaleReport().then(function(response) {
                $scope.stores = response;
                $scope.selectedStore =  {"id": SessionService.currentUser.employeeStoreId};
                $scope.searchDailyStoreReports();
            });

        };

        $scope.getTimestamp = function(timestamp) {
            return ApplicationService.convertEpochToTimestamp(timestamp);
        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };

        $scope.expand = function(dailyStoreReport) {
            dailyStoreReport.expanded = !dailyStoreReport.expanded;
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

        $scope.openCurrentDailyStoreReportModal = function () {
            $mdDialog.show({
              locals:{currentDailyStoreReport: DailyStoreReportService.calculateDailyStoreReport($scope.selectedStore.id)},
              controller: 'DailyStoreReportModalController',
              templateUrl: 'views/modals/daily-store-report-modal.html',
              parent: angular.element(document.body)
            })
            .then(function(answer) {
              console.log('>>> first function');
            }, function() {
              console.log('>>> second function');
            });

        };

    });

