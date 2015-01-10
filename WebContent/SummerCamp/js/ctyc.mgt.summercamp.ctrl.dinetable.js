(function(){
	angular.module('ctyc.mgt.summercamp')
	.controller('ctyc.mgt.summercamp.ctrl.dinetable', controller);
	
	controller.$inject = ['$scope'];
	
	function controller($scope){
		
		var vm = this;
		
		vm.getTableName = getTableName;
		
		////////////////////////////
		
		function getTableName(){
			return vm.dineTableData.tableNumber;
		}
	}
})();