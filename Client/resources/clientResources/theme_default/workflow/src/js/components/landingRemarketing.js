


var
    animation_elements = $('.toload'),
    lazy = $('.lazy')
;

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
window.addEventListener('load', function () {
    $('#documentType').doOnce(function () {
        $('#documentType').select2({
            minimumResultsForSearch: -1,
            width: '80px'
        });
    });
    $('#documentType').on('change', function () {
        $('#documentNumber').val('');
        if ($(this).val() == 2) {
            $('#frmRemarketing').data('validatorJson').rules.documentNumber.maxlength = 9;
        } else {
            $('#frmRemarketing').data('validatorJson').rules.documentNumber.maxlength = 8;
        }
        $('#frmRemarketing').validateForm($('#frmRemarketing').data('validatorJson'));
        $('#frmRemarketing').data('validator').resetForm();
    });

    $('#btnRemarketing').click(function (event) {
        event.preventDefault();
        if ($('#frmRemarketing').valid()) {
            $('#btnRemarketing').loading();
            defaultAjax({
                url: '/saca-tu-credito' ,
                type: 'POST',
                formValidation: $('#frmRemarketing').data('validator'),
                data: $('#frmRemarketing').serializeObject(),
                success: function (data) {},
                error: function (xhr, errorJson) {
                    $('#btnRemarketing').unloading();
                    if (errorJson != null && errorJson.type == 'message') {
                        $('.errorContainer').html(
                            '<span class="help-block help-block-error error-nofound">' +
                            errorJson.message + '</span>');
                        return false;
                    }
                }
            });
        }
    });
    (function () {

        $('#cofirmCodePromo').forceLettersOnly('', true, 10, 10);
        var confimationButtonModal = $('#confirmation-button'),
            confirmationModal = $('#confirmation-modal');
        confimationButtonModal.doOnce(function () {
            confimationButtonModal.on('click', function (e) {
                e.preventDefault();

            });
        });

        $('#sendMessage').on('click', function (event) {
            event.preventDefault();
            validate();
        });

        function validate() {
            if ($('#contactForm').valid()) {
                grecaptcha.reset();
                grecaptcha.execute();
            }
        }

        function submit() {
            $('#sendMessage').loading('', 2);
            defaultAjax({
                url: '/contact' ,
                type: 'POST',
                formValidation: $('#contactForm').data('validator'),
                data: $('#contactForm').serializeObject(),
                success: function (data) {
                    $('#sendMessage').unloading();
                    $('#contactForm').data('validator').resetForm();
                    $('#contactForm')[0].reset();
                    $('#response-block').html(successAjaxMessage(
                        'Gracias, te responderemos a la brevedad.'));
                },
                error: function () {
                    $('#sendMessage').unloading();
                }
            });
        }
    }());
});