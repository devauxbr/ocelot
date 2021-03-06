/**
 * 
 * Default value, can be override by gulp arguments
 * example : gulp --rootapp=app --dist=dist --bowerfolder=bower_components --jsname=appscripts.js --cssname=styles.css --bowerjs=bower.js
 */
/**
 * The directory where gulp export 
 */
module.exports.dist = 'target/classes/dashboard';
/**
 * The source root of website
 */
module.exports.rootApp = 'src/main/html';
/**
 * The name of js file result from app js
 */
module.exports.jsName = 'js/main.js'; 
/**
 * The name of css file result from app css and bower css
 */
module.exports.cssName = 'css/main.css'; 
/**
 * The name of js file result from bower js
 */
module.exports.bowerJs = 'js/vendors.js';
/**
 * The context path of server, the application name.
 */
module.exports.applicationContext = '';
/**
 * All vendors css files, they will be concat in cssName file
 */
module.exports.vendorsCssFiles = [
   'bootstrap/dist/css/bootstrap.css',
   'fontawesome/css/font-awesome.css',
   'animate.css/animate.css',
   'codemirror/lib/codemirror.css',
   'codemirror/theme/monokai.css',
	'highlightjs/styles/androidstudio.css',
	'angular-chart.js/dist/angular-chart.css'
];
/**
 * All dependencies js files from bower install
 */
module.exports.vendorsJsFiles = [
   'jquery/dist/jquery.min.js',
   'angular/angular.js',
   'angular-bootstrap/ui-bootstrap.js',
   'bootstrap/dist/js/bootstrap.js',
   'codemirror/lib/codemirror.js',
	'angular-ui-router/release/angular-ui-router.js',
   'angular-ui-codemirror/ui-codemirror.js',
	'codemirror/mode/javascript/javascript.js',
	'highlightjs/highlight.pack.js',
	'angular-highlightjs/angular-highlightjs.js',
	'Chart.js/Chart.js',
   'angular-chart.js/dist/angular-chart.js'
];
/**
 * Asset vendors files, can be a simple resource or structured object {"source":"", "target":""}
 * Typiquely asset tah can't be concate with others, or library try to access dynamicly (map, fonts, images...)
 */
module.exports.vendorsAssetFiles = [
   {"source": 'fontawesome/fonts/*', "target":'fonts'},
   {"source": 'bootstrap/dist/css/bootstrap.css.map', "target":'css'}
];

