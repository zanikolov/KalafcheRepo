'use strict';

angular.module('kalafcheFrontendApp')
    .controller('EmployeeController', function($scope, EmployeeService, StoreService, ApplicationService) {
        
        init();

        function init() {
            $scope.employee = {};
            $scope.stores = [];
            $scope.employees = [];
            $scope.currentPage = 1; 
            $scope.employeesPerPage = 15; 

            getAllStores();
            getAllEmployees();
        }

        function getAllStores() {
            StoreService.getAllStores().then(function(response) {
                $scope.stores = response;
                console.log($scope.stores);
            });

        };

        function getAllEmployees() {
            EmployeeService.getAllEmployees().then(function(response) {
                $scope.employees = response;
            });

        };

        $scope.submitEmployee = function() {          
            EmployeeService.submitEmployee($scope.employee).then(function(response) {
                $scope.resetEmployeeForm();
                getAllEmployees();           
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.employeeForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.openEmployeeForEdit = function (employee) {
            $scope.employee = angular.copy(employee);
        };

        function resetEmployee() {
            $scope.employee = null;
        };

        $scope.resetEmployeeForm = function() {
            resetEmployee();
            $scope.resetServerErrorMessages();
            $scope.employeeForm.$setPristine();
            $scope.employeeForm.$setUntouched();
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };

    });