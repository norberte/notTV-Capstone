import './NavBar.js';

// workaround for adding common styles.
const links = [
    "/css/styles.css",
    "/css/bootstrap/bootstrap-theme.min.css",
    // TODO: remove CDN reference
    "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
];
const HEAD = $("head")[0];
$(links).each((idx, l) => {
    $(HEAD).append(
	"<link href=\"" + l +"\" rel=\"stylesheet\" />"
    );
});
