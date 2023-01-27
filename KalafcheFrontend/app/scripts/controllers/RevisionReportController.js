'use strict';

angular.module('kalafcheFrontendApp')
    .directive('revisionReport', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/revision/revision-report.html',
            controller: RevisionReportController
        }
    });

    function RevisionReportController($scope, $rootScope, ApplicationService, RevisionService, AuthService,  StoreService, SessionService) {

       init();

        function init() {
            $scope.currentPage = 1;  
            $scope.revisionsPerPage = 15;
            $scope.itemsCurrentPage = 1;
            $scope.revisionItemsPerPage = 15;
            $scope.revisions = []; 
            $scope.stores = [];
            $scope.selectedStore = {};
            
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

            getAllStores(); 
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

        $scope.searchRevisions = function() {
            getRevisions();         
        }

        function getRevisions() {
            RevisionService.searchRevisions($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedStore.id).then(function(response) {
                $scope.revisions = response;
            });           
        }

        function getRevisionDetailedData(revision) {
            RevisionService.getRevisionDetailedData(revision.id).then(function(response) {
                revision.revisionItems = response.revisionItems;
                revision.deviceModels = response.deviceModels;
                revision.revisers = response.revisers;
                revision.createTimestamp = response.createTimestamp;
                revision.typeCode = response.typeCode;

                console.log(revision);
            }); 
        }

        $scope.clear = function() {
            $scope.startDate = null;
            $scope.endDate = null;
        };

        $scope.changeStartDate = function() {
            $scope.startDate.setHours(0);
            $scope.startDate.setMinutes(1);
            $scope.startDateMilliseconds = $scope.startDate.getTime();
        };

        $scope.changeEndDate = function() {
            $scope.endDate.setHours(23);
            $scope.endDate.setMinutes(59);
            $scope.endDateMilliseconds = $scope.endDate.getTime();
        };

        $scope.changeStore = function() {
            $scope.searchRevisions();
        };

        function getAllStores() {
            StoreService.getAllStoresForSaleReport().then(function(response) {
                $scope.stores = response;
                $scope.selectedStore =  {"id": SessionService.currentUser.employeeStoreId};
                getRevisions();
            });

        };

        $scope.getRevisionTimestamp = function(revisionSubmitTimestamp) {
            return ApplicationService.convertEpochToTimestamp(revisionSubmitTimestamp);
        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };

        $scope.expand = function(revision) {
            getRevisionDetailedData(revision);
            revision.expanded = !revision.expanded;
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

        // $scope.syncRevisionItem = function(revisionItem) {
        //      RevisionService.syncRevisionItem(revisionItem).then(function(response) {
        //         revisionItem.synced = true;
        //     });
        // }

    };

