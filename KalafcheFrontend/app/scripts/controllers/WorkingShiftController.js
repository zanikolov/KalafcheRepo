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
            $scope.now = new Date();
            getAllWorkingShifts();
        }

        function getAllWorkingShifts() {
            WorkingShiftService.getAllWorkingShifts().then(function(response) {
                $scope.workingShifts = response;
            });
        };

        $scope.editWorkingShift = function(workingShift) {
            $scope.workingShift = angular.copy(workingShift);   
            $scope.workingShift.startTime = $scope.convertMinutesToTime($scope.workingShift.startTimeMinutes);
            $scope.workingShift.endTime = $scope.convertMinutesToTime($scope.workingShift.endTimeMinutes);
            $scope.workingShift.duration = $scope.convertMinutesToTime($scope.workingShift.durationMinutes);
        };

        $scope.convertMinutesToTime = function(minutes) {
            var time = angular.copy($scope.now);
            time.setHours(minutes / 60);
            time.setMinutes(minutes % 60);
            time.setSeconds(0);
            time.setMilliseconds(0);

            return time;
        }

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
            WorkingShiftService.submitWorkingShift($scope.workingShift).then(function(response) {
                $scope.resetWorkingShiftForm();
                getAllWorkingShifts();
            });
        };

        $scope.changeStartTime = function() {
            $scope.workingShift.startTimeMinutes = getTimeInMinutes($scope.workingShift.startTime);
        }

        $scope.changeEndTime = function() {
            $scope.workingShift.endTimeMinutes = getTimeInMinutes($scope.workingShift.endTime);
        }

        $scope.changeDuration = function() {
            $scope.workingShift.durationMinutes = getTimeInMinutes($scope.workingShift.duration);
        }

        function getTimeInMinutes(dateTime) {
            if (dateTime != null && dateTime.getHours() != null && dateTime.getMinutes() != null) {
                return dateTime.getHours()*60 + dateTime.getMinutes();
            }

            return null;
        }

    };