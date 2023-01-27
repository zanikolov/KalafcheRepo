'use strict';

angular.module('kalafcheFrontendApp')
	.factory('SessionTimeoutInterceptor', function ($rootScope, $q, AuthEvents) {
		return {
			response: function (response) { 
				//console.log(response);
				if ((typeof response.data === 'string') && response.data.indexOf('Login Page') != -1) {
					$rootScope.$broadcast(AuthEvents.sessionTimeout);

					return $q.reject(response);
				} else{
					return response;
				}	
			}
		};
})