'use strict';

angular.module('kalafcheFrontendApp')
	.controller('DailyStoreReportModalController', function ($scope, currentDailyStoreReport, $mdDialog, DailyStoreReportService, ApplicationService, ServerValidationService) {

        init();

        function init() {
            $scope.dailyStoreReport = currentDailyStoreReport;
            $scope.isFinalizeButtonVisible = false;

            checkFinalizeButtonVisibility();
        };

        $scope.finalizeDailyStoreReport = function() {
            $scope.loading = true;
            DailyStoreReportService.generateDailyStoreReport().then(
                function(response) {
                    $mdDialog.cancel();
                    $scope.loading = false;
                },
                function(error) {
                    $scope.loading = false;
                }
            );
            
        };

		$scope.closeModal = function () {
            $mdDialog.cancel();
		};

        function checkFinalizeButtonVisibility() {
            return DailyStoreReportService.isDailyStoreReportCanBeFinalized($scope.dailyStoreReport.storeId).then(
                function(response) {
                    console.log("##### " + response);
                    $scope.isFinalizeButtonVisible = response;
                }                
            );
        };

        $scope.getTimestamp = function(timestamp) {
            return ApplicationService.convertEpochToTimestamp(timestamp);
        };

	});