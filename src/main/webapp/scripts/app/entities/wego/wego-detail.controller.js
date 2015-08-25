'use strict';

angular.module('sandboxApp')
    .controller('WegoDetailController', function ($scope, $stateParams, Wego) {
        $scope.wego = {};
        $scope.load = function (id) {
            Wego.get({id: id}, function(result) {
              $scope.wego = result;
            });
        };
        $scope.load($stateParams.id);
    });
