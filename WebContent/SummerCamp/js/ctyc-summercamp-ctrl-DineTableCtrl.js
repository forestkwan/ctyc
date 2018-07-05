(function(){
	angular.module('ctyc-summercamp')
	.controller('ctyc-summercamp-ctrl-DineTableCtrl', controller);
	
	controller.$inject = ['$scope'];
	
	function controller(
			$scope){
		
		var vm = this;
		
		vm.getTableName = getTableName;
		vm.getTableCampName = getTableCampName;
		vm.onParticipantDrop = onParticipantDrop;
		vm.dropSuccessHandler = dropSuccessHandler;
		vm.displayAlertClass = displayAlertClass;
		vm.showAvailability = showAvailability;
		
		vm.getCost = getCost;
		
		////////////////////////////
		
		function getTableName(){
			return vm.dineTableData.tableNumber;
		}
		
		function getTableCampName(){
			return vm.dineTableData.campName;
		}
		
		function onParticipantDrop($event, $data, participants){
			vm.dineTableData.participants.push($data);
		}
		
		function dropSuccessHandler($event, $index, participant){
			
			for (var i=0 ; i < vm.dineTableData.participants.length; i++){
				if (vm.dineTableData.participants[i].id === participant.id){
					vm.dineTableData.participants.splice(i, 1);
					break;
				}
			}
			
			$scope.$emit('DINE_ASSIGNMENT_CHANGE', vm.dineTableData.participants);
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
		
		function displayAlertClass(cost){
			if (cost > 0){
				return 'constraint-alert';
			}
		}
		
		function showAvailability(numberOfDay, timeOfDay, dineAvailabilitys){
			var targetDineAvailability = null;
			for (var i=0; i<dineAvailabilitys.length; i++){
				if (dineAvailabilitys[i].numberOfDay === numberOfDay &&
						dineAvailabilitys[i].timeOfDay === timeOfDay){
					targetDineAvailability = dineAvailabilitys[i];
				}
			}
			
			if (targetDineAvailability !== null){
				return targetDineAvailability.join;
			}
			
			return false;
		}
	}
})();