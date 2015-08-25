'use strict';

angular.module('sandboxApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('wego', {
                parent: 'entity',
                url: '/wego',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'sandboxApp.wego.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/wego/wegos.html',
                        controller: 'WegoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('wego');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('wego.detail', {
                parent: 'entity',
                url: '/wego/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'sandboxApp.wego.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/wego/wego-detail.html',
                        controller: 'WegoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('wego');
                        return $translate.refresh();
                    }],
                    entity:['$stateParams','Wego',function($stateParams,Wego){
                        return Wego.get({id: $stateParams.id});
                    }]
                }
            })

            .state('wego.addnew',{
                parent:'wego',
                url:'/new',
                data:{
                    roles:['ROLE_USER'],
                    pageTitle:'sandboxApp.wego.new.title'
                },
                views:{
                    'content@':{
                        templateUrl:'scripts/app/entities/wego/wego-addnew.html',
                        controller:'WegoNewController'
                    }
                },
                resolve: {
                    translatePartialLoader:[ '$translate','$translatePartialLoader',function($translate,$translatePartialLoader){
                        $translatePartialLoader.addPart('wego');
                        return $translate.refresh();
                    }]
                }

            })
            /*.state('wego.new',{
                parent:'wego',
                url:'/new',
                data:{
                    roles:['ROLE_USER']
                },
                onEnter:['$stateParams','$state','$modal',function($stateParams,$state,$modal){
                    $modal.open({
                        templateUrl:'scripts/app/entities/wego/wego-dialog.html',
                        controller:'WegoDialogController',
                        size:'lg',
                        resolve:{
                            entity:function(){
                                return {
                                    title:null,description:null,start:null,end:null,file:null
                                };
                            }
                        }
                    }).result.then(function(result){
                            $state.go('wego',null,{reload:true});
                        },function(){
                            $state.go('wego');
                        })
                }]
            })*/
            .state('wego.edit',{
                parent:'wego',
                url:'/{id}/edit',
                data:{
                    roles:['ROLE_USER']
                },
                onEnter:['$stateParams','$state','$modal',function($stateParams,$state,$modal){
                    $modal.open({
                        templateUrl:'scripts/app/entities/wego/wego-dialog.html',
                        controller:'WegoDialogController',
                        size:'lg',
                        resolve:{
                            entity:['Wego',function(Wego){
                                return Wego.get({id:$stateParams.id});
                            }]
                        }
                    }).result.then(function(result){
                            $state.go('wego',null,{reload:true});
                        },function(){
                            $state.go('^');
                        })
                }]
            });
    });
