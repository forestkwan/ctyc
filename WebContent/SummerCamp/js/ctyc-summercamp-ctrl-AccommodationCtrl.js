(function(){
	angular.module('ctyc-summercamp')
	.controller('ctyc-summercamp-ctrl-AccommodationCtrl', controller);
	
	controller.$inject = 
		['$scope',
		 '$location',
		 'ctyc-svc-SocketSvc'];
	
	function controller(
			$scope,
			$location,
			SocketSvc){
	
		var vm = this;
		
		vm.openPrintTemplate = openPrintTemplate;
		vm.getConnectionClass = getConnectionClass;
		
		init();
		
		//////////////////
		
		function init(){
			
		}
		
		function openPrintTemplate(){
			$location.path('/AccommodationContact');
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