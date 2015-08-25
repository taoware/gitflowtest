'use strict';

angular.module('sandboxApp')
    .controller('DummyDetailController', function ($scope, $rootScope, $stateParams, entity, Dummy) {
        $scope.dummy = entity;
        $scope.load = function (id) {
            Dummy.get({id: id}, function(result) {
                $scope.dummy = result;
            });
        };
        $rootScope.$on('sandboxApp:dummyUpdate', function(event, result) {
            $scope.dummy = result;
        });
    });
