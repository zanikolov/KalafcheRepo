'use strict';

angular.module('kalafcheFrontendApp')
    .directive('newStockValidation', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/new-stock/validation.html',
            controller: NewStockValidationController
        }
    });

    function NewStockValidationController($scope, $mdDialog, ServerValidationService, NewStockService) {

        init();

        function init() {
            $scope.unexistingItems = [];
        };

        $scope.validateFile = function () {
            var file = $scope.file;
            console.log(file);
            NewStockService.validateFile(file).then(function(response) {
                $scope.unexistingItems = response;
            },
            function(errorResponse) {
                ServerValidationService.processServerErrors(errorResponse, $scope.fileForm);
                $scope.serverErrorMessages = errorResponse.data.errors;
            });
        };

        $scope.openCreateItemModal = function (item) {
            $scope.selectedItem = item;

            $mdDialog.show({
                locals:{selectedItem: $scope.selectedItem},
                controller: function ($scope, $mdDialog, selectedItem){

                    $scope.selectedItem = selectedItem;

                    $scope.closeModal = function() {
                        $mdDialog.cancel();
                    }

                    $scope.$on('submitSuccess', function (event, data) {
                        $scope.selectedItem.added = true;
                        $mdDialog.cancel();
                    });
                },
                templateUrl: 'views/partials/new-stock/validation-modal.html',
                parent: angular.element(document.body),
                clickOutsideToClose:true
            })
            .then(function(selectedItem) {
                $scope.selectedItem = selectedItem;
            }, function() {
              $scope.status = 'You cancelled the dialog.';
            });
        };

  };