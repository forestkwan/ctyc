(function(){

	angular.module('ctyc.mgt.summercamp').controller('ctyc.mgt.summercamp.ctrl.datainput', controller);
	
	controller.$inject = ['$scope', 'MESSAGE_TYPE', 'ctycWebSocket', 'notify'];
	
	function controller($scope, MESSAGE_TYPE, $ctycWebSocket, notify){
		
		var vm = this;
		
		vm.isLoading = false;
		vm.selectedCamp = 'A';
		vm.inputView = 'DINE_TABLE';
		vm.newTableCapacity = 8;
		vm.camps = {
				'A' : {},
				'B' : {}
		};
		
		vm.dineTableGrid = {
				columnDefs : [
				              { name: 'number', displayName: 'Table Number', enableCellEdit: false, width: '50%' },
				              { name: 'capacity', displayName: 'Table Capacity', width: '50%', type: 'number' }
				              ],
				enableRowSelection: true,
				enableRowHeaderSelection: false,
				enableCellEditOnFocus : true,
				multiSelect : false,
				onRegisterApi : function( dineTableGridApi ) {
					$scope.dineTableGridApi = dineTableGridApi;
				},
				data : vm.camps[vm.selectedCamp].canteenTables
		}
		
		vm.participantGrid = {
				columnDefs : [
				              { name: 'id', displayName: 'ID', enableCellEdit: false, width: '50' },
				              { name: 'name', displayName: 'Name', enableCellEdit: false, width: '100' },
				              { name: 'gender', displayName: 'Gender', enableCellEdit: false, width: '100' },
				              { name: 'yearOfBirth', displayName: 'Year of Birth', enableCellEdit: false, width: '80' },
				              { name: 'mentor', displayName: 'Mentor', enableCellEdit: false, width: '100' },
				              { name: 'groupNumber', displayName: 'Group Number', enableCellEdit: false, width: '80' },
				              { name: 'groupMentor', displayName: 'Group Mentor', enableCellEdit: false, width: '80' }
				              ],
				enableRowSelection: true,
				enableRowHeaderSelection: false,
				enableCellEditOnFocus : true,
				multiSelect : false,
				onRegisterApi : function( participantGridApi ) {
					$scope.participantGridApi = participantGridApi;
				},
				data : vm.camps[vm.selectedCamp].participants
		}
		
		vm.changeCampSite = changeCampSite;
		vm.addNewDineTable = addNewDineTable;
		vm.deleteSelectedTable = deleteSelectedTable;
		vm.saveDineTable = saveDineTable;
		vm.addNewParticipant = addNewParticipant;
		vm.deleteParticipant = deleteParticipant;
		vm.saveParticipant = saveParticipant;
		
		/////////
		
		$ctycWebSocket.sendMessage(MESSAGE_TYPE.GET_CAMP_SITE, {});
		$scope.$on('websocket-message', function(event, jsonMessage){
			var message = JSON.parse(jsonMessage);
			
			if (message.type === 'CAMP_SITE_DATA'){
				var campSitesData = message.data.campSites;
				for (prop in vm.camps){
					vm.camps[prop] = campSitesData[prop];
				}
				vm.dineTableGrid.data = vm.camps[vm.selectedCamp].canteenTables;
				vm.participantGrid.data = vm.camps[vm.selectedCamp].participants;
			}
			
			if (message.type === 'SERVER_RESPONSE'){
				if (message.data.isSuccess === true){
					notify('Save Complete');
					vm.isLoading = false;
				}
			}
			
			$scope.$digest();
		});
		
		notify.config({
			duration : 3000
		});
		
		//////////
		
		function changeCampSite(selectedCamp){
			vm.selectedCamp = selectedCamp;
			vm.dineTableGrid.data = vm.camps[vm.selectedCamp].canteenTables;
			vm.participantGrid.data = vm.camps[vm.selectedCamp].participants;
		}
		
		function addNewDineTable(){
			var newTableNumber = vm.camps[vm.selectedCamp].canteenTables.length + 1;
			vm.camps[vm.selectedCamp].canteenTables.push({number: newTableNumber, capacity: vm.newTableCapacity});
		};
		
		function deleteSelectedTable(){
			var selectedRows = $scope.dineTableGridApi.selection.getSelectedRows();
			if (selectedRows === undefined || selectedRows === null || selectedRows.length <= 0){
				return;
			}
			
			/* Remove the selected row */
			var removedTableNumber = selectedRows[0].number;
			for (var i=0; i < vm.camps[vm.selectedCamp].canteenTables.length; i++){
				if (removedTableNumber !== vm.camps[vm.selectedCamp].canteenTables[i].number){
					continue;
				}
				
				vm.camps[vm.selectedCamp].canteenTables.splice(i, 1);
			}
			
			/* increment the table number after the removed table */
			for (var i=0; i < vm.camps[vm.selectedCamp].canteenTables.length; i++){
				if (removedTableNumber < vm.camps[vm.selectedCamp].canteenTables[i].number){
					vm.camps[vm.selectedCamp].canteenTables[i].number--;
				}
				
				/* Select the table with the same table number as the removed table */
				if (vm.camps[vm.selectedCamp].canteenTables[i].number === removedTableNumber){
					$scope.dineTableGridApi.selection.selectRow(vm.camps[vm.selectedCamp].canteenTables[i]);
				}
			}
		};
		
		function saveDineTable(){
			var data = {
					camp : vm.selectedCamp,
					dineTables : vm.camps[vm.selectedCamp].canteenTables
			};
			$ctycWebSocket.sendMessage(MESSAGE_TYPE.UPDATE_DINE_TABLE, data);
			
			vm.isLoading = true;
			notify('Saving...');
		};
		
		function addNewParticipant(){
			
		};
		
		function deleteParticipant(){
			
		};
		
		function saveParticipant(){
			
		};

	};
})();