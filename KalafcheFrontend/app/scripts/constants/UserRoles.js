'use strict';

angular.module('kalafcheFrontendApp')
	.constant('UserRoles', {
	  	all: '*',
	  	superAdmin: 'ROLE_SUPERADMIN',
  		admin: 'ROLE_ADMIN',
  		manager: 'ROLE_MANAGER',
  		user: 'ROLE_USER'
	}
);