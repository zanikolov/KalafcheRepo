'use strict';

angular.module('kalafcheFrontendApp')
    .directive('discountCampaign', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/discount/campaign.html',
            controller: DiscountCampaignController
        }
    });

    function DiscountCampaignController($scope, $rootScope, ApplicationService, DiscountService, AuthService, SessionService) {

       init();

        function init() {
            $scope.campaignsPerPage = 15;
            $scope.currentPage = 1;
            getAllDiscountTypes();
            getAllDiscountCampaigns();
        }

        function getAllDiscountTypes() {
            DiscountService.getAllDiscountTypes().then(function(response) {
                $scope.types = response;
            });
        };

        function getAllDiscountCampaigns() {
            DiscountService.getAllDiscountCampaigns().then(function(response) {
                $scope.campaigns = response;
            });
        };

        $scope.getCampaignTimestamp = function(campaignCreateTimestamp) {
            return ApplicationService.convertEpochToTimestamp(campaignCreateTimestamp);
        };

        $scope.editCampaign = function(campaign) {
            $scope.campaign = angular.copy(campaign);
        };

        $scope.resetCampaign = function() {
            $scope.campaign = null;
        };

        $scope.resetCampaignForm = function() {
            $scope.resetCampaign();
            $scope.campaignForm.$setPristine();
            $scope.campaignForm.$setUntouched();
        };

        $scope.submitCampaign = function() {
            DiscountService.submitCampaign($scope.campaign).then(function(response) {
                $scope.resetCampaignForm();
                getAllDiscountCampaigns();
            });
        };

    };