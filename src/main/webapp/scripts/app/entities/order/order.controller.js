'use strict';

angular.module('sandboxApp')
    .controller('OrderController', function ($scope, Order, ParseLinks) {
        $scope.orders = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Order.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.orders = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Order.get({id: id}, function(result) {
                $scope.order = result;
                $('#deleteOrderConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Order.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteOrderConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.order = {myString: null, myInteger: null, myLong: null, myFloat: null, myDouble: null, myDecimal: null, myDate: null, myDateTime: null, myBoolean: null, myEnumeration: null, id: null};
        };
    });
