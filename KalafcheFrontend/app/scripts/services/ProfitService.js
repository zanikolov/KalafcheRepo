'use strict';

angular.module('kalafcheFrontendApp')
	.service('ProfitService', function($http, Environment) {
		angular.extend(this, {
			getProfitReport: getProfitReport
		});

        function getProfitReport(startDateMilliseconds, endDateMilliseconds) { 
            var params = {"params" : 
            	{"startDateMilliseconds": startDateMilliseconds, 
            	"endDateMilliseconds": endDateMilliseconds}};
            console.log(params);

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/profit', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

	});