import './NavBar.js';

// workaround for adding common styles.
const links = [
    // "/css/styles.css",
    "/css/nav.css",
    "/css/bootstrap/bootstrap-theme.min.css",
    // TODO: remove CDN reference
    "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
];
const HEAD = $("head")[0];
$(links).each((idx, l) => {
    $(HEAD).prepend(
        "<link href=\"" + l +"\" rel=\"stylesheet\" />"
    );
});

// reads URL parameters - from http://www.jquerybyexample.net/2012/06/get-url-parameters-using-jquery.html
function GetURLParameter(sParam) {
	var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
        	return sParameterName[1];
	    }
	}
}​
