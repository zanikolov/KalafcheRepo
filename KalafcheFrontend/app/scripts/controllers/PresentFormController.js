'use strict';

angular.module('kalafcheFrontendApp')
    .directive('presentForm', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/schedule/present-form.html',
            controller: PresentFormController
        }
    });

    function PresentFormController($scope, $rootScope, MonthlyScheduleService, StoreService, EmployeeService, WorkingShiftService, AuthService) {

        init();

        function init() {
            $scope.stores = [];
            $scope.workingShifts = [];
            $scope.selectedStore = null;   
            $scope.selectedMonth = null;  
            $scope.presentForm = null;
            $scope.isFinalizeButtonVisible = false;    
            $scope.loading = false;  

            getAllStores();
            getAllWorkingShifts();
        }

        $scope.searchPresentForm = function() {
            if ($scope.selectedMonth != null && $scope.selectedStore != null) {
                MonthlyScheduleService.searchPresentForm($scope.selectedStore.id, $scope.selectedMonth.getMonth() + 1, $scope.selectedMonth.getFullYear()).then(function(response) {
                    $scope.presentForm = response;
                    checkFinalizeButtonVisibility();
                }); 
            }
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

        $scope.updateDailyShift = function(dailyShift) {
            $scope.loading = true;
            MonthlyScheduleService.updateDailyShift(dailyShift).then(
                function(response) {
                    $scope.presentForm.employeesHours = response;
                    checkFinalizeButtonVisibility();
                    $scope.loading = false;
                },
                function(error) {
                    $scope.loading = false;
                }); 
        }

        $scope.finalizePresentForm = function() {
            $scope.loading = true;
            var body = {"id": $scope.presentForm.id}
            MonthlyScheduleService.finalizePresentForm(body).then(
                function() {    
                    $scope.searchPresentForm(); 
                    $scope.loading = false;             
                },
                function(error) {
                    $scope.loading = false;
                });
        }

        $scope.clearWorkingShiftSearchTerm= function() {
            $scope.workingShiftSearchTerm = "";
        }

        function checkFinalizeButtonVisibility() {
            if ($scope.presentForm != '' && $scope.presentForm != null && !$scope.presentForm.isFinalized) {
                if ($scope.presentForm.employeesHours != null) {
                    var hasEmployeeWithOvertime = false;
                    angular.forEach($scope.presentForm.employeesHours, function(employee, key) {
                        if (employee.overtimeInMinutes > 1800) {
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

            console.log($scope.isFinalizeButtonVisible);
            console.log($scope.isUser());
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

