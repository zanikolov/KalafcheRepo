'use strict';

angular.module('kalafcheFrontendApp')
    .directive('saleReportByTransaction', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/sale-report/by-transaction.html',
            controller: SaleTransactionReportController
        }
    });

    function SaleTransactionReportController($scope, $rootScope, $mdDialog, ApplicationService, SaleService, AuthService,  StoreService, BrandService, ModelService, SessionService) {

        init();

        function init() {
            $scope.currentPage = 1;  
            $scope.salesPerPage = 15;
            $scope.sales = []; 
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
            var yesterday = new Date();
            yesterday.setDate(yesterday.getDate() - 1);
            var today = new Date();
            $scope.startMinDate = AuthService.isAdmin() ? new Date(2015, 5, 22) : yesterday;
            $scope.startMaxDate = today;
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

        $scope.searchSales = function() {
            getSales();         
        }

        function getSales() {
            SaleService.searchSales($scope.startDateMilliseconds, $scope.endDateMilliseconds, $scope.selectedStore.id).then(function(response) {
                $scope.report = response;
            });           
        }

        function getSaleItems(sale) {
            SaleService.getSaleItems(sale.id).then(function(response) {
                sale.saleItems = response;
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
            $scope.searchSales();
        };

        function getAllStores() {
            StoreService.getAllStoresForSaleReport().then(function(response) {
                $scope.stores = response;
                $scope.selectedStore =  {"id": SessionService.currentUser.employeeStoreId};
                getSales();
            });

        };

        $scope.getSaleTimestamp = function(saleTimestamp) {
            return ApplicationService.convertEpochToTimestamp(saleTimestamp);
        };

        $scope.getReportDate = function(reportTimestamp) {
            return ApplicationService.convertEpochToDate(reportTimestamp);
        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };

        $scope.expand = function(sale) {
            getSaleItems(sale);
            sale.expanded = !sale.expanded;
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

        $scope.openRefundModal = function(saleItem) {
            $scope.selectedSaleItem = saleItem;
            $mdDialog.show({
              locals:{selectedSaleItem: $scope.selectedSaleItem},
              controller: 'RefundModalController',
              templateUrl: 'views/modals/refund-modal.html',
              parent: angular.element(document.body)
            })
            .then(function(answer) {
              $scope.status = 'You said the information was "".';
            }, function() {
              $scope.status = 'You cancelled the dialog.';
            });
        };

    };

