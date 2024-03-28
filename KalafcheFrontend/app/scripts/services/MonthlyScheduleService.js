'use strict';

angular.module('kalafcheFrontendApp')
	.service('MonthlyScheduleService', function($http, Environment, FileSaver) {
		angular.extend(this, {
			searchMonthlySchedule: searchMonthlySchedule,
            searchPresentForm: searchPresentForm,
            generateMonthlySchedule: generateMonthlySchedule,
            updateMonthlySchedule: updateMonthlySchedule,
            updateDailyShift: updateDailyShift,
            finalizeMonthlySchedule: finalizeMonthlySchedule,
            finalizePresentForm: finalizePresentForm,
            getPresentFormsForFinalizing: getPresentFormsForFinalizing,
            printPresentForm: printPresentForm,
            printMonthlySchedule: printMonthlySchedule,
            addEmployeeToPresentForm: addEmployeeToPresentForm
		});

        function searchMonthlySchedule(storeId, month, year) {
            var params = {"params" : 
                {"storeId": storeId, 
                "month": month, 
                "year": year}};
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function searchPresentForm(storeId, month, year) {
            var params = {"params" : 
                {"storeId": storeId, 
                "month": month, 
                "year": year}};
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule/presentForm', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function generateMonthlySchedule(monthlySchedule) {
            return $http.put(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule', monthlySchedule)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
        }

        function finalizeMonthlySchedule(monthlySchedule) {
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule', monthlySchedule)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
        }

        function finalizePresentForm(monthlySchedule) {
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule/presentForm', monthlySchedule)
                    .then(
                        function(response) {
                            return response.data;
                        }
                    )
        }

        function updateMonthlySchedule(monthlySchedule) { 
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule', monthlySchedule)
                .then(
                    function(response) {
                        return response.data;
                    }
                )
        }

        function updateDailyShift(dailyShift) { 
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule/dailyShift', dailyShift)
                .then(
                    function(response) {
                        return response.data;
                    }
                )
        }

        function getPresentFormsForFinalizing(month, year) {
            var params = {"params" : 
                {"month": month, 
                "year": year}};
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule/presentFormsForFinalizing', params)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

        function printPresentForm(companyId, selectedMonth) { 
            var request = {};
            request.companyId = companyId;
            request.month = selectedMonth.value;
            request.year = selectedMonth.year;

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule/presentFormReport', request, {responseType: "arraybuffer"})
                .then(
                    function(response) {
                        var blob = new Blob([response.data], {type: "application/vnd.openxmlformat-officedocument.spreadsheetml.sheet;"});
                        FileSaver.saveAs(blob, 'Присъствена форма ' + selectedMonth.value + '-' + selectedMonth.year + '.xlsx')
                    }
                );
        }

        function printMonthlySchedule(storeId, selectedMonth) { 
            var request = {};
            request.storeId = storeId;
            request.month = selectedMonth.value;
            request.year = selectedMonth.year;

            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule/print', request, {responseType: "arraybuffer"})
                .then(
                    function(response) {
                        var blob = new Blob([response.data], {type: "application/vnd.openxmlformat-officedocument.spreadsheetml.sheet;"});
                        FileSaver.saveAs(blob, 'График_' + selectedMonth.value + '-' + selectedMonth.year + '.xlsx')
                    }
                );
        }

        function addEmployeeToPresentForm(presentFormId, employeeId) {
            var params = {"params" : 
                {"presentFormId": presentFormId, 
                "employeeId": employeeId}};
            return $http.post(Environment.apiEndpoint + '/KalafcheBackend/monthlySchedule/presentForm/' + presentFormId + '/employee/' + employeeId)
                .then(
                    function(response) {
                        return response.data
                    }
                );
        }

	});