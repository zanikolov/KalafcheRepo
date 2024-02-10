'use strict';

angular.module('kalafcheFrontendApp')
	.service('EmployeeService', function($http, Environment) {
		angular.extend(this, {
			getAllEmployees: getAllEmployees,
            submitEmployee: submitEmployee,
            getAllActiveEmployees: getAllActiveEmployees,
            getAllActiveEmployeesGroupedByStore: getAllActiveEmployeesGroupedByStore,
            getAllActiveEmployeesByStore: getAllActiveEmployeesByStore
		});

        function submitEmployee(employee) { 
            if (employee.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/employee', employee)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/employee', employee)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

        function getAllEmployees() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/employee')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAllActiveEmployees() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/employee/enabled')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAllActiveEmployeesByStore(storeId) {  
            var params = {"params" : 
                {"storeId": storeId}};
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/employee/store', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAllActiveEmployeesGroupedByStore() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/employee/groupedByStore')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

	});