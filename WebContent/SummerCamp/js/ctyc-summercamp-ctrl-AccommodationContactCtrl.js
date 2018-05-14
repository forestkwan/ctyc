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
		
		vm.showAvailability = showAvailability;
		vm.onTestClicked = onTestClicked;
		
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
		
		function onTestClicked(){
			
			var url = 'accommodationcontact?camp=A';
			var data = {
				data : {camp : 'A'}	
			};
			var config = {
				dataType: 'json',
				headers: { 'Content-Type' : 'application/json' }
			};
			
//			$http({
//		        method : "POST",
//		        url : 'accommodationcontact',
//		        data : {
//		        	camp : 'A'
//		        }
//		    },
//		    {
//	        	camp : 'A'
//	        }).then(function success(response) {
//		    	
//		    	console.log('Success');
//		    	
//		    }, function error(response) {
//		    	
//		    	console.log('Fail');
//		    	
//		    });
			
			$http.get(url, data, config).then(function success(response) {
				
				var accommocationContact = response.data;
				
			}, function error(response) {
				
				console.log('Fail');
			
			});
		}
	}
})();