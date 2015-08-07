'use strict';

angular.module('sandboxApp')
    .controller('OrderDetailController', function ($scope, $rootScope, $stateParams, entity, Order) {
        $scope.order = entity;
        $scope.load = function (id) {
            Order.get({id: id}, function(result) {
                $scope.order = result;
            });
        };
        $rootScope.$on('sandboxApp:orderUpdate', function(event, result) {
            $scope.order = result;
        });
    });
