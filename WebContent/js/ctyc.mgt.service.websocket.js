(function(){
	angular.module('ctyc.mgt').factory('ctycWebSocket', ctycWebSocket);
	
	ctycWebSocket.$inject = ['$rootScope'];
	
	function ctycWebSocket($rootScope){
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
					$rootScope.$broadcast('websocket-message', event.data);
			    };
			    
			},
			getWebSocket : function(){
				if (this.webSocket === null){
					this.initWebSocket();
				}
				return this.webSocket;
			},
			sendMessage : function(type, data){
				var message = {
						type : type,
						data : data
				};
				this.webSocket.send(JSON.stringify(message));
			}
		}
	};
})();