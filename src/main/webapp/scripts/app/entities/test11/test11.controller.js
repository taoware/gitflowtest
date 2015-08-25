'use strict';

angular.module('sandboxApp')
    .controller('Test11Controller', function ($scope, Test11) {
        $scope.test11s = [];
        $scope.loadAll = function() {
            Test11.query(function(result) {
               $scope.test11s = result;
            });
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Test11.get({id: id}, function(result) {
                $scope.test11 = result;
                $('#saveTest11Modal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.test11.id != null) {
                Test11.update($scope.test11,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Test11.save($scope.test11,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Test11.get({id: id}, function(result) {
                $scope.test11 = result;
                $('#deleteTest11Confirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Test11.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTest11Confirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTest11Modal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.test11 = {id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
