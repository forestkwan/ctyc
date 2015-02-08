(function(){
	var dependency = 
		['ngRoute',
		 'ui.grid',
		 'ui.grid.edit',
		 'ui.grid.selection',
		 'ctyc-summercamp'];
	
	angular.module('ctyc', dependency)
	.constant('MESSAGE_TYPE', {
		"GET_CAMP_SITE" : "GET_CAMP_SITE",
		"UPDATE_DINE_TABLE" : "UPDATE_DINE_TABLE",
		"GET_DINE_ASSIGNMENT" : "GET_DINE_ASSIGNMENT",
		"UPDATE_DINE_ASSIGNMENT" : "UPDATE_DINE_ASSIGNMENT",
		"AUTO_ASSIGN" : "AUTO_ASSIGN",
		"CALCULATE_COST" : "CALCULATE_COST"
	})
	.config([ '$routeProvider', function($routeProvider) {
		$routeProvider
		.when('/', {
			templateUrl : 'Home.html'
		})
		.when('/DataInput', {
			templateUrl : 'SummerCamp/DataInput.html',
			controller : 'ctyc-summercamp-ctrl-DataInputCtrl',
			controllerAs : 'vm'
		})
		.when('/DineAssignment', {
			templateUrl : 'SummerCamp/DineAssignment.html',
			controller : 'ctyc-summercamp-ctrl-DineAssignmentCtrl',
			controllerAs : 'vm'
		})
		.otherwise({
			redirectTo : '/'
		});
		}
	])
	.run(['ctyc-svc-SocketSvc', function(SocketSvc) {
		SocketSvc.initWebSocket();
	}]);
})();