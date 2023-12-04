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

    function PresentFormController($scope, $rootScope, MonthlyScheduleService, StoreService, EmployeeService, WorkingShiftService) {

        init();

        function init() {
            $scope.stores = [];
            $scope.workingShifts = [];
            $scope.selectedStore = null;   
            $scope.selectedMonth = null;  
            $scope.presentForm = null;        

            getAllStores();
            getAllWorkingShifts();
        }

        $scope.searchPresentForm = function() {
            if ($scope.selectedMonth != null && $scope.selectedStore != null) {
                MonthlyScheduleService.searchPresentForm($scope.selectedStore.id, $scope.selectedMonth.getMonth() + 1, $scope.selectedMonth.getFullYear()).then(function(response) {
                    $scope.presentForm = response;
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

        $scope.updateDailyShift = function(dailyShift) {
            MonthlyScheduleService.updateDailyShift(dailyShift).then(function(response) {
                $scope.monthlySchedule.employeesHours = response;
            }); 
        }

        $scope.finalizePresentForm = function() {
            var body = {"id": $scope.presentForm.id}
            MonthlyScheduleService.finalizePresentForm(body).then(function() {    
                $scope.searchPresentForm();              
            });
        }

    };

