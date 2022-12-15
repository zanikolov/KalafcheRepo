'use strict';

angular.module('kalafcheFrontendApp')
	.service('ExpenseService', function($http, Environment) {
		angular.extend(this, {
			submitExpense: submitExpense,
			searchExpenses: searchExpenses,
            getExpenseTypes: getExpenseTypes,
            submitExpenseType: submitExpenseType
		});

        function submitExpense(expense, file) {
            var fileFormData = new FormData();
            fileFormData.append('expenseImage', file);
            fileFormData.append('price', expense.price);
            fileFormData.append('typeCode', expense.typeCode);
            fileFormData.append('description', expense.description);
            fileFormData.append('storeId', expense.storeId);

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/expense', fileFormData, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
 
            }).then(function (response) {
                return response.data;
            })
        }

        function searchExpenses(startDateMilliseconds, endDateMilliseconds, storeIds) { 
            var params = {"params" : 
            	{"startDateMilliseconds": startDateMilliseconds, 
            	"endDateMilliseconds": endDateMilliseconds, 
                "storeIds": storeIds}};
            console.log(params);

            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/expense', params)
                .then(
                    function(response) {
                        console.log(response.data);
                        return response.data
                    }
                );
        }


        function getExpenseTypes() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/expense/type')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function submitExpenseType(expenseType) {
            if (expenseType.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/expense/type', expenseType)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/expense/type', expenseType)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

	});