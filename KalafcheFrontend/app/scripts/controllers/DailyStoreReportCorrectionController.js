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

            searchDailyStoreReportsForCorrection();

        };

        function searchDailyStoreReportsForCorrection() {
            DailyStoreReportService.searchDailyStoreReportsForCorrection().then(function(response) {
                $scope.dailyStoreReportsForCorrection = response;
            });    
        };

        $scope.updateDailyStoreReport = function(report) {
            DailyStoreReportService.updateDailyStoreReport(report).then(function(response) {
                searchDailyStoreReportsForCorrection();
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

