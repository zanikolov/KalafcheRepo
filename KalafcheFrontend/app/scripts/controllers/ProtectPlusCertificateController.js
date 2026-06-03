'use strict';

angular.module('kalafcheFrontendApp')
    .controller('ProtectPlusCertificateController', function($scope, ProtectPlusCertificateService, BrandService, ModelService, StoreService, ServerValidationService) {

        init();

        function init() {
            $scope.activeSearch = {};
            $scope.activationRequest = {};
            $scope.deviceModelChangeRequest = {};
            $scope.selectedCertificate = null;
            $scope.selectedActiveCertificate = null;
            $scope.inactiveCertificates = [];
            $scope.activeCertificates = [];
            $scope.usageRecords = [];
            $scope.renewalRecords = [];
            $scope.deviceModelChangeRecords = [];
            $scope.callRecords = [];
            $scope.callRecordingUpload = {};
            $scope.customerEmailUpdate = {};
            $scope.brands = [];
            $scope.models = [];
            $scope.stores = [];
            $scope.inactiveCurrentPage = 1;
            $scope.activeCurrentPage = 1;
            $scope.certificatesPerPage = 15;
            $scope.serverErrorMessages = {};

            getAllBrands();
            getAllModels();
            if (isAdmin()) {
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

        $scope.searchActiveCertificates = function() {
            $scope.activeCertificates = [];
            $scope.resetActiveCertificateDetails();
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
            if (isAdmin()) {
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
                deviceBrandId: certificate.deviceBrandId,
                deviceModelId: certificate.deviceModelId
            };
            $scope.gdprConsentImage = null;
            $scope.serverErrorMessages = {};
        };

        $scope.activateCertificate = function() {
            ProtectPlusCertificateService.activateCertificate($scope.selectedCertificate.id, $scope.activationRequest, $scope.selectedCertificate.gdprConsentImage).then(
                function(activatedCertificate) {
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

        $scope.selectActiveCertificateDetails = function(certificate) {
            angular.forEach($scope.activeCertificates, function(activeCertificate) {
                if (activeCertificate.id !== certificate.id) {
                    activeCertificate.expanded = false;
                }
            });

            if ($scope.selectedActiveCertificate && $scope.selectedActiveCertificate.id === certificate.id && certificate.expanded) {
                $scope.resetActiveCertificateDetails();
                return;
            }

            $scope.selectedActiveCertificate = certificate;
            certificate.expanded = true;
            $scope.deviceModelChangeRequest = {
                deviceBrandId: certificate.deviceBrandId,
                deviceModelId: certificate.deviceModelId
            };
            $scope.serverErrorMessages = {};
            $scope.customerEmailUpdate = {
                email: certificate.loyalCustomerEmail
            };
            if (isAdmin()) {
                loadActiveCertificateHistory(certificate.id);
            }
        };

        function loadActiveCertificateHistory(certificateId) {
            loadUsageRecords(certificateId);
            loadRenewalRecords(certificateId);
            loadDeviceModelChangeRecords(certificateId);
            loadCallRecords(certificateId);
        }

        function loadUsageRecords(certificateId) {
            ProtectPlusCertificateService.getUsageRecords(certificateId).then(function(response) {
                $scope.usageRecords = response;
            });
        }

        function loadRenewalRecords(certificateId) {
            ProtectPlusCertificateService.getRenewalRecords(certificateId).then(function(response) {
                $scope.renewalRecords = response;
            });
        }

        function loadDeviceModelChangeRecords(certificateId) {
            ProtectPlusCertificateService.getDeviceModelChangeRecords(certificateId).then(function(response) {
                $scope.deviceModelChangeRecords = response;
            });
        }

        function loadCallRecords(certificateId) {
            ProtectPlusCertificateService.getCallRecords(certificateId).then(function(response) {
                $scope.callRecords = response;
            });
        }

        $scope.uploadCallRecording = function() {
            ProtectPlusCertificateService.uploadCallRecording($scope.selectedActiveCertificate.id,
                $scope.callRecordingUpload.file, $scope.callRecordingUpload.note).then(
                function(callRecord) {
                    $scope.callRecords.unshift(callRecord);
                    $scope.callRecordingUpload = {};
                    resetCallRecordingInput();
                    if ($scope.protectPlusCallRecordForm) {
                        $scope.protectPlusCallRecordForm.$setPristine();
                        $scope.protectPlusCallRecordForm.$setUntouched();
                    }
                },
                function(errorResponse) {
                    ServerValidationService.processServerErrors(errorResponse, $scope.protectPlusCallRecordForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                }
            );
        };

        function resetCallRecordingInput() {
            var callRecordingInput = document.getElementById('protectPlusCallRecordingInput');
            if (callRecordingInput) {
                callRecordingInput.value = null;
            }
        }

        $scope.downloadCallRecording = function(callRecord) {
            ProtectPlusCertificateService.downloadCallRecording($scope.selectedActiveCertificate.id, callRecord);
        };

        $scope.downloadGdprConsent = function(certificate) {
            ProtectPlusCertificateService.downloadGdprConsent(certificate);
        };


        $scope.updateCustomerEmail = function(certificate) {
            ProtectPlusCertificateService.updateCustomerEmail(certificate.id, $scope.customerEmailUpdate.email).then(
                function(updatedCertificate) {
                    certificate.loyalCustomerEmail = updatedCertificate.loyalCustomerEmail;
                    $scope.customerEmailUpdate.email = updatedCertificate.loyalCustomerEmail;
                },
                function(errorResponse) {
                    if (errorResponse.data && errorResponse.data.errors) {
                        $scope.serverErrorMessages = errorResponse.data.errors;
                    }
                }
            );
        };

        $scope.canUploadCallRecording = function() {
            return $scope.selectedActiveCertificate && $scope.callRecordingUpload.file;
        };

        $scope.canChangeDeviceModel = function(certificate) {
            return isAdmin() || !certificate.deviceModelChangeUsed;
        };

        $scope.changeCertificateDeviceModel = function() {
            ProtectPlusCertificateService.changeDeviceModel($scope.selectedActiveCertificate.id, $scope.deviceModelChangeRequest.deviceModelId).then(
                function() {
                    $scope.searchActiveCertificates();
                    $scope.resetActiveCertificateDetails();
                },
                function(errorResponse) {
                    ServerValidationService.processServerErrors(errorResponse, $scope.protectPlusDeviceModelChangeForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                }
            );
        };

        $scope.resetActiveCertificateDetails = function() {
            if ($scope.selectedActiveCertificate) {
                $scope.selectedActiveCertificate.expanded = false;
            }
            $scope.selectedActiveCertificate = null;
            $scope.deviceModelChangeRequest = {};
            $scope.usageRecords = [];
            $scope.renewalRecords = [];
            $scope.deviceModelChangeRecords = [];
            $scope.callRecords = [];
            $scope.callRecordingUpload = {};
            $scope.customerEmailUpdate = {};
            $scope.serverErrorMessages = {};
            if ($scope.protectPlusDeviceModelChangeForm) {
                $scope.protectPlusDeviceModelChangeForm.$setPristine();
                $scope.protectPlusDeviceModelChangeForm.$setUntouched();
            }
        };

        $scope.onDeviceModelChangeBrandChanged = function() {
            $scope.deviceModelChangeRequest.deviceModelId = null;
        };

        $scope.clearModelSearchTerm = function() {
            $scope.modelSearchTerm = "";
        };

        function isAdmin() {
            return angular.isFunction($scope.isAdmin) && $scope.isAdmin();
        }
    });
