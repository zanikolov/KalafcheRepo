'use strict';

angular.module('kalafcheFrontendApp')
    .directive('attributeForm', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/formula/attribute-form.html',
            controller: AttributeFormController
        }
    });


    function AttributeFormController ($scope, $rootScope, FormulaService, StoreService, ServerValidationService, SessionService, AuthService, ApplicationService) {

        init();

        function init() {
            $scope.attribute = {}; 
            $scope.attributes = [];
            $scope.currentPage = 1; 
            $scope.attributesPerPage = 50;
            $scope.serverErrorMessages = {};

            getAllAttributeTypes();
            getAllAttributeContexts();
            getAllAttributes();
        }

        $scope.changeStartDate = function() {
            $scope.attribute.startDate.setHours(0);
            $scope.attribute.startDate.setMinutes(1);
            $scope.attribute.fromTimestamp = $scope.attribute.startDate.getTime();
        };

        $scope.changeEndDate = function(variable) {
            $scope.attribute.endDate.setHours(23);
            $scope.attribute.endDate.setMinutes(59);
            $scope.attribute.toTimestamp = $scope.attribute.endDate.getTime();
        };

        $scope.resetAttribute = function () {
            $scope.attribute = null;
        };

        function getAllAttributeTypes() {
            FormulaService.getAllAttributeTypes().then(function(response) {
                $scope.types = response;
            });
        };

        function getAllAttributeContexts() {
            FormulaService.getAllAttributeContexts().then(function(response) {
                $scope.contexts = response;
            });
        };

        function getAllAttributes() {
            FormulaService.getAllAttributes().then(function(response) {
                $scope.attributes = response;
            });
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };

        $scope.editAttribute = function (attribute) {
            $scope.attribute = angular.copy(attribute);

            if ($scope.attribute.typeCode == "ABSOLUTE") {
                $scope.attribute.startDate = getDateFromDateObjectFromTimestamp($scope.attribute.fromTimestamp);
                $scope.attribute.endDate = getDateFromDateObjectFromTimestamp($scope.attribute.toTimestamp);
            }
        };

        function getDateFromDateObjectFromTimestamp(timestamp) {
            var dateString = $scope.getDate(timestamp);
            var dateObject = new Date();
            dateObject.setDate(dateString.split('-')[0]);
            dateObject.setMonth(dateString.split('-')[1] - 1);
            dateObject.setFullYear(dateString.split('-')[2]);

            return dateObject;
        }

        $scope.resetAttributeForm = function() {
            $scope.resetAttribute();
            $scope.resetServerErrorMessages();
            $scope.attributeForm.$setPristine();
            $scope.attributeForm.$setUntouched();
        };

        $scope.submitAttribute = function() {
            console.log($scope.attribute);
            FormulaService.submitAttribute($scope.attribute).then(function(response) {
                $scope.resetAttributeForm();
                getAllAttributes();
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.attributeForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.isSuperAdmin = function() {
            return AuthService.isSuperAdmin();
        }

        $scope.filterByAttributeName = function() {
            return function predicateFunc(product) {
                return $scope.attributeName == $scope.attribute.name;
            };
        };

        $scope.getDate = function(epochTimestamp) {
            return ApplicationService.convertEpochToDate(epochTimestamp)
        };

    };