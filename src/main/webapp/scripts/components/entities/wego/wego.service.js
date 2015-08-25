'use strict';

angular.module('sandboxApp')
    .factory('Wego', function ($resource, DateUtils) {
        return $resource('api/wegos/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.start = DateUtils.convertDateTimeFromServer(data.start);
                    data.end = DateUtils.convertDateTimeFromServer(data.end);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.myDate = DateUtils.convertLocaleDateToServer(data.myDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.myDate = DateUtils.convertLocaleDateToServer(data.myDate);
                    return angular.toJson(data);
                }
            }
        });
    })
    .factory('Wegos',function($resource){
        return $resource('api/wegos/q',{},{
            'query':{method:'GET'}
        });
    });
