'use strict';

angular.module('kalafcheFrontendApp')
.directive('itemForm', function() {
	return {
		restrict: 'E',
		scope: {
			item: '='
		},
		templateUrl: "views/partials/assortment/item-form.html"
	};
});