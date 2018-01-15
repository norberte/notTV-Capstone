// http://codys.club/blog/2015/07/04/webpack-create-multiple-bundles-with-entry-points/
var path = require('path');
var webpack = require('webpack');

// pack common dependencies
let commonsPlugin = new webpack.optimize.CommonsChunkPlugin({
    name: 'commons',
    filename: 'common.js' // Name of the output file
});

module.exports = {
    entry: {
	global: ['jquery', 'bootstrap', './src/main/js/NavBar.js'],
	browse: './src/main/js/browse/Browse.js',
	upload: './src/main/js/upload/Upload.js'
    },
    output: {
	path: path.join(__dirname, 'src/main/resources/static/js/bundle'),
	filename: '[name]-bundle.js'
    },
    plugins: [ commonsPlugin ],
    module: {
    	loaders: [
    	    {
    		exclude: /(node_modules)/,
    		loader: 'babel-loader',
    		query: {
    		    presets: ['env', 'react']
    		}
    	    }
    	]
    }
}
