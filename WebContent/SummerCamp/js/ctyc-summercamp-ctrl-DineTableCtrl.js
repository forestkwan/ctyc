(function(){
	angular.module('ctyc-summercamp')
	.controller('ctyc-summercamp-ctrl-DineTableCtrl', controller);
	
	controller.$inject = ['$scope'];
	
	function controller($scope){
		
		var vm = this;
		
		vm.getTableName = getTableName;
		vm.onParticipantDrop = onParticipantDrop;
		vm.dropSuccessHandler = dropSuccessHandler;
		
		////////////////////////////
		
		function getTableName(){
			return vm.dineTableData.tableNumber;
		}
		
		function onParticipantDrop($event, $data, participants){
			vm.dineTableData.participants.push($data);
		}
		
		function dropSuccessHandler($event, $index, participant){
			vm.dineTableData.participants.splice($index, 1);
		}
	}
})();