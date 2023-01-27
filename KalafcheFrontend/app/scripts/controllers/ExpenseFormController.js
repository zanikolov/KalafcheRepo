'use strict';

angular.module('kalafcheFrontendApp')
    .directive('expenseForm', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/expense/form.html',
            controller: ExpenseFormController
        }
    });


	function ExpenseFormController ($scope, $rootScope, ExpenseService, StoreService, ServerValidationService, SessionService, AuthService) {

        init();

        function init() {
            $scope.expense = {};
            $scope.types = [];
            $scope.submitInProcess = false;

            getExpenseTypes();
            getAllStores();
        };

        function getExpenseTypes() {
            ExpenseService.getExpenseTypes().then(function(response) {
                $scope.types = response;
            });
        };

        function getAllStores() {
            StoreService.getAllStores().then(function(response) {
                $scope.stores = response;
                $scope.expense.storeId = SessionService.currentUser.employeeStoreId;
            });

        };

        $scope.submitExpense = function() {
            $scope.submitInProcess = true;
            var image = $scope.image;
            ExpenseService.submitExpense($scope.expense, image).then(
                function(response) {
                    $scope.submitInProcess = false;
                    resetExpenseForm();
                }, function(errorResponse) {
                    $scope.submitInProcess = false;
                    ServerValidationService.processServerErrors(errorResponse, $scope.expenseForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                    $rootScope.$emit("ExpenseCreated");
                }
            );
        };

        function resetExpenseForm() {
            $scope.expense = {};
            $scope.expense.storeId = SessionService.currentUser.employeeStoreId
            $scope.image = null;
            $scope.filepreview = null;
            $scope.serverErrorMessages = {};
            $scope.expenseForm.$setPristine();
            $scope.expenseForm.$setUntouched();
        };


        $scope.isAdmin = function() {
            return AuthService.isAdmin();
        }
        
        $scope.isManager = function() {
            return AuthService.isManager();
        }

        $scope.isUser = function() {
            return AuthService.isUser();
        }

	};