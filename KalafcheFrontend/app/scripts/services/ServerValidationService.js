'use strict';

angular.module('kalafcheFrontendApp')
    .service('ServerValidationService', function($mdDialog) {
        angular.extend(this, {
            processServerErrors: processServerErrors
        });

        function processServerErrors(errorResponse, formController) {
            if (errorResponse.status == 409 || errorResponse.status == 400 || errorResponse.status == 404) {
                    // if (errorResponse.data.non_field_errors) {
                    //     formController.$setValidity('serverError', false);
                    //     formController.$setSubmitted();
                    // }
                Object.getOwnPropertyNames(errorResponse.data.errors).forEach(function (field) {
                    if (angular.isObject(formController[field]) &&
                        formController[field].hasOwnProperty('$modelValue')) {
                        console.log(formController[field]);
                        formController[field].$setValidity('serverError', false);
                        formController[field].$setTouched();
                    } else {
                        console.error('Unknown server error ${field}.');
                    }
                });
            } else {   
                $mdDialog.show({
                    controller: function($scope, $mdDialog) { 
                        $scope.closeModal = function() {
                            $mdDialog.cancel();
                        }; 
                    },
                    templateUrl: 'views/modals/error-modal.html',
                    clickOutsideToClose:true,
                    parent: angular.element(document.body)
                })
            }
        }

    });