'use strict';

angular.module('kalafcheFrontendApp')
    .directive('monthlySchedule', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/schedule/monthly-schedule.html',
            controller: MonthlyScheduleController
        }
    });

    function MonthlyScheduleController($scope, $rootScope, MonthlyScheduleService, StoreService, EmployeeService, WorkingShiftService) {

        init();

        function init() {
            $scope.stores = [];
            $scope.employees = [];
            $scope.workingShifts = [];
            $scope.selectedStore = null;   
            $scope.selectedMonth = null; 
            $scope.selectedEmployees = [];  
            $scope.monthlySchedule = null;        

            getAllStores();
            getAllWorkingShifts();
            getAllActiveEmployeesGroupedByStore();
        }

        $scope.searchMonthlySchedule = function() {
            if ($scope.selectedMonth != null && $scope.selectedStore != null) {
                MonthlyScheduleService.searchMonthlySchedule($scope.selectedStore.id, $scope.selectedMonth.getMonth() + 1, $scope.selectedMonth.getFullYear()).then(function(response) {
                    $scope.monthlySchedule = response;
                }); 
            }
        }

        $scope.generateMonthlySchedule = function() {
            var employeesHours = [];
            angular.forEach($scope.selectedEmployees, function(employee, key) {
                employeesHours.push({"employee": employee, "hours": "00:00"});
            });
            var body = {"storeId": $scope.selectedStore.id, 
                "month": $scope.selectedMonth.getMonth() + 1, 
                "year": $scope.selectedMonth.getFullYear(),
                "employeesHours": employeesHours}
            MonthlyScheduleService.generateMonthlySchedule(body).then(function(response) {
                $scope.monthlySchedule = response;
            }); 
        }

        function getAllStores() {
            StoreService.getAllStoresForSaleReport().then(function(response) {
                $scope.stores = response;
            });
        };

        function getAllWorkingShifts() {
            WorkingShiftService.getAllWorkingShifts().then(function(response) {
                for (var i = 0; i < response.length; i++) {
                    response[i].displaynName = $scope.generateWorkingShiftDisplayName(response[i].id, response[i].name, response[i].startTime, response[i].endTime);
                }
                $scope.workingShifts = response;
            });
        };

        $scope.generateWorkingShiftDisplayName = function(id, name, startTime, endTime) {
            if (id == null) {
                return "";
            }

            return name 
                + '  (' + startTime
                + '-' + endTime + ')';
        }

        function getAllActiveEmployeesGroupedByStore() {
            EmployeeService.getAllActiveEmployeesGroupedByStore().then(function(response) {
                $scope.employees = response;
            });

        };

        $scope.updateDailyShift = function(dailyShift) {
            MonthlyScheduleService.updateDailyShift(dailyShift).then(function(response) {
                $scope.monthlySchedule.employeesHours = response;
            }); 
        }

        $scope.finalizeMonthlySchedule = function() {
            var body = {"id": $scope.monthlySchedule.id}
            MonthlyScheduleService.finalizeMonthlySchedule(body).then(function() {    
                $scope.searchMonthlySchedule();              
            });
        }

        $scope.clearWorkingShiftSearchTerm= function() {
            $scope.workingShiftSearchTerm = "";
        }

    };

