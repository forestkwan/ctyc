(function(){
	angular.module('ctyc.mgt.summercamp')
	.controller('ctyc.mgt.summercamp.ctrl.dineassignment', controller);
	
	controller.$inject = ['$scope', 'MESSAGE_TYPE', 'ctycWebSocket', 'notify'];
	
	function controller($scope, MESSAGE_TYPE, $ctycWebSocket, notify){
	
		var vm = this;
		
		vm.selectedCamp = 'A';
		vm.camps = {
				'A' : {},
				'B' : {}
		};
		vm.isLoading = false;
		
		vm.changeCampSite = changeCampSite;
		vm.saveAssignment = saveAssignment;
		
		//////////////////
		
		$ctycWebSocket.sendMessage(MESSAGE_TYPE.GET_DINE_ASSIGNMENT, {});
		
		$scope.$on('websocket-message', function(event, jsonMessage){
			var message = JSON.parse(jsonMessage);
			
			if (message.type === 'DINE_ASSIGNMENT_DATA'){
				var dineAssignmentData = message.data.dineAssignment;
				for (prop in vm.camps){
					vm.camps[prop].assignmentPlan = dineAssignmentData[prop];
				}
			}

			$scope.$digest();
		});
		
		function changeCampSite(selectedCamp){
			vm.selectedCamp = selectedCamp;
		}
		
		function saveAssignment(){
			var data = {
					camp : vm.selectedCamp,
					dineAssignment : vm.camps[vm.selectedCamp].assignmentPlan
			};
			$ctycWebSocket.sendMessage(MESSAGE_TYPE.UPDATE_DINE_ASSIGNMENT, data);
			
			vm.isLoading = true;
			notify('Saving...');
		}
	};
})();