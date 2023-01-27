'use strict';

angular.module('kalafcheFrontendApp')
    .directive('currentRevision', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/revision/current-revision.html',
            controller: CurrentRevisionController
        }
    });

    function CurrentRevisionController($scope, $rootScope, AuthService, RevisionService, StoreService, ApplicationService, EmployeeService) {

        init();

        function init() {
            $scope.revision = {};
            $scope.revisionItemsPerPage = 20;
            $scope.currentPage = 1;
            $scope.formData = {};
            $scope.formData.showOnlyMismatches = true;

            if (!AuthService.isAdmin()) {
                getCurrentRevision();
            } else {
                getAllStores();
            }
            getRevisionTypes();
            getAllActiveEmployees();
        }

        $scope.initiateRevision = function() {
            $scope.revision.storeId = $scope.formData.storeId;
            console.log($scope.revision);
            RevisionService.initiateRevision($scope.revision).then(function(response) {
                    $scope.revision = response;
                });
        }

        $scope.getCurrentRevision = function() {
            getCurrentRevision($scope.formData.storeId);
        }

        function getCurrentRevision(storeId) {
            RevisionService.getCurrentRevision(storeId).then(function(response) {
                    if(response) {
                       $scope.revision = response; 
                    } else {
                        $scope.revision = {}; 
                    }
                });
        }

        function getRevisionTypes() {
            RevisionService.getRevisionTypes().then(function(response) {
                $scope.types = response;
            });
        }

        function getAllActiveEmployees() {
            EmployeeService.getAllActiveEmployees().then(function(response) {
                $scope.employees = response;
            });
        }

        function getAllStores() {
            StoreService.getAllStores().then(function(response) {
                $scope.stores = response;
            });

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

        $scope.getRevisionTimestamp = function(revisionTimestamp) {
            return ApplicationService.convertEpochToTimestamp(revisionTimestamp);
        };

        $scope.onChangeBarcode = function() {
            findByBarcode();
        }

        $scope.barcodeScanned = function(barcode) {                             
            $scope.formData.barcode = barcode;  
            findByBarcode();      
        }

        function findByBarcode() {
            if ($scope.formData.barcode.length == 13) {
                RevisionService.getRevisionItemByBarcode($scope.revision.id, $scope.formData.barcode).then(function(response) {
                    if(response) {
                        $scope.selectedRevisionItem = response; 
                    }
                });
            } else {
                $scope.selectedRevisionItem = null;
            }
        }

        $scope.findRevisionItem = function(revisionItem) {
            RevisionService.findRevisionItem(revisionItem).then(function(response) {
                if (revisionItem.id) {
                    angular.forEach($scope.revision.revisionItems, function(item) {
                        if (revisionItem.id == item.id) {
                            item.actual = item.actual + 1;
                            return;
                        }
                    });
                } else {
                    revisionItem.id = response;
                    revisionItem.actual = 1;
                    $scope.revision.revisionItems.push(revisionItem);
                }

                $scope.formData.barcode = "";
                $scope.selectedRevisionItem = null;
            });
        }

        $scope.submitRevision = function() {
            RevisionService.submitRevision($scope.revision).then(function(response) {
                console.log(response);
            });
        }

        $scope.filterExpectedEqualsToActual = function() {
            return function predicateFunc(revisionItem) {
                return !$scope.formData.showOnlyMismatches || revisionItem.actual != revisionItem.expected;
            };
        }

        // $scope.$watch('barcode', function(newValue, oldValue) {
        //     console.log(">>>> barcode: " + newValue);
        // });

    };
