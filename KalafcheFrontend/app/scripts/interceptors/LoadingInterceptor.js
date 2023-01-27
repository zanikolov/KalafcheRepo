angular.module('kalafcheFrontendApp').service('LoadingInterceptor', ['$q', '$rootScope', '$log', 
function ($q, $rootScope, $log) {
    'use strict';
 
    var xhrCreations = 0;
    var xhrResolutions = 0;
 
    function isLoading() {
        return xhrResolutions < xhrCreations;
    }
 
    function updateStatus() {
        $rootScope.isLoading = isLoading();


            // $mdDialog.show({
            //   template: '',
            //   parent: angular.element(document.body),
            //   clickOutsideToClose:true
            // })
            // .then(function(answer) {
            //   $scope.status = 'You said the information was "".';
            // }, function() {
            //   $scope.status = 'You cancelled the dialog.';
            // });
    }
 
    return {
        request: function (config) {
            xhrCreations++;
            updateStatus();
            return config;
        },
        requestError: function (rejection) {
            xhrResolutions++;
            updateStatus();
            $log.error('Request error:', rejection);
            return $q.reject(rejection);
        },
        response: function (response) {
            xhrResolutions++;
            updateStatus();
            return response;
        },
        responseError: function (rejection) {
            xhrResolutions++;
            updateStatus();
            $log.error('Response error:', rejection);
            return $q.reject(rejection);
        }
    };
}]);