TatamiApp.factory('StatusService', ['$resource', function($resource) {
    return $resource('/tatami/rest/statuses/:statusId');
}]);