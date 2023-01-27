'use strict';

angular.module('kalafcheFrontendApp')
    .directive('saleReportByMonth', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/sale-report/by-month.html',
            controller: MonthlyTurnoverReportController
        }
    });

    function MonthlyTurnoverReportController($scope, ApplicationService, AuthService, SaleService, SessionService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.turnoverByStorePerPage = 25;

            $scope.selectedMonth = {};

            var now = new Date();
            var currYear = now.getFullYear();
            var currMonth = now.getMonth();
            $scope.months = [];
            $scope.monthNames = ["Януари", "Февруари", "Март", "Април", "Май", "Юни", "Юли", "Август", "Септември", "Октомври", "Ноември", "Декември"];
            var year = currYear;
            var month = currMonth;

            while ($scope.months.length < 12) {
                if (month == -1) {
                    month = 11;
                    year--;
                    continue;
                }
                var monthObj = {};
                monthObj.fullName = $scope.monthNames[month] + ", " + year;
                monthObj.value = month + "-" + year ;
                monthObj.name = $scope.monthNames[month];
                monthObj.year = year;
                $scope.months.push(monthObj);
                month--;
            }

            $scope.selectedMonth = $scope.months[0];
        }

        $scope.searchMonthlyTurnover = function(selectedMonth) {
            console.log(selectedMonth);
            getMonthlyTurnover();         
        }

        function getMonthlyTurnover() {
            //console.log($scope.selectedMonth);
            SaleService.getMonthlyTurnover($scope.selectedMonth.value).then(function(response) {
                $scope.report = response;
                $scope.selectedMonthName = $scope.monthNames[response.selectedMonthMonth] + ", " + response.selectedMonthYear;
                $scope.previousMonthName = $scope.monthNames[response.prevMonthMonth] + ", " + response.prevMonthYear;
                $scope.previousYearName = $scope.monthNames[response.prevYearMonth] + ", " + response.prevYearYear;
            });           
        }

        

        $scope.getReportDate = function(reportTimestamp) {
            return ApplicationService.convertEpochToDate(reportTimestamp)
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

    };

