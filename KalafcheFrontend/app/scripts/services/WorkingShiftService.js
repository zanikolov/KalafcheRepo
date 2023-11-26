'use strict';

angular.module('kalafcheFrontendApp')
    .service('WorkingShiftService', function($http, Environment) {
        angular.extend(this, {
            getAllWorkingShifts: getAllWorkingShifts,
            submitWorkingShift: submitWorkingShift
        });

        function getAllWorkingShifts() {
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/workingShift')
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function submitWorkingShift(workingShift) { 
            if (workingShift.id) {
                return $http.post(Environment.apiEndpoint + '/KalafcheBackend/workingShift', workingShift)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            } else {
                return $http.put(Environment.apiEndpoint + '/KalafcheBackend/workingShift', workingShift)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
            }
        }

    });