'use strict';

angular.module('kalafcheFrontendApp')
	.service('DailyStoreReportService', function($http, Environment) {
		angular.extend(this, {
			searchDailyStoreReports: searchDailyStoreReports,
            calculateDailyStoreReport: calculateDailyStoreReport,
            finalizeDailyStoreReport: finalizeDailyStoreReport,
            isDailyStoreReportCanBeFinalized: isDailyStoreReportCanBeFinalized
		});

        function searchDailyStoreReports(startDateMilliseconds, endDateMilliseconds, storeIds) { 
            var params = {"params" : 
            	{"startDateMilliseconds": startDateMilliseconds, 
            	"endDateMilliseconds": endDateMilliseconds, 
                "storeIds": storeIds}};
            console.log(params);

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/dailyStoreReport', params)
                .then(
                    function(response) {
                        console.log(response.data);
                        return response.data
                    }
                );
        }

        function calculateDailyStoreReport(storeId) {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/dailyStoreReport/current/' + storeId)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function finalizeDailyStoreReport(storeId) {
            return $http.put(Environment.apiEndpoint + '/KalafcheBackend/dailyStoreReport/' + storeId)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
        }

        function isDailyStoreReportCanBeFinalized(storeId) {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/dailyStoreReport/checkFinalization/' + storeId)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
        }

	});