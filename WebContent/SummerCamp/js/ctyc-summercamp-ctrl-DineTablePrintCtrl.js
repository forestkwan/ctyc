(function(){
	angular.module('ctyc-summercamp')
	.controller('ctyc-summercamp-ctrl-DineTablePrintCtrl', controller);
	
	controller.$inject = ['$scope'];
	
	function controller(
			$scope){
		
		var vm = this;
		
		vm.showAvailability = showAvailability;
		
		////////////////////////////
		
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