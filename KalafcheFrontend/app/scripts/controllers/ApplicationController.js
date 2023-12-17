'use strict';

angular.module('kalafcheFrontendApp')
	.controller('ApplicationController', function ($scope, $rootScope, $mdSidenav, $mdDialog, UserRoles, AuthService, AuthEvents, SessionService, ApplicationService) {

	  init();

		function init() {
      $scope.userRoles = UserRoles;
      $scope.credentials = {};
      $scope.currentUser = SessionService.getCurrentUser();
		};

    $scope.logout = function() {
        AuthService.logout().then(function (response) {
            $scope.toggleSidenav('left');
            SessionService.destroy();
            $rootScope.currentUser = {};
            $rootScope.$broadcast(AuthEvents.logoutSuccess);
        });
    }

    $rootScope.$on(AuthEvents.loginSuccess, function () {
	    $scope.currentUser = SessionService.currentUser;
    })

  	$scope.isAuthorized = function(roles) {
  		return AuthService.isAuthorized(roles);
  	}

  	$scope.isAdmin = function() {
  		var roles = SessionService.currentUser.userRoles;

  		if (roles) {
	  		for (var i = 0; i < roles.length; i++) {
                var role = roles[i];

                if (role === "ROLE_ADMIN" || role === "ROLE_SUPERADMIN") {
                    return true;
                }
            }
		  }

      return false;
  	}	

  	$scope.isSuperAdmin = function() {
  		var roles = SessionService.currentUser.userRoles;
  		if (roles) {
	  		for (var i = 0; i < roles.length; i++) {
                var role = roles[i];
                if (role === "ROLE_SUPERADMIN") {
                    return true;
                }
            }
		  }

      return false;
  	}

    $scope.isManager = function() {
      var roles = SessionService.currentUser.userRoles;
      if (roles) {
        for (var i = 0; i < roles.length; i++) {
                var role = roles[i];
                if (role === "ROLE_MANAGER") {
                    return true;
                }
            }
      }

      return false;
    }

    $scope.isUser = function() {
      var roles = SessionService.currentUser.userRoles;
      if (roles) {
        for (var i = 0; i < roles.length; i++) {
                var role = roles[i];
                if (role === "ROLE_USER") {
                    return true;
                }
            }
      }

      return false;
    }

  	$scope.convertEpochToDate = function(epochTime) {

          if(epochTime != 0) {
              var timeStamp = new Date(epochTime);

              var minutes = ApplicationService.getTwoDigitNumber(timeStamp.getMinutes());
              var hh = ApplicationService.getTwoDigitNumber(timeStamp.getHours());
              var dd = ApplicationService.getTwoDigitNumber(timeStamp.getDate());
              var mm = ApplicationService.getTwoDigitNumber(timeStamp.getMonth() + 1);
              var yyyy = timeStamp.getFullYear();

              return dd + '-' + mm + '-' + yyyy + ' ' + hh + ':' + minutes;
          } else {
              return '';
          }
      };

      $scope.login = function() {
          AuthService.login($scope.credentials).then(function (response) {
                            console.log("---------");
              console.log(response.data);
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

      $scope.toggleSidenav = function(menuId) {
        $mdSidenav(menuId).toggle();
      };

      $scope.menu = [
        {
          link : 'assortment',
          title: 'Асортимент',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        }
        ,{
          link : 'inStock',
          title: 'Търсене',
          icon: 'dashboard',
          admin: true,
          manager: true,
          user: true
        },    
        {
          link : 'new-stock',
          title: 'Нова стока',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: true
        },  
        {
          link : 'device',
          title: 'Устройства',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        },    
        {
          link : 'employee',
          title: 'Служители',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        },
        {
          link : 'store',
          title: 'Обекти',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        },
        {
          link : 'loyalCustomer',
          title: 'Лоялни клиенти',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        },        
        {
          link : 'partner',
          title: 'Партньори',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        },
        {
          link : 'partnerStore',
          title: 'Обекти партньори',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        },
        {
          link : 'saleReport',
          title: 'Справки',
          icon: 'dashboard',
          admin: true,
          manager: true,
          user: true
        },        
        {
          link : 'wasteReport',
          title: 'Брак',
          icon: 'dashboard',
          admin: true,
          manager: true,
          user: true
        },        
        {
          link : 'refundReport',
          title: 'Рекламации',
          icon: 'dashboard',
          admin: true,
          manager: true,
          user: true
        },
        {
          link : 'relocation',
          title: 'Релокации',
          icon: 'dashboard',
          admin: true,
          manager: true,
          user: true
        },
        {
          link : 'activityReport',
          title: 'Активности',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        },
        {
          link : 'expense',
          title: 'Разходи',
          icon: 'dashboard',
          admin: true,
          manager: true,
          user: true
        },        
        {
          link : 'dailyStoreReport',
          title: 'Дневни отчети',
          icon: 'dashboard',
          admin: true,
          manager: true,
          user: true
        },
        {
          link : 'schedule',
          title: 'Графици',
          icon: 'dashboard',
          admin: true,
          manager: true,
          user: true
        },
        {
          link : 'revision',
          title: 'Ревизии',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: true
        },
        {
          link : 'discount',
          title: 'Промоции',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        },
        {
          link : 'rawItem',
          title: 'Баркодове',
          icon: 'dashboard',
          admin: true,
          manager: false,
          user: false
        }
      ];
	});