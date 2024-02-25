'use strict';

angular.module('kalafcheFrontendApp')
    .directive('printSchedule', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/schedule/print-schedule.html',
            controller: PrintMonthlyScheduleController
        }
    });

    function PrintMonthlyScheduleController($scope, ApplicationService, MonthlyScheduleService, SessionService, AuthService) {

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
            var month = currMonth + 1;

            while ($scope.months.length < 12) {
                if (month == -1) {
                    month = 11;
                    year--;
                    continue;
                }
                var monthObj = {};
                monthObj.fullName = $scope.monthNames[month] + ", " + year;
                monthObj.value = month + 1;
                monthObj.name = $scope.monthNames[month];
                monthObj.year = year;
                $scope.months.push(monthObj);
                month--;
            }

            $scope.selectedMonth = $scope.months[0];
        }

        $scope.printPresentForm = function() {
            MonthlyScheduleService.printPresentForm($scope.selectedMonth).then(
                function(response) {
            });           
        }

        $scope.printMonthlySchedule = function() {
            MonthlyScheduleService.printMonthlySchedule(SessionService.currentUser.employeeStoreId, $scope.selectedMonth).then(
                function(response) {
            });           
        }

        $scope.isAdmin = function() {
            return AuthService.isAdmin();
        }

        $scope.isUser = function() {
            return AuthService.isUser();
        }

    };

