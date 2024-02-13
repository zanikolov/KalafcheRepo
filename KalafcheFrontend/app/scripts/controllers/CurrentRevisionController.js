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

    function CurrentRevisionController($scope, $rootScope, AuthService, RevisionService, StoreService, ApplicationService, EmployeeService, SessionService, BrandService, ModelService) {

        init();

        function init() {
            $scope.revision = {};
            $scope.revisionItemsPerPage = 20;
            $scope.currentPage = 1;
            $scope.formData = {};
            $scope.formData.showOnlyMismatches = false;
            $scope.loading = false; 
            if (AuthService.isUser()) {
                $scope.formData.storeId = SessionService.currentUser.employeeStoreId;
                getCurrentRevision($scope.formData.storeId);
            } else {
                getAllStores();
            }
            getRevisionTypes();
            getAllActiveEmployees();
            getAllBrands();
            getAllDeviceModels();    
        }

        function getAllBrands() {
            BrandService.getAllDeviceBrands().then(function(response) {
                $scope.brands = response;
            });
        };

        function getAllDeviceModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response; 
            });
        };

        $scope.initiateRevision = function() {
            $scope.loading = true;
            $scope.revision.storeId = $scope.formData.storeId;
            RevisionService.initiateRevision($scope.revision).then(function(response) {
                    $scope.revision = response;
                    $scope.loading = false;
            },
            function(error) {
                $scope.loading = false;
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
            StoreService.getAllStoresForSaleReport().then(function(response) {
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

        $scope.plusRevisionItem = function(revisionItem, foundByBarcode) {
            RevisionService.plusRevisionItem(revisionItem).then(function(response){
                changeRevisionItemActual(revisionItem, 1, response)
            });
        }

        $scope.minusRevisionItem = function(revisionItem, foundByBarcode) {
            RevisionService.minusRevisionItem(revisionItem).then(function(response){
                changeRevisionItemActual(revisionItem, -1, response)
            });
        }

        function changeRevisionItemActual(revisionItem, actualChange, response) {
            if (revisionItem.id) {
                angular.forEach($scope.revision.revisionItems, function(item) {
                    if (revisionItem.id == item.id) {
                        item.actual = item.actual + actualChange;
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
        }

        $scope.finalizeRevision = function() {
            $scope.loading = true;
            RevisionService.finalizeRevision($scope.revision).then(function(response) {
                getCurrentRevision();
                $scope.loading = false;
            },
            function(error) {
                $scope.loading = false;
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
