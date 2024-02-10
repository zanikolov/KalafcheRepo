'use strict';

angular.module('kalafcheFrontendApp')

    .directive('publicHoliday', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/schedule/public-holiday.html',
            controller: PublicHolidayController
        }
    });

    function PublicHolidayController ($scope, CalendarService) {

        init();

        function init() {
            $scope.publicHolidays = [];
            $scope.currentPage = 1; 
            $scope.publicHolidaysPerPage = 50;

            getPublicHolidays();
        }

        function getPublicHolidays() {
            CalendarService.getPublicHolidays().then(function(response) {
                $scope.publicHolidays = response;
            });
        };
    };