'use strict';
  
angular.module('kalafcheFrontendApp')
    .service('SessionService', function ($cookies, $rootScope, AuthEvents, Environment) {
        angular.extend(this, {
            create: create,
            destroy: destroy,
            getCurrentUser: getCurrentUser
        });

        this.currentUser = {};

        function create(employee) {
            console.log(employee);
         
            this.currentUser.employeeId = employee.id;
            this.currentUser.userId = employee.userId;
            this.currentUser.employeeName = employee.name;
            this.currentUser.username = employee.username;
            this.currentUser.employeeStoreId = employee.storeId;
            this.currentUser.employeeJobResponsibilityId = employee.jobResponsibilityId;
            this.currentUser.userRoles = employee.roles;

            $cookies.putObject("currentUser", this.currentUser);

            $rootScope.$broadcast(AuthEvents.loginSuccess);
        };

        function destroy() {
            this.currentUser = {};
            $cookies.remove("currentUser")
        };

        function getCurrentUser() {
            return $cookies.getObject("currentUser");
        }
    }
);