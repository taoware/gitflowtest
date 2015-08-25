'use strict';

angular.module('sandboxApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('dummy', {
                parent: 'entity',
                url: '/dummys',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'sandboxApp.dummy.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/dummy/dummys.html',
                        controller: 'DummyController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('dummy');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('dummy.detail', {
                parent: 'entity',
                url: '/dummy/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'sandboxApp.dummy.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/dummy/dummy-detail.html',
                        controller: 'DummyDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('dummy');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Dummy', function($stateParams, Dummy) {
                        return Dummy.get({id : $stateParams.id});
                    }]
                }
            })
            .state('dummy.new', {
                parent: 'dummy',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/dummy/dummy-dialog.html',
                        controller: 'DummyDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {code: null, name: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('dummy', null, { reload: true });
                    }, function() {
                        $state.go('dummy');
                    })
                }]
            })
            .state('dummy.edit', {
                parent: 'dummy',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/dummy/dummy-dialog.html',
                        controller: 'DummyDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Dummy', function(Dummy) {
                                return Dummy.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('dummy', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
