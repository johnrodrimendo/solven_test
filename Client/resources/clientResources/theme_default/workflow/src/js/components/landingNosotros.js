


var 
    animation_elements = $('.toload'),
    $lazy = $('.lazy')
;

lazy();
function lazy() {
    $lazy.unveil(0, function () {
        $(this).on('load', function () {
            $(this).addClass('loaded');
        });
    });
}
$(window).on('scroll', function () {
    if ($(window).scrollTop() > 50) {
        console.log($(window).scrollTop());
        $("header").addClass("header-scroll");
    } else {
        $("header").removeClass("header-scroll");
    }
});
$(window).on('load', function () {
    check_if_in_view();
    $(window).on('scroll resize', check_if_in_view);
    $(window).trigger('scroll');
});
function check_if_in_view() {
    var window_height = $(window).height(),
        window_top_position = $(window).scrollTop(),
        window_bottom_position = (window_top_position + window_height) - 50;

    $.each(animation_elements, function () {
        var element = $(this),
            element_height = element.outerHeight(),
            element_top_position = element.offset().top,
            element_bottom_position = (element_top_position + element_height);
        if ((element_bottom_position >= window_top_position) && (element_top_position < window_bottom_position)) {
            element.addClass('loaded').removeClass('toload').find('.lazy').lazy();
        } else {
            //element.removeClass('loaded');
        }
    });
}

$('.h1').on('click', function (event) {
    var li = $(this).parent();
    $(this).parent().addClass('active');

    if ($('.p1').parent().hasClass('actived')) {
        console.log('algun li tiene actived - contenido');
        $(this).parent().removeClass('active');
        $(this).parent().removeClass('actived');
        $('.p1').parent().removeClass('actived');
    } else {
        console.log('ningun li tiene active');
        $(this).parent().parent().find('li').removeClass('active');
        $(this).parent().parent().find('li').removeClass('actived');
        $(this).parent().addClass('active');
        $('.p1').parent().addClass('actived');
    }
    $('html, body').animate({
        scrollTop: $("#sec_about").offset().top
    }, 1050);
});
$('.h2').on('click', function (event) {
    var li = $(this).parent();
    $(this).parent().addClass('active');

    if ($('.p2').parent().hasClass('actived')) {
        console.log('algun li tiene actived - contenido');
        $(this).parent().removeClass('active');
        $(this).parent().removeClass('actived');
        $('.p2').parent().removeClass('actived');
    } else {
        console.log('ningun li tiene active');
        $(this).parent().parent().find('li').removeClass('active');
        $(this).parent().parent().find('li').removeClass('actived');
        $(this).parent().addClass('active');
        $('.p2').parent().addClass('actived');
    }
    $('html, body').animate({
        scrollTop: $("#sec_about").offset().top
    }, 1050);
});
$('.h3').on('click', function (event) {
    var li = $(this).parent();
    $(this).parent().addClass('active');

    if ($('.p3').parent().hasClass('actived')) {
        console.log('algun li tiene actived - contenido');
        $(this).parent().removeClass('active');
        $(this).parent().removeClass('actived');
        $('.p3').parent().removeClass('actived');
    } else {
        console.log('ningun li tiene active');
        $(this).parent().parent().find('li').removeClass('active');
        $(this).parent().parent().find('li').removeClass('actived');
        $(this).parent().addClass('active');
        $('.p3').parent().addClass('actived');
    }
    $('html, body').animate({
        scrollTop: $("#sec_about").offset().top
    }, 1050);
});
$('.h4').on('click', function (event) {
    var li = $(this).parent();
    $(this).parent().addClass('active');

    if ($('.p4').parent().hasClass('actived')) {
        console.log('algun li tiene actived - contenido');
        $(this).parent().removeClass('active');
        $(this).parent().removeClass('actived');
        $('.p4').parent().removeClass('actived');
    } else {
        console.log('ningun li tiene active');
        $(this).parent().parent().find('li').removeClass('active');
        $(this).parent().parent().find('li').removeClass('actived');
        $(this).parent().addClass('active');
        $('.p4').parent().addClass('actived');
    }
    $('html, body').animate({
        scrollTop: $("#sec_about").offset().top
    }, 1050);
});
$('.h5').on('click', function (event) {
    var li = $(this).parent();
    $(this).parent().addClass('active');

    if ($('.p5').parent().hasClass('actived')) {
        console.log('algun li tiene actived - contenido');
        $(this).parent().removeClass('active');
        $(this).parent().removeClass('actived');
        $('.p5').parent().removeClass('actived');
    } else {
        console.log('ningun li tiene active');
        $(this).parent().parent().find('li').removeClass('active');
        $(this).parent().parent().find('li').removeClass('actived');
        $(this).parent().addClass('active');
        $('.p5').parent().addClass('actived');
    }
    $('html, body').animate({
        scrollTop: $(".p5").parent().offset().top - 400
    }, 1050);
});
$('.h6').on('click', function (event) {
    var li = $(this).parent();
    $(this).parent().addClass('active');

    if ($('.p6').parent().hasClass('actived')) {
        console.log('algun li tiene actived - contenido');
        $(this).parent().removeClass('active');
        $(this).parent().removeClass('actived');
        $('.p6').parent().removeClass('actived');
    } else {
        console.log('ningun li tiene active');
        $(this).parent().parent().find('li').removeClass('active');
        $(this).parent().parent().find('li').removeClass('actived');
        $(this).parent().addClass('active');
        $('.p6').parent().addClass('actived');
    }
    $('html, body').animate({
        scrollTop: $(".p6").parent().offset().top - 400
    }, 1050);
});
$('.h7').on('click', function (event) {
    var li = $(this).parent();
    $(this).parent().addClass('active');

    if ($('.p7').parent().hasClass('actived')) {
        console.log('algun li tiene actived - contenido');
        $(this).parent().removeClass('active');
        $(this).parent().removeClass('actived');
        $('.p7').parent().removeClass('actived');
    } else {
        console.log('ningun li tiene active');
        $(this).parent().parent().find('li').removeClass('active');
        $(this).parent().parent().find('li').removeClass('actived');
        $(this).parent().addClass('active');
        $('.p7').parent().addClass('actived');
    }
    $('html, body').animate({
        scrollTop: $(".p7").parent().offset().top - 400
    }, 1050);
});