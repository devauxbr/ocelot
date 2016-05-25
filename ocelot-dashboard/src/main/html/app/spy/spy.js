(function () {
	'use strict';
	angular.module('spy.module', []).config(config);

	/* @ngInject */
	function config($stateProvider) {
		$stateProvider.state('spy', {
			parent: 'template',
			url: '/spy',
			views: {
				"content@": {
					templateUrl: "app/spy/spy.html",
					controller: SpyCtrl,
					controllerAs: "ctrl",
					resolve: {
						sessions: initSessions
					}
				}
			}
		});
	}
	/* @ngInject */
	function SpyCtrl($scope, sessions) {
		var MSG = "// Enabled a client to monitor it !!!";
		var ctrl = this;
		ctrl.requestSubscriber = null;
		ctrl.addSubscriber = null;
		ctrl.removeSubscriber = null;
		ctrl.updateSubscriber = null;
		ctrl.sessions = sessions;
		ctrl.requests = [];
		ctrl.filterInput = "";
		ctrl.request = MSG;
		ctrl.monitored = null;
		ctrl.showOnlyWarning = false;
		ctrl.triggerDelay = 20;
		ctrl.refresh = refresh;
		ctrl.switchMonitor = switchMonitor;
		ctrl.selectRequest = selectRequest;
		activate();
		$scope.$on('$destroy', desactivate);

		function desactivate(){
			if(ctrl.requestSubscriber) {
				ctrl.requestSubscriber.unsubscribe();
				ctrl.requestSubscriber = null;
			}
			if(ctrl.addSubscriber) {
				ctrl.addSubscriber.unsubscribe();
				ctrl.addSubscriber = null;
			}
			if(ctrl.removeSubscriber) {
				ctrl.removeSubscriber.unsubscribe();
				ctrl.removeSubscriber = null;
			}
			if(ctrl.updateSubscriber) {
				ctrl.updateSubscriber.unsubscribe();
				ctrl.updateSubscriber = null;
			}
		}

		function activate() {
			console.log("acivate spy view");
			ctrl.addSubscriber = new Subscriber("sessioninfo-add").message(add);
			ctrl.removeSubscriber = new Subscriber("sessioninfo-remove").message(remove);
			ctrl.updateSubscriber = new Subscriber("sessioninfo-update").message(update);
		}
		function add(session) {
			ctrl.sessions.push(session);
			$scope.$apply();
		}
		function update(session) {
			ctrl.sessions.every(function (s, idx, arr) {
				if (s.id === session.id) {
					arr.splice(idx, 1, session);
					$scope.$apply();
					return false;
				}
				return true;
			});
		}
		function remove(session) {
			ctrl.sessions.every(function (s, idx, arr) {
				if (s.id === session.id) {
					arr.splice(idx, 1);
					$scope.$apply();
					return false;
				}
				return true;
			});
		}
		
		function selectRequest(request) {
			ctrl.request = "// Request\n" + JSON.stringify(request.mfc, null, 3) + "\n// Response\n" + JSON.stringify(request.mtc, null, 3);
		}
		function switchMonitor(id) {
			ctrl.requests = [];
			if (ctrl.monitored) {
				if (ctrl.requestSubscriber) {
					ctrl.requestSubscriber.unsubscribe().event(function (event) {
						sessionServices.unmonitorSession(ctrl.monitored);
					});
				}
			}
			ctrl.request = MSG;
			ctrl.requestSubscriber = null;
			if (this.monitored === id) {
				ctrl.monitored = null;
			} else {
				ctrl.monitored = id;
				ctrl.requestSubscriber = new Subscriber("request-event-" + id).message(function (result) {
					ctrl.requests.splice(0, 0, result);
					$scope.$apply();
				});
				sessionServices.monitorSession(id).then(function () {
					ctrl.request = "// Monitoring "+id+" enabled";
					$scope.$apply();
				}).catch(function (fault) {
					ctrl.request = "// Monitoring "+id+" failed\n"+JSON.stringify(fault, null, 3);
					ctrl.monitored = null;
					$scope.$apply();
					ctrl.requestSubscriber.unsubscribe();
				});
			}
		}
		function refresh() {
			sessionServices.getSessionInfos().then(function (sessions) {
				ctrl.sessions = sessions;
				$scope.$apply();
			});
		}
	}
	/* @ngInject */
	function initSessions($q) {
		var deferred = $q.defer();
		sessionServices.getSessionInfos().then(function (sessions) {
			deferred.resolve(sessions);
		});
		return deferred.promise;
	}
})();

