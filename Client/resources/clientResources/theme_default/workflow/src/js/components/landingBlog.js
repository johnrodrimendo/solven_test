


var 
    animation_elements = $('.toload'),
    $lazy = $('.lazy')
;

lazy();
function lazy() {
    $lazy.unveil(0, function () {
        $(this).on('load', function () {
            $(this).addClass('loaded');
            if ($(".avatar figure").hasClass('loaded')) {
                $(".avatar figure").addClass("avatar-figure");
            }

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
    apareceImagen();
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

 function apareceImagen() {
    if ($(window).width() < 768) {
        var $laImagen = $('.main-banner').find('#bannerPrincipal');
        var $laImagenResponsive = $('.main-banner').find('#bannerResponsive');
        var $srcOriginal = $laImagen.attr('data-src');
        console.log($srcOriginal);
        var $srcOriginalResponsive = $laImagenResponsive.attr('data-src');
        if ($srcOriginal !== undefined) {
            $laImagen.removeAttr('src');
            $laImagenResponsive.attr('src', $srcOriginalResponsive);
            console.log($srcOriginalResponsive);
        } else {
            if ($srcOriginal !== undefined) {
                $laImagen.attr('src', $srcOriginal);
            }
        }
    } else {
        var $laImagen = $('.main-banner').find('#bannerPrincipal');
        var $laImagenResponsive = $('.main-banner').find('#bannerResponsive');
        var $srcOriginal = $laImagen.attr('data-src');
        var $srcOriginalResponsive = $laImagenResponsive.attr('data-src');
        if ($srcOriginal !== undefined) {
            $laImagenResponsive.removeAttr('src');
            $laImagen.attr('src', $srcOriginal);
        } else {
            if ($srcOriginal !== undefined) {
                $laImagen.attr('src', $srcOriginal);
            }
        }
    }
}
$('#subscribe').on('click', function (e) {
    e.preventDefault();
    var responseBlock = $('#response-block'),
        btn = $(this);
    if (!isEmail($('#email').val())) {
        responseBlock.html(errorAjaxMessage('Ingresa un email v&aacute;lido.'));
    } else {
        responseBlock.html('');
        btn.loading('', 2);
        defaultAjax({
            url: ajaxSubcribe,
            type: 'POST',
            data: {
                email: $('#email').val(),
            },
            success: function (data) {
                btn.unloading();
                $('#email-block').addClass('block-hidden');
                responseBlock.html(successAjaxMessage('Gracias por suscribirte, te enviaremos novedades.'));

            },
            error: function (xhr, data) {
                btn.unloading();
                responseBlock.html(errorAjaxMessage(data.message));
                return false;
            }
        });
    }
