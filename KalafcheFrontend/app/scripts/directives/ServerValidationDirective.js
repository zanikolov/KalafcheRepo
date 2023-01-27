'use strict';

angular.module('kalafcheFrontendApp')
    .directive('serverValidation', function() {
        return {
            restrict: 'A',
            require: 'form',
            link: function(scope, element, attrs, formController) {
                // const resetServerValidity = fieldController => {
                //     const storedFieldController = fieldController;
                //     return function() {
                //         storedFieldController.$setValidity('serverError', true);
                //         formController.$setValidity('serverError', true);
                //         return true;
                //     };
                // };
                // scope.$watchCollection(() => formController, updatedFormController => {
                //     angular.forEach(Object.getOwnPropertyNames(updatedFormController), function(field) {
                //         // Search for form controls with ng-model controllers
                //         // Which do not have attached server error resetter validator
                //         if (angular.isObject(updatedFormController[field]) &&
                //             updatedFormController[field].hasOwnProperty('$modelValue') &&
                //             angular.isObject(updatedFormController[field].$validators) &&
                //             !updatedFormController[field].$validators.hasOwnProperty('serverValidityResetter')) {
                //             updatedFormController[field].$validators.serverValidityResetter = resetServerValidity(updatedFormController[field]);
                //         }
                //     });
                // });

                var resetServerValidity = function resetServerValidity(fieldController) {
                    var storedFieldController = fieldController;
                    return function () {
                        storedFieldController.$setValidity('serverError', true);
                        formController.$setValidity('serverError', true);
                        return true;
                    };
                };
                scope.$watchCollection(function () {
                        return formController;
                    }, function (updatedFormController) {
                        angular.forEach(Object.getOwnPropertyNames(updatedFormController), function (field) {
                        // Search for form controls with ng-model controllers
                        // Which do not have attached server error resetter validator
                        if (angular.isObject(updatedFormController[field]) && updatedFormController[field].hasOwnProperty('$modelValue') && angular.isObject(updatedFormController[field].$validators) && !updatedFormController[field].$validators.hasOwnProperty('serverValidityResetter')) {
                            updatedFormController[field].$validators.serverValidityResetter = resetServerValidity(updatedFormController[field]);
                        }
                    });
                });

            }
        };
    });