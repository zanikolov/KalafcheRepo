'use strict';

angular.module('kalafcheFrontendApp')
    .directive('saleModal', function () {
        return {
            templateUrl: 'views/directives/sale-modal-directive.html',
            restrict: 'E',
            controller: 'InStockController',
            replace:true,
            link: function postLink(scope, element, attrs) {
                scope.title = attrs.title;
                
                scope.$watch(attrs.visibility, function(value){
                    if(value == true) {
                        $(element).modal('show');
                    }
                    else {
                        $(element).modal('hide');
                    }
                });

                $(element).on('shown.bs.modal', function(){
                    scope.$apply(function(){
                        scope.showSaleModal = true;
                    });
                });

                $(element).on('hidden.bs.modal', function(){
                    scope.$apply(function(){
                        scope.showSaleModal = false;
                        scope.partnerCode = null;
                        scope.salePrice = null;
                    });
                });
            }
        };
    });