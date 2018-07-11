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
		
		vm.websocketStatus = true;
		vm.selectedPrintType = 'DINE';
		vm.selectedCamp = 'A';
		vm.selectedCampLocation = 'METHODIST';
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
		vm.changeCampLocation = changeCampLocation;
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
		vm.countDayTotal = countDayTotal;
		vm.resolveDate = resolveDate;
		vm.resolveTimeOfDine = resolveTimeOfDine;
		vm.reloadData = reloadData;
		vm.getConnectionClass = getConnectionClass;
		
		init();
		
		//////////////////
		
		setTimeout(function(){
			SocketSvc.sendMessage(MESSAGE_TYPE.GET_DINE_ASSIGNMENT, {});
		}, 1000);
		
		function init(){
//			notify('Data loading...');
			
			$scope.$on('DINE_ASSIGNMENT_CHANGE', function(event, data){
				calculateCost();
				saveAssignment();
			});
			
			$scope.$on('websocket-message', function(event, jsonMessage){
				var message = JSON.parse(jsonMessage);
				
				if (message.type === 'DINE_ASSIGNMENT_DATA'){
					//notify('Loading completed. Last data fetch time: ' + message.data.lastDataFetchTime);
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

						notify.closeAll();
						notify('Calculate Complete');
						vm.isLoading = false;
					}
				}
				
				if (message.type === 'RELOAD_DATA_COMPLETE'){
					if (message.data.isSuccess === true){
						
						notify.closeAll();
						notify('Reload Data Complete');
						vm.isLoading = false;
						
						SocketSvc.sendMessage(MESSAGE_TYPE.GET_DINE_ASSIGNMENT, {});
					}
				}

				$scope.$digest();
			});
			
			$scope.$on('websocket-connected', function(event, data){
				vm.websocketStatus = true;
				$scope.$digest();
			});
			
			$scope.$on('websocket-closed', function(event, data){
				vm.websocketStatus = false;
				$scope.$digest();
			});
			
		}
		
		function changePrintType(printType){
			vm.selectedPrintType = printType;
		}
		
		function changeCampSite(selectedCamp){
			vm.selectedCamp = selectedCamp;
		}
		
		function changeCampLocation(selectedCampLocation){
			vm.selectedCampLocation = selectedCampLocation;
		}
		
		function changeDineDay(selectedDay){
			vm.selectedDay = selectedDay;
		}
		
		function changeTimeOfDine(selectedTimeOfDine){
			vm.selectedTimeOfDine = selectedTimeOfDine;
		}
		
		function getSelectedDineAssignmentPlan(){
			for (var i=0; i<vm.dineAssignmentPlans.length; i++){
				if (vm.dineAssignmentPlans[i].campName === vm.selectedCamp
						&& vm.dineAssignmentPlans[i].day === vm.selectedDay){
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
						campName : dineTableGroup.campName,
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
		
		function reloadData(){
			DineAssignmentSvc.reloadData();
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
						campName : dineTableGroup.campName,
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
		
		function getTableNumberForParticularDine(numberOfDay, dineAvailabilitys){
			var targetDineAvailabilities = [];
			for (var i=0; i<dineAvailabilitys.length; i++){
				if (dineAvailabilitys[i].numberOfDay === numberOfDay){
					targetDineAvailabilities.push(dineAvailabilitys[i]);
				}
			}
			
			if (_.isEmpty(targetDineAvailabilities)){
				return '-';
			}
			
			var isJoin = false;
			for (var i = 0; i < targetDineAvailabilities.length; i++){
				isJoin = (isJoin || targetDineAvailabilities[i].join);
			}
			
			if (isJoin){
				return targetDineAvailabilities[0].assignedTableNumber;
			} else {
				return '-';
			}
		}
		
		function displayPrintHeader(){
			if (vm.selectedPrintType === 'DINE'){
				return '2018夏令會' + vm.selectedCamp + '  膳食安排（按枱號）';
			}
			
			if (vm.selectedPrintType === 'GROUP'){
				return '2018夏令會' + vm.selectedCamp + ' 膳食座位安排（按組別）';
			}
			
			if (vm.selectedPrintType === 'STATISTICS'){
				return '2018夏令會' + vm.selectedCamp + ' 膳食統計';
			}
		}
		
		function getSelectedDineStatistics(){
			
			var campStat = vm.dineAssignmentStatistics.campDineStatisticsMap[vm.selectedCamp];
			var campLocationStat = campStat.campDineTimeStatisticsMap[vm.selectedCampLocation];
			var timeOfDayStat = campLocationStat[vm.selectedTimeOfDine];
			return timeOfDayStat.dineTableStatisticsList;
		}
		
		function countDayTotal(dineStatistics, att){	
			var total = 0;
			for (var i = 0; i < dineStatistics.length; i++){
				
				if (dineStatistics[i][att] < 0 ){
					continue;
				}
				total += dineStatistics[i][att];
			}
			return total;
		}
		
		function resolveDate(nthDay){
			
			var startDay = 1;
			if (vm.selectedCamp === 'A'){
				var startDay = 19;
			}else if (vm.selectedCamp === 'B'){
				var startDay = 26;
			}
			
			var compensateDay = 0;
			if (vm.selectedTimeOfDine !== 'NIGHT'){
				compensateDay = 1;
			}
			
			switch (nthDay) {
			case 1:
				return (startDay + nthDay - 1 + compensateDay) + '/7(日)';
			case 2:
				return (startDay + nthDay - 1 + compensateDay) + '/7(一)';
			case 3:
				return (startDay + nthDay - 1 + compensateDay) + '/7(二)';
			case 4:
				return (startDay + nthDay - 1 + compensateDay) + '/7(三)';
			}
			
			return '';
		}
		
		function resolveTimeOfDine(input){
			if (input === 'NIGHT'){
				return '晚';
			}
			if (input === 'MORNING'){
				return '早';
			}
			if (input === 'NOON'){
				return '午';
			}
			return '';
		}
		
		function getConnectionClass(){
			
			var ws = SocketSvc.getWebSocket();
			
			if (vm.websocketStatus){
				return "green";
			}
			
			return "red";
		}
	};
})();