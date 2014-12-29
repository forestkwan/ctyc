(function(){
	angular.module('ctyc.mgt.summercamp').controller('ctyc.mgt.summercamp.ctrl.dineassignment', controller);
	
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
		
		//////////////////
		
		$ctycWebSocket.sendMessage(MESSAGE_TYPE.GET_DINE_ASSIGNMENT, {});
		
		function changeCampSite(selectedCamp){
			vm.selectedCamp = selectedCamp;
		}
	};
})();