'use strict';

angular.module('kalafcheFrontendApp')
    .directive('model', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/device/model.html',
            controller: ModelController
        }
    });
  
    function ModelController($scope, ModelService, BrandService, ServerValidationService) {

        init();

        function init() {
            $scope.deviceModel = {};
            $scope.brands = [];
            $scope.models = [];
            $scope.currentPage = 1; 
            $scope.modelsPerPage = 10; 
            $scope.serverErrorMessages = {};

            getAllDeviceBrands();
            getAllDeviceModels();
        }

        $scope.submitModel = function() {         
            ModelService.submitModel($scope.deviceModel).then(function(response) {
                getAllDeviceModels();
                resetDeviceModelState();
                $scope.resetServerErrorMessages();
                $scope.deviceModelForm.$setPristine();
                $scope.deviceModelForm.$setUntouched();
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.deviceModelForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };

        function getAllDeviceBrands() {
            BrandService.getAllDeviceBrands().then(function(response) {
                $scope.brands = response;
            });

        };

        function getAllDeviceModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response;
            });

        };

        function resetDeviceModelState() {
            var deviceBrandId = $scope.deviceModel.deviceBrandId;
            $scope.deviceModel = {};
            $scope.deviceModel.deviceBrandId = deviceBrandId;
        }

        $scope.resetDeviceModelName = function() {
            $scope.deviceModel.name = '';
        }

        $scope.cancelEditDeviceModel = function() {
            resetDeviceModelState();
            $scope.resetServerErrorMessages();
            $scope.deviceModelForm.name.$setPristine();
            $scope.deviceModelForm.name.$setUntouched();
        }

        $scope.editDeviceModel = function(selectedDeviceModel) {
            $scope.deviceModel = angular.copy(selectedDeviceModel);
        }
    };