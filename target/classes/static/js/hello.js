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
	}).when('/customer', {
		templateUrl : 'customerspage.html',
		controller : 'customerctrl',
		controllerAs: 'controller'
	}).otherwise('/');

	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

	// $sceProvider.enabled(true);
});

appModule.controller('navigation', function($rootScope, $http, $location, $route) {
			
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
			
			self.openCustomer = function() {
				$location.path('/customer');
			}

			var authenticate = function(credentials, callback) {

				var headers = credentials ? {
					authorization : "Basic "
							+ btoa(credentials.username + ":"
									+ credentials.password)
				} : {};

				$http.get('user', {
					headers : headers
				}).then(function(response) {
					if (response.data.name) {
						$rootScope.authenticated = true;
					} else {
						$rootScope.authenticated = false;
					}
					callback && callback($rootScope.authenticated);
				}, function() {
					$rootScope.authenticated = false;
					callback && callback(false);
				});

			}

			authenticate();

			self.credentials = {};
			self.login = function() {
				authenticate(self.credentials, function(authenticated) {
					if (authenticated) {
						console.log("Login succeeded")
						$location.path("/");
						self.error = false;
						$rootScope.authenticated = true;
					} else {
						console.log("Login failed")
						$location.path("/login");
						self.error = true;
						$rootScope.authenticated = false;
					}
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

appModule.controller('customerctrl', function($http,$sce, $window, $scope,$cookies) {
	var self = this;
	self.submitCustomer = function() {
		var csrf_token = $cookies.get('CSRF-TOKEN');
		$http.defaults.headers.post['X-CSRF-Token'] = csrf_token;
		
		$http.post('postcustomer', { firstname: self.firstname, lastname: self.lastname})
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
	
	// self.loadCustomers = function(){
		$http.get('getallcustomer').then(function(response) {
			$scope.allcustomers = response.data.data;
			console.log(response.data.data);
		},
		function(response) {
			console.log('error');
			console.log(response.data);
		}
		);		
	// };
	
});

appModule.controller('xss', function($http, $sce, $window, $scope,$cookies) {
	var self = this;
	self.inputtext = "<p> <input type='button' name='Redirect' class='btn btn-primary' value='Submit'  onclick='window.alert(document.cookie);' /></p>"; 
	self.outputtext = '';	
	
	$http.get('/getinfo/').then(function(response) {
		self.inputtext = angular.fromJson(response.data.data);
		console.log(response.data.data)
	});		
	
	
	self.submitxss = function() {
		self.outputtext = self.inputtext;
		 // send login data
		var csrf_token = $cookies.get('CSRF-TOKEN');
		$http.defaults.headers.post['X-CSRF-Token'] = csrf_token;
		
		$http.post('postxss', { xssinput: self.inputtext})
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