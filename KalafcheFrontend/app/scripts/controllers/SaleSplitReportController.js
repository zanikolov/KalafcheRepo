'use strict';

angular.module('kalafcheFrontendApp')
    .directive('saleSplitReport', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/sale-report/split-report.html',
            controller: SaleSplitReportController
        }
    });

    function SaleSplitReportController($scope, $rootScope, SaleService, AuthService, StoreService, SessionService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.daysPerPage = 15;
            $scope.days = []; 
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
                maxDate: new Date() - 1,
                minDate: new Date(2015, 5, 22),
                startingDay: 1,
                showWeeks: false
            };
            $scope.endDateOptions = {
                formatYear: 'yy',
                maxDate: new Date() - 1,
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

        $scope.generateProductTypeSplitInExcel = function() {
            SaleService.getProductTypeSplitReport($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedStore.id).then(
                function(response) {
            });           
        }

        $scope.generateTransactionSplitInExcel = function() {
            SaleService.getTransactionSplitReport($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedStore.id).then(
                function(response) {
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

        $scope.isAdmin = function() {
            return AuthService.isAdmin();
        }
        
        $scope.isManager = function() {
            return AuthService.isManager();
        }

        $scope.isUser = function() {
            return AuthService.isUser();
        }

    };

