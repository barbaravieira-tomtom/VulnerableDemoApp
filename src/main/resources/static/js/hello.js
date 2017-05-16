var appModule = angular.module('hello', [ 'ngRoute', 'ngCookies', 'ngResource' ]);

appModule.config(['$httpProvider', function($httpProvider) {
	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest'; 
 }]);

appModule.config(function($routeProvider, $httpProvider, $sceProvider) {
	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'home',
		controllerAs: 'controller'
	}).when('/login', {
		templateUrl : 'login1.html',
		controller : 'navigation',
		controllerAs: 'controller'
	}).when('/xss', {
		templateUrl : 'xss2.html',
		controller : 'xss',
		controllerAs: 'controller'
	}).when('/xss1', {
		templateUrl : 'xss1.html'
	}).when('/error', {
		templateUrl : 'error.html'
	})
	.when('/usersinfo', {
		templateUrl : 'usersinfopage.html',
		controller : 'usersinfoctrl',
		controllerAs: 'controller'
	}).otherwise('/');

	$httpProvider.defaults.headers.common['X-Requested-With'] ='XMLHttpRequest';

	// $sceProvider.enabled(true);
});

appModule.controller('navigation', function($rootScope, $http, $location, $route, $cookies) {
			
			var self = this;

			self.tab = function(route) {
				return $route.current && route === $route.current.controller;
			};
			
			self.openXSS = function() {
				$location.path('/xss');
			}
			
			self.openLogin = function() {
				$location.path('/login');
			}
			
			self.openXSS1 = function() {
				$location.path('/xss1');
			}
			
			self.openHome = function() {
				$location.path('/home');
			}
			
			self.openUsersInfo = function() {
				$location.path('/usersinfo');
			}
			
			var csrf_token = $cookies.get('CSRF-TOKEN');
			$http.defaults.headers.post['X-CSRF-Token'] = csrf_token;
			self.tab = function(route) {
				return $route.current && route === $route.current.controller;
			};

			var authenticate = function(callback) {

				$http.get('user').then(function(response) {
					if (response.data.name) {
						$rootScope.authenticated = true;
					} else {
						$rootScope.authenticated = false;
					}
					callback && callback();
				}, function() {
					$rootScope.authenticated = false;
					callback && callback();
				});

			}

			authenticate();

			self.credentials = {};
			self.login = function() {
				$http.post('login', $.param(self.credentials), {
					headers : {
						"content-type" : "application/x-www-form-urlencoded"
					}
				}).then(function() {
					authenticate(function() {
						if ($rootScope.authenticated) {
							console.log("Login succeeded")
							$location.path("/");
							self.error = false;
							$rootScope.authenticated = true;
						} else {
							console.log("Login failed with redirect")
							$location.path("/login");
							self.error = true;
							$rootScope.authenticated = false;
						}
					});
				}, function() {
					console.log("Login failed")
					$location.path("/login");
					self.error = true;
					$rootScope.authenticated = false;
				})
			};

			self.logout = function() {
				$http.post('logout', {}).finally(function() {
					$rootScope.authenticated = false;
					$location.path("/");
				});
			}

		});

appModule.controller('home', function($http) {
	var self = this;
	$http.get('/resource/').then(function(response) {
		self.greeting = response.data;
	})
});

appModule.controller('usersinfoctrl', function($http,$sce,$location,$rootScope, $window, $scope,$cookies) {
	var self = this;
	
	self.submitUsersInfo = function() {
		var csrf_token = $cookies.get('CSRF-TOKEN');
		$http.defaults.headers.post['X-CSRF-Token'] = csrf_token;
		
		$http.post('postusersinfo', { firstname: self.firstname, lastname: self.lastname})
		   .then(
		       function(response){
		    	  self.addusersinfo = 'Successfully added user to the system.';
		          console.log('success'); 
		       }, 
		       function(response){
		    	 self.addusersinfo = 'Failed added user to the system.';
		    	 console.log(response.data)
		         console.log('failure');
		       }
		    );
	};
	
	$scope.propertyName = 'firstname'; // constructor.constructor('alert(document.cookie)')()
	$scope.reverse = true; 
	$scope.sortBy = function(propertyName) {
	    $scope.propertyName = propertyName;
	};
	
		$http.get('getallusersinfo').then(function(response) {
			if(response) {
				$scope.allusersinfo = response.data.data;
				console.log('success');
				console.log(response.data.data);
				$location.path("/usersinfo");

			}
			else {
				$location.path("/usersinfo");
				console.log('error');
			}
		},
		function(response) {
			$location.path("/usersinfo");
			console.log('error');
			console.log(response.data);
		}
		);		
	
});

appModule.controller('xss', function($http, $sce, $window, $scope,$cookies) {
	var self = this;
	self.inputtext = "<p> <input type='button' name='Redirect' "+ 
					" class='btn btn-primary' value='Submit'  " + 
					" onclick='window.alert(document.cookie);' /></p>"; 
	self.outputtext = '';	
	
	$http.get('/getxss').then(function(response) {
		self.inputtext = response.data.data;
		console.log(response.data)
	});		

	
	self.submitxss = function() {
		self.outputtext = self.inputtext;
		 // send login data
		var csrf_token = $cookies.get('CSRF-TOKEN');
		$http.defaults.headers.post['X-CSRF-Token'] = csrf_token;
		
		$http.post('postxss', self.inputtext)
		   .then(
		       function(response){
		          console.log('success'); 
		       }, 
		       function(response){
		    	 console.log(response.data)
		         console.log('failure');
		       }
		    );
	};
});