angular.module('org.ctyc.mgt.summercamp', [])
.controller('org.ctyc.mgt.summercamp.DataInputCtrl', ['$scope', '$http', 'ctycWebSocket', function($scope, $http, $ctycWebSocket){
	
	$scope.camp = 'A';
	$scope.inputType = 'DINE_TABLE';
	$scope.newTableCapacity = 8;
	
	$scope.dineTableGrid = {
			columnDefs : [
			              { name: 'tableNumber', enableCellEdit: false, width: '50%' },
			              { name: 'capacity', displayName: 'Table Capacity', width: '50%', type: 'number' }
			              ],
			enableRowSelection: true,
			enableRowHeaderSelection: false,
			enableCellEditOnFocus : true,
			multiSelect : false,
			data : []
	};
	
	$scope.dineTableGrid.onRegisterApi = function( gridApi ) {
		$scope.gridApi = gridApi;
	};
	
	$scope.addNewDineTable = function(){
		var newTableNumber = $scope.dineTableGrid.data.length + 1;
		$scope.dineTableGrid.data.push({tableNumber: newTableNumber, capacity: $scope.newTableCapacity});
	};
	
	$scope.deleteSelectedTable = function(){
		var selectedRows = $scope.gridApi.selection.getSelectedRows();
		if (selectedRows === undefined || selectedRows === null || selectedRows.length <= 0){
			return;
		}
		
		/* Remove the selected row */
		var removedTableNumber = selectedRows[0].tableNumber;
		for (var i=0; i<$scope.dineTableGrid.data.length; i++){
			if (removedTableNumber !== $scope.dineTableGrid.data[i].tableNumber){
				continue;
			}
			
			$scope.dineTableGrid.data.splice(i, 1);
		}
		
		/* increment the table number after the removed table */
		for (var i=0; i<$scope.dineTableGrid.data.length; i++){
			if (removedTableNumber < $scope.dineTableGrid.data[i].tableNumber){
				$scope.dineTableGrid.data[i].tableNumber--;
			}
			
			/* Select the table with the same table number as the removed table */
			if ($scope.dineTableGrid.data[i].tableNumber === removedTableNumber){
				$scope.gridApi.selection.selectRow($scope.dineTableGrid.data[i]);
			}
		}
	};
	
	$scope.saveDineTable = function(){
		var data = {
				camp : $scope.camp,
				dineTables : $scope.dineTableGrid.data
		};
		var webSocket = $ctycWebSocket.sendMessage('UPDATE_DINE_TABEL', data);
	};
	
	$http.get('campSiteData')
	.success(function(data, status, headers, config) {
		$scope.campsite = data;
	})
	
}]);