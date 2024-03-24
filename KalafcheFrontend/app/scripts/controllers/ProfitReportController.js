'use strict';

angular.module('kalafcheFrontendApp')
    .directive('profitReport', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/profit/profit-report.html',
            controller: ProfitReportController
        }
    });

    function ProfitReportController($scope, $rootScope, $mdDialog, ApplicationService, SaleService, AuthService,  StoreService, ProfitService, ModelService, SessionService) {

       init();

        function init() {
            $scope.currentPage = 1;  
            $scope.reportsPerPage = 50;
            $scope.reports = []; 
            
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

        $scope.getProfitReport = function() {
            ProfitService.getProfitReport($scope.startDateMilliseconds, $scope.endDateMilliseconds).then(function(response) {
                $scope.reports = response;
            });    
        }

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

        $scope.getTimestamp = function(timestamp) {
            return ApplicationService.convertEpochToTimestamp(timestamp);
        };

        $scope.expand = function(report) {
            report.expanded = !report.expanded;
        };
    };

