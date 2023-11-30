'use strict';

angular.module('kalafcheFrontendApp')
    .directive('monthlySchedule', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/schedule/monthly-schedule.html',
            controller: MonthlyScheduleController
        }
    });

    function MonthlyScheduleController($scope, $rootScope, $mdDialog, ApplicationService, MonthlyScheduleService, AuthService, StoreService, SessionService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.expensesPerPage = 15;
            $scope.monthlySchedule = {}; 
            $scope.stores = [];
            $scope.selectedStore = null;   
            $scope.selectedMonth = null;   
            $scope.monthlySchedule = null;        

            getAllStores(); 
        }

        $scope.searchMonthlySchedule = function() {
           
        }

        $scope.generateMonthlySchedule = function() {
            console.log($scope.selectedStore);
            console.log($scope.selectedStore);
        }

        $scope.searchExpenses = function() {
            console.log("--------- " + $scope.selectedStore.id);
            ExpenseService.searchExpenses($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedStore.id, $scope.selectedType.id).then(function(response) {
                $scope.expenses = response.expenses;
            });    
        }

        $scope.clear = function() {
            $scope.startDate = null;
            $scope.endDate = null;
        };

        $scope.changeStore = function() {
            $scope.searchSales();
        };

        function getAllStores() {
            StoreService.getAllStoresForSaleReport().then(function(response) {
                $scope.stores = response;
            });

        };

        function getExpenseTypes() {
            ExpenseService.getExpenseTypes().then(function(response) {
                $scope.types = response;
            });
        };

        $scope.getTimestamp = function(timestamp) {
            return ApplicationService.convertEpochToTimestamp(timestamp);
        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };

        $scope.expand = function(expense) {
            expense.expanded = !expense.expanded;
        };

        $scope.isAdmin = function() {
            return AuthService.isAdmin();
        };
        
        $scope.isManager = function() {
            return AuthService.isManager();
        }

        $scope.isUser = function() {
            return AuthService.isUser();
        }

        $scope.showImage = function(expense){
            $mdDialog.show({
                locals:{imgSrc:"https://drive.google.com/uc?export=view&id=" + expense.fileId},
                controller: function($scope, imgSrc) { $scope.imgSrc = imgSrc; },
                templateUrl: 'views/modals/image-modal.html',
                clickOutsideToClose:true,
                parent: angular.element(document.body)
            })
            .then(function(answer) {
                $scope.status = 'You said the information was "".';
            }, function() {
                $scope.status = 'You cancelled the dialog.';
            });
        };

        $rootScope.$on("ExpenseCreated", function () {
            $scope.searchExpenses();
        })

    };

