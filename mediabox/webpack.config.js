var path = require('path');
var webpack = require('webpack');

module.exports = {
    entry: {
	global: [
	    'jquery',
	    'bootstrap',
	    './src/main/js/global.js'
	],
	browse: './src/main/js/browse/Browse.js',
	upload: './src/main/js/upload/Upload.js'
    },
    output: {
	path: path.join(__dirname, 'src/main/resources/static/js/bundle'),
	filename: '[name]-bundle.js'
    },
    plugins: [
	new webpack.ProvidePlugin({
	    $: "jquery",
	    jQuery: "jquery"
	}),
	new webpack.optimize.CommonsChunkPlugin({
	    name: 'commons',
	    filename: 'common.js' // Name of the output file
	})
    ],
    module: {
    	rules : [
    	    {
		test: /\.js/,
    		exclude: /(node_modules)/,
    		loader: 'babel-loader',
    		query: {
    		    presets: ['env', 'react']
    		}
    	    }
    	]
    }
}
