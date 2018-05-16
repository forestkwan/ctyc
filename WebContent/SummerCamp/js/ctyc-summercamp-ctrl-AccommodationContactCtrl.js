(function(){
	angular.module('ctyc-summercamp')
	.controller('ctyc-summercamp-ctrl-AccommodationContactCtrl', controller);
	
	controller.$inject = 
		['$scope',
		 '$http'];
	
	function controller(
			$scope,
			$http){
		
		var vm = this;
		
		vm.accommodationContact = {};
		
		vm.onCampAClicked = onCampAClicked;
		vm.onCampBClicked = onCampBClicked;
		vm.onTestClicked = onTestClicked;
		
		////////////////////////////
		
		function onCampAClicked(){
			
			var url = 'accommodationcontact?camp=A';
			var data = {
				data : {camp : 'A'}	
			};
			var config = {
				dataType: 'json',
				headers: { 'Content-Type' : 'application/json' }
			};
			
			$http.get(url, data, config).then(function success(response) {
				
				vm.accommodationContact = response.data;
				
			}, function error(response) {
				
				console.log('Fail');
			
			});
		}
		
		function onCampBClicked(){
			
			var url = 'accommodationcontact?camp=B';
			var data = {
				data : {camp : 'B'}	
			};
			var config = {
				dataType: 'json',
				headers: { 'Content-Type' : 'application/json' }
			};
			
			$http.get(url, data, config).then(function success(response) {
				
				vm.accommodationContact = response.data;
				
			}, function error(response) {
				
				console.log('Fail');
			
			});
		}
		
		function onTestClicked(){
			console.log('Test');
		}
	}
})();