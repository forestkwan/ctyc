(function(){
	var dependency = 
		['ngRoute',
		 'ui.grid',
		 'ui.grid.edit',
		 'ui.grid.selection',
		 'ctyc.mgt.summercamp'];
	
	angular.module('ctyc.mgt', dependency)
	.constant('MESSAGE_TYPE', {
		"GET_CAMP_SITE" : "GET_CAMP_SITE",
		"UPDATE_DINE_TABLE" : "UPDATE_DINE_TABLE",
		"GET_DINE_ASSIGNMENT" : "GET_DINE_ASSIGNMENT"
	})
	.config([ '$routeProvider', function($routeProvider) {
		$routeProvider
		.when('/', {
			templateUrl : 'Home.html'
		})
		.when('/DataInput', {
			templateUrl : 'SummerCamp/DataInput.html',
			controller : 'ctyc.mgt.summercamp.ctrl.datainput',
			controllerAs : 'vm'
		})
		.when('/DineAssignment', {
			templateUrl : 'SummerCamp/DineAssignment.html',
			controller : 'ctyc.mgt.summercamp.ctrl.dineassignment',
			controllerAs : 'vm'
		})
		.otherwise({
			redirectTo : '/'
		});
		}
	])
	.run(['ctycWebSocket', function(ctycWebSocket) {
		ctycWebSocket.initWebSocket();
	}]);
})();