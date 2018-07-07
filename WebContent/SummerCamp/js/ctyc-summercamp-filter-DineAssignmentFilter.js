(function(){
	angular.module('ctyc-summercamp')
	.filter('GenderFilter', genderFilter)
	.filter('CampNameFilter', campNameFilter)
	.filter('GroupAssignmentFilter', groupAssignmentFilter)
	.filter('CustomNumberFilter', customNumberFilter)
	.filter('GroupMentorOnTop', groupMentorOnTop)
	.filter('MaidAtBottom', maidAtBottom)
	.filter('GroupDisplayFilter', groupDisplayFilter);
	
	genderFilter.$inject = [];
	
	function genderFilter(data){
		return function(data){
			if (!Array.isArray(data)){
				if (data === 'FEMALE'){
					return '女';
				}
				if (data === 'MALE'){
					return '男';
				}
			}
		};
	}
	
	campNameFilter.$inject = [];
	
	function campNameFilter(data){
		return function(data){
			if (data === 'METHODIST'){
				return '衛理園';
			}
			if (data === 'RECREATION'){
				return '康樂營';
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
			var drWong = null;
			for (var i = 0 ; i < data.length; i++){
				if (data[i].name.indexOf('黃耀銓') >= 0){
					drWong = data[i];
				}else if (data[i].sundaySchoolClass.indexOf('導師') >= 0){
					ordered.splice(0, 0, data[i]);
				}else {
					ordered.push(data[i]);
				}
			}
			
			if (drWong){
				ordered.splice(0, 0, drWong);
			}
			
			return ordered;
			
		};
	}
	
	maidAtBottom.$inject = [];
	
	function maidAtBottom() {
		return function(data) {
			if (!_.isArray(data)) {
				return data;
			}
			
			var ordered = [];
			var maid = null;
			for (var i = 0; i < data.length; i++) {
				if (data[i].sundaySchoolClass.indexOf('外傭') >= 0) {
					maid = data[i];
				} else {
					ordered.push(data[i]);
				}
			}

			if (maid) {
				ordered.push(maid);
			}

			return ordered;
		}
	}
	
	groupDisplayFilter.$inject = [];
	
	function groupDisplayFilter(){
		return function(data){
			if (!_.isNumber(data)){
				data = parseInt(data);
			}
			
			switch (data) {
				case 0: return '/';
				case 1: return '第一組';
				case 2: return '第二組';
				case 3: return '第三組';
				case 4: return '第四組';
				case 5: return '第五組';
				case 6: return '第六組';
				case 7: return '第七組';
				case 8: return '第八組';
				case 9: return '第九組';
				case 10: return '第十組';
				case 11: return '第十一組';
				case 12: return '第十二組';
				case 13: return '第十三組';
				case 14: return '第十四組';
				case 15: return '第十五組';
				case 16: return '第十六組';
				case 17: return '第十七組';
				case 18: return '第十八組';
				case 19: return '第十九組';
				case 20: return '第二十組';
				case 21: return '第廿一組';
				case 22: return '第廿二組';
				case 23: return '第廿三組';
				case 24: return '第廿四組';
				case 25: return '第廿五組';
				case 26: return '第廿六組';
				case 27: return '第廿七組';
				case 28: return '第廿八組';
				case 29: return '第廿九組';
				case 30: return '第三十組';
				default : return data;
			}
		};
	}
})();