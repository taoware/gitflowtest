'use strict';

angular.module('sandboxApp')
    .controller('WegoController', function ($scope, $state, Wego, Wegos) {
        $scope.wegos = [];
        $scope.page = 1;
        $scope.pageSize = 20;

        var columnDefs = [
            {
                headerName: 'Action',
                cellRenderer: function (params) {
                    return '<button type="submit"' +
                        ' ng-click=view(' + params.data.id + ')' +
                        ' class="btn btn-primary btn-xs btn-entity">' +
                        '    <span class="glyphicon glyphicon-eye-open"></span>&nbsp;<span translate="entity.action.view"> View</span>' +
                        '</button>' +
                        '<button type="submit"' +
                        ' ng-click=edit(' + params.data.id + ')' +
                        ' class="btn btn-info btn-xs btn-entity">' +
                        '    <span class="glyphicon glyphicon-pencil"></span>&nbsp;<span translate="entity.action.edit"> Edit</span>' +
                        '</button>' +
                        '<button type="submit"' +
                        ' ng-click=delete(' + params.data.id + ')' +
                        ' class="btn btn-danger btn-xs btn-entity">' +
                        '    <span class="glyphicon glyphicon-remove-circle"></span>&nbsp;<span translate="entity.action.delete"> Delete</span>' +
                        '</button>';
                }
            },
            {
                headerName:"#",width:50,
                cellRenderer:function(params) {
                    return params.node.id + 1;
                },
                suppressSorting:true,
                suppressMenu: true
            },
            {headerName:"Id",field:"id",filter:'number',filterParams:{newRowsAction:'keep'}},
            {headerName:"Title",field:"title",filter:'text',filterParams:{newRowsAction:'keep'}},
            {headerName:"Description",field:"description"},
            {headerName:"Start",field:'start'},
            {headerName:'End',field:"end"},
            {headerName:"File",field:"file"}
        ];

        $scope.gridOptions = {
            enableServerSideSorting:true,
            enableServerSideFilter:true,
            enableColResize:true,
            columnDefs:columnDefs,
            ready: function(api) {
                console.log('Callback ready:api = ' + api);
                console.log(api);
            },
            angularCompileRows:true
        };

        $scope.loadAll = function () {
            Wego.query({page: $scope.page, per_page: $scope.pageSize}, function () {
                createNewDatasource();
            });
        };

        $scope.loadAll();

        $scope.view = function(id){
            $state.go('wego.detail',{id: id});
        }
        $scope.edit = function(id){
            $state.go('wego.edit',{id: id})
        };

        $scope.delete = function (id) {
            Wego.get({id: id}, function (result) {
                $scope.wego = result;
                $('#deleteWegoConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Wego.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteWegoConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.wego = {title: null, description: null, start: null, end: null, file: null, id: null};
        };

        function createNewDatasource() {
            var dataSource = {
                pageSize:$scope.pageSize,
                getRows: function(params) {
                    var data = [];
                    var total = 0;
                    var page = Math.floor(params.startRow / $scope.pageSize) + 1;
                    console.log(params);
                    console.log('asking for' + params.startRow + 'to' + params.endRow);
                    Wegos.query({page: page, per_page: $scope.pageSize, sort: params.sortModel, filter:params.filterModel},
                    function(result){
                        data = result.content;
                        total = result.totalElements;
                    });
                    setTimeout (function(){
                        params.successCallback(data,total);
                    },500);
                }
            };
            $scope.gridOptions.api.setDatasource(dataSource);
        }
    });
