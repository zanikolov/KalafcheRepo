'use strict';

angular.module('kalafcheFrontendApp')
	.controller('SaleModalController', function ($scope, currentSale, $mdDialog, DiscountService, SaleService, ProtectPlusCertificateService, EmployeeService, ServerValidationService, SessionService, ApplicationService, FiscalMiddlewareService, BrandService, ModelService) {

        init();

        function init() {
            $scope.sale = currentSale;
            $scope.sale.paid = null;
            $scope.replacementSale = null;
            $scope.replacementSaleHasRefundedItem = false;
            $scope.discountCode = null;
            $scope.serverErrorMessages = {};
            $scope.submitSaleErrorMessage = null;
            $scope.showSoldForDeviceModelValidation = false;
            $scope.protectPlusSearch = {};
            $scope.protectPlusCertificates = [];
            $scope.selectedProtectPlusCertificate = $scope.sale.selectedProtectPlusCertificate;
            $scope.protectPlusSearchPerformed = false;
            $scope.totalSumReport = 0;
            $scope.brands = [];
            $scope.models = [];

            if ($scope.selectedProtectPlusCertificate) {
                $scope.protectPlusSearch.query = $scope.selectedProtectPlusCertificate.certificateNumber;
            }

            normalizeSelectedStocks();
            getEmployeesByStore();
            getAllBrands();
            getAllModels();
            calculateTotalSum();
        };

        function normalizeSelectedStocks() {
            angular.forEach($scope.sale.selectedStocks, function(stock) {
                if (!stock.soldForDeviceModelId && stock.soldForDeviceModel && stock.soldForDeviceModel.id) {
                    stock.soldForDeviceModelId = stock.soldForDeviceModel.id;
                }

                stock.soldForDeviceModel = null;
            });
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

        function getEmployeesByStore() {
            var userStoreId = SessionService.currentUser.employeeStoreId ? SessionService.currentUser.employeeStoreId : 0;
            EmployeeService.getAllActiveEmployeesByStore(userStoreId).then(
                function(response){
                    console.log(response);
                    $scope.employees = response;
                })
        }

	        function calculateTotalSum() {
	            if (hasMissingProductRequiredSoldForDeviceModel()) {
	                return;
	            }

	            SaleService.getTotalSum($scope.sale).then(
	                function(response){
	                    console.log(response);
                    $scope.totalSumReport = response;
                    applyCalculatedSaleItems(response.selectedSaleItems);
                })
        };

        function applyCalculatedSaleItems(selectedSaleItems) {
            angular.forEach($scope.sale.selectedStocks, function(stock, index) {
                var calculatedSaleItem = selectedSaleItems && selectedSaleItems[index] ? selectedSaleItems[index] : {};
                stock.salePrice = calculatedSaleItem.salePrice;
                stock.discountAmount = calculatedSaleItem.discountAmount;
                stock.discountPercent = calculatedSaleItem.discountPercent;
                stock.protectPlusApplied = calculatedSaleItem.protectPlusApplied;
            });
        }

        $scope.submitSale = function() {
            $scope.loading = true;
            $scope.serverErrorMessages = {};
            $scope.submitSaleErrorMessage = null;
            if (hasMissingRequiredSoldForDeviceModel() || hasMissingUnknownSoldForDeviceModelDescription()) {
                $scope.showSoldForDeviceModelValidation = true;
                $scope.loading = false;
                return;
            }
            var requestBody = {};

            requestBody.isCashPayment = $scope.sale.isCashPayment;
	            requestBody.employeeId = $scope.sale.employeeId;
	            requestBody.replacementSaleUSI = $scope.sale.replacementSaleUSI;
	            requestBody.protectPlusCertificateId = $scope.sale.protectPlusCertificateId;
	            requestBody.description = $scope.sale.description;
	            requestBody.storeId = SessionService.currentUser.employeeStoreId ? SessionService.currentUser.employeeStoreId : 0;
            requestBody.saleItems = [];
            angular.forEach($scope.sale.selectedStocks, function(stock){
                var item = {};
                item.itemId = stock.itemId;
                item.discountCode = stock.discountCode ? stock.discountCode.code : null;
                item.soldForDeviceModelId = getSoldForDeviceModelId(stock);
                requestBody.saleItems.push(item);
            });
            SaleService.submitSale(requestBody).then(
                function(response) {
                    // console.log("Call Fiscal Middleware!");
                    // console.log(response);
                    // FiscalMiddlewareService.printFiscalReceipt(response).then(
                    //     function(fiscalResponse) {
                    //         console.log("Success!");
                    //     },
                    //     function(error) {
                    //         console.log("Error!");
                    //     }
                    // )
	                    $scope.sale.selectedStocks = [];
	                    $scope.sale.paid = null;
	                    $scope.sale.isCashPayment = null;
	                    $scope.sale.employeeId = null;
	                    $scope.sale.description = null;
	                    $scope.sale.protectPlusCertificateId = null;
                    $scope.sale.selectedProtectPlusCertificate = null;
                    $scope.selectedProtectPlusCertificate = null;
                    $mdDialog.cancel();
                    $scope.loading = false;
                },
                function(errorResponse) {
                    $scope.loading = false;
                    $scope.serverErrorMessages = getServerErrorMessages(errorResponse);
                    $scope.submitSaleErrorMessage = getSubmitSaleErrorMessage($scope.serverErrorMessages.request);
                    if (hasSaleFormFieldErrors($scope.serverErrorMessages)) {
                        ServerValidationService.processServerErrors(errorResponse, $scope.saleForm);
                    }
                }
            );

        };

        $scope.removeStock = function(index, stock) {
            $scope.sale.selectedStocks.splice(index, 1);
            restoreStockQuantity(stock);
            calculateTotalSum();

            if ($scope.sale.selectedStocks.length < 1) {
                $mdDialog.cancel();
            }
        }

		$scope.closeModal = function () {
			//$uibModalInstance.dismiss('cancel');
            $mdDialog.cancel();
		};

        $scope.resetSale = function() {
            angular.forEach($scope.sale.selectedStocks, function(stock){
                restoreStockQuantity(stock);
            });
            $scope.sale.selectedStocks = [];
	            $scope.sale.paid = null;
	            $scope.sale.currency = null;
	            $scope.sale.description = null;
	            $scope.sale.protectPlusCertificateId = null;
            $scope.sale.selectedProtectPlusCertificate = null;
            $scope.selectedProtectPlusCertificate = null;
            $mdDialog.cancel();
        }

        function restoreStockQuantity(stock) {
            if (stock.sourceStock) {
                stock.sourceStock.quantity += 1;
            } else {
                stock.quantity += 1;
            }
        }

        $scope.onChangePaidAmount = function () {
            calculateTotalSum();
        }

        $scope.onChangeDiscountCode = function (stock) {
            if (stock.code) {
                DiscountService.getDiscountCode(stock.code).then(
                    function(discountCode) {
                        stock.discountCode = discountCode;
                        $scope.serverErrorMessages = {};
                        calculateTotalSum();
                    },
                    function(errorResponse) {
                        stock.discountCode = null
                        ServerValidationService.processServerErrors(errorResponse, $scope.saleForm);
                        $scope.serverErrorMessages = errorResponse.data.errors;
                        calculateTotalSum();
                    }                );
            } else {
                $scope.serverErrorMessages = {};
                stock.discountCode = null;
                calculateTotalSum();
            }
        }

	        $scope.onSoldForDeviceBrandChanged = function(stock) {
	            stock.soldForDeviceModelId = null;
	            stock.soldForDeviceModelSearchTerm = "";
	            calculateTotalSum();
	        }

	        $scope.onSoldForDeviceModelChanged = function(stock) {
	            stock.soldForDeviceModelId = getSoldForDeviceModelId(stock);
	            calculateTotalSum();
	        }

		        $scope.isSoldForDeviceModelRequired = isSoldForDeviceModelRequired;
		        $scope.isSoldForDeviceModelMissing = isSoldForDeviceModelMissing;
		        $scope.isSoldForUnknownDeviceModelSelected = isSoldForUnknownDeviceModelSelected;
		        $scope.hasUnknownSoldForDeviceModel = hasUnknownSoldForDeviceModel;
		        $scope.hasMissingUnknownSoldForDeviceModelDescription = hasMissingUnknownSoldForDeviceModelDescription;
		        $scope.hasMissingRequiredSoldForDeviceModel = hasMissingRequiredSoldForDeviceModel;

	        function isSoldForDeviceModelRequired(stock) {
	            return stock && (stock.soldForDeviceModelRequired === true || isProtectPlusSoldForDeviceModelRequired(stock));
	        }

	        function isProtectPlusSoldForDeviceModelRequired(stock) {
	            return isProtectPlusStock(stock) && getProtectPlusStocks().length > 1;
	        }

	        function isProtectPlusStock(stock) {
	            return stock && stock.productCode === '0500';
	        }

	        function getProtectPlusStocks() {
	            if (!$scope.sale.selectedStocks) {
	                return [];
	            }

	            return $scope.sale.selectedStocks.filter(function(stock) {
	                return isProtectPlusStock(stock);
	            });
	        }

	        function isSoldForDeviceModelMissing(stock) {
	            return isSoldForDeviceModelRequired(stock) && !getSoldForDeviceModelId(stock);
	        }

	        function hasMissingRequiredSoldForDeviceModel() {
	            return $scope.sale.selectedStocks && $scope.sale.selectedStocks.some(function(stock) {
	                return isSoldForDeviceModelMissing(stock);
	            });
	        }

	        function hasMissingProductRequiredSoldForDeviceModel() {
	            return $scope.sale.selectedStocks && $scope.sale.selectedStocks.some(function(stock) {
	                return stock && stock.soldForDeviceModelRequired === true && !getSoldForDeviceModelId(stock);
	            });
	        }

	        $scope.shouldShowSoldForDeviceModelMissing = function(stock) {
	            return $scope.showSoldForDeviceModelValidation && isSoldForDeviceModelMissing(stock);
	        }

	        $scope.shouldShowMissingUnknownSoldForDeviceModelDescription = function() {
	            return $scope.showSoldForDeviceModelValidation && hasMissingUnknownSoldForDeviceModelDescription();
	        }

	        $scope.clearSoldForDeviceModelSearchTerm = function(stock) {
	            stock.soldForDeviceModelSearchTerm = "";
	        }

	        function getSoldForDeviceModelId(stock) {
	            if (stock.soldForDeviceModel && stock.soldForDeviceModel.id) {
	                return stock.soldForDeviceModel.id;
	            }

	            return stock.soldForDeviceModelId;
	        }

	        function isSoldForUnknownDeviceModelSelected(stock) {
	            var soldForDeviceModelId = getSoldForDeviceModelId(stock);
	            if (!soldForDeviceModelId) {
	                return false;
	            }

	            return $scope.models.some(function(model) {
	                return model.id == soldForDeviceModelId && model.unknownModel === true;
	            });
	        }

	        function hasMissingUnknownSoldForDeviceModelDescription() {
	            return hasUnknownSoldForDeviceModel() && (!$scope.sale.description || !$scope.sale.description.trim());
	        }

	        function hasUnknownSoldForDeviceModel() {
	            return $scope.sale.selectedStocks && $scope.sale.selectedStocks.some(function(stock) {
	                return isSoldForUnknownDeviceModelSelected(stock);
	            });
	        }

        $scope.onChangeReplacementSaleUSI = function () {
            if ($scope.sale.replacementSaleUSI && $scope.sale.replacementSaleUSI.length == 20) {
                SaleService.getSaleByUSI($scope.sale.replacementSaleUSI).then(
                    function(sale) {
                        $scope.replacementSale = sale;
                        $scope.serverErrorMessages = {};
                    },
                    function(errorResponse) {
                        $scope.replacementSale = null;
                        ServerValidationService.processServerErrors(errorResponse, $scope.saleForm);
                        $scope.serverErrorMessages = errorResponse.data.errors;
                    }                );
            } else {
                $scope.serverErrorMessages = {};
                $scope.replacementSale = null;
            }
        }

        $scope.onIsReplacementChanged = function () {
            $scope.sale.replacementSaleUSI = null;
            $scope.replacementSale = null;
            $scope.serverErrorMessages = {};
        }

        $scope.searchProtectPlusCertificates = function () {
            $scope.protectPlusSearchPerformed = true;
            $scope.protectPlusCertificates = [];

            if (!$scope.protectPlusSearch.query || $scope.protectPlusSearch.query.length < 5) {
                return;
            }

            $scope.clearSelectedProtectPlusCertificate();
            ProtectPlusCertificateService.searchCertificates({query: $scope.protectPlusSearch.query}).then(
                function(response) {
                    $scope.protectPlusCertificates = response;
                },
                function(errorResponse) {
                    $scope.protectPlusCertificates = [];
                    ServerValidationService.processServerErrors(errorResponse, $scope.saleForm);
                    $scope.serverErrorMessages = errorResponse.data.errors;
                }
            );
        }

        $scope.selectProtectPlusCertificate = function (certificate) {
            if (!isProtectPlusCertificateSelectable(certificate)) {
                return;
            }

            $scope.selectedProtectPlusCertificate = certificate;
            $scope.sale.protectPlusCertificateId = certificate.id;
            $scope.sale.selectedProtectPlusCertificate = certificate;
            $scope.protectPlusSearch.query = certificate.certificateNumber;
            calculateTotalSum();
        }

        $scope.isProtectPlusCertificateSelectable = isProtectPlusCertificateSelectable;
        $scope.isProtectPlusCertificateExpired = isProtectPlusCertificateExpired;
        $scope.getProtectPlusCertificateSaleStatus = getProtectPlusCertificateSaleStatus;
        $scope.getProtectPlusCertificateExpirationNotice = getProtectPlusCertificateExpirationNotice;
        $scope.getProtectPlusCertificateValidUntil = getProtectPlusCertificateValidUntil;
        $scope.getProtectPlusCertificateFreeProtectorStatus = getProtectPlusCertificateFreeProtectorStatus;
        $scope.getProtectPlusCertificateDeviceChangeStatus = getProtectPlusCertificateDeviceChangeStatus;
        $scope.getProtectPlusCertificateFreeDisplayServiceStatus = getProtectPlusCertificateFreeDisplayServiceStatus;
        $scope.getProtectPlusCertificateFreeBatteryServiceStatus = getProtectPlusCertificateFreeBatteryServiceStatus;

        function isProtectPlusCertificateSelectable(certificate) {
            return certificate && (certificate.status == 'ACTIVE' || certificate.status == 'EXPIRED');
        }

        function isProtectPlusCertificateExpired(certificate) {
            return certificate && (certificate.status == 'EXPIRED' ||
                    (certificate.validUntilTimestamp && certificate.validUntilTimestamp < ApplicationService.getCurrentTimestamp()));
        }

        function getProtectPlusCertificateSaleStatus(certificate) {
            if (certificate.status == 'EXPIRED' && !certificate.validUntilTimestamp) {
                return 'Изтекъл';
            }
            if (!certificate.validUntilTimestamp) {
                return 'Няма валидност';
            }
            if (isProtectPlusCertificateExpired(certificate)) {
                return 'Изтекъл на ' + ApplicationService.convertEpochToDate(certificate.validUntilTimestamp);
            }

            return certificate.status;
        }

        function getProtectPlusCertificateExpirationNotice(certificate) {
            if (!isProtectPlusCertificateExpired(certificate)) {
                return null;
            }

            return 'Сертификатът е изтекъл, но ще бъде удължен безплатно с 6 месеца след тази покупка.';
        }

        function getProtectPlusCertificateValidUntil(certificate) {
            if (!certificate || !certificate.validUntilTimestamp) {
                return '';
            }

            return ApplicationService.convertEpochToDate(certificate.validUntilTimestamp);
        }

        function getProtectPlusCertificateFreeProtectorStatus(certificate) {
            return certificate && certificate.freeProtectorUsed ? 'Използван' : 'Наличен';
        }

        function getProtectPlusCertificateDeviceChangeStatus(certificate) {
            return certificate && certificate.deviceModelChangeUsed ? 'Използвана' : 'Налична';
        }

        function getProtectPlusCertificateFreeDisplayServiceStatus(certificate) {
            return certificate && certificate.freeDisplayReplacementServiceUsed ? 'Използван' : 'Наличен';
        }

        function getProtectPlusCertificateFreeBatteryServiceStatus(certificate) {
            return certificate && certificate.freeBatteryReplacementServiceUsed ? 'Използван' : 'Наличен';
        }

        $scope.clearSelectedProtectPlusCertificate = function () {
            $scope.selectedProtectPlusCertificate = null;
            $scope.sale.protectPlusCertificateId = null;
            $scope.sale.selectedProtectPlusCertificate = null;
            calculateTotalSum();
        }

        $scope.getSaleTimestamp = function(saleTimestamp) {
            return ApplicationService.convertEpochToTimestamp(saleTimestamp)
        };

        function getSubmitSaleErrorMessage(errorCode) {
            if (errorCode === 'protectPlusPurchaseRequiresProtector') {
                return 'Protect+ сертификат може да бъде закупен само заедно с протектор.';
            }
	            if (errorCode === 'protectPlusSoldForDeviceModelRequired') {
	                return 'Попълнете "Продаден за" за всеки Protect+ сертификат в продажбата.';
	            }
	            if (errorCode === 'protectPlusProtectorForDeviceModelRequired') {
	                return 'За всеки Protect+ сертификат трябва да има протектор за същия модел устройство.';
	            }
	            if (errorCode === 'protectPlusPurchaseAndUsageInSameSale') {
	                return 'Protect+ сертификат не може да бъде закупен и използван в една и съща продажба.';
	            }
		            if (errorCode === 'soldForDeviceModelRequired') {
		                return 'Попълнете "Продаден за" за продуктите, които изискват модел устройство.';
		            }
		            if (errorCode === 'soldForUnknownDeviceModelDescriptionRequired') {
		                return 'Опишете реалния модел устройство, когато "Продаден за" е Unknown.';
		            }
		            return errorCode;
		        }

        function getServerErrorMessages(errorResponse) {
            if (!errorResponse || !errorResponse.data) {
                return {};
            }

            if (angular.isString(errorResponse.data)) {
                try {
                    return angular.fromJson(errorResponse.data).errors || {};
                } catch (e) {
                    return {request: errorResponse.data};
                }
            }

            return errorResponse.data.errors || {};
        }

        function hasSaleFormFieldErrors(errors) {
            return Object.getOwnPropertyNames(errors).some(function(field) {
                return field !== 'request' && angular.isObject($scope.saleForm[field]);
            });
        }

	});
