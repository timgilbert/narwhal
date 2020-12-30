exports.config = {
  // See http://brunch.io/#documentation for docs.
  files: {
    javascripts: {
      joinTo: 'js/app.js'
    },
    stylesheets: {
      joinTo: 'css/app.css'
    },
    templates: {
      joinTo: 'js/app.js'
    }
  },

  conventions: {
    // This option sets where we should place non-css and non-js assets in.
    // By default, we set this to '/assets/static'. Files in this directory
    // will be copied to `paths.public`, which is 'priv/static' by default.
    assets: /^(static)/
  },

  // Phoenix paths configuration
  paths: {
    // Dependencies and current project directories to watch
    watched: ['static', 'css', 'scss', 'js', 'vendor', 'elm'],
    // Where to compile files to
    public: '../priv/static'
  },

  // Configure your plugins
  plugins: {
    babel: {
      // Do not use ES6 compiler in vendor code
      ignore: [/vendor/]
    },
    sass: {
      options: {
        includePaths: [
          'node_modules/bulma/sass',
          'node_modules/font-awesome/fonts'
        ]
      }
    },
    copycat: {
      fonts: [
        'node_modules/font-awesome/fonts',
      ],
      verbose : true
    },
    elmBrunch: {
      elmFolder: 'src',
      mainModules: ['Main.elm'],
      outputFolder: '../js',
      onlyChanged: true
    }
  },

  modules: {
    autoRequire: {
      'js/app.js': ['js/app']
    }
  },

  npm: {
    enabled: true
  }
};
