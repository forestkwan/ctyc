(function(){
	angular.module('ctyc-summercamp')
	.controller('ctyc-summercamp-ctrl-DineAssignmentCtrl', controller);
	
	controller.$inject = 
		['$scope',
		 'MESSAGE_TYPE',
		 'ctyc-svc-SocketSvc',
		 'ctyc-summercamp-svc-DineAssignmentSvc',
		 'notify',
		 '$modal'];
	
	function controller(
			$scope,
			MESSAGE_TYPE,
			SocketSvc,
			DineAssignmentSvc,
			notify,
			$modal){
	
		var vm = this;
		
		vm.selectedCamp = 'A';
		vm.camps = {
				'A' : {},
				'B' : {}
		};
		vm.filter = {
				genderBalance : true,
				familySameTable : true,
				mentorInTable : true,
				sameSundayClass : true
		}
		vm.isLoading = false;
		
		vm.changeCampSite = changeCampSite;
		vm.saveAssignment = saveAssignment;
		vm.autoAssign = autoAssign;
		vm.calculateCost = calculateCost;
		
		init();
		
		//////////////////
		
		SocketSvc.sendMessage(MESSAGE_TYPE.GET_DINE_ASSIGNMENT, {});
		
		
		function init(){
			$scope.$on('websocket-message', function(event, jsonMessage){
				var message = JSON.parse(jsonMessage);
				
				if (message.type === 'DINE_ASSIGNMENT_DATA'){
					var dineAssignmentData = message.data.dineAssignment;
					for (prop in vm.camps){
						vm.camps[prop].assignmentPlan = dineAssignmentData[prop];
					}
				}
				
				if (message.type === 'UPDATE_DINE_ASSIGNMENT_COMPLETE'){
					if (message.data.isSuccess === true){
						notify('Save Complete');
						vm.isLoading = false;
					}
				}
				
				if (message.type === 'AUTO_ASSIGN_COMPLETE'){
					if (message.data.isSuccess === true){
						
						notify('Auto Assignment Complete');
						
						var dineAssignmentData = message.data.dineAssignment;
						for (prop in vm.camps){
							vm.camps[prop].assignmentPlan = dineAssignmentData[prop];
						}
						
						vm.isLoading = false;
					}
				}
				
				if (message.type === 'CALCULATE_COST_COMPLETE'){
					if (message.data.isSuccess === true){
						vm.camps[message.data.camp].assignmentPlan = message.data.dineAssignment;
						notify('Calculate Complete');
						vm.isLoading = false;
					}
				}

				$scope.$digest();
			});
		}
		
		function changeCampSite(selectedCamp){
			vm.selectedCamp = selectedCamp;
		}
		
		function saveAssignment(){
			
			var dineTableGroups = [];
			for (var i=0; i<vm.camps[vm.selectedCamp].assignmentPlan.dineTableGroups.length; i++){
				
				var dineTableGroup = vm.camps[vm.selectedCamp].assignmentPlan.dineTableGroups[i];
				
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
					dineTableGroups : dineTableGroups
			};
			SocketSvc.sendMessage(MESSAGE_TYPE.UPDATE_DINE_ASSIGNMENT, data);
			
			vm.isLoading = true;
			notify('Saving...');
		}
		
		function autoAssign(){
			DineAssignmentSvc.autoDineAssignment(vm.selectedCamp);
		}
		
		function calculateCost(){
			var dineTableGroups = [];
			for (var i=0; i<vm.camps[vm.selectedCamp].assignmentPlan.dineTableGroups.length; i++){
				
				var dineTableGroup = vm.camps[vm.selectedCamp].assignmentPlan.dineTableGroups[i];
				
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
					dineTableGroups : dineTableGroups
			};
			SocketSvc.sendMessage(MESSAGE_TYPE.CALCULATE_COST, data);
			
			vm.isLoading = true;
			notify('Calculating Cost...');
		}
	};
})();