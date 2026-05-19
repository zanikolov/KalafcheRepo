'use strict';

angular.module('kalafcheFrontendApp')
	.service('ProtectPlusCertificateService', function($http, Environment) {
		angular.extend(this, {
			getInactiveCertificates: getInactiveCertificates,
			searchCertificates: searchCertificates,
			activateCertificate: activateCertificate
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

	});
