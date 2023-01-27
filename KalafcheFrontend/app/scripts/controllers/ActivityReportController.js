'use strict';

angular.module('kalafcheFrontendApp')
    .controller('ActivityReportController', function($scope, ApplicationService, ActivityReportService, EmployeeService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.activitiesPerPage = 15;
            $scope.activities = []; 
            $scope.employees = [];
            $scope.selectedEmployee = {};
            
            $scope.dateFormat = 'dd-MMMM-yyyy';
            $scope.activityDate = {};
            $scope.activityDateMilliseconds = {};
            // $scope.endDateMilliseconds = {};
            $scope.activityDatePopup = {opened: false};

            $scope.maxDate = new Date();
            $scope.minDate = new Date(2015, 5, 22);

            getCurrentDate();
            $scope.activityDateOptions = {
                formatYear: 'yy',
                maxDate: new Date(),
                minDate: new Date(2015, 5, 22),
                startingDay: 1,
                showWeeks: false
            };

            getAllEmployees();   
        }

        function getCurrentDate() {
            $scope.activityDate = new Date();
            $scope.activityDate.setHours(0);
            $scope.activityDate.setMinutes(0);
            $scope.activityDateMilliseconds = $scope.activityDate.getTime();
        };

        $scope.changeActivityDate = function() {
            $scope.activityDate.setHours(0);
            $scope.activityDate.setMinutes(1);
            $scope.activityDateMilliseconds = $scope.activityDate.getTime();

            $scope.searchActivities();
        };

        $scope.searchActivities = function() {
           getActivities();         
        }

        function getActivities() {
            console.log($scope.selectedEmployee.id);
            ActivityReportService.searchActivities($scope.activityDateMilliseconds).then(function(response) {
                $scope.activities = response;
                console.log($scope.activities);
            });           
        }

        // $scope.clear = function() {
        //     $scope.startDate = null;
        //     $scope.endDate = null;
        // };

        $scope.changeEmployee = function() {
            $scope.searchSales();
        };

        $scope.openActivityDatePopup = function() {
            $scope.activityDatePopup.opened = true;
        };

        function getAllEmployees() {
            EmployeeService.getAllActiveEmployees().then(function(response) {
                $scope.employees = response;
                getActivities();
            });

        };

        $scope.getActivityTimestamp = function(activityTimestamp) {
            return ApplicationService.convertEpochToTimestamp(activityTimestamp);
        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };
    });