(function(){
	angular.module('ctyc')
	.filter('ToArray', toArray);
	
	toArray.$inject = [];
	
	function toArray(){
		return function(data){
			return _.toArray(data);
		}
	}
})();