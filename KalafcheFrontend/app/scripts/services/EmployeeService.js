'use strict';

angular.module('kalafcheFrontendApp')
	.service('EmployeeService', function($http, Environment) {
		angular.extend(this, {
			getAllEmployees: getAllEmployees,
            submitEmployee: submitEmployee,
            deactivateAccount: deactivateAccount,
            updateEmployee: updateEmployee,
            getAllActiveEmployees: getAllActiveEmployees

		});

        function submitEmployee(employee) {   
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/service/employee', employee)
                .then(
                    function(response) {
                        console.log(response);
                    }
                )
        }

        function getAllEmployees() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/service/employee/all')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAllActiveEmployees() {  
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/service/employee/enabled')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function deactivateAccount(userId) { 
            return $http.put(Environment.apiEndpoint + '/KalafcheBackend/service/employee/disable', userId)
                .then(
                    function(response) {
                        console.log(response);
                    }
                );
        }

        function updateEmployee(employee) { 
            return $http.put(Environment.apiEndpoint + '/KalafcheBackend/service/employee', employee)
                .then(
                    function(response) {
                        console.log(response);
                    }
                );
        }

	});