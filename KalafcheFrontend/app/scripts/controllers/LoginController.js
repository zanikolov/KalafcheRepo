'use strict';

angular.module('kalafcheFrontendApp')
    .controller('LoginController', function($scope, $rootScope, AuthService, AuthEvents, SessionService) {
        $scope.credentials = {};
        $scope.currentUser = {};

        $scope.login = function() {
            AuthService.login($scope.credentials).then(function (response) {
                SessionService.create(response.data);
                $scope.currentUser = SessionService.currentUser;
                $rootScope.currentUser = SessionService.currentUser;
            }, function (response) {
                if (response.status === 401) {
                    $scope.loginForm.username.$invalid = true;
                    $scope.loginForm.password.$invalid = true;
                };
            });
        }
    });