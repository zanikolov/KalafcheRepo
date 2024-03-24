'use strict';

angular.module('kalafcheFrontendApp')
	.service('FormulaService', function($http, Environment, FileSaver) {
		angular.extend(this, {
            calculate: calculate,
            getAllFormulas: getAllFormulas,
            submitFormula: submitFormula,
            deleteFormula: deleteFormula,
            getAllAttributes: getAllAttributes,
            submitAttribute: submitAttribute,
            deleteAttribute: deleteAttribute,
            getAllAttributeTypes: getAllAttributeTypes,
            getAllAttributeContexts: getAllAttributeContexts
		});

        function calculate(formula) { 
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/formula/calculate', formula)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAllFormulas() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/formula')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function submitFormula(formula) { 
            if (formula.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/formula', formula)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/formula', formula)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

        function getAllAttributes() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/formula/attribute')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function submitAttribute(attribute) { 
            if (attribute.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/formula/attribute', attribute)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/formula/attribute', attribute)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

        function deleteAttribute(attribute) { 
            return $http.delete(Environment.apiEndpoint + '/KalafcheBackend/formula/attribute',  {"params": {"attributeId": attribute.id}})
                .then(
                    function(response) {
                        return response.data;
                    }
                ); 
        }

        function deleteFormula(formula) { 
            return $http.delete(Environment.apiEndpoint + '/KalafcheBackend/formula',  {"params": {"formulaId": formula.id}})
                .then(
                    function(response) {
                        return response.data;
                    }
                ); 
        }

        function getAllAttributeTypes() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/formula/attribute/type')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function getAllAttributeContexts() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/formula/attribute/context')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }
	});