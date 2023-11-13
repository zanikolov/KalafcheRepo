'use strict';

angular.module('kalafcheFrontendApp')
	.service('DailyStoreReportService', function($http, Environment) {
		angular.extend(this, {
			searchDailyStoreReports: searchDailyStoreReports,
            searchDailyCompanyReports: searchDailyCompanyReports,
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

        function searchDailyCompanyReports(startDateMilliseconds, endDateMilliseconds, companyId) { 
            var params = {"params" : 
                {"startDateMilliseconds": startDateMilliseconds, 
                "endDateMilliseconds": endDateMilliseconds,
                "companyId": companyId}};
            console.log(params);

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/dailyStoreReport/companies', params)
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