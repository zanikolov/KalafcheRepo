'use strict';

angular.module('kalafcheFrontendApp')
    .directive('formulaForm', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/formula/formula-form.html',
            controller: FormulaFormController
        }
    });


	function FormulaFormController ($scope, $rootScope, FormulaService, StoreService, ServerValidationService, SessionService, AuthService) {

        init();

       function init() {
            $scope.formula = {}; 
            $scope.formulas = [];
            $scope.currentPage = 1; 
            $scope.formulasPerPage = 50;
            $scope.serverErrorMessages = {};

            getAllFormulas();
        }

        function getAllFormulas() {
            FormulaService.getAllFormulas().then(function(response) {
                $scope.formulas = response;
            });
        };

        $scope.resetFormula = function () {
            $scope.formula = null;
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };

        $scope.editFormula = function (formula) {
            $scope.formula = angular.copy(formula);
        };

        $scope.resetFormulaForm = function() {
            $scope.resetFormula();
            $scope.resetServerErrorMessages();
            $scope.formulaForm.$setPristine();
            $scope.formulaForm.$setUntouched();
        };

        $scope.submitFormula = function() {
            console.log($scope.formula);
            FormulaService.submitFormula($scope.formula).then(function(response) {
                $scope.resetFormulaForm();
                getAllFormulas();
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.formulaForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.expand = function(formula) {
            if (!formula.expanded){
                calculate(formula);
            }
            formula.expanded = !formula.expanded;
            console.log(formula);
        };

        function calculate(formula) {
            //$scope.calculationInProcess = true;
            FormulaService.calculate(formula).then(
                function(response) {
                    //$scope.calculationInProcess = false;
                    formula.result = response;
                }
                // , function(errorResponse) {
                //     //$scope.calculationInProcess = false;
                // }
            );
        };

        $scope.filterByFormulaName = function() {
            return function predicateFunc(product) {
                return $scope.attributeName == $scope.attribute.name;
            };
        };


        // function init() {
        //     $scope.formula = {};
        //     $scope.formula.variables = [];
        //     $scope.calculationInProcess = false;
        //     $scope.calculationResponse = {};

        //     getAllStores();
        // };

        // $scope.addNewVariable = function() {
        //     var newVariable = {};

        //     newVariable.startDate = new Date();
        //     newVariable.startDate.setHours(0);
        //     newVariable.startDate.setMinutes(0);
        //     newVariable.startDateMilliseconds = newVariable.startDate.getTime();
        //     newVariable.endDate = new Date();
        //     newVariable.endDate.setHours(23);
        //     newVariable.endDate.setMinutes(59);
        //     newVariable.endDateMilliseconds = newVariable.endDate.getTime();

        //     $scope.formula.variables.push(newVariable);
        //     console.log($scope.formula.variables);
        // };

        // $scope.changeStartDate = function(variable) {
        //     variable.startDate.setHours(0);
        //     variable.startDate.setMinutes(1);
        //     variable.startDateMilliseconds = variable.startDate.getTime();
        // };

        // $scope.changeEndDate = function(variable) {
        //     variable.endDate.setHours(23);
        //     variable.endDate.setMinutes(59);
        //     variable.endDateMilliseconds = variable.endDate.getTime();
        // };

        // function getAllStores() {
        //     StoreService.getAllStores().then(function(response) {
        //         $scope.stores = response;
        //         $scope.formula.storeId = SessionService.currentUser.employeeStoreId;
        //     });

        // };

        // $scope.calculateFormula = function() {
        //     $scope.calculationInProcess = true;
        //     FormulaService.calculate($scope.formula).then(
        //         function(response) {
        //             $scope.calculationInProcess = false;
        //             $scope.calculationResponse = response;
        //             //resetExpenseForm();
        //         }, function(errorResponse) {
        //             $scope.calculationInProcess = false;
        //             // ServerValidationService.processServerErrors(errorResponse, $scope.expenseForm);
        //             // $scope.serverErrorMessages = errorResponse.data.errors;
        //             // $rootScope.$emit("ExpenseCreated");
        //         }
        //     );
        // };

        // // function resetFormulaForm() {
        // //     $scope.expense = {};
        // //     $scope.expense.storeId = SessionService.currentUser.employeeStoreId
        // //     $scope.image = null;
        // //     $scope.filepreview = null;
        // //     $scope.serverErrorMessages = {};
        // //     $scope.expenseForm.$setPristine();
        // //     $scope.expenseForm.$setUntouched();
        // // };


        // $scope.isAdmin = function() {
        //     return AuthService.isAdmin();
        // }
        
        // $scope.isManager = function() {
        //     return AuthService.isManager();
        // }

        // $scope.isUser = function() {
        //     return AuthService.isUser();
        // }

	};