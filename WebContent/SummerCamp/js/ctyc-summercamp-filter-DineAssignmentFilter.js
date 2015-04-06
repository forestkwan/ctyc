(function(){
	angular.module('ctyc-summercamp')
	.filter('GenderFilter', genderFilter)
	.filter('GroupAssignmentFilter', groupAssignmentFilter);
	
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
	
	groupAssignmentFilter.$inject = [];
	
	function groupAssignmentFilter(){
		return function(data, campName){
			if (_.isEmpty(data) || _.isEmpty(campName)){
				return data;
			}
			
			var filteredData = {};
			for (var prop in data){
				if (data[prop].campName === campName){
					filteredData[prop] = data[prop];
				}
			}
			
			return filteredData;
		}
	}
})();