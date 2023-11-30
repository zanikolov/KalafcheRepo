'use strict';

angular.module('kalafcheFrontendApp')
	.service('MonthlyScheduleService', function($http, Environment) {
		angular.extend(this, {
			searchMonthlySchedule: searchMonthlySchedule,
            generateMonthlySchedule: generateMonthlySchedule,
            updateMonthlySchedule: updateMonthlySchedule
		});

        function searchMonthlySchedule(storeId, month, year) { 
            var params = {"params" : 
                {"storeId": storeId, 
                "month": month, 
                "year": year}};

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule', params)
                .then(
                    function(response) {
                        console.log(response.data);
                        return response.data
                    }
                );
        }

        function generateMonthlySchedule(storeId, month, year) {
            var params = {"params" : 
                {"storeId": storeId, 
                "month": month, 
                "year": year}};
            return $http.put(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule', params)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
        }

        function updateMonthlySchedule(monthlySchedule) { 
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule', monthlySchedule)
                .then(
                    function(response) {
                        return response.data;
                    }
                )
        }

	});