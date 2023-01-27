'use strict';

angular.module('kalafcheFrontendApp')
    .directive('brand', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/device/brand.html',
            controller: BrandController,
            controllerAs: 'vm'
        }
    });
  
    function BrandController($scope, BrandService, ServerValidationService) {
        var vm = this;
        init();

        function init() {
            $scope.deviceBrand = {};
            $scope.brands = [];
            $scope.serverErrorMessages = {};

            $scope.brandsPerPage = 10;
            $scope.currentPage = 1;

            getAllDeviceBrands();
        }

        $scope.submitBrand = function() {
            BrandService.submitBrand($scope.deviceBrand).then(
                function(response) {
                    getAllDeviceBrands();
                    resetBrandState();
                    $scope.resetServerErrorMessages();
                    $scope.deviceBrandForm.$setPristine();
                    $scope.deviceBrandForm.$setUntouched();
                },
                function(errorResponse) {
                    ServerValidationService.processServerErrors(errorResponse, $scope.deviceBrandForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                });
        };

        $scope.resetServerErrorMessages = function() {
            $scope.serverErrorMessages = {};
        };

        function resetBrandState() {
            $scope.deviceBrand = {};
        }

        function getAllDeviceBrands() {
            BrandService.getAllDeviceBrands().then(function(response) {
                $scope.brands = response;
            });
        };
    };

    // .controller('BrandController', function($scope, BrandService, ServerValidationService) {
        
    //     init();

    //     function init() {
    //         $scope.deviceBrand = {};
    //         $scope.brands = [];
    //         $scope.serverErrorMessages = {};

    //         $scope.brandsPerPage = 10;
    //         $scope.currentPage = 1;

    //         getAllDeviceBrands();
    //     }

    //     $scope.submitBrand = function() {
    //         BrandService.submitBrand($scope.deviceBrand).then(
    //             function(response) {
    //                 getAllDeviceBrands();
    //                 resetBrandState();
    //                 resetServerErrorMessages();
    //                 $scope.deviceBrandForm.$setPristine();
    //                 $scope.deviceBrandForm.$setUntouched();
    //             },
    //             function(errorResponse) {
    //                 ServerValidationService.processServerErrors(errorResponse, $scope.deviceBrandForm);
    //                 $scope.serverErrorMessages = errorResponse.data.errors;
    //             });
    //     };

    //      function resetServerErrorMessages() {
    //         $scope.serverErrorMessages = {};
    //     };

    //     function resetBrandState() {
    //         $scope.deviceBrand = {};
    //     }

    //     function getAllDeviceBrands() {
    //         BrandService.getAllDeviceBrands().then(function(response) {
    //             $scope.brands = response;
    //         });
    //     };
    // });