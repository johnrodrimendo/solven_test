var header = $('header'),
    submitButton = $('#submitBtn'),
    animation_elements = $('.toload'),
    talkToUS = $('.talk-to-us')
;
  

$('select').select2();
talkToUS.slick({
    dots: true,
    slidesToShow: 5,
    slidesToScroll: 5,
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
    ]
});
$(window).on('load', function () {
    check_if_in_view();
    $(window).on('scroll resize', check_if_in_view);
    $(window).trigger('scroll');
});

$(document).on('ready', function () {
    //check_if_in_view();

    var phone = $('#phone'),
        source = $('#source'),
        email = $('#email'),
        name = $('#name'),
        position = $('#position'),
        size = $('#size'),
        company = $('#company');

    phone.on('change', function () {
        $(this).valid();
    });
    source.on('change', function () {
        $(this).valid();
    });
    email.on('change', function () {
        $(this).valid();
    });
    name.on('change', function () {
        $(this).valid();
    });
    size.on('change', function () {
        $(this).valid();
    });
    position.on('change', function () {
        $(this).valid();
    });
    company.on('change', function () {
        $(this).valid();
    });

});
$("#loanForm").find(".field input[type='text']").on('keyup', function (event) {
    if (event.keyCode == 13) {
        if ($('#businessContactForm').valid()) {
            $("#submitBtn").click();
        }
        event.preventDefault();
        return false;
    }
});

if (businessFormValidator != null) {
    var jsonValidator = JSON.parse(businessFormValidator);
    $('#businessContactForm').validateForm(createFormValidationJson(jsonValidator, $('#businessContactForm')));

}


/* --------- Formulario contact form ------- */

(function () {
    var contactForm = $('#businessContactForm');
    var name = contactForm.find('select[name=name]');
    var email = contactForm.find('input[name=email]');
    var company = contactForm.find('input[name=company]');
    var position = contactForm.find('input[name=position]');
    var position2 = contactForm.find('input[name=position2]');
    var size = contactForm.find('input[name=size]');
    var phone = contactForm.find('input[name=phone]');
    var source = contactForm.find('input[name=source]');
    var source2 = contactForm.find('input[name=source2]');
    var submitButton = contactForm.find('#submitBtn');


    submitButton.click(function (e) {
        e.preventDefault();

        if (contactForm.valid()) {
            submitButton.loading('Enviando');
            defaultAjax({
                url: '/empresas',
                formValidation: contactForm.data('validator'),
                data: $.extend(contactForm.serializeObject(), {
                    name: name.val(),
                    phone: phone.val(),
                    email: email.val(),
                    source: source.val(),
                    size: size.val(),
                    company: company.val(),
                    position: position.val(),
                    position2: position2.val() 
                }),
                type: 'POST',
                success: function (data) {
                    $('#size').val('').trigger('change');
                    $('#source').val('').trigger('change');
                    $('#businessContactForm').data('validator').resetForm();
                    $('#businessContactForm')[0].reset();
                    submitButton.unloading();
                    swal({
                        title: 'Tu solicitud ha sido recibida ',
                        type: 'success',
                        text: '¡Muchas Gracias! \nNos pondremos en contacto contigo\n en las próximas 24hs.',
                        showConfirmButton: false,
                        timer: 3000
                    })
                },
                error: function (data) {
                    submitButton.unloading();
                }
            });
        }
    });
})(); 

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
$(window).on('scroll', function () {
    console.log("scroll")
    if ($(window).scrollTop() > 50) {
        $(".headerLanding").addClass("header-scroll");
    } 
    else {
        $(".headerLanding").removeClass("header-scroll");
    }
}); 
