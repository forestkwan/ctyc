(function(){
	angular.module('ctyc-summercamp')
	.directive('dineTable', function(){
		return {
			restrict: 'EA',
			replace : true,
			controller: 'ctyc-summercamp-ctrl-DineTableCtrl',
			controllerAs: 'vm',
			templateUrl: 'SummerCamp/DineTable.html',
			bindToController: true,
			scope: {
				dineTableData : '=',
				filter : '='
			}
		};
	});
})();