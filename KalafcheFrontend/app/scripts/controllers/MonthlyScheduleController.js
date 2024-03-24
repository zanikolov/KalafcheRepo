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

    function MonthlyScheduleController($scope, $rootScope, MonthlyScheduleService, StoreService, EmployeeService, AuthService, WorkingShiftService) {

        init();

        function init() {
            $scope.stores = [];
            $scope.employees = [];
            $scope.workingShifts = [];
            $scope.selectedStore = null;   
            $scope.selectedMonth = null; 
            $scope.selectedEmployees = [];  
            $scope.monthlySchedule = null;
            $scope.isFinalizeButtonVisible = false; 
            $scope.isGenerateButtonVisible = false;
            $scope.loading = false;    
            var now = new Date();  
            $scope.currentMonth = new Date(now.getFullYear(), now.getMonth(), 1);

            getAllStores();
            getAllWorkingShifts();
            getAllActiveEmployeesGroupedByStore();
        }

        $scope.searchMonthlySchedule = function() {
            if ($scope.selectedMonth != null && $scope.selectedStore != null) {
                MonthlyScheduleService.searchMonthlySchedule($scope.selectedStore.id, $scope.selectedMonth.getMonth() + 1, $scope.selectedMonth.getFullYear()).then(function(response) {
                    $scope.monthlySchedule = response;
                    $scope.selectedEmployees = [];
                    checkFinalizeButtonVisibility();
                    checkGenerateButtonVisibility();
                }); 
            }
        }

        $scope.generateMonthlySchedule = function() {
            $scope.loading = true;
            var employeesHours = [];
            angular.forEach($scope.selectedEmployees, function(employee, key) {
                employeesHours.push({"employee": employee, "hours": "00:00"});
            });
            var body = {"storeId": $scope.selectedStore.id, 
                "month": $scope.selectedMonth.getMonth() + 1, 
                "year": $scope.selectedMonth.getFullYear(),
                "employeesHours": employeesHours};
            MonthlyScheduleService.generateMonthlySchedule(body).then(
                function(response) {
                    $scope.monthlySchedule = response;
                    checkFinalizeButtonVisibility();
                    checkGenerateButtonVisibility();
                    $scope.loading = false;
                },
                function(error) {
                    $scope.loading = false;
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
                    response[i].displaynName = $scope.generateWorkingShiftDisplayName(response[i].id, response[i].name, response[i].startTime, response[i].endTime, response[i].duration);
                }
                $scope.workingShifts = response;
            });
        };

        $scope.generateWorkingShiftDisplayName = function(id, name, startTime, endTime, duration) {
            if (id == null) {
                return "";
            }

            return name 
                + '  (' + startTime
                + '-' + endTime + ') ' + duration;
        }

        function getAllActiveEmployeesGroupedByStore() {
            EmployeeService.getAllActiveEmployeesGroupedByStore().then(function(response) {
                $scope.employees = response;
            });
        };

        $scope.updateDailyShift = function(dailyShift) {
            $scope.loading = true;
            MonthlyScheduleService.updateDailyShift(dailyShift).then(
                function(response) {
                    $scope.monthlySchedule.employeesHours = response;
                    checkFinalizeButtonVisibility();
                    $scope.loading = false;
                },
                function(error) {
                    $scope.loading = false;
                }); 
        }

        $scope.finalizeMonthlySchedule = function() {
            $scope.loading = true;
            var body = {"id": $scope.monthlySchedule.id}
            MonthlyScheduleService.finalizeMonthlySchedule(body).then(
                function() {    
                    $scope.searchMonthlySchedule();              
                    $scope.loading = false;
                },
                function(error) {
                    $scope.loading = false;
                });
        }

        $scope.clearWorkingShiftSearchTerm = function() {
            $scope.workingShiftSearchTerm = "";
        }

        $scope.selectEmployee = function() {
            checkGenerateButtonVisibility();
        }

        function checkFinalizeButtonVisibility() {
            if ($scope.monthlySchedule != '' && $scope.monthlySchedule != null && !$scope.monthlySchedule.isFinalized) {
                if ($scope.monthlySchedule.employeesHours != null) {
                    var hasEmployeeWithOvertime = false;
                    angular.forEach($scope.monthlySchedule.employeesHours, function(employee, key) {
                        if (employee.overtimeInMinutes != null) {
                            hasEmployeeWithOvertime = true;
                            return;
                        }
                    });
                }

                if (!hasEmployeeWithOvertime) {
                    $scope.isFinalizeButtonVisible = true;
                } else {
                    $scope.isFinalizeButtonVisible = false;
                }
            } else {
                $scope.isFinalizeButtonVisible = false;
            }
        };


        function checkGenerateButtonVisibility() {
            if (($scope.monthlySchedule == '' || $scope.monthlySchedule == null) 
                && $scope.selectedMonth != null && $scope.selectedStore != null 
                && $scope.selectedEmployees != null && $scope.selectedEmployees.length > 0 
                && $scope.selectedMonth >= $scope.currentMonth) {
                $scope.isGenerateButtonVisible = true;
            } else {
                $scope.isGenerateButtonVisible = false;
            }
        };

        $scope.isUser = function() {
            return AuthService.isUser();
        }

    };

