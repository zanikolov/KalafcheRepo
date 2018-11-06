'use strict';

angular.module('kalafcheFrontendApp')
.directive('itemForm', function() {
	return {
		restrict: 'E',
		scope: {
			item: '='
		},
		templateUrl: "../views/directives/item/item-form.html"
	};
});