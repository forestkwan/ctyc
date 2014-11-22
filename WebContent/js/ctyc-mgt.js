angular.module('org.ctyc.mgt', [ 'ngRoute', 'org.ctyc.mgt.summercamp' ])
.config([ '$routeProvider', function($routeProvider) {
	$routeProvider
	.when('/', {
		templateUrl : 'Home.html'
	})
	.when('/DataInput', {
		templateUrl : 'SummerCamp/DataInput.html',
		controller : 'org.ctyc.mgt.summercamp.DataInputCtrl'
	})
	.when('/DineAssignment', {
		templateUrl : 'SummerCamp/DineAssignment.html'
	})
	.otherwise({
		redirectTo : '/'
	});
	}
]);