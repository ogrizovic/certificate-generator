(function(){
	'use strict'
	
	angular
		.module('app',['ngRoute','ngCookies'])
		.config(config)
		.run(run);
	config.$inject = ['$qProvider','$routeProvider','$locationProvider'];
	function config($qProvider,$routeProvider,$locationProvider){
	    $qProvider.errorOnUnhandledRejections(false);

		$routeProvider
		.when('/',{
			templateUrl:'pocetna/pocetna.view.html',			
			controller:'PocetnaController',
			controllerAs:'vm'
		})
		.when('/add',{
			templateUrl:'dodavanje/add.view.html',			
			controller:'AddController',
			controllerAs:'vm'
		})
		.when('/delete',{
			templateUrl:'brisanje/delete.view.html',			
			controller:'DeleteController',
			controllerAs:'vm'
		})
		.when('/get',{
			templateUrl:'prikazivanje/get.view.html',			
			controller:'GetController',
			controllerAs:'vm'
		})
	
		.otherwise({redirectTo:'/#'})
	}
})();