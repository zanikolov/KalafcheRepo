'use strict';

angular.module('kalafcheFrontendApp')
    .directive('workingShift', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/schedule/working-shift.html',
            controller: WorkingShiftController
        }
    });

    function WorkingShiftController($scope, $rootScope, WorkingShiftService, ApplicationService, AuthService, SessionService) {

       init();

        function init() {
            $scope.workingShiftsPerPage = 15;
            $scope.currentPage = 1;
            $scope.workingShifts = [];
            $scope.workingShift = null;
            //getAllWorkingShifts();
        }

        function getAllWorkingShifts() {
            WorkingShiftService.getAllWorkingShifts().then(function(response) {
                $scope.workingShifts = response;
            });
        };

        $scope.editWorkingShift = function(workingShift) {
            $scope.workingShift = angular.copy(workingShift);
            var startTime = new Date();
            startTime.setHours($scope.workingShift.startTimeMinutes / 60);
            startTime.setMinutes($scope.workingShift.startTimeMinutes % 60);
            $scope.workingShift.startTime = startTime;

            console.log($scope.workingShift.startTime);
        };

        $scope.resetWorkingShift = function() {
            $scope.workingShift = null;
        };

        $scope.resetWorkingShiftForm = function() {
            $scope.resetWorkingShift();
            $scope.workingShiftForm.$setPristine();
            $scope.workingShiftForm.$setUntouched();
        };

        $scope.submitWorkingShift = function() {
            console.log($scope.workingShift);
            // WorkingShiftService.submitWorkingShift($scope.workingShift).then(function(response) {
            //     $scope.resetWorkingShiftForm();
            //     getAllWorkingShifts();
            // });
        };

        $scope.changeStartTime = function() {
            $scope.workingShift.startTimeMinutes = getTimeInMinutes($scope.workingShift.startTime);
        }

        function getTimeInMinutes(dateTime) {
            return dateTime.getHours()*60 + dateTime.getMinutes();
        }

    };