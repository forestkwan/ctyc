angular.module('org.ctyc.mgt', [ 'ngRoute', 'ui.grid', 'ui.grid.edit', 'ui.grid.selection', 'org.ctyc.mgt.summercamp' ])
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
])
.factory('ctycWebSocket', function(){
	return {
		webSocket : null,
		initWebSocket : function(){
			this.webSocket = new WebSocket('ws://localhost:8080/CTYCManagement/ctyc');
			
			this.webSocket.onopen = function(event) {
				console.log('WebSocket open');
		    };
		    
		    this.webSocket.onclose = function(event) {
				console.log('WebSocket close');
		    };
		    
		    this.webSocket.onerror = function(event) {
				console.log('WebSocket error');
		    };
		    
			this.webSocket.onmessage = function(event) {
				console.log(event.data);
		    };
		    
		},
		getWebSocket : function(){
			if (this.webSocket === null){
				this.initWebSocket();
			}
			return this.webSocket;
		}
	}
});