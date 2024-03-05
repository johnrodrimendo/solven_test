// npm install -g gulp
// npm install --save-dev

var gulp            = require('gulp'),
    autoprefixer    = require('autoprefixer'),
    babel           = require('gulp-babel'),
    changed         = require('gulp-changed'),
    concat          = require('gulp-concat'),
    extReplace      = require("gulp-ext-replace");
    htmlmin         = require('gulp-htmlmin');
    imagemin        = require('gulp-imagemin'),
    postcss         = require('gulp-postcss'),
    sass            = require('gulp-sass'),
    sourcemaps      = require('gulp-sourcemaps'),
    uglify          = require('gulp-uglify'),
    webp            = require('imagemin-webp');
    browserSync     = require('browser-sync').create(),
    debug = require('gulp-debug'),
    minify = require('gulp-minify'),
    terser = require('gulp-terser'),
    reload          = browserSync.reload;


var scriptWatch = [
    './assets/js/**/*.js',
    './assets/js/*.js',
    './assets/scripts/**/*.js',
    './assets/scripts/*.js',
    './src/js/**/*.js',
    './src/js/*.js'
]

var config = {
    /*----------------------------------------------------------------------------------*/
    /* HTML
    /*----------------------------------------------------------------------------------*/
    html:{
        entry: [
            '../../../html/*.html',
            '../../../../html/*.html'
        ],
        watch: [
            '../../../html/*.html',
            '../../../../html/*.html'
        ],
        output: '../../../html'
    },


    /*----------------------------------------------------------------------------------*/
    /* CSS
    /*----------------------------------------------------------------------------------*/
    styles: {
        aux : ['./src/scss/lendingService.scss'],
        entry: [
            './assets/sass/*.scss',
            './assets/scss/*.scss',
            './src/scss/*.scss',
            './src/scss/lendingService.scss',
            './src/scss/entityExt/*.scss',
            './src/scss/companyExt/*.scss',
            './src/scss/efectivoAltoque/*.scss'],
        watch:  [
        './assets/sass/*.scss',
        './assets/sass/**/*.scss' ,
        './assets/scss/*.scss',
        './assets/scss/**/*.scss',
        './src/scss/*.scss',
        './src/scss/**/*.scss'
        ],
        output:  '../build/css'
    },
    /*----------------------------------------------------------------------------------*/
    /* JS
    /*----------------------------------------------------------------------------------*/

    jsLibraries:{
        entry:  [
            './assets/scripts/libraries/jquery-3.6.0.min.js',
            './assets/scripts/libraries/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/jquery.unveil.js',
            './assets/scripts/plugins/lazy.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './assets/scripts/libraries/bootstrap.js',
            './assets/scripts/libraries/select2.full.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/libraries/sweetalert.min.js',
            './assets/scripts/libraries/moment.min.js',
            './assets/scripts/libraries/moment-with-locales.min.js',
            './assets/scripts/libraries/bootstrap-datepicker.min.js',
            './assets/scripts/libraries/bootstrap-datepicker.es.min.js',
            './assets/scripts/libraries/bootstrap-datetimepicker.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/jquery.validate.min.js',
            './assets/scripts/libraries/additional-methods.min.js',
            './assets/scripts/libraries/owl.carousel.min.js',
            './assets/scripts/libraries/imgProgress.js',
            './assets/scripts/libraries/nprogress.js',
            './assets/js/QuestionFramework.js',
            './assets/scripts/plugins/jquery.media.js',
            './assets/js/Oauthy.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/js/take-picture/Tp.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsMain: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/jquery.mask.js',
            './assets/scripts/plugins/jquery.unveil.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/owl.carousel.js',
            './assets/scripts/main.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingHomePeru: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './src/js/components/landingHomePeru.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingHomeColombia: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './src/js/components/landingHomeColombia.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingHomeArgentina: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './src/js/components/landingHomeArgentina.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },

    jsLanding5:{
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/libraries/bootstrap.js',
            '../../external/jquery-masknumber/dist/jquery.masknumber.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/sweetalert.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/jquery.mask.js',
            './src/js/components/owl.carousel.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './src/js/components/landing5.js',
            './src/js/components/globalModalContact.js',
            './src/js/components/localstorageLanding.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingReferred:{
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/sweetalert.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/sweetalert.js',
            './assets/scripts/plugins/jquery.mask.js',
            './src/js/components/owl.carousel.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './src/js/components/landingReferred.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },

    howItWorks:{
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/sweetalert.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/jquery.mask.js',
            './src/js/components/owl.carousel.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './assets/scripts/libraries/clipboard.min.js',
            './src/js/components/howItWorks.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },

    statisticsPayments:{
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/sweetalert.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/jquery.mask.js',
            './src/js/components/owl.carousel.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './assets/scripts/libraries/clipboard.min.js',
            './src/js/components/statisticsPayments.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },

    landingReferralUserData:{
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/sweetalert.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/jquery.mask.js',
            './src/js/components/owl.carousel.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './src/js/components/landingReferralUserData.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    
    jsLandingEmpresas: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/jquery.mask.js',
            './src/js/components/landingEmpresas.js',
            './src/js/components/globalModalContact.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingColaboradores: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/jquery.mask.js',
            './src/js/components/landingColaboradores.js',
            './src/js/components/globalModalContact.js'],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLendingService: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/sweetalert.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/jquery.mask.js',
            './src/js/components/lendingService.js',
            './src/js/components/globalModalContact.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },

    jsLandingCambios: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/sweetalert.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './src/js/components/landingCambios.js',
            './src/js/components/globalModalContact.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingRemarketing: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/jquery.mask.js',
            './assets/scripts/plugins/jquery.unveil.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/landingRemarketing.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingProducts: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/additional-methods.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/libraries/mobile-detect.js',
            './assets/scripts/plugins/jquery.mask.js',
            './assets/scripts/plugins/jquery.unveil.js',
            './assets/scripts/plugins/moment-with-locales.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/landingProducts.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingNosotros: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/jquery.unveil.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/landingNosotros.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingEfectivoAltoque: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/jquery.mask.js',
            './assets/scripts/plugins/jquery.validate.js',
            './assets/scripts/libraries/jquery.numeric.min.js',
            './assets/scripts/plugins/select2.full.js',
            './assets/scripts/plugins/jquery.unveil.js',
            './src/js/components/slick.js',
            './src/js/components/slick.min.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/sweetalert.js',
            './assets/scripts/libraries/mobile-detect.js',
            './src/js/components/landingEfectivoAltoque.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsLandingBlog: {
        entry: [
            './assets/scripts/plugins/jquery-3.6.0.min.js',
            './assets/scripts/plugins/jquery-migrate-3.3.2.min.js',
            './assets/scripts/plugins/lazy.js',
            './assets/scripts/plugins/crypto-js/crypto-js.js',
            './assets/js/util.js',
            './assets/scripts/plugins/jquery.unveil.js',
            './assets/scripts/plugins/sweetalert.js',
            './src/js/components/landingBlog.js'
        ],
        watch: [scriptWatch],
        output: '../build/js'
    },
    jsBoTableFilter: {
        entry: [
            './assets/js/bo_tablefilter.js',
        ],
        output: '../build/js'
    },
    /*----------------------------------------------------------------------------------*/
    /* IMAGES
    /*----------------------------------------------------------------------------------*/
    images: {
        entry:  ['./assets/img/**/*.+(png|jpg|gif|jpeg|svg)','./assets/img/*.+(png|jpg|gif|jpeg|svg)'],
        watch:  ['./assets/img/**/*.+(png|jpg|gif|jpeg|svg)','./assets/img/*.+(png|jpg|gif|jpeg|svg)'],
        output: '../build/img'
    },
    webp: {
        entry: [
            '../build/img/**/*.+(png|jpg|jpeg)', 
            '../build/img/*.+(png|jpg|jpeg)'
        ],
        watch: [
            '../build/img/**/*.+(png|jpg|jpeg)', 
            '../build/img/*.+(png|jpg|jpeg)'],
            output: '../build/img'
    },

    /*----------------------------------------------------------------------------------*/
    /* DOCUMENTS
    /*----------------------------------------------------------------------------------*/
    documents: {
        entry:  ['./src/documents/*.+(pdf|txt|gif|jpeg|svg)'],
        watch:  ['./src/documents/*.+(pdf|txt|gif|jpeg|svg)'],
        output: '../build/documents'
    },

    /*----------------------------------------------------------------------------------*/
    /* browserFiles
    /*----------------------------------------------------------------------------------*/
    browserFiles:{
        entry:  [
            './assets/sass/*.scss',
            './assets/scss/*.scss',
            './src/scss/*.scss',
            './src/js/*.js',
            './assets/scripts/*.js',
            './assets/js/*.js',
            '../../../html/*.html', '../../../../html/*.html']
    }
};


var  postcssPlugins = [
    autoprefixer({overrideBrowserslist: ["last 20 version"]
    })
];



/*----------------------------------------------------------------------------------*/
/* HTML
/*----------------------------------------------------------------------------------*/
gulp.task('html', function () {
    gulp.src(config.html.entry)
        .pipe(minifyHTML({
            empty: true,
            cdata: true, 
            conditionals:true,
            comments: true,
            spare: true,
            quotes:true
        }))
        .pipe(gulp.dest(config.html.output))
        .pipe(browserSync.stream());
});

/*----------------------------------------------------------------------------------*/
/* CSS
/*----------------------------------------------------------------------------------*/

gulp.task('styles-lending', function () {
    gulp.src(config.styles.aux)
        .pipe(sourcemaps.init({loadMaps: true}))
        .pipe(debug({title: 'debug:'}))
        .pipe(sass({outputStyle: 'compressed'
        }).on('error', sass.logError))
        .pipe(postcss(postcssPlugins))
        .pipe(sourcemaps.write('.', {includeContent: false, debug: true}))
        .pipe(gulp.dest(config.styles.output))
        .pipe(browserSync.stream());
});

gulp.task('styles', function () {
    gulp.src(config.styles.entry)
        .pipe(sourcemaps.init({loadMaps: true}))
        .pipe(debug({title: 'debug:'}))
        .pipe(sass({outputStyle: 'compressed'
        }).on('error', sass.logError))
        .pipe(postcss(postcssPlugins))
        .pipe(sourcemaps.write('.', {includeContent: false, debug: true}))
        .pipe(gulp.dest(config.styles.output))
        .pipe(browserSync.stream());
});
/*----------------------------------------------------------------------------------*/
/* JS
/*----------------------------------------------------------------------------------*/
gulp.task('jsLibraries', function () {
    console.log('<---jsLibraries --->');
    gulp.src(config.jsLibraries.entry)
        .pipe(concat('libraries.min.js'))
        .pipe(debug({title: 'debug:'}))
        .pipe(babel())
        // .pipe(uglify({compress: true}))
        .pipe(gulp.dest(config.jsLibraries.output))
        .pipe(browserSync.stream());
});

gulp.task('jsMain', function() {
    gulp.src(config.jsMain.entry)
        .pipe(concat('main.min.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsMain.output))
        .pipe(browserSync.stream());
});
gulp.task('jsLandingHomePeru', function () {
    gulp.src(config.jsLandingHomePeru.entry)
        .pipe(concat('landingHomePeru.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingHomePeru.output))
        .pipe(browserSync.stream());
});

gulp.task('jsLandingHomeColombia', function () {
    gulp.src(config.jsLandingHomeColombia.entry)
        .pipe(concat('landingHomeColombia.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingHomeColombia.output))
        .pipe(browserSync.stream());
});
gulp.task('jsLandingHomeArgentina', function () {
    gulp.src(config.jsLandingHomeArgentina.entry)
        .pipe(concat('landingHomeArgentina.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingHomeArgentina.output))
        .pipe(browserSync.stream());
});
gulp.task('jsLanding5', function () {
    gulp.src(config.jsLanding5.entry)
        .pipe(concat('landing5.js'))
        //.pipe(babel())
        //.pipe(uglify())
        .pipe(gulp.dest(config.jsLanding5.output))
        .pipe(browserSync.stream());
});

gulp.task('jsLandingReferred', function () {
    gulp.src(config.jsLandingReferred.entry)
        .pipe(concat('landingReferred.js'))
        //.pipe(babel())
        //.pipe(uglify())
        .pipe(gulp.dest(config.jsLandingReferred.output))
        .pipe(browserSync.stream());
});

gulp.task('howItWorks', function () {
    gulp.src(config.howItWorks.entry)
        .pipe(concat('howItWorks.js'))
        //.pipe(babel())
        //.pipe(uglify())
        .pipe(gulp.dest(config.howItWorks.output))
        .pipe(browserSync.stream());
});

gulp.task('statisticsPayments', function () {
    gulp.src(config.statisticsPayments.entry)
        .pipe(concat('statisticsPayments.js'))
        //.pipe(babel())
        //.pipe(uglify())
        .pipe(gulp.dest(config.statisticsPayments.output))
        .pipe(browserSync.stream());
});

gulp.task('jsLandingReferralUserData', function () {
    gulp.src(config.landingReferralUserData.entry)
        .pipe(concat('landingReferralUserData.js'))
        //.pipe(babel())
        //.pipe(uglify())
        .pipe(gulp.dest(config.landingReferralUserData.output))
        .pipe(browserSync.stream());
});

gulp.task('jsLandingCambios', function() {
    gulp.src(config.jsLandingCambios.entry)
        .pipe(concat('landingCambios.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingCambios.output))
        .pipe(browserSync.stream());
});
gulp.task('jsLandingEmpresas', function () {
    gulp.src(config.jsLandingEmpresas.entry)
        .pipe(concat('landingEmpresas.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingEmpresas.output))
        .pipe(browserSync.stream());
});

gulp.task('jsLandingEfectivoAltoque', function () {
    gulp.src(config.jsLandingEfectivoAltoque.entry)
        .pipe(concat('landingEfectivoAltoque.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingEfectivoAltoque.output))
        .pipe(browserSync.stream());
});
gulp.task('jsLandingColaboradores', function() {
    gulp.src(config.jsLandingColaboradores.entry)
        .pipe(concat('landingColaboradores.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingColaboradores.output))
        .pipe(browserSync.stream());
});
gulp.task('jsLendingService', function () {
    gulp.src(config.jsLendingService.entry)
        .pipe(concat('lendingService.js'))
        .pipe(babel()) 
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLendingService.output))
        .pipe(browserSync.stream());
});

gulp.task('jsLandingRemarketing', function () {
    gulp.src(config.jsLandingRemarketing.entry)
        .pipe(concat('landingRemarketing.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingRemarketing.output))
        .pipe(browserSync.stream());;
});
gulp.task('jsLandingProducts', function () {
    gulp.src(config.jsLandingProducts.entry)
        .pipe(concat('landingProducts.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingProducts.output))
        .pipe(browserSync.stream());;
});
gulp.task('jsLandingNosotros', function () {
    gulp.src(config.jsLandingNosotros.entry)
        .pipe(concat('landingNosotros.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingNosotros.output))
        .pipe(browserSync.stream());;
});
gulp.task('jsLandingBlog', function () {
    gulp.src(config.jsLandingBlog.entry)
        .pipe(concat('landingBlog.js'))
        .pipe(babel())
        .pipe(uglify())
        .pipe(gulp.dest(config.jsLandingBlog.output))
        .pipe(browserSync.stream());;
});

gulp.task('jsBoTableFilter', function () {
    console.log('<---jsBoTableFilter --->');
    gulp.src(config.jsBoTableFilter.entry)
        .pipe(concat('bo_tablefilter.js'))
        .pipe(debug({title: 'debug:'}))
        .pipe(terser())
        .pipe(gulp.dest(config.jsBoTableFilter.output))
        .pipe(browserSync.stream());
});

gulp.task('images', function() {
    gulp.src(config.images.entry)
        .pipe(changed(config.images.output))
        .pipe(imagemin({progressive: true, verbose: true}))
        .pipe(gulp.dest(config.images.output));
});

gulp.task('webp', function () {
    gulp.src(config.webp.entry)
        .pipe(imagemin([
            webp()
        ]))
        .pipe(extReplace(".webp"))
        .pipe(gulp.dest(config.webp.output));
});

gulp.task('documents', function () {
    gulp.src(config.documents.entry)
        .pipe(gulp.dest(config.documents.output));
});


gulp.task('browser-sync', function() {
    browserSync.init(config.browserFiles.entry,{
        open:false,
        notify: false,
        proxy: "http://solven-test.pe:8080/"
    });
});

gulp.task('scripts', ['jsLibraries', 'jsMain', 'jsLandingHomePeru', 'jsLandingHomeColombia'
    ,'jsLandingHomeArgentina', 'jsLanding5', 'jsLandingCambios', 'howItWorks', 'statisticsPayments','jsLandingReferred', 'jsLandingReferralUserData','jsLandingEmpresas', 'jsLandingColaboradores', 'jsLendingService', 'jsLandingRemarketing', 'jsLandingProducts', 'jsLandingNosotros', 'jsLandingBlog', 'jsLandingEfectivoAltoque', 'jsBoTableFilter'
]);

gulp.task('watch', ['documents','images', 'scripts', 'styles', 'webp', 'browser-sync'], function () {
    gulp.watch(config.images.watch, ['images']);
    gulp.watch(scriptWatch, ['scripts']);
    gulp.watch(config.styles.watch, ['styles']);
    gulp.watch(config.webp.watch, ['webp']);
});



gulp.task('deploy', ['documents','styles', 'scripts', 'images']);
gulp.task('default', ['watch']);
