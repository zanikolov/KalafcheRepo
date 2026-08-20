'use strict';

angular.module('kalafcheFrontendApp')
    .controller('ProtectPlusCertificateController', function($scope, $mdDialog, ProtectPlusCertificateService, BrandService, ModelService, StoreService, ProductService, ServerValidationService) {

        init();

        function init() {
            $scope.activeSearch = {};
            $scope.activeAdvancedSearch = {};
            $scope.advancedSearchExpanded = false;
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
            $scope.customerNameUpdate = {};
            $scope.customerPhoneUpdate = {};
            $scope.discountPolicies = [];
            $scope.discountPolicy = createEmptyDiscountPolicy();
            $scope.products = [];
            $scope.brands = [];
            $scope.models = [];
            $scope.stores = [];
            $scope.inactiveCurrentPage = 1;
            $scope.activeCurrentPage = 1;
            $scope.certificatesPerPage = 15;
            $scope.serverErrorMessages = {};
            $scope.usedFilterOptions = [
                {label: 'Всички', value: null},
                {label: 'Използван', value: true},
                {label: 'Неизползван', value: false}
            ];

            getAllBrands();
            getAllModels();
            if (isAdmin()) {
                getAllStores();
                getAllProducts();
                loadDiscountPolicies();
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

        function getAllProducts() {
            ProductService.getAllProducts().then(function(response) {
                $scope.products = response;
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

            ProtectPlusCertificateService.searchCertificates(buildActiveSearchFilters()).then(
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
                    $scope.activeSearch.storeId || $scope.activeSearch.deviceBrandId || $scope.activeSearch.deviceModelId ||
                    hasAdvancedSearchCriteria();
            }

            return $scope.activeSearch.certificateNumber || $scope.activeSearch.phoneNumber;
        };

        $scope.toggleAdvancedSearch = function() {
            $scope.advancedSearchExpanded = !$scope.advancedSearchExpanded;
        };

        function buildActiveSearchFilters() {
            var filters = angular.copy($scope.activeSearch);
            addDateRangeFilters(filters, 'activation', $scope.activeAdvancedSearch.activationFromDate,
                $scope.activeAdvancedSearch.activationToDate);
            addDateRangeFilters(filters, 'usage', $scope.activeAdvancedSearch.usageFromDate,
                $scope.activeAdvancedSearch.usageToDate);
            addDateRangeFilters(filters, 'call', $scope.activeAdvancedSearch.callFromDate,
                $scope.activeAdvancedSearch.callToDate);
            addDateRangeFilters(filters, 'validUntil', $scope.activeAdvancedSearch.validUntilFromDate,
                $scope.activeAdvancedSearch.validUntilToDate);

            filters.usageCount = $scope.activeAdvancedSearch.usageCount;
            filters.callCount = $scope.activeAdvancedSearch.callCount;
            filters.freeProtectorUsed = $scope.activeAdvancedSearch.freeProtectorUsed;
            filters.freeDisplayReplacementServiceUsed = $scope.activeAdvancedSearch.freeDisplayReplacementServiceUsed;
            filters.freeBatteryReplacementServiceUsed = $scope.activeAdvancedSearch.freeBatteryReplacementServiceUsed;
            filters.deviceModelChangeUsed = $scope.activeAdvancedSearch.deviceModelChangeUsed;
            return filters;
        }

        function addDateRangeFilters(filters, prefix, fromDate, toDate) {
            if (!fromDate && !toDate) {
                return;
            }

            filters[prefix + 'FromTimestamp'] = fromDate ? getStartOfDayTimestamp(fromDate) : null;
            filters[prefix + 'ToTimestamp'] = getEndOfDayTimestamp(toDate || new Date());
        }

        function getStartOfDayTimestamp(date) {
            var startOfDay = new Date(date.getTime());
            startOfDay.setHours(0, 0, 0, 0);
            return startOfDay.getTime();
        }

        function getEndOfDayTimestamp(date) {
            var endOfDay = new Date(date.getTime());
            endOfDay.setHours(23, 59, 59, 999);
            return endOfDay.getTime();
        }

        function hasAdvancedSearchCriteria() {
            return $scope.activeAdvancedSearch.activationFromDate || $scope.activeAdvancedSearch.activationToDate ||
                $scope.activeAdvancedSearch.usageFromDate || $scope.activeAdvancedSearch.usageToDate ||
                $scope.activeAdvancedSearch.usageCount !== null && $scope.activeAdvancedSearch.usageCount !== undefined ||
                $scope.activeAdvancedSearch.callFromDate || $scope.activeAdvancedSearch.callToDate ||
                $scope.activeAdvancedSearch.callCount !== null && $scope.activeAdvancedSearch.callCount !== undefined ||
                $scope.activeAdvancedSearch.validUntilFromDate || $scope.activeAdvancedSearch.validUntilToDate ||
                $scope.activeAdvancedSearch.freeProtectorUsed !== null && $scope.activeAdvancedSearch.freeProtectorUsed !== undefined ||
                $scope.activeAdvancedSearch.freeDisplayReplacementServiceUsed !== null && $scope.activeAdvancedSearch.freeDisplayReplacementServiceUsed !== undefined ||
                $scope.activeAdvancedSearch.freeBatteryReplacementServiceUsed !== null && $scope.activeAdvancedSearch.freeBatteryReplacementServiceUsed !== undefined ||
                $scope.activeAdvancedSearch.deviceModelChangeUsed !== null && $scope.activeAdvancedSearch.deviceModelChangeUsed !== undefined;
        }

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
                loyalCustomer: {
                    name: certificate.customerName,
                    phoneNumber: certificate.phoneNumber,
                    email: certificate.loyalCustomerEmail
                },
                deviceBrandId: certificate.deviceBrandId,
                deviceModelId: certificate.deviceModelId
            };
            $scope.gdprConsentImage = null;
            $scope.serverErrorMessages = {};
        };

        $scope.activateCertificate = function() {
            if ($scope.isActivationDeviceModelUnknown()) {
                return;
            }

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

        $scope.saveInactiveCertificateDraft = function() {
            if (!$scope.canSaveInactiveCertificateDraft()) {
                return;
            }

            ProtectPlusCertificateService.saveInactiveCertificateDraft($scope.selectedCertificate.id,
                $scope.activationRequest, $scope.selectedCertificate.gdprConsentImage).then(
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

        $scope.isActivationDeviceModelUnknown = function() {
            var deviceModel = getDeviceModelById($scope.activationRequest.deviceModelId);
            return deviceModel && deviceModel.unknownModel === true;
        };

        $scope.canSaveInactiveCertificateDraft = function() {
            return $scope.selectedCertificate && $scope.activationRequest.deviceModelId;
        };

        $scope.hasGdprConsentImage = function() {
            return $scope.selectedCertificate &&
                ($scope.selectedCertificate.gdprConsentImage || $scope.selectedCertificate.gdprConsentFileId);
        };

        $scope.hasIncompleteDraftCustomerData = function() {
            var loyalCustomer = $scope.activationRequest.loyalCustomer;
            if (!loyalCustomer) {
                return false;
            }

            var hasAnyCustomerData = !!(loyalCustomer.name || loyalCustomer.phoneNumber || loyalCustomer.email);
            var hasMinimumCustomerData = !!(loyalCustomer.name && loyalCustomer.phoneNumber);
            return hasAnyCustomerData && !hasMinimumCustomerData;
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
            $scope.customerNameUpdate = {
                name: certificate.customerName || certificate.loyalCustomerName
            };
            $scope.customerPhoneUpdate = {
                phoneNumber: certificate.phoneNumber || certificate.loyalCustomerPhoneNumber
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

        $scope.updateCustomerName = function(certificate) {
            ProtectPlusCertificateService.updateCustomerName(certificate.id, $scope.customerNameUpdate.name).then(
                function(updatedCertificate) {
                    certificate.customerName = updatedCertificate.loyalCustomerName;
                    certificate.loyalCustomerName = updatedCertificate.loyalCustomerName;
                    $scope.customerNameUpdate.name = updatedCertificate.loyalCustomerName;
                },
                function(errorResponse) {
                    if (errorResponse.data && errorResponse.data.errors) {
                        $scope.serverErrorMessages = errorResponse.data.errors;
                    }
                }
            );
        };

        $scope.updateCustomerPhone = function(certificate) {
            ProtectPlusCertificateService.updateCustomerPhone(certificate.id, $scope.customerPhoneUpdate.phoneNumber).then(
                function(updatedCertificate) {
                    certificate.phoneNumber = updatedCertificate.loyalCustomerPhoneNumber;
                    certificate.loyalCustomerPhoneNumber = updatedCertificate.loyalCustomerPhoneNumber;
                    $scope.customerPhoneUpdate.phoneNumber = updatedCertificate.loyalCustomerPhoneNumber;
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
                function(updatedCertificate) {
                    $scope.selectedActiveCertificate.deviceBrandId = updatedCertificate.deviceBrandId;
                    $scope.selectedActiveCertificate.deviceModelId = updatedCertificate.deviceModelId;
                    $scope.selectedActiveCertificate.deviceModelName = updatedCertificate.deviceModelName;
                    $scope.selectedActiveCertificate.deviceModelChangeUsed = updatedCertificate.deviceModelChangeUsed;
                    $scope.deviceModelChangeRequest = {
                        deviceBrandId: updatedCertificate.deviceBrandId,
                        deviceModelId: updatedCertificate.deviceModelId
                    };
                    loadDeviceModelChangeRecords(updatedCertificate.id);
                },
                function(errorResponse) {
                    ServerValidationService.processServerErrors(errorResponse, $scope.protectPlusDeviceModelChangeForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                }
            );
        };

        $scope.cancelCertificate = function(certificate, event) {
            if (!certificate || !isAdmin()) {
                return;
            }
            if (event) {
                event.stopPropagation();
            }

            var inactiveCertificate = certificate.status === 'INACTIVE';
            var confirm = $mdDialog.confirm()
                .title('Анулиране на Protect+ сертификат')
                .textContent('Сертификатът ще бъде анулиран и личните данни на клиента ще бъдат изтрити. Ако сертификатът не е използван, покупката на сертификата ще бъде върната. Продължи?')
                .ariaLabel('Анулиране на Protect+ сертификат')
                .ok('Анулирай')
                .cancel('Отмени');

            $mdDialog.show(confirm).then(function() {
                ProtectPlusCertificateService.cancelCertificate(certificate.id).then(
                    function() {
                        if (inactiveCertificate) {
                            loadInactiveCertificates();
                            $scope.resetActivationForm();
                        } else {
                            $scope.searchActiveCertificates();
                            $scope.resetActiveCertificateDetails();
                        }
                    },
                    function(errorResponse) {
                        $scope.serverErrorMessages = getServerErrorMessages(errorResponse);
                    }
                );
            });
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
            $scope.customerNameUpdate = {};
            $scope.customerPhoneUpdate = {};
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

        function loadDiscountPolicies() {
            ProtectPlusCertificateService.getDiscountPolicies().then(function(response) {
                $scope.discountPolicies = response;
            });
        }

        function createEmptyDiscountPolicy() {
            return {
                active: true,
                defaultPolicy: false,
                productRules: []
            };
        }

        $scope.editDiscountPolicy = function(policy) {
            $scope.discountPolicy = angular.copy(policy);
            $scope.discountPolicy.validFromDate = new Date(policy.validFromTimestamp);
            $scope.discountPolicy.validUntilDate = policy.validUntilTimestamp ? new Date(policy.validUntilTimestamp) : null;
            $scope.serverErrorMessages = {};
        };

        $scope.resetDiscountPolicyForm = function() {
            $scope.discountPolicy = createEmptyDiscountPolicy();
            $scope.productSearchTerm = "";
            $scope.serverErrorMessages = {};
            if ($scope.protectPlusDiscountPolicyForm) {
                $scope.protectPlusDiscountPolicyForm.$setPristine();
                $scope.protectPlusDiscountPolicyForm.$setUntouched();
                $scope.protectPlusDiscountPolicyForm.$submitted = false;
            }
        };

        $scope.addDiscountPolicyProductRule = function() {
            if (!$scope.discountPolicy.productRules) {
                $scope.discountPolicy.productRules = [];
            }
            $scope.discountPolicy.productRules.push({});
        };

        $scope.removeDiscountPolicyProductRule = function(index) {
            $scope.discountPolicy.productRules.splice(index, 1);
        };

        $scope.saveDiscountPolicy = function() {
            var policy = angular.copy($scope.discountPolicy);
            policy.validFromTimestamp = getStartOfDayTimestamp(policy.validFromDate);
            policy.validUntilTimestamp = policy.validUntilDate ? getEndOfDayTimestamp(policy.validUntilDate) : null;
            delete policy.validFromDate;
            delete policy.validUntilDate;

            ProtectPlusCertificateService.saveDiscountPolicy(policy).then(
                function() {
                    loadDiscountPolicies();
                    $scope.resetDiscountPolicyForm();
                },
                function(errorResponse) {
                    $scope.serverErrorMessages = getServerErrorMessages(errorResponse);
                }
            );
        };

        function getServerErrorMessages(errorResponse) {
            if (!errorResponse || !errorResponse.data) {
                return {};
            }
            var errors = {};
            if (angular.isString(errorResponse.data)) {
                try {
                    errors = angular.fromJson(errorResponse.data).errors || {request: errorResponse.data};
                } catch (error) {
                    errors = {request: errorResponse.data};
                }
            } else {
                errors = errorResponse.data.errors || {};
            }

            return translateProtectPlusDiscountPolicyErrors(errors);
        }

        function translateProtectPlusDiscountPolicyErrors(errors) {
            var translations = {
	                protectPlusDiscountPolicyInvalidPeriod: 'Крайната дата трябва да бъде след началната дата.',
	                protectPlusDiscountPolicyMultipleActiveDefaults: 'Може да има само една активна Protect+ policy по подразбиране.',
	                protectPlusDiscountPolicyOverlapsActiveCampaign: 'Protect+ кампанията се припокрива с друга активна кампания.',
	                protectPlusDiscountPolicyDefaultCanNotBeManaged: 'Protect+ policy по подразбиране не може да се редактира от този екран.'
	            };

            angular.forEach(errors, function(message, field) {
                errors[field] = translations[message] || message;
            });

            return errors;
        }

        $scope.convertPolicyDate = function(timestamp) {
            return timestamp ? new Date(timestamp).toLocaleDateString('bg-BG') : '';
        };

        $scope.toggleDiscountPolicyProducts = function(policy) {
            policy.expanded = !policy.expanded;
        };

        $scope.hasDiscountPolicyProductRules = function(policy) {
            return policy.productRules && policy.productRules.length > 0;
        };

        function isAdmin() {
            return angular.isFunction($scope.isAdmin) && $scope.isAdmin();
        }

        function getDeviceModelById(deviceModelId) {
            if (!deviceModelId) {
                return null;
            }

            for (var i = 0; i < $scope.models.length; i++) {
                if ($scope.models[i].id === deviceModelId) {
                    return $scope.models[i];
                }
            }

            return null;
        }
    });
