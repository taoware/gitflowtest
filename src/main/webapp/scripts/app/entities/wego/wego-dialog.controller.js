/** * Created by bigezhang on 8/24/15. */'use strict';angular.module('sandboxApp').controller('WegoDialogController',    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Wego',        function ($scope, $stateParams, $modalInstance, entity, Wego) {            $scope.wego = entity;            $scope.load = function (id) {                Wego.get({id: id}, function (result) {                    $scope.wego = result;                });            };            var onSaveFinished = function (result) {                $scope.$emit('sandboxApp:orderUpdate', result);                $modalInstance.close(result);            };            $scope.save = function () {                if ($scope.wego.id != null) {                    Wego.update($scope.wego, onSaveFinished);                } else {                    Wego.save($scope.wego, onSaveFinished);                }            };            $scope.clear = function () {                $modalInstance.dismiss('cancel');            };        }]);