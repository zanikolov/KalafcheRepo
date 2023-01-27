'use strict';

angular.module('kalafcheFrontendApp')
	.controller('ProductModalController', function ($scope, $uibModalInstance, selectedProduct, ProductService, SessionService, ApplicationService) {

        init();

        function init() {
            $scope.selectedProduct = selectedProduct;
            $scope.isSubmitButonDisabled = false;
            $scope.invalidPartnerCodeErrorText = "Несъществуващ код!";
            $scope.invalidSalePriceErrorText = "Въведете намалената цена!";

            $scope.showPartnerCodeErrorText = false;
            $scope.showSalePriceErrorText = false;
        };

        $scope.submitProduct = function() {
            $scope.isSubmitButonDisabled = true;
        //     if ($scope.partnerCode) {
        //         if (!$scope.salePrice) {
        //             $scope.showSalePriceErrorText = true;
        //             console.log("Incorrect discount percentage!!!");
        //             $scope.isSubmitButonDisabled = false;
        //         } else {
        // 			if ($scope.partner) {  
        // 				var stock = $scope.selectedStock;
        //                 var partner = $scope.partner;
        // 				var sale = {"stockId": stock.id, "partnerId": partner.id, "employeeId": SessionService.currentUser.employeeId, "cost": stock.productPrice, "salePrice": $scope.salePrice, "saleTimestamp": ApplicationService.getCurrentTimestamp()};
    		  //       	SaleService.submitSale(sale).then(
    			 //            function(response) {
    			 //            	$scope.selectedStock.quantity -= 1;
    				// 			$uibModalInstance.close($scope.selectedStock);
        //                         $scope.isSubmitButonDisabled = false;
    			 //            }
    			 //        );
    				// }
        //         }
        //     } else {
        //         var stock = $scope.selectedStock;
        //         var sale = {"stockId": stock.id, "employeeId": SessionService.currentUser.employeeId, "cost": stock.productPrice, "salePrice": $scope.salePrice, "saleTimestamp": ApplicationService.getCurrentTimestamp()};
        //         SaleService.submitSale(sale).then(
        //             function(response) {
        //                 $scope.selectedStock.quantity -= 1;
        //                 $uibModalInstance.close($scope.selectedStock);
        //                 $scope.isSubmitButonDisabled = false;
        //             }
        //         );
        //     }
            
        };

		$scope.closeModal = function () {
			$uibModalInstance.dismiss('cancel');
		};

	});