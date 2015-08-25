'use strict';

angular.module('sandboxApp')
    .controller('Test11DetailController', function ($scope, $stateParams, Test11) {
        $scope.test11 = {};
        $scope.load = function (id) {
            Test11.get({id: id}, function(result) {
              $scope.test11 = result;
            });
        };
        $scope.load($stateParams.id);
    });
