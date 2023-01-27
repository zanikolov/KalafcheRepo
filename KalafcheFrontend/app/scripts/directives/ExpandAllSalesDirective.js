'use strict';

angular.module('kalafcheFrontendApp')        
.directive('expand', function () {
            function link(scope, element, attrs) {
                scope.$on('onExpandAll', function (event, args) {
                    angular.forEach(scope.sales, function(sale){
                        console.log("<><><>");
                        sale.expanded = args.expanded;
                    });
                });
            }
            return {
                link: link
            };
        });