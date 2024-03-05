var select                  = $('select'),
    img_lazy                = $('.lazy'),
    animation_elements      = $('.toload'),
    input                   = $('input'),
    option_select2          = $('body').find('.select2-container li'),
    favicon                 = $("#favicon"),
    theme_color             = $('meta[name="theme-color"]') ,
    workerUs                = $('.worker-us')
;

$(document).on('ready', function() {
    // lazy();
    select.select2();
    //CHROME NO ACEPTA DOS REL=ICON. DEBO ELIMINAR EL PRIMERO
    $('link[rel="shortcut icon"]').remove();


});



workerUs.slick({
    dots: true,
    slidesToShow: 5,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 2000,
    responsive: [
        {
            breakpoint: 700,
            settings: {
                slidesToShow: 4,
                slidesToScroll: 1
            }
        },
        {
            breakpoint: 600,
            settings: {
                slidesToShow: 3,
                slidesToScroll: 1
            }
        },
        {
            breakpoint: 480,
            settings: {
                slidesToShow: 2,
                slidesToScroll: 1
            }
        }

        // You can unslick at a given breakpoint now by adding:
        // settings: "unslick"
        // instead of a settings object
    ]

});

/* --------- Formulario loan application ------- */
var oldDocumenNumber = -1;

$(window).on('load', function() {
    check_if_in_view();
    $(window).on('scroll resize', check_if_in_view);
    $(window).trigger('scroll');
    $("#loanForm").find(".field input[type='text']").on('keyup', function (event) {
        if (event.keyCode == 13) {
            if ($('#loanForm').valid()) {
                $("#btnReview").click();
            }
            event.preventDefault();
            return false;
        }
    });

    if (loanFormValidator != null) {
        var jsonValidator = JSON.parse(loanFormValidator);
        $('#loanForm').validateForm(createFormValidationJson(jsonValidator, $('#loanForm')));
    }

    var docType = $('#docType'),
        documentNumber = $('#documentNumber'),
        email = $('#email'),
        activityType = $('#activityType'),
        productType = $('#productType'),
        firstOnChange = true;

        activityType.on('change',function(){
            $(this).valid();
        });

        productType.on('change',function(){
            $(this).valid();
        });

    docType.on('change', function () {
        if (!firstOnChange) {
            documentNumber.val('');
        } else {
            firstOnChange = false;
        }
        if ($(this).val() == 2) {
            $('#documentNumber').removeClass('document-input').addClass('document-input-ce');
            $('#loanForm').data('validatorJson').rules.documentNumber.minlength = 8;
            $('#loanForm').data('validatorJson').rules.documentNumber.maxlength = 9;

        } else if ($(this).val() == 1) {
            $('#documentNumber').removeClass('document-input-ce').addClass('document-input');
            $('#loanForm').data('validatorJson').rules.documentNumber.minlength = 8;
            $('#loanForm').data('validatorJson').rules.documentNumber.maxlength = 8;
        } else {
            $('#documentNumber').removeClass('document-input-ce').addClass('document-input');
            $('#loanForm').data('validatorJson').rules.documentNumber.minlength = 11;
            $('#loanForm').data('validatorJson').rules.documentNumber.maxlength = 11;
        }
        $('#loanForm').validateForm($('#loanForm').data('validatorJson'));
        $('#loanForm').data('validator').resetForm();
    });
    docType.change();

    $('#documentNumber').focusout(function() {
        if(oldDocumenNumber!=$('#documentNumber').val() && ($('#documentNumber').val().length==8 ||$('#documentNumber').val().length==9)){
            oldDocumenNumber=$('#documentNumber').val();
            defaultAjax({
                url: '/efectivo-al-toque/synthesized',
                data:{documentNumber: $('#documentNumber').val()},
                type: 'POST',
                success: function (data) {
                },
                error: function (data) {
                }
            });
        }
    });


    $('#btnReview').click(function(e){
        e.preventDefault();
        var isValid = $('#loanForm').valid();
            if (isValid) {
                $('#btnReview').loading();
                defaultAjax({
                    url: registerLoanUrl,
                    formValidation: $('#loanForm').data('validator'),
                    data:$.extend($('#loanForm').serializeObject(), {
                        source: readCookie("utm_source"),
                        medium: readCookie("utm_medium"),
                        campaign: readCookie("utm_campaign"),
                        term: readCookie("utm_term"),
                        content: readCookie("utm_content"),
                        gclid: readCookie('gclid'),
                        gaClientID: localStorage.getItem('ga:clientId'),
                    }),
                    type: 'POST',
                    success: function (data) {
                        console.log("Enviado correctamente ");
                    },
                    error: function (data) {
                        $('#btnReview').unloading();
                    }
                });
            }
    });
});

/* --------- Funciones ------- */

/*----------------------------------------------------------------------------------*/
/* Lazy Images
/*----------------------------------------------------------------------------------*/

function check_if_in_view() {
    var window_height = $(window).height(),
        window_top_position = $(window).scrollTop(),
        window_bottom_position = (window_top_position + window_height) - 50;

    $.each(animation_elements, function() {
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


function readCookie(name) {
    var n = name + "=";
    var cookie = document.cookie.split(';');
    for(var i=0;i < cookie.length;i++) {
        var c = cookie[i];
        while (c.charAt(0)==' '){c = c.substring(1,c.length);}
        if (c.indexOf(n) == 0){return c.substring(n.length,c.length);}
    }
    return null;
}