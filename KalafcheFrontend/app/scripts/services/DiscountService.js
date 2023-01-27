'use strict';

angular.module('kalafcheFrontendApp')
	.service('DiscountService', function($http, Environment) {
		angular.extend(this, {
			getAllDiscountTypes: getAllDiscountTypes,
            getAllDiscountCampaigns: getAllDiscountCampaigns,
            submitCampaign: submitCampaign,
            getAllDiscountCodes: getAllDiscountCodes,
            submitDiscountCode: submitDiscountCode,
            getDiscountCode: getDiscountCode,
            getAvailablePartnerDiscountCodes: getAvailablePartnerDiscountCodes,
            getAvailableLoyalDiscountCodes: getAvailableLoyalDiscountCodes
		});

        function getAllDiscountTypes() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/discount/type')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAllDiscountCampaigns() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/discount/campaign')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function submitCampaign(campaign) { 
            if (campaign.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/discount/campaign', campaign)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/discount/campaign', campaign)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

        function getAllDiscountCodes() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/discount/code')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAvailablePartnerDiscountCodes() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/discount/code/partner')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAvailableLoyalDiscountCodes() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/discount/code/loyal')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function submitDiscountCode(discountCode) { 
            if (discountCode.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/discount/code', discountCode)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/discount/code', discountCode)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

        function getDiscountCode(code) {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/discount/code/' + code)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

	});