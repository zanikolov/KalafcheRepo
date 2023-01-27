'use strict';

angular.module('kalafcheFrontendApp')
	.service('ApplicationService', function() {
		angular.extend(this, {
			validateDuplication: validateDuplication,
            validateProductCodeDuplication: validateProductCodeDuplication,
            getCurrentTimestamp: getCurrentTimestamp,
            getTwoDigitNumber: getTwoDigitNumber,
            convertEpochToDate: convertEpochToDate,
            convertEpochToTimestamp: convertEpochToTimestamp
		});

        function validateDuplication(valueForValidation, list) {  
            for (var i = 0; i < list.length; i++) {
                var current = list[i];

                if (valueForValidation === current.name) {
                    return false;
                }
            }

            return true;
        };

        function validateProductCodeDuplication(product, products) {
            for (var i = 0; i < products.length; i++) {
                var current = products[i];

                if (product.code === current.code) {
                    return false;
                }
            }

            return true;
        };

        function getCurrentTimestamp() { 
            var today = new Date();

            return today.getTime();         
        };

        function getTwoDigitNumber(number) { 
            if (number < 10) {
                number = "0" + number;
            }   

            return number;      
        };

        function convertEpochToTimestamp(epochTime) {

          if(epochTime != 0) {
              var timeStamp = new Date(epochTime);

              var minutes = getTwoDigitNumber(timeStamp.getMinutes());
              var hh = getTwoDigitNumber(timeStamp.getHours());
              var dd = getTwoDigitNumber(timeStamp.getDate());
              var mm = getTwoDigitNumber(timeStamp.getMonth() + 1);
              var yyyy = timeStamp.getFullYear();

              return dd + '-' + mm + '-' + yyyy + ' ' + hh + ':' + minutes;
          } else {
              return '';
          }
      };

      function convertEpochToDate(epochTime) {

          if(epochTime != 0) {
              var timeStamp = new Date(epochTime);

              var dd = getTwoDigitNumber(timeStamp.getDate());
              var mm = getTwoDigitNumber(timeStamp.getMonth() + 1);
              var yyyy = timeStamp.getFullYear();

              return dd + '-' + mm + '-' + yyyy;
          } else {
              return '';
          }
      };

	});