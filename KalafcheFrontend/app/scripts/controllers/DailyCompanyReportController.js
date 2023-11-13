'use strict';

angular.module('kalafcheFrontendApp')
    .directive('dailyCompanyReport', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/daily-report/daily-company-report.html',
            controller: DailyCompanyReportController,
            controllerAs: 'vm'
        }
    });

    function DailyCompanyReportController($scope, $rootScope, $mdDialog, CompanyService, ApplicationService, DailyStoreReportService, AuthService) {
        var vm = this;
        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.dailyCompanyReportsPerPage = 15;
            $scope.dailyCompanyReports = [];
            $scope.companies = [];
            $scope.selectedCompany = {};
            $scope.startDateMilliseconds = {};
            $scope.endDateMilliseconds = {};
            getAllCompanies();
            getCurrentDate();
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

        function getAllCompanies() {
            CompanyService.getAllCompanies().then(function(response) {
                $scope.companies = response;
            });

        };

         $scope.searchDailyCompanyReports = function() {
            DailyStoreReportService.searchDailyCompanyReports($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedCompany.id).then(function(response) {
                $scope.dailyCompanyReports = response;
            });    
        }

        $scope.getTimestamp = function(timestamp) {
            return ApplicationService.convertEpochToTimestamp(timestamp);
        };

        $scope.expand = function(dailyStoreReport) {
            dailyStoreReport.expanded = !dailyStoreReport.expanded;
        };

        $scope.isAdmin = function() {
            return AuthService.isAdmin();
        };

    };

