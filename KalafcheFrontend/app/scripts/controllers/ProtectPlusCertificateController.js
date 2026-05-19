'use strict';

angular.module('kalafcheFrontendApp')
    .controller('ProtectPlusCertificateController', function($scope, ProtectPlusCertificateService, BrandService, ModelService, StoreService, ServerValidationService) {

        init();

        function init() {
            $scope.activeSearch = {};
            $scope.activationRequest = {};
            $scope.selectedCertificate = null;
            $scope.inactiveCertificates = [];
            $scope.activeCertificates = [];
            $scope.brands = [];
            $scope.models = [];
            $scope.stores = [];
            $scope.inactiveCurrentPage = 1;
            $scope.activeCurrentPage = 1;
            $scope.certificatesPerPage = 15;
            $scope.serverErrorMessages = {};

            getAllBrands();
            getAllModels();
            if ($scope.isAdmin && $scope.isAdmin()) {
                getAllStores();
            }
            loadInactiveCertificates();
        }

        function getAllBrands() {
            BrandService.getAllDeviceBrands().then(function(response) {
                $scope.brands = response;
            });
        }

        function getAllModels() {
            ModelService.getAllDeviceModels().then(function(response) {
                $scope.models = response;
            });
        }

        function getAllStores() {
            StoreService.getAllStores(false).then(function(response) {
                $scope.stores = response;
            });
        }

        function loadInactiveCertificates() {
            ProtectPlusCertificateService.getInactiveCertificates().then(
                function(response) {
                    $scope.inactiveCertificates = response;
                    $scope.inactiveCurrentPage = 1;
                },
                function(errorResponse) {
                    $scope.inactiveCertificates = [];
                    ServerValidationService.processServerErrors(errorResponse, $scope.protectPlusSearchForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                }
            );
        }

        $scope.loadInactiveCertificates = loadInactiveCertificates;

        $scope.searchActiveCertificates = function() {
            $scope.activeCertificates = [];
            $scope.resetActivationForm();

            if (!$scope.canSearchActiveCertificates()) {
                return;
            }

            ProtectPlusCertificateService.searchCertificates($scope.activeSearch).then(
                function(response) {
                    $scope.activeCertificates = response;
                    $scope.activeCurrentPage = 1;
                },
                function(errorResponse) {
                    $scope.activeCertificates = [];
                    ServerValidationService.processServerErrors(errorResponse, $scope.protectPlusSearchForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                }
            );
        };

        $scope.canSearchActiveCertificates = function() {
            if ($scope.isAdmin()) {
                return $scope.activeSearch.certificateNumber || $scope.activeSearch.phoneNumber ||
                    $scope.activeSearch.storeId || $scope.activeSearch.deviceBrandId || $scope.activeSearch.deviceModelId;
            }

            return $scope.activeSearch.certificateNumber || $scope.activeSearch.phoneNumber;
        };

        $scope.selectCertificateForActivation = function(certificate) {
            if (certificate.status != 'INACTIVE') {
                return;
            }

            angular.forEach($scope.inactiveCertificates, function(inactiveCertificate) {
                if (inactiveCertificate.id !== certificate.id) {
                    inactiveCertificate.expanded = false;
                }
            });

            if ($scope.selectedCertificate && $scope.selectedCertificate.id === certificate.id && certificate.expanded) {
                $scope.resetActivationForm();
                return;
            }

            $scope.selectedCertificate = certificate;
            $scope.selectedCertificate.gdprConsentImage = null;
            certificate.expanded = true;
            $scope.activationRequest = {
                loyalCustomer: {},
                deviceModelId: null
            };
            $scope.gdprConsentImage = null;
            $scope.serverErrorMessages = {};
        };

        $scope.activateCertificate = function() {
            ProtectPlusCertificateService.activateCertificate($scope.selectedCertificate.id, $scope.activationRequest, $scope.selectedCertificate.gdprConsentImage).then(
                function() {
                    loadInactiveCertificates();
                    $scope.resetActivationForm();
                },
                function(errorResponse) {
                    ServerValidationService.processServerErrors(errorResponse, $scope.protectPlusActivationForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                }
            );
        };

        $scope.resetActivationForm = function() {
            if ($scope.selectedCertificate) {
                $scope.selectedCertificate.expanded = false;
                $scope.selectedCertificate.gdprConsentImage = null;
            }
            $scope.selectedCertificate = null;
            $scope.activationRequest = {};
            $scope.gdprConsentImage = null;
            $scope.serverErrorMessages = {};
            if ($scope.protectPlusActivationForm) {
                $scope.protectPlusActivationForm.$setPristine();
                $scope.protectPlusActivationForm.$setUntouched();
            }
        };

        $scope.onActivationBrandChanged = function() {
            $scope.activationRequest.deviceModelId = null;
        };

        $scope.onActiveSearchBrandChanged = function() {
            $scope.activeSearch.deviceModelId = null;
        };

        $scope.clearModelSearchTerm = function() {
            $scope.modelSearchTerm = "";
        };
    });
