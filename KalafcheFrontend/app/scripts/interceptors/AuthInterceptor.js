'use strict';
  
angular.module('kalafcheFrontendApp')
  .factory('AuthInterceptor', function ($rootScope, $q, AuthEvents) {
  return {
    responseError: function (response) { 
      console.log("<> <> <>");
      console.log(response);
      $rootScope.$broadcast({
        '-1': AuthEvents.notAuthenticated
      }[response.status], response);
      $rootScope.$broadcast({
        '401': AuthEvents.notAuthenticated
      }[response.status], response);
      return $q.reject(response);
    }
  };
})