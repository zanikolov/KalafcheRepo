'use strict';

angular.module('kalafcheFrontendApp')
    .controller('LoyalCustomerController', function($scope, $element, LoyalCustomerService, ApplicationService, BrandService, ModelService, DiscountService, ServerValidationService) {
        
       init();

        function init() {
            $scope.loyalCustomer = {};
            $scope.brands = [];
            $scope.models = [];
            $scope.discountCodes = [];
            $scope.loyalCustomers = [];
            $scope.currentPage = 1; 
            $scope.loyalCustomersPerPage = 15; 

            getAllBrands();
            getAllModels();
            getAvailableDiscountCodes();
            getAllLoyalCustomers();
        }

        function getAllBrands() {
            BrandService.getAllDeviceBrands().then(function(response) {
                $scope.brands = response;
            });
        };

        function getAllModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response; 
            });
        };

        function getAvailableDiscountCodes() {
            DiscountService.getAvailableLoyalDiscountCodes().then(function(response) {
                $scope.discountCodes = response;
            });
        };

        function getAllLoyalCustomers() {
            LoyalCustomerService.getAllLoyalCustomers().then(function(response) {
                $scope.loyalCustomers = response;
            });

        };

        $scope.submitLoyalCustomer = function() {          
            LoyalCustomerService.submitLoyalCustomer($scope.loyalCustomer).then(function(response) {
                $scope.resetLoyalCustomerForm();
                getAllLoyalCustomers();
                getAvailableDiscountCodes();         
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.loyalCustomerForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.openLoyalCustomerForEdit = function (loyalCustomer) {
            $scope.loyalCustomer = angular.copy(loyalCustomer);
        };

        function resetLoyalCustomer() {
            $scope.loyalCustomer = null;
        };

        $scope.resetLoyalCustomerForm = function() {
            resetLoyalCustomer();
            $scope.resetServerErrorMessages();
            $scope.loyalCustomerForm.$setPristine();
            $scope.loyalCustomerForm.$setUntouched();
        };

         $scope.clearModelSearchTerm= function() {
            $scope.modelSearchTerm = "";
        }

        $element.find('#modelSearchTerm').on('keydown', function(ev) {
            ev.stopPropagation();
        });

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };
    });