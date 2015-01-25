(function(){
	angular.module('ctyc-summercamp')
	.controller('ctyc-summercamp-ctrl-DineTableCtrl', controller);
	
	controller.$inject = ['$scope'];
	
	function controller($scope){
		
		var vm = this;
		
		vm.getTableName = getTableName;
		vm.onParticipantDrop = onParticipantDrop;
		vm.dropSuccessHandler = dropSuccessHandler;
		
		vm.getCost = getCost;
		
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
		
		function getCost(type){
			var evaluationResultMap = vm.dineTableData.evaluationResultMap;
			if (evaluationResultMap === undefined || evaluationResultMap === null){
				return -2;
			}
			
			var cost = evaluationResultMap[type];
			if (cost === undefined || cost === null){
				return -1;
			}
			return cost;
		}
	}
})();