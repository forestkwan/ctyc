(function(){
	angular.module('ctyc-summercamp')
	.filter('GenderFilter', genderFilter);
	
	genderFilter.$inject = [];
	
	function genderFilter(data){
		return function(data){
			if (!Array.isArray(data)){
				if (data === 'FEMALE'){
					return 'F';
				}
				if (data === 'MALE'){
					return 'M';
				}
			}
		}
	}
})();