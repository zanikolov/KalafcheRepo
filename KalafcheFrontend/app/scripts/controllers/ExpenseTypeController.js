'use strict';

angular.module('kalafcheFrontendApp')

    .directive('expenseType', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/expense/type.html',
            controller: ExpenseTypeController
        }
    });

    function ExpenseTypeController ($scope, ExpenseService, AuthService, ServerValidationService, TaxService) {

        init();

        function init() {
            $scope.expenseType = {}; 
            $scope.expenseTypes = [];
            $scope.currentPage = 1; 
            $scope.expenseTypesPerPage = 20;
            $scope.serverErrorMessages = {};

            getTaxes();
            getExpenseTypes();
        }

        $scope.resetExpenseType = function () {
            $scope.expenseType = null;
        };

        function getExpenseTypes() {
            ExpenseService.getExpenseTypes().then(function(response) {
                $scope.expenseTypes = response;
            });
        };

        function getTaxes() {
            TaxService.getTaxes().then(function(response) {
                $scope.taxes = response;
            });
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };

        $scope.resetExpenseTypeForm = function() {
            $scope.resetExpenseType();
            $scope.resetServerErrorMessages();
            $scope.expenseTypeForm.$setPristine();
            $scope.expenseTypeForm.$setUntouched();
        };

        $scope.editExpenseType = function(expenseType) {
            $scope.expenseType = angular.copy(expenseType);
        };

        $scope.submitExpenseType = function() {
            ExpenseService.submitExpenseType($scope.expenseType).then(function(response) {
                $scope.resetExpenseTypeForm();
                getExpenseTypes();
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.expenseTypeForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.isSuperAdmin = function() {
            return AuthService.isSuperAdmin();
        }

    };