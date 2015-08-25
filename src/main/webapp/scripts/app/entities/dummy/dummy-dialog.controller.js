'use strict';

angular.module('sandboxApp').controller('DummyDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Dummy',
        function($scope, $stateParams, $modalInstance, entity, Dummy) {

        $scope.dummy = entity;
        $scope.load = function(id) {
            Dummy.get({id : id}, function(result) {
                $scope.dummy = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('sandboxApp:dummyUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.dummy.id != null) {
                Dummy.update($scope.dummy, onSaveFinished);
            } else {
                Dummy.save($scope.dummy, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
