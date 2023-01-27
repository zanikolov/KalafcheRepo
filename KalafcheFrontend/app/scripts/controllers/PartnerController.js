'use strict';

angular.module('kalafcheFrontendApp')
    .controller('PartnerController', function($scope, PartnerService, PartnerStoreService, DiscountService, ApplicationService, ServerValidationService) {
        
       init();

        function init() {
            $scope.partner = {};
            $scope.partnerStores = [];
            $scope.discountCodes = [];
            $scope.partners = [];
            $scope.currentPage = 1; 
            $scope.partnersPerPage = 15; 

            getAllPartnerStores();
            getAvailableDiscountCodes();
            getAllPartners();
        }

       function getAllPartnerStores() {
            PartnerStoreService.getAllPartnerStores().then(function(response) {
                $scope.partnerStores = response;
            });
        };

       function getAvailableDiscountCodes() {
            DiscountService.getAvailablePartnerDiscountCodes().then(function(response) {
                $scope.discountCodes = response;
            });
        };

        function getAllPartners() {
            PartnerService.getAllPartners().then(function(response) {
                $scope.partners = response;
            });
        };

        $scope.submitPartner = function() {          
            PartnerService.submitPartner($scope.partner).then(function(response) {
                $scope.resetPartnerForm();
                getAllPartners();
                getAvailableDiscountCodes();          
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.partnerForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.openPartnerForEdit = function (partner) {
            $scope.partner = angular.copy(partner);
            //$scope.discountCodes.push($scope.partner.discountCodeCode);
        };

        function resetPartner() {
            $scope.partner = null;
        };

        $scope.resetPartnerForm = function() {
            resetPartner();
            $scope.resetServerErrorMessages();
            $scope.partnerForm.$setPristine();
            $scope.partnerForm.$setUntouched();
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };
    });