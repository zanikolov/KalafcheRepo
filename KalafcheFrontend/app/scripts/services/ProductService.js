'use strict';

angular.module('kalafcheFrontendApp')
	.service('ProductService', function($http, Environment) {
		angular.extend(this, {
			submitProduct: submitProduct,
            getAllProducts: getAllProducts,
            getProductByCode: getProductByCode,
            getProductSpecificPrice: getProductSpecificPrice,
            getAllTypes: getAllTypes,
            getAllProductMasterTypes: getAllProductMasterTypes,
            submitProductType: submitProductType
		});

        function submitProduct(product) { 
            if (product.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/product', product)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/product', product)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

        function submitProductType(productType) { 
            if (productType.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/product/type', productType)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/product/type', productType)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

        function getAllProducts() {   
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/product')
                .then(
                    function(response) {
                        return response.data;
                    }
                ) ;
        }

        function getProductSpecificPrice(productId) {   
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/product/specificPrice/' + productId)
                .then(
                    function(response) {
                        return response.data;
                    }
                ) ;
        }

        function getProductByCode(code) { 
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/product/' + code)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAllTypes() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/product/type')
                .then(
                    function(response) {
                        return response.data;
                    }
                ) ;
        }

        function getAllProductMasterTypes() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/product/masterType')
                .then(
                    function(response) {
                        return response.data;
                    }
                ) ;
        }

	});