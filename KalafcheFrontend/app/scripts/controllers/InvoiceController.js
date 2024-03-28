'use strict';

angular.module('kalafcheFrontendApp')
    .directive('invoice', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/invoice/invoice.html',
            controller: InvoiceController
        }
    });

    function InvoiceController($scope, $rootScope, $mdDialog, ApplicationService, InvoiceService, AuthService, CompanyService, SessionService) {

        init();

        function init() {
            $scope.companies = [];
            $scope.selectedCompany = {};
            
            $scope.dateFormat = 'dd-MMMM-yyyy';
            $scope.startDate = {};
            $scope.endDate = {};
            $scope.startDateMilliseconds = {};
            $scope.endDateMilliseconds = {};
            $scope.startDatePopup = {opened: false};
            $scope.endDatePopup = {opened: false};

            getCurrentDate();
            $scope.startDateOptions = {
                formatYear: 'yy',
                maxDate: new Date(),
                minDate: new Date(2015, 5, 22),
                startingDay: 1,
                showWeeks: false
            };
            $scope.endDateOptions = {
                formatYear: 'yy',
                maxDate: new Date(2025, 5, 22),
                minDate: $scope.startDate,
                startingDay: 1,
                showWeeks: false
            };

            getAllCompanies(); 
        }

        function getCurrentDate() {
            $scope.startDate = new Date();
            $scope.startDate.setHours(0);
            $scope.startDate.setMinutes(0);
            $scope.startDateMilliseconds = $scope.startDate.getTime();
            $scope.endDate = new Date();
            $scope.endDate.setHours(23);
            $scope.endDate.setMinutes(59);
            $scope.endDateMilliseconds = $scope.endDate.getTime();

        };

        $scope.generateInvoice = function() {
            InvoiceService.generateInvoice($scope.selectedCompany.id, $scope.startDateMilliseconds, $scope.endDateMilliseconds).then(
                    function(response) {
                    }
                );     
        };

        $scope.changeStartDate = function() {
            $scope.endDateOptions.minDate = $scope.startDate;
            $scope.startDate.setHours(0);
            $scope.startDate.setMinutes(1);
            $scope.startDateMilliseconds = $scope.startDate.getTime();
        };

        $scope.changeEndDate = function() {
            $scope.endDate.setHours(23);
            $scope.endDate.setMinutes(59);
            $scope.endDateMilliseconds = $scope.endDate.getTime();
        };

        function getAllCompanies() {
            CompanyService.getAllCompanies().then(function(response) {
                $scope.companies = response;
            });

        };

    };

