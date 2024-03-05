/*
npm install -g gulp
npm install --save-dev
*/

var
    autoprefixer    = require('autoprefixer'),
    babel           = require('gulp-babel'),
    browserSync     = require('browser-sync').create(),
    changed         = require('gulp-changed'),
    concat          = require('gulp-concat'),
    gulp            = require('gulp'),
    imagemin        = require('gulp-imagemin'),
    postcss         = require('gulp-postcss'),
    reload          = browserSync.reload,
    sass            = require('gulp-sass'),
    sourcemaps      = require('gulp-sourcemaps'),
    uglify          = require('gulp-uglify');

var Plugins = [
    autoprefixer({
        overrideBrowserslist: ['last 20 version']
    })
];

gulp.task('styles', function(){
    var scssFiles = ['scss/*.scss'];
    gulp.src(scssFiles)
        .pipe(sourcemaps.init({loadMaps: true}))
        .pipe(sass({outputStyle: 'compressed'}).on('error', sass.logError))
        .pipe(postcss(Plugins))
        .pipe(sourcemaps.write('.', {includeContent: false, debug: true}))
        .pipe(gulp.dest('css'))
        .pipe(browserSync.stream());
});


gulp.task('watch', ['styles'], function () {
    gulp.watch(['scss/*.scss', 'scss/**/*.scss'], ['sass']);
});

gulp.task('default', [ 'watch']);
