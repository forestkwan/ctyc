angular.module("org.ctyc.mgt", [ "ngRoute" ])
.config([ '$routeProvider', function($routeProvider) {
	$routeProvider
	.when('/', {
		templateUrl : 'Home.html'
	})
	.when('/DineAssignment', {
		templateUrl : 'SummerCamp/DineAssignment.html'
	})
	.otherwise({
		redirectTo : '/'
	});
	}
]);