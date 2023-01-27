'use strict';

angular.module('kalafcheFrontendApp')
    .controller('PartnerStoreController', function($scope, PartnerStoreService, ApplicationService, ServerValidationService) {
        
       init();

        function init() {
            $scope.partnerStore = {};
            $scope.partnerStores = [];
            $scope.currentPage = 1; 
            $scope.partnerStoresPerPage = 10; 
            $scope.serverErrorMessages = {};

            getAllPartnerStores();
        }

        function getAllPartnerStores() {
            PartnerStoreService.getAllPartnerStores().then(function(response) {
                $scope.stores = response;
            });

        };

        $scope.submitPartnerStore = function() {          
            PartnerStoreService.submitPartnerStore($scope.partnerStore).then(function(response) {
                $scope.resetPartnerStoreForm();
                getAllPartnerStores();           
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.partnerStoreForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.openPartnerStoreForEdit = function(partnerStore) {
            $scope.partnerStore = angular.copy(partnerStore);
        };

        function resetPartnerStore() {
            $scope.partnerStore = null;
        };

        $scope.resetPartnerStoreForm = function() {
            resetPartnerStore();
            $scope.resetServerErrorMessages();
            $scope.partnerStoreForm.$setPristine();
            $scope.partnerStoreForm.$setUntouched();
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };
    });