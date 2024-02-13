'use strict';

angular.module('kalafcheFrontendApp')
	.service('FormulaService', function($http, Environment, FileSaver) {
		angular.extend(this, {
            calculate: calculate
		});

        function calculate(formula) { 

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/formula', formula)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

	});