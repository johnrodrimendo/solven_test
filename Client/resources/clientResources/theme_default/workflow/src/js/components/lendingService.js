var GOOGLE_SHEETS_MACROS_API_URL = 'https://script.google.com/macros/s/AKfycbyfCrr0IW_Ogt2_s7qcInhxM3CsOthvJtvcer6M0kXdUakR4CA/exec';

if (hostIsProduction) {
    GOOGLE_SHEETS_MACROS_API_URL = 'https://script.google.com/macros/s/AKfycbwpoH1oA7my-Y5jP6MisXxVZtTJpjcxy5y1eir2zcoczYRomJK3/exec';
}
 
var lazy                    = $('.lazy'),
    body                    = $('body'),
    animation_elements      = $('.toload'),
    sec_news                = $('.sec-news'),
    sec_benefits            = $('.sec-benefits'),
    btn_down                = $('#btn-down'),
    btn_call_modalContact   = $('#btnContact'),
    modalContact            = $('#contact-modal'),
    header                  = $('header'),
    //talkAboutUs             = $('.sec-workus .owlCarousel'),
    //talkToUS                = $('.talk-to-us .owlCarousel'),
    boxWhite                = $('.box-trans'),
    talkToUS                = $('.talk-to-us'),
    workerUs                = $('.worker-us'),
    closes                  = $('#close'),
    modalChange             = $("#modalChange"),
    shutDown = $(".shutDown"),
    form = $("#lending-form"),
    phone = $('[name=phone]'),
    code = $('[name=code]')
;


$(document).on('ready', function () {


});
$(window).on('scroll', function () {
    if ($(window).scrollTop() > 50) {
        $("header").addClass("header-scroll");
    } else {
        $("header").removeClass("header-scroll");
    }
});
$(window).on('load', function () {
    check_if_in_view();
    $(window).on('scroll resize', check_if_in_view);
    $(window).trigger('scroll');

    if (formValidator != null) {
        var jsonValidator = JSON.parse(formValidator);
        form.validateForm(createFormValidationJson(jsonValidator, form));
    }

    form.find(".field input[type='text']", '.field textarea').on('keyup', function (event) {
        if (event.keyCode === 13) {
            if ($('#loanForm').valid()) {
                $("#btnReview").click();
            }
            event.preventDefault();
            return false;
        }
    });

    phone.mask('000-#');

    if (countryId === 54) {
        var staticPhoneNumberMaxlength = form.data('validatorJson').rules.phone.maxlength;
        $('body').on('keyup', '[name=code]', function () {
            form.data('validatorJson').rules.phone.maxlength = staticPhoneNumberMaxlength - $(this).val().length;
            form.data('validatorJson').rules.phone.minlength = staticPhoneNumberMaxlength - $(this).val().length;
            form.validateForm(form.data('validatorJson'));
            form.data('validator').resetForm();
        });

        $('body').on('keyup', '[name=code]', function () {
            var actualCodeLength = $(this).val().length;
            if (actualCodeLength === 2) {
                phone.mask('0000-#');
            } else if (actualCodeLength > 2) {
                phone.mask('000-#');
            }
        }); 
    } 
});  

function modalEnviar(e) {
    e.preventDefault();
    var formData = document.forms['partner-sheets-form'];

    if (form.valid()) {
        $('#enviar').loading();

        fetch(GOOGLE_SHEETS_MACROS_API_URL, {method: 'POST', body: new FormData(formData)})
            .then(function (response) {
                return response.json()
            })
            .then(function (response) {
                if (response.result !== 'error') {
                    form.trigger('reset');
                    swal({
                        title: "¡Muchas gracias, por escribirnos!",
                        imageUrl: '/img/call-center.svg',
                        text: "Te contactaremos a la brevedad.",
                        button: "¡Listo!",
                        html: true,
                        customClass: 'swal-modalEnviar',
                        showConfirmButton: false,
                        timer: 3000
                    });
                    dataLayer.push({"event":"lendingSent"});
                } else {
                    throw new Error();
                }
            })
            .catch(function (error) {
                showErrorModal('Se produjo un error inesperado, <br/>por favor inténtalo nuevamente o contáctanos.');
            })
            .finally(function () {
                $('#enviar').unloading();
            });
    } else {

    }
}

document.querySelector('#enviar').addEventListener("click", modalEnviar);

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