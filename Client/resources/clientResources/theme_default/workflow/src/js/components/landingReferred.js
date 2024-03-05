$(document).on('ready', function () {
    $('select').select2();

    $('#registerForm').validateForm(createFormValidationJson(JSON.parse(registerFormValidator), $('#registerForm')));

    firstOnChange = true;
    $('#docType').on('change', function () {

        if (!firstOnChange) {
            $('#docNumber').val('');
        } else {
            firstOnChange = false;
        }

        if ($(this).val() == 2) {
            $('#registerForm').data('validatorJson').rules.docNumber.minlength = 8;
            $('#registerForm').data('validatorJson').rules.docNumber.maxlength = 9;

        } else if ($(this).val() == 1) {
            $('#registerForm').data('validatorJson').rules.docNumber.minlength = 8;
            $('#registerForm').data('validatorJson').rules.docNumber.maxlength = 8;
        }
        $('#registerForm').validateForm($('#registerForm').data('validatorJson'));
        $('#registerForm').data('validator').resetForm();
    });
    $('#docType').change();


    $('#registerBtn').click(function (e) {
        $('#registerBtn').addClass('to-invest');
        e.preventDefault();
        if ($('#registerForm').valid()) {
            if($('input[name=acceptAgreement]').is(':checked')){
                $('#registerBtn').loading();
                $('#registerBtn').removeClass('to-invest');
                defaultAjax({
                    url: registerFormUrl,
                    formValidation: $('#registerForm').data('validator'),
                    data: $('#registerForm').serializeObject(),
                    type: 'POST',
                    success: function (data) {
                        console.log("Enviado correctamente");
                    },
                    error: function (data) {
                        $('#registerBtn').unloading();
                    },
                    complete: function (data) {
                        $('#registerBtn').removeClass('to-invest');
                    }
                });
            }
        }
    });
});
$(window).on("scroll", function () {
    if ($(window).scrollTop() > 50) {
        console.log($(window).scrollTop());
        $(".header").addClass("header-scroll");
    } 
    else {
        $(".header").removeClass("header-scroll");
 
    }
});

$('.header .menu-mobile .hamburger').on('click',function(){
    if ($('.hamburger').hasClass('active') == false) {
        $('.hamburger').addClass('active');
        $('.header .menu-mobile .menu-list').addClass('is-active');
        $('body').addClass('active');

    } else {
        $(this).removeClass('active');
        $('.header .menu-mobile .menu-list').removeClass('is-active');
        $('body').removeClass('active');;
    }
})


