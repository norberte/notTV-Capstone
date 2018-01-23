var path = require('path');
var webpack = require('webpack');

module.exports = {
    entry: {
        global: [
            'jquery',
            'bootstrap',
            './src/main/js/global.js'
        ],
        admin: './src/main/js/admin/Admin.js',
        browse: './src/main/js/browse/Browse.js',
        upload: './src/main/js/upload/Upload.js',
        userProfile: './src/main/js/userProfile/userProfile.js',
        watch:  './src/main/js/video/Video.js',
        account: './src/main/js/account/Account.js'
    },
    output: {
	path: path.join(__dirname, 'src/main/resources/static/js/bundle'),
	filename: '[name]-bundle.js'
    },
    resolve: { // resolve utilities file.
        alias: {
            utils: path.resolve(__dirname, './src/main/js/utils.js')  
        }
    },
    plugins: [
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery",
            utils: "utils"
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
};
