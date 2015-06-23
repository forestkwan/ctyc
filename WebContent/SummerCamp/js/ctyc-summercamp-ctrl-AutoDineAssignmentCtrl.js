(function(){
	angular.module('ctyc-summercamp')
	.controller('ctyc-summercamp-ctrl-AutoDineAssignmentCtrl', controller);
	
	controller.$inject =
		['$scope',
		 '$modalInstance',
		 'camp',
		 'day'];
	
	function controller(
			$scope,
			$modalInstance,
			camp,
			day){
		
		var vm = this;
		vm.tableCapacity = 8;
		vm.seed = 0;
		vm.constraints = {
				genderBalance : true,
				familySameTable : true,
				mentorInTable : true,
				sameSundayClass : true
		}
		vm.camp = camp;
		vm.day = day;
		
		vm.confirmClicked = confirmClicked;
		vm.cancelClicked = cancelClicked;
		
		/////////////////////////
		
		function confirmClicked(){
			var data = {
					camp : vm.camp,
					day : day,
					tableCapacity : vm.tableCapacity,
					//constraints : vm.constraints,
					seed : vm.seed
			}
			
			$modalInstance.close(data);
		}
		
		function cancelClicked(){
			$modalInstance.dismiss();
		}
	}
})()