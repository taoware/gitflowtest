'use strict';

angular.module('sandboxApp')
    .controller('DummyController', function ($scope, Dummy, ParseLinks) {
        $scope.dummys = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Dummy.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.dummys = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Dummy.get({id: id}, function(result) {
                $scope.dummy = result;
                $('#deleteDummyConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Dummy.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteDummyConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.dummy = {code: null, name: null, id: null};
        };
    });
