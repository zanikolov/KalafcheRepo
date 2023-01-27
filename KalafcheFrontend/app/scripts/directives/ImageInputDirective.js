'use strict';

angular.module('kalafcheFrontendApp')
  .directive("imageinput", [function() {
    return {
      scope: {
        imageinput: "=",
        filepreview: "="
      },
      link: function(scope, element, attributes) {
        element.bind("change", function(changeEvent) {
          scope.imageinput = changeEvent.target.files[0];
          var reader = new FileReader();
          reader.onload = function(loadEvent) {
            scope.$apply(function() {
              scope.filepreview = loadEvent.target.result;
            });
          }
          reader.readAsDataURL(scope.imageinput);
        });
      }
    }
  }]);