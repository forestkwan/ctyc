(function(){
	angular.module('ctyc-summercamp')
	.controller('ctyc-summercamp-ctrl-DineAssignmentCtrl', controller);
	
	controller.$inject = 
		['$scope',
		 '$location',
		 'MESSAGE_TYPE',
		 'ctyc-svc-SocketSvc',
		 'ctyc-summercamp-svc-DineAssignmentSvc',
		 'notify',
		 '$modal'];
	
	function controller(
			$scope,
			$location,
			MESSAGE_TYPE,
			SocketSvc,
			DineAssignmentSvc,
			notify,
			$modal){
	
		var vm = this;
		
		vm.selectedPrintType = 'DINE';
		vm.selectedCamp = 'A';
		vm.selectedTimeOfDine = 'NIGHT';
		vm.selectedDay = 1;
		vm.dineAssignmentPlans = [];
		vm.groupAssignmentPlans = [];
		vm.dineAssignmentStatistics = {};
		vm.filter = {
				genderBalance : true,
				familySameTable : true,
				mentorInTable : true,
				sameSundayClass : true
		}
		vm.isLoading = false;
		
		vm.changePrintType = changePrintType;
		vm.changeCampSite = changeCampSite;
		vm.changeDineDay = changeDineDay;
		vm.changeTimeOfDine = changeTimeOfDine;
		vm.getSelectedDineAssignmentPlan = getSelectedDineAssignmentPlan;
		vm.saveAssignment = saveAssignment;
		vm.autoAssign = autoAssign;
		vm.calculateCost = calculateCost;
		vm.openPrintTemplate = openPrintTemplate;
		vm.showAvailability = showAvailability;
		vm.getTableNumberForParticularDine = getTableNumberForParticularDine;
		vm.displayPrintHeader = displayPrintHeader;
		vm.getSelectedDineStatistics = getSelectedDineStatistics;
		
		init();
		
		//////////////////
		
		SocketSvc.sendMessage(MESSAGE_TYPE.GET_DINE_ASSIGNMENT, {});		
		
		function init(){
			$scope.$on('websocket-message', function(event, jsonMessage){
				var message = JSON.parse(jsonMessage);
				
				if (message.type === 'DINE_ASSIGNMENT_DATA'){
					vm.dineAssignmentPlans = message.data.dineAssignmentPlans;
					vm.groupAssignmentPlans = message.data.groupAssignmentPlans;
					vm.dineAssignmentStatistics = message.data.dineAssignmentStatistics;
				}
				
				if (message.type === 'UPDATE_DINE_ASSIGNMENT_COMPLETE'){
					if (message.data.isSuccess === true){
						notify('Save Complete');
						vm.isLoading = false;
					}
				}
				
				if (message.type === 'AUTO_ASSIGN_COMPLETE'){
					if (message.data.isSuccess === true){

						var newDineAssignmentPlan = message.data.dineAssignmentPlan;
						
						for (var i=0; i<vm.dineAssignmentPlans.length; i++){
							if (vm.dineAssignmentPlans[i].campName === newDineAssignmentPlan.campName &&
									vm.dineAssignmentPlans[i].day === newDineAssignmentPlan.day){
								vm.dineAssignmentPlans[i] = newDineAssignmentPlan;
								break;
							}
						}
						
						notify('Auto Assignment Complete');
						vm.isLoading = false;
					}
				}
				
				if (message.type === 'CALCULATE_COST_COMPLETE'){
					if (message.data.isSuccess === true){
						var newDineAssignmentPlan = message.data.dineAssignmentPlan;
						
						for (var i=0; i<vm.dineAssignmentPlans.length; i++){
							if (vm.dineAssignmentPlans[i].campName === newDineAssignmentPlan.campName &&
									vm.dineAssignmentPlans[i].day === newDineAssignmentPlan.day){
								vm.dineAssignmentPlans[i] = newDineAssignmentPlan;
								break;
							}
						}

						notify('Calculate Complete');
						vm.isLoading = false;
					}
				}

				$scope.$digest();
			});
		}
		
		function changePrintType(printType){
			vm.selectedPrintType = printType;
		}
		
		function changeCampSite(selectedCamp){
			vm.selectedCamp = selectedCamp;
		}
		
		function changeDineDay(selectedDay){
			vm.selectedDay = selectedDay;
		}
		
		function changeTimeOfDine(selectedTimeOfDine){
			vm.selectedTimeOfDine = selectedTimeOfDine;
		}
		
		function getSelectedDineAssignmentPlan(){
			for (var i=0; i<vm.dineAssignmentPlans.length; i++){
				if (vm.dineAssignmentPlans[i].campName === vm.selectedCamp &&
						vm.dineAssignmentPlans[i].day === vm.selectedDay){
					return vm.dineAssignmentPlans[i];
				}
			}
			return {};
		}
		
		function saveAssignment(){
			
			var dineTableGroups = [];
			var dineAssignmentPlan = getSelectedDineAssignmentPlan();
			for (var i=0; i<dineAssignmentPlan.dineTableGroups.length; i++){
				
				var dineTableGroup = dineAssignmentPlan.dineTableGroups[i];
				
				var participants = []
				for (j=0; j<dineTableGroup.participants.length; j++){
					participants.push({id: dineTableGroup.participants[j].id})
				}
				
				dineTableGroups.push({
						tableNumber : dineTableGroup.tableNumber,
						participants : participants
				});
			}

			var data = {
					camp : vm.selectedCamp,
					day : vm.selectedDay,
					dineTableGroups : dineTableGroups
			};
			SocketSvc.sendMessage(MESSAGE_TYPE.UPDATE_DINE_ASSIGNMENT, data);
			
			vm.isLoading = true;
			notify('Saving...');
		}
		
		function autoAssign(){
			DineAssignmentSvc.autoDineAssignment(vm.selectedCamp, vm.selectedDay);
		}
		
		function calculateCost(){
			var dineTableGroups = [];
			var dineAssignmentPlan = getSelectedDineAssignmentPlan();
			
			for (var i=0; i<dineAssignmentPlan.dineTableGroups.length; i++){
				
				var dineTableGroup = dineAssignmentPlan.dineTableGroups[i];
				
				var participants = []
				for (j=0; j<dineTableGroup.participants.length; j++){
					participants.push({id: dineTableGroup.participants[j].id})
				}
				
				dineTableGroups.push({
						tableNumber : dineTableGroup.tableNumber,
						participants : participants
				});
			}

			var data = {
					camp : vm.selectedCamp,
					day : vm.selectedDay,
					dineTableGroups : dineTableGroups
			};
			SocketSvc.sendMessage(MESSAGE_TYPE.CALCULATE_COST, data);
			
			vm.isLoading = true;
			notify('Calculating Cost...');
		}
		
		function openPrintTemplate(){
			$location.path('/DineAssignmentPrint');
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
		
		function getTableNumberForParticularDine(numberOfDay, timeOfDay, dineAvailabilitys){
			var targetDineAvailability = null;
			for (var i=0; i<dineAvailabilitys.length; i++){
				if (dineAvailabilitys[i].numberOfDay === numberOfDay &&
						dineAvailabilitys[i].timeOfDay === timeOfDay){
					targetDineAvailability = dineAvailabilitys[i];
				}
			}
			
			if (!targetDineAvailability || targetDineAvailability.join === false || !targetDineAvailability.assignedTableNumber){
				return '-';
			}
			
			return targetDineAvailability.assignedTableNumber;
		}
		
		function displayPrintHeader(){
			if (vm.selectedPrintType === 'DINE'){
				return '2015夏令會' + vm.selectedCamp + '  膳食安排（按枱號）';
			}
			
			if (vm.selectedPrintType === 'GROUP'){
				return '2015夏令會' + vm.selectedCamp + ' 膳食座位安排（按組別）';
			}
			
			if (vm.selectedPrintType === 'STATISTICS'){
				return '2015夏令會' + vm.selectedCamp + ' 膳食統計';
			}
		}
		
		function getSelectedDineStatistics(){
			var statistics = vm.dineAssignmentStatistics[vm.selectedCamp][vm.selectedTimeOfDine].dineTableStatisticsList;
			return statistics;
		}
	};
})();