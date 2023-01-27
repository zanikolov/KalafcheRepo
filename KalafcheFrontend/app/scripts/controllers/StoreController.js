'use strict';

angular.module('kalafcheFrontendApp')
    .controller('StoreController', function($scope, StoreService, ApplicationService, ServerValidationService) {
        
        init();

        function init() {
            $scope.store = {};
            $scope.stores = [];
            $scope.currentPage = 1; 
            $scope.storesPerPage = 10; 
            $scope.serverErrorMessages = {};

            getAllStores();
        }

        function getAllStores() {
            StoreService.getAllStores().then(function(response) {
                $scope.stores = response;
            });

        };

        $scope.submitStore = function() {          
            StoreService.submitStore($scope.store).then(function(response) {
                $scope.resetStoreForm();
                getAllStores();           
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.storeForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.openStoreForEdit = function(store) {
            $scope.store = angular.copy(store);
        };

        function resetStore() {
            $scope.store = null;
        };

        $scope.resetStoreForm = function() {
            resetStore();
            $scope.resetServerErrorMessages();
            $scope.storeForm.$setPristine();
            $scope.storeForm.$setUntouched();
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };
    });