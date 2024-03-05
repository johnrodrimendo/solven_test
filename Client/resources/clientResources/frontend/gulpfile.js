// npm install -g gulp
// npm install --save-dev

var gulp         = require('gulp'),
    autoprefixer = require('autoprefixer'),
    postcss      = require('gulp-postcss'),
    uglify       = require('gulp-uglify'),
    concat       = require('gulp-concat'),
    sourcemaps   = require('gulp-sourcemaps'),
    sass         = require('gulp-sass'),
    changed      = require('gulp-changed'),
    imagemin     = require('gulp-imagemin'),
    babel        = require('gulp-babel'),
    browserSync  = require('browser-sync').create(),
    reload       = browserSync.reload;

var config = {
    sassLandingAlToque: {
        entry:  ['./static/sass/*.scss'],
        watch:  ['./static/sass/**/*.scss','./static/sass/**/**/*.scss'],
        output:  '../theme_default/build/css'
    },
    jsLandingAlToque:{
        entry: [
            './static/scripts/plugins/jquery.js',
            './static/scripts/plugins/lazy.js',
            './static/scripts/plugins/jquery.mask.js',
            './static/scripts/plugins/jquery.validate.js',
            './static/scripts/plugins/jquery.numeric.js',
            './static/scripts/plugins/select2.full.js',
            './static/scripts/plugins/jquery.unveil.js',
            './static/scripts/plugins/slick.js',
            './static/scripts/plugins/util.js',
            './static/scripts/plugins/sweetalert.js',
            './static/scripts/plugins/mobile-detect.js',
            './static/scripts/LandingEfectivoAltoque.js'],
        watch:  ['./static/scripts/**/*.js','./static/scripts/*.js','./static/scripts/**/*.js'],
        output: '../theme_default/build/js'
    },
    images: {
        entry:  ['./static/img/**/*.+(png|jpg|gif|jpeg|svg)','./static/img/*.+(png|jpg|gif|jpeg|svg)'],
        watch:  ['./static/img/**/*.+(png|jpg|gif|jpeg|svg)','./static/img/*.+(png|jpg|gif|jpeg|svg)'],
        output: '../theme_default/build/img'
    },
    browserFiles:{
        entry:  [ './static/sass/*.scss', './static/scss/*.scss',
            './static/scripts/*.js', './static/scripts/*.js',
            '../../../html/*.html', '../../../../html/*.html']
    }
};
var  postcssPlugins = [
    autoprefixer({browsers: ['last 2 version']})
];
gulp.task('sassLandingAlToque', function(){
    gulp.src(config.sassLandingAlToque.entry)
        .pipe(sourcemaps.init({loadMaps: true}))
        .pipe(sass({outputStyle: 'compressed'}).on('error', sass.logError))
        .pipe(postcss(postcssPlugins))
        .pipe(sourcemaps.write('.', {includeContent: false, debug: true}))
        .pipe(gulp.dest(config.sassLandingAlToque.output))
        .pipe(browserSync.stream());
});


gulp.task('jsLandingAlToque', function() {
    gulp.src(config.jsLandingAlToque.entry)
        .pipe(concat('LandingEfectivoAltoque.js'))
        .pipe(babel())
        .pipe(uglify({compress: true}))
        .pipe(gulp.dest(config.jsLandingAlToque.output))
        .pipe(browserSync.stream());
});

gulp.task('images', function() {
    gulp.src(config.images.entry)
        .pipe(changed(config.images.output))
        .pipe(imagemin({progressive: true, verbose: true}))
        .pipe(gulp.dest(config.images.output));
});

gulp.task('browser-sync', function() {
    browserSync.init(config.browserFiles.entry,{
        notify: false,
        open: false,
        proxy: 'http://localefectivoaltoque.com:8080/'
    });
});

gulp.task('watch', ['jsLandingAlToque' , 'sassLandingAlToque' ,  'images', 'browser-sync'], function () {
    gulp.watch(config.jsLandingAlToque.watch, ['jsLandingAlToque']);
    gulp.watch(config.sassLandingAlToque.watch, ['sassLandingAlToque']);
    gulp.watch(config.images.watch,     ['images']);

});

gulp.task('build', ['jsLandingAlToque' , 'sassLandingAlToque' ,  'images']);
gulp.task('default', ['watch']);
