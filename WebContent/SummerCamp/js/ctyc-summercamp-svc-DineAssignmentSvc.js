(function(){
	angular.module('ctyc-summercamp')
	.factory('ctyc-summercamp-svc-DineAssignmentSvc', service);
	
	service.$inject = 
		['$modal',
		 '$http',
		 'ctyc-svc-SocketSvc',
		 'MESSAGE_TYPE',
		 'notify'];
	
	function service(
			$modal,
			$http,
			SocketSvc,
			MESSAGE_TYPE,
			notify){
	
		return {
			autoDineAssignment : autoDineAssignment,
			reloadData : reloadData,
			exportAllAssignment : exportAllAssignment,
			addNewTable : addNewTable,
			removeLastTable : removeLastTable
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
		
		function exportAllAssignment(){
			
			notify('Exporting Assignment...');
			$http({
				method : 'GET',
				url : '/CTYCManagement/exportAssignment'
			}).then(function successCallback(response) {
				var downloadLink = document.getElementById('download');
				downloadLink.href = response.data;
				downloadLink.click();
			}, function errorCallback(response) {
				notify('Fail to export.');
			});
		}
		
		function addNewTable(newTableInfo, callBackFn){
			
			notify('Adding New Table...');
			$http({
				method : 'POST',
				url : '/CTYCManagement/addNewTable',
				params : newTableInfo
			}).then(function successCallback(response) {
				notify('Add new table');
				callBackFn();
			}, function errorCallback(response) {
				notify('Fail to add new table.');
			});
			
		}
		
		function removeLastTable(lastTableInfo, callBackFn){
			
			notify('Removing Last Table...');
			$http({
				method : 'POST',
				url : '/CTYCManagement/removeLastTable',
				params : lastTableInfo
			}).then(function successCallback(response) {
				notify('Removing Last table');
				callBackFn();
			}, function errorCallback(response) {
				notify('Fail to remove last table.');
			});
			
		}
	};
})();