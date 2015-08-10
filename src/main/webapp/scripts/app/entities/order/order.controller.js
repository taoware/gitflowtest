'use strict';

angular.module('sandboxApp')
    .controller('OrderController', function ($scope, $state, Order, Orders) {
        $scope.orders = [];
        $scope.page = 1;
        $scope.pageSize = 20;
        $scope.total = 0;
        var columnDefs = [
            {
                headerName: "#", width: 50,
                cellRenderer: function(params) {
                    return params.node.id + 1;
                },
                // we don't want to sort by the row index, this doesn't make sense as the point
                // of the row index is to know the row index in what came back from the server
                suppressSorting: true,
                suppressMenu: true
            },
            {headerName: "Id", field: "id"},
            {headerName: "MyString", field: "myString", filter: 'text'},
            {headerName: "MyInteger", field: "myInteger", filter: 'number'},
            {headerName: "MyLong", field: "myLong"},
            {headerName: "MyFloat", field: "myFloat"},
            {headerName: "MyDouble", field: "myDouble"},
            {headerName: "MyDecimal", field: "myDecimal"},
            {headerName: "MyDate", field: "myDate"},
            {headerName: "MyDateTime", field: "myDateTime"},
            {headerName: "MyBoolean", field: "myBoolean"},
            {headerName: "MyEnumeration", field: "myEnumeration"},
            {
                headerName: "Action", width: 50,
                cellRenderer: function (params) {
                    return '<button type="submit"' +
                            ' ng-click=edit(' + params.data.id + ')' +
                    //' ui-sref="order.edit(' + params.data.id + ')"' +
                    ' class="btn btn-primary btn-sm">' +
                    '    <span class="glyphicon glyphicon-pencil"></span>&nbsp;<span translate="entity.action.edit"> Edit</span>' +
                    '</button>';
                }
            }
        ];

        $scope.edit = function(id) {
            $state.go('order.edit', {id: id});
        }

        $scope.gridOptions = {
            enableServerSideSorting: true,
            enableServerSideFilter: true,
            enableColResize: true,
            columnDefs: columnDefs,
            ready: function(api) {
                console.log('Callback ready: api = ' + api);
                console.log(api);
            },
            angularCompileRows: true
        };

        $scope.loadAll = function() {
            Order.query({page: $scope.page, per_page: $scope.pageSize}, function() {
                createNewDatasource();
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

        /* grid */
        /*
         ID	MyString	MyInteger	MyLong	MyFloat	MyDouble	MyDecimal	MyDate	MyDateTime	MyBoolean	MyEnumeration
         */

        //$scope.onPageSizeChanged = function() {
        //    createNewDatasource();
        //};
        //
        // when json gets loaded, it's put here, and  the datasource reads in from here.
        // in a real application, the page will be got from the server.
        //var allOfTheData;
        //
        //$http.get("../olympicWinners.json")
        //    .then(function(result){
        //        allOfTheData = result.data;
        //        createNewDatasource();
        //    });

        function createNewDatasource() {
            //if (!allOfTheData) {
            //    // in case user selected 'onPageSizeChanged()' before the json was loaded
            //    return;
            //}
            var dataSource = {
                pageSize: $scope.pageSize,
                getRows: function (params) {
                    // this code should contact the server for rows. however for the purposes of the demo,
                    // the data is generated locally, a timer is used to give the experience of
                    // an asynchronous call
                    var data = [];
                    var total = 0;
                    var page = Math.floor(params.startRow / $scope.pageSize) + 1;
                    console.log(params);

                    console.log('asking for ' + params.startRow + ' to ' + params.endRow);

                    Orders.query({page: page, per_page: $scope.pageSize, sort: params.sortModel, filter: params.filterModel}, function(result, headers) {
                        data = result.content;
                        total = result.totalElements;
                    });

                    setTimeout( function() {
                        params.successCallback(data, total);
                    }, 500);
                }
            };

            $scope.gridOptions.api.setDatasource(dataSource);
        }

        function sortAndFilter(sortModel, filterModel) {
            return sortData(sortModel, filterData(filterModel, $scope.orders))
        }

        function sortData(sortModel, data) {
            var sortPresent = sortModel && sortModel.length > 0;
            if (!sortPresent) {
                return data;
            }
            // do an in memory sort of the data, across all the fields
            var resultOfSort = data.slice();
            resultOfSort.sort(function(a,b) {
                for (var k = 0; k<sortModel.length; k++) {
                    var sortColModel = sortModel[k];
                    var valueA = a[sortColModel.field];
                    var valueB = b[sortColModel.field];
                    // this filter didn't find a difference, move onto the next one
                    if (valueA==valueB) {
                        continue;
                    }
                    var sortDirection = sortColModel.sort === 'asc' ? 1 : -1;
                    if (valueA > valueB) {
                        return sortDirection;
                    } else {
                        return sortDirection * -1;
                    }
                }
                // no filters found a difference
                return 0;
            });
            return resultOfSort;
        }

        function filterData(filterModel, data) {
            var filterPresent = filterModel && Object.keys(filterModel).length > 0;
            if (!filterPresent) {
                return data;
            }

            var resultOfFilter = [];
            //for (var i = 0; i<data.length; i++) {
            //    var item = data[i];
            //
            //    //if (filterModel.age) {
            //    //    var age = item.age;
            //    //    var allowedAge = parseInt(filterModel.age.filter);
            //    //    // EQUALS = 1;
            //    //    // LESS_THAN = 2;
            //    //    // GREATER_THAN = 3;
            //    //    if (filterModel.age.type == 1) {
            //    //        if (age !== allowedAge) {
            //    //            continue;
            //    //        }
            //    //    } else if (filterModel.age.type == 2) {
            //    //        if (age >= allowedAge) {
            //    //            continue;
            //    //        }
            //    //    } else {
            //    //        if (age <= allowedAge) {
            //    //            continue;
            //    //        }
            //    //    }
            //    //}
            //    //
            //    //if (filterModel.year) {
            //    //    if (filterModel.year.indexOf(item.year.toString()) < 0) {
            //    //        // year didn't match, so skip this record
            //    //        continue;
            //    //    }
            //    //}
            //    //
            //    //if (filterModel.country) {
            //    //    if (filterModel.country.indexOf(item.country) < 0) {
            //    //        // year didn't match, so skip this record
            //    //        continue;
            //    //    }
            //    //}
            //
            //    resultOfFilter.push(item);
            //}

            return resultOfFilter;
        }

    });
