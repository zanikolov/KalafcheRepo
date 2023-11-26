'use strict';

angular.module('kalafcheFrontendApp')
    .directive('dailyStoreReportCorrection', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/daily-report/daily-store-report-correction.html',
            controller: DailyStoreReportCorrectionController,
            controllerAs: 'vm'
        }
    });

    function DailyStoreReportCorrectionController($scope, $rootScope, $mdDialog, ApplicationService, DailyStoreReportService, AuthService, StoreService, SessionService) {
        var vm = this;
        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.dailyStoreReportsPerPage = 30;
            $scope.dailyStoreReportsForCorrection = []; 
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
        };

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

        function getAllStores() {
            StoreService.getAllStoresForSaleReport().then(function(response) {
                $scope.stores = response;
            });

        }

        $scope.searchDailyStoreReportsForCorrection = function() {
            DailyStoreReportService.searchDailyStoreReportsForCorrection($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedStore.id).then(function(response) {
                $scope.dailyStoreReportsForCorrection = response;
            });    
        };

        $scope.updateDailyStoreReport = function(report) {
            DailyStoreReportService.updateDailyStoreReport(report).then(function(response) {
                $scope.searchDailyStoreReportsForCorrection();
            });    
        };

        $scope.getTimestamp = function(timestamp) {
            return ApplicationService.convertEpochToTimestamp(timestamp);
        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };

        $scope.expand = function(report) {
            report.expanded = !report.expanded;
        };

    };

