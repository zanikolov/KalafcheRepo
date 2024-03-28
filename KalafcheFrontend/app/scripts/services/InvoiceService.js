'use strict';

angular.module('kalafcheFrontendApp')
	.service('InvoiceService', function($http, SessionService, Environment, FileSaver) {
		angular.extend(this, {
			generateInvoice: generateInvoice
		});

        function generateInvoice(companyId, startDateMilliseconds, endDateMilliseconds) {
                        var params = {"params" : {"startDateMilliseconds": startDateMilliseconds, "endDateMilliseconds": endDateMilliseconds, 
                "companyId": companyId}, "responseType": "arraybuffer"};
            return $http.get(Environment.apiEndpoint + '/KalafcheBackend/invoice', params)
                .then(
                    function(response) {
                        var fileName = getFileName(response.headers('Content-Disposition'));
                        var blob = new Blob([response.data], {type: "application/pdf"});
                        FileSaver.saveAs(blob, fileName);
                    }
                ); 
        }

        function getFileName(contentDispositionHeader) {
            var result = contentDispositionHeader.split(';')[1].trim().split('=')[1];
            return result.replace(/"/g, '');
        }

	});