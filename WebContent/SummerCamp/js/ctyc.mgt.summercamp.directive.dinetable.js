(function(){
	angular.module('ctyc.mgt.summercamp')
	.directive('dineTable', function(){
		return {
			restrict: 'EA',
			replace : true,
			controller: 'ctyc.mgt.summercamp.ctrl.dinetable',
			controllerAs: 'vm',
			templateUrl: 'SummerCamp/DineTable.html',
			bindToController: true,
			scope: {
				dineTableData : '='
			}
		};
	});
})();