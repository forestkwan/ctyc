(function(){
	angular.module('ctyc-summercamp')
	.filter('GenderFilter', genderFilter)
	.filter('GroupAssignmentFilter', groupAssignmentFilter)
	.filter('CustomNumberFilter', customNumberFilter)
	.filter('GroupMentorOnTop', groupMentorOnTop);
	
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
		};
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
		};
	}
	
	customNumberFilter.$inject = [];
	
	function customNumberFilter(){
		return function(data){
			if (!_.isNumber(data)){
				return data;
			}
			
			if (data < 0){
				return 0;
			}
			
			return data;
		};
	}
	
	groupMentorOnTop.$inject = [];
	
	function groupMentorOnTop(){
		return function(data){
			if (!_.isArray(data)){
				return data;
			}
			
			var ordered = [];
			for (var i = 0 ; i < data.length; i++){
				if (data[i].sundaySchoolClass === '導師'){
					ordered.splice(0, 0, data[i]);
				}else {
					ordered.push(data[i]);
				}
			}
			return ordered;
			
		};
	}
})();