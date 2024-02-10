'use strict';

angular.module('kalafcheFrontendApp')
    .controller('RelocationController', function ($scope, StoreService, RelocationService, SessionService, ApplicationService) {       

        init();

        function init() {
            $scope.relocations = [];
            $scope.outgoingStockRelocations = [];
            $scope.incomingStockRelocations = [];
            $scope.sourceStore = {"id": 4};
            $scope.destStore = {"id": SessionService.currentUser.employeeStoreId};
            $scope.selectedStore = {};

            $scope.isCompleted = false;
            getAllStores();

        };

        $scope.searchRelocations = function() {
            RelocationService.searchRelocations($scope.sourceStore.id, $scope.destStore.id, $scope.isCompleted)
            .then(function(response) {
                $scope.relocations = response;
            });
        };

        function getAllStores() {
            StoreService.getAllEntities().then(function(response) {
                $scope.stores = response;
            });

        };

        $scope.changeStatus = function(relocation, newStatus) {
            var relocationCopy = angular.copy(relocation);
            relocationCopy.status = newStatus;
            RelocationService.changeRelocationStatus(relocationCopy).then(function(response) {
                relocation.status = newStatus;
            });        
        }

        $scope.archiveStockRelocation = function(relocation) {
            RelocationService.archiveRelocation(relocation.id).then(function(response) {
                relocation.archived = true;
            });  
        }

        $scope.shouldArchiveButtonBeVisible = function(relocation) {

            if ($scope.isSuperAdmin() && (relocation.status == 'REJECTED' || relocation.status == 'DELIVERED')) {
                return true;
            }

            return false;
        }

        $scope.isEmployeeOfSourceStore = function(relocation) {
            return SessionService.currentUser.employeeStoreId == relocation.sourceStoreId;
        }

        $scope.isEmployeeOfDestStore = function(relocation) {
            return SessionService.currentUser.employeeStoreId == relocation.destStoreId;
        };

        $scope.isSourceStoreWarehouse = function(relocation) {
            return relocation.sourceStoreCode == 'RU_WH';
        }
  });