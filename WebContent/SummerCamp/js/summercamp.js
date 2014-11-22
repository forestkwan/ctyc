angular.module('org.ctyc.mgt.summercamp', [])
.controller('org.ctyc.mgt.summercamp.DataInputCtrl', ['$scope', '$http', function($scope, $http){
	
	$http.get('campSiteData')
	.success(function(data, status, headers, config) {
		$scope.campsite = data;
	})
	
}]);