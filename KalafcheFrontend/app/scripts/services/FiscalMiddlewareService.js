'use strict';

angular.module('kalafcheFrontendApp')
	.service('FiscalMiddlewareService', function($http, Environment) {
		angular.extend(this, {
			printFiscalReceipt: printFiscalReceipt
		});

        function printFiscalReceipt(sale) { 
            return $http.post(Environment.fiscalMiddlewareEndpoint + '/fiscal/print', sale)
                .then(
                    function(response) {
                        return response.data;
                    }
                )
        }

	});