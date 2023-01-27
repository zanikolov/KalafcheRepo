'use strict';

angular.module('kalafcheFrontendApp')
    .directive('discountCode', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/discount/code.html',
            controller: DiscountCodeController
        }
    });

    function DiscountCodeController($scope, $rootScope, ApplicationService, DiscountService, AuthService, SessionService, ServerValidationService) {

       init();

        function init() {
            $scope.discountCodesPerPage = 15;
            $scope.currentPage = 1;

            getAllDiscountCampaigns();
            getAllDiscountCodes();
        }

        function getAllDiscountCampaigns() {
            DiscountService.getAllDiscountCampaigns().then(function(response) {
                $scope.campaigns = response;
            });
        };
        
        function getAllDiscountCodes() {
            DiscountService.getAllDiscountCodes().then(function(response) {
                $scope.discountCodes = response;
            });
        }

        $scope.editDiscountCode = function(discountCode) {
            $scope.discountCode = angular.copy(discountCode);
        };

        $scope.resetDiscountCode = function() {
            $scope.discountCode = null;
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };

        $scope.resetDiscountCodeForm = function() {
            $scope.resetDiscountCode();
            $scope.resetServerErrorMessages();
            $scope.discountCodeForm.$setPristine();
            $scope.discountCodeForm.$setUntouched();
        };

        $scope.submitDiscountCode = function() {
            DiscountService.submitDiscountCode($scope.discountCode).then(function(response) {
                $scope.resetDiscountCodeForm();
                getAllDiscountCodes();
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.discountCodeForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };
    };

