(function(){
	angular.module('ctyc')
	.factory('ctyc-svc-SocketSvc', socketSvc);
	
	socketSvc.$inject = 
		[
		 '$rootScope',
		 '$location'];
	
	function socketSvc(
			$rootScope,
			$location){
		return {
			webSocket : null,
			initWebSocket : function(){
				var wsUrl = 'ws://' + $location.host() + ':' + $location.port() + '/ctyc';
				this.webSocket = new WebSocket(wsUrl);
				
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