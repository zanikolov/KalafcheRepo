'use strict';

angular.module('kalafcheFrontendApp')
		.service('ProtectPlusCertificateService', function($http, Environment, FileSaver) {
			angular.extend(this, {
				getInactiveCertificates: getInactiveCertificates,
				searchCertificates: searchCertificates,
				activateCertificate: activateCertificate,
	            changeDeviceModel: changeDeviceModel,
	            getUsageRecords: getUsageRecords,
	            getRenewalRecords: getRenewalRecords,
	            getDeviceModelChangeRecords: getDeviceModelChangeRecords,
	            getCallRecords: getCallRecords,
	            uploadCallRecording: uploadCallRecording,
	            downloadCallRecording: downloadCallRecording,
	            downloadGdprConsent: downloadGdprConsent,
	            updateCustomerEmail: updateCustomerEmail
			});

		function getInactiveCertificates() {
			return $http.get(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/inactive')
				.then(
					function(response) {
						return response.data;
					}
				);
		}

		function searchCertificates(filters, phoneNumber) {
			var searchFilters = angular.isObject(filters) ? filters : {
				certificateNumber: filters,
				phoneNumber: phoneNumber
			};
			var params = {"params": {}};

			angular.forEach(searchFilters, function(value, key) {
				if (value !== null && value !== undefined && value !== '') {
					params.params[key] = value;
				}
			});

			return $http.get(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/search', params)
				.then(
					function(response) {
						return response.data;
					}
				);
		}

		function activateCertificate(certificateId, certificate, gdprConsentImage) {
			var fileFormData = new FormData();
			fileFormData.append('certificate', new Blob([angular.toJson(certificate)], { type: 'application/json' }));
			fileFormData.append('gdprConsentImage', gdprConsentImage);

			return $http.post(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificateId + '/activate',
				fileFormData, {
					transformRequest: angular.identity,
					headers: {'Content-Type': undefined}
				}).then(function(response) {
					return response.data;
				});
		}
		
		function getCallRecords(certificateId) {
			return $http.get(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificateId + '/callRecords')
				.then(function(response) {
					return response.data;
				});
		}

		function getUsageRecords(certificateId) {
			return $http.get(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificateId + '/usageRecords')
				.then(function(response) {
					return response.data;
				});
		}

		function getRenewalRecords(certificateId) {
			return $http.get(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificateId + '/renewalRecords')
				.then(function(response) {
					return response.data;
				});
		}

		function getDeviceModelChangeRecords(certificateId) {
			return $http.get(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificateId + '/deviceModelChangeRecords')
				.then(function(response) {
					return response.data;
				});
		}

        function uploadCallRecording(certificateId, callRecording, note) {
            var fileFormData = new FormData();
            fileFormData.append('callRecording', callRecording);
            if (note) {
                fileFormData.append('note', note);
            }

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificateId + '/callRecording',
                fileFormData, {
                    transformRequest: angular.identity,
                    headers: {'Content-Type': undefined}
                }).then(function(response) {
                    return response.data;
                });
        }

		function downloadCallRecording(certificateId, callRecord) {
			var params = {responseType: 'arraybuffer'};
			return $http.get(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificateId +
				'/callRecords/' + callRecord.id + '/download', params).then(function(response) {
					var blob = new Blob([response.data], {type: 'application/octet-stream'});
					FileSaver.saveAs(blob, callRecord.callRecordingFileName);
				});
		}

		function downloadGdprConsent(certificate) {
			var params = {responseType: 'arraybuffer'};
			return $http.get(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificate.id +
				'/gdprConsent/download', params).then(function(response) {
					var blob = new Blob([response.data], {type: 'application/octet-stream'});
					FileSaver.saveAs(blob, 'protect-plus-gdpr-' + certificate.certificateNumber);
				});
		}


		function updateCustomerEmail(certificateId, email) {
			return $http.post(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificateId + '/customerEmail', {
				email: email
			}).then(function(response) {
				return response.data;
			});
		}

		function changeDeviceModel(certificateId, deviceModelId) {
			return $http.post(Environment.apiEndpoint + '/KalafcheBackend/protectPlus/certificate/' + certificateId + '/deviceModel', {
				deviceModelId: deviceModelId
			}).then(function(response) {
				return response.data;
			});
		}

	});
