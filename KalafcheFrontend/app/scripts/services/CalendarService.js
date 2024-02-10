'use strict';

angular.module('kalafcheFrontendApp')
	.service('CalendarService', function($http, Environment) {
		angular.extend(this, {
            getPublicHolidays: getPublicHolidays
		});

        function getPublicHolidays() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/calendar/publicHoliday')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }
	});