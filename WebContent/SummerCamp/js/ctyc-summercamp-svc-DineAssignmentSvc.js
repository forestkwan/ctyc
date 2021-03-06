(function(){
	angular.module('ctyc-summercamp')
	.factory('ctyc-summercamp-svc-DineAssignmentSvc', service);
	
	service.$inject = 
		['$modal',
		 'ctyc-svc-SocketSvc',
		 'MESSAGE_TYPE',
		 'notify'];
	
	function service(
			$modal,
			SocketSvc,
			MESSAGE_TYPE,
			notify){
	
		return {
			autoDineAssignment : autoDineAssignment,
			reloadData : reloadData
			};
		
		function autoDineAssignment(camp, day){
			var autoDineAssignmentModal = $modal.open({
				templateUrl : 'SummerCamp/AutoDineAssignmentForm.html',
				controller : 'ctyc-summercamp-ctrl-AutoDineAssignmentCtrl',
				controllerAs : 'vm',
				resolve : {
					camp : function () {
				          return camp;
			        },
			        day : function(){
			        	return day;
			        }
				}
			});
			
			autoDineAssignmentModal.result.then(autoAssignment);
		}
		
		function autoAssignment(data){
			SocketSvc.sendMessage(MESSAGE_TYPE.AUTO_ASSIGN, data);
			notify('Auto Assignment in progress...');
		}
		
		function reloadData(){
			SocketSvc.sendMessage(MESSAGE_TYPE.RELOAD_DATA, {});
			notify('Reload Data in progress...');
		}
	};
})();