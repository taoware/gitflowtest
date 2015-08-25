'use strict';

angular.module('sandboxApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('test11', {
                parent: 'entity',
                url: '/test11',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'sandboxApp.test11.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/test11/test11s.html',
                        controller: 'Test11Controller'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('test11');
                        return $translate.refresh();
                    }]
                }
            })
            .state('test11Detail', {
                parent: 'entity',
                url: '/test11/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'sandboxApp.test11.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/test11/test11-detail.html',
                        controller: 'Test11DetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('test11');
                        return $translate.refresh();
                    }]
                }
            });
    });
