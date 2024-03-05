var lazy                    = $('.lazy'),
    body                    = $('body'),
    animation_elements      = $('.toload'),
    sec_news                = $('.sec-news'),
    sec_benefits            = $('.sec-benefits'),
    btn_down                = $('#btn-down'),
    btn_call_companyContact = $('#btnContactCompany'),
    header                  = $('header'),
    hamburger               = $('#hamburger'),
    owl                     = $('.owl-carousel'),
    talkToUS                = $('.talk-to-us'),
    $adelanto1Module        = $('.adelanto-1'),
    $adelanto2Module        = $('.adelanto-2'),
    $adelanto3Module        = $('.adelanto-3'),
    $selectionModule        = $('.frm-selection'),
    $backButton             = $('.back-button'),
    moduleShowed            = 'home';
 
$(document).on('ready', function() {

    btn_down.click(function() {
        var cls = $(this).closest("main").next().offset().top - 50;
        $("html, body").animate({scrollTop: cls}, "slow");
    });
    menu_responsive_calc();

    hamburger.click(function(event) {
        event.preventDefault();
        var t = $(this);
        t.closest('nav').find('.list-menu').find('li').eq(1).toggleClass('active-menu');
        $(this).toggleClass('opened');
        body.toggleClass('ov-hidden');
    });

});

$(window).on('load', function() {
    check_if_in_view();
    $(window).on('scroll resize', check_if_in_view);
    $(window).trigger('scroll');
});

$(window).on('resize', function(){
    menu_responsive_calc();
});
$('.intereted-credit').click(function(){
    showModule($(this).data('value'))
});
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

(function() {
    $backButton.on('click', function(){
        if(moduleShowed == 'consumo'){
            showModule('home');
        }else if(moduleShowed == 'adelanto1'){
            showModule('home');
        }else if(moduleShowed == 'adelanto2'){
            showModule('adelanto1');
        }else if(moduleShowed == 'adelanto3'){
            showModule('adelanto1');
        }
    });

    $('#fillWithEmailButton').on('click', function(){
        showModule('adelanto2');
    });

    $('#fillWithPhoneButton').on('click', function(){
        showModule('adelanto1');
    });

    var oldDocumenNumber=-1;
    var $consumoForm = $('#loanForm');
    var $docType = $consumoForm.find('select[name=docType]');
    var $documentNumber = $consumoForm.find('input[name=documentNumber]');
    var $birthDay = $consumoForm.find('input[name=birthDay]');
    var $birthMonth = $consumoForm.find('input[name=birthMonth]');
    var $birthYear = $consumoForm.find('input[name=birthYear]');
    var $phone = $consumoForm.find('input[name=phone]');
    var $submitbutton = $consumoForm.find('.form-button');

    $consumoForm.validateForm(createFormValidationJson(JSON.parse(consumoFormValidator), $consumoForm));

    $docType.on('change', function () {
        $documentNumber.val('');
        if ($(this).val() == 2) {
            $consumoForm.data('validatorJson').rules.documentNumber.minlength = 8;
            $consumoForm.data('validatorJson').rules.documentNumber.maxlength = 9;
        } else if ($(this).val() == 1) {
            $consumoForm.data('validatorJson').rules.documentNumber.minlength = 8;
            $consumoForm.data('validatorJson').rules.documentNumber.maxlength = 8;
        } else {
            $consumoForm.data('validatorJson').rules.documentNumber.minlength = 11;
            $consumoForm.data('validatorJson').rules.documentNumber.maxlength = 11;
        }
        $consumoForm.validateForm($consumoForm.data('validatorJson'));
        $consumoForm.data('validator').resetForm();
    });
    $docType.change();

    $documentNumber.focusout(function() {
        if(oldDocumenNumber != $documentNumber.val() && ($documentNumber.val().length==8 || $documentNumber.val().length==9)){
            oldDocumenNumber = $documentNumber.val();
            defaultAjax({
                url: '/credito-de-consumo-5/synthesized',
                data:{documentNumber: $documentNumber.val()},
                type: 'POST',
                success: function (data) {
                },
                error: function (data) {
                }
            });
        }
    });

    $birthDay.forceIntegerOnly(true, 1, 31);
    $birthMonth.forceIntegerOnly(true, 1, 12);
    $birthYear.forceIntegerOnly(true, yearFrom, yearTo);

    $birthDay.keyup(function (event) {
        if ($(this).val().length == 2) {
            $birthMonth.focus();
        }
    });

    $birthMonth.keyup(function (event) {
        if ($(this).val().length == 2) {
            $birthYear.focus();
        }
    });

    $submitbutton.click(function(e){
        e.preventDefault();
        var birthday = moment($birthDay.val() + "/" + $birthMonth.val() + "/" + $birthYear.val(), "DD/MM/YYYY");
        var birthdayIsRequired = $consumoForm.data('validatorJson').rules.birthday.required != undefined;
        var isValid = $consumoForm.valid();
        if (birthdayIsRequired && !birthday.isValid() || birthday.get('years') > yearTo || birthday.get('years') < yearFrom) {
            $('.errorContainer.birthday').html('<span id="name-error" class="help-block help-block-error">La fecha es incorrecta</span>');
            $birthDay.closest('.field').addClass('field-error');
        } else {
            if (isValid) {
                $submitbutton.loading();
                defaultAjax({
                    url: '/credito-de-consumo-5',
                    formValidation: $consumoForm.data('validator'),
                    data:$.extend($consumoForm.serializeObject(), {
                        source: readCookie("utm_source"),
                        medium: readCookie("utm_medium"),
                        campaign: readCookie("utm_campaign"),
                        term: readCookie("utm_term"),
                        content: readCookie("utm_content"),
                        gclid: readCookie('gclid'),
                        gaClientID: localStorage.getItem('ga:clientId'),
                        birthday: birthday.isValid() ? birthday.format("DD/MM/YYYY") : null
                    }),
                    type: 'POST',
                    success: function (data) {
                        console.log("Enviado correctamente ");
                    },
                    error: function (data) {
                        $submitbutton.unloading();
                    }
                });
            }
        }
    });

    $consumoForm.find(".field input[type='text']").on('keyup', function (event) {
        if (event.keyCode == 13) {
            if ($consumoForm.valid()) {
                $submitbutton.click();
            }
            event.preventDefault();
            return false;
        }
    });


    var loanApplicationToken;
    var $adelantoForm = $('#adelantoForm');
    var $docType = $adelantoForm.find('select[name=docType]');
    var $docNumber = $adelantoForm.find('input[name=docNumber]');
    var $cellphone = $adelantoForm.find('input[name=cellphone]');
    var $authToken = $adelantoForm.find('input[name=authToken]');
    var $email = $adelantoForm.find('input[name=email]');
    var $adelanto1Button = $adelantoForm.find('#adelanto1Button');
    var $adelanto2Button = $adelantoForm.find('#adelanto2Button');
    var $adelanto3Button = $adelantoForm.find('#adelanto3Button');
    var $resendPin = $adelantoForm.find('#resendPin');
    var $callPin = $adelantoForm.find('#callPin');
    $adelantoForm.validateForm(createFormValidationJson(JSON.parse(adelantoFormValidator), $adelantoForm));

    $docType.on('change', function(){
        $docNumber.val('');
        if ( $(this).val() == 1) {
            $adelantoForm.data('validatorJson').rules.docNumber.minlength = 8;
            $adelantoForm.data('validatorJson').rules.docNumber.maxlength = 8;
        } else {
            $adelantoForm.data('validatorJson').rules.docNumber.minlength = 8;
            $adelantoForm.data('validatorJson').rules.docNumber.maxlength = 9;
        }
        $adelantoForm.validateForm($adelantoForm.data('validatorJson'));
        $adelantoForm.data('validator').resetForm();
    });

    $adelanto1Button.click(function(e){
        e.preventDefault();
        if ($adelantoForm.valid()) {
            $adelanto1Button.loading();
            defaultAjax({
                url: '/salaryadvanceloanapplication/register',
                type: 'POST',
                form: $adelantoForm,
                data: {
                    docType: $docType.val(),
                    docNumber: $docNumber.val(),
                    cellphone: $cellphone.val(),
                    source: readCookie("utm_source"),
                    medium: readCookie("utm_medium"),
                    campaign: readCookie("utm_campaign"),
                    term: readCookie("utm_term"),
                    content: readCookie("utm_content"),
                    gclid: readCookie('gclid'),
                    gaClientID: localStorage.getItem('ga:clientId')
                },
                success: function (data) {
                    $adelanto1Button.unloading();

                    var json = JSON.parse(data);
                    loanApplicationToken = json.loanApplicationToken;
                    showModule('adelanto3');
                },
                error: function (xhr, data) {
                    $adelanto1Button.unloading();
                }
            });
        }
    });

    $adelanto2Button.click(function(e){
        e.preventDefault();
        if ($adelantoForm.valid()) {
            $adelanto2Button.loading();
            defaultAjax({
                url: '/salaryadvanceloanapplication/register',
                type: 'POST',
                form: $adelantoForm,
                data: {
                    email: $email.val(),
                    source: readCookie("utm_source"),
                    medium: readCookie("utm_medium"),
                    campaign: readCookie("utm_campaign"),
                    term: readCookie("utm_term"),
                    content: readCookie("utm_content"),
                    gclid: readCookie('gclid'),
                    gaClientID: localStorage.getItem('ga:clientId')
                },
                success: function (data) {
                    $adelanto2Button.unloading();

                    var json = JSON.parse(data);
                    loanApplicationToken = json.loanApplicationToken;
                    showModule('home');
                    showMessageModal('Te enviamos un email con el link para acceder a tu crédito. Puede tardar unos segundos.')
                },
                error: function (xhr, data) {
                    $adelanto2Button.unloading();
                }
            });
        }
    });

    $resendPin.click(function(e){
        e.preventDefault();
        $resendPin.loading();
        defaultAjax({
            url: '/salaryadvanceloanapplication/resendPin',
            type: 'POST',
            data: {
                loanApplicationToken: loanApplicationToken
            },
            success: function (data) {
                $resendPin.unloading();
            },
            error: function (xhr, data) {
                $resendPin.unloading();
            }
        });
    });

    $callPin.click(function(e){
        e.preventDefault();
        $callPin.loading();
        defaultAjax({
            url: '/salaryadvanceloanapplication/voiceCallPin',
            type: 'POST',
            data: {
                loanApplicationToken: loanApplicationToken
            },
            success: function (data) {
                $callPin.unloading();
            },
            error: function (xhr, data) {
                $callPin.unloading();
            }
        });
    });

    $adelanto3Button.click(function(e){
        e.preventDefault();

        if ($authToken.val().length == 4) {
            $adelanto3Button.loading();
            defaultAjax({
                url: '/adelanto-de-sueldo/cellphone/validate',
                type: 'POST',
                form: $adelantoForm,
                data: {
                    loanApplicationToken: loanApplicationToken,
                    authToken: $authToken.val()
                },
                success: function (data) {
                    $adelanto3Button.unloading();
                },
                error: function () {
                    $adelanto3Button.unloading();
                }
            });
        }
    });
})();


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

 

function showModule(module){
    moduleShowed = module;
    $selectionModule.hide();
    $adelanto1Module.hide();
    $adelanto2Module.hide();
    $adelanto3Module.hide();
    $backButton.hide(); 
    $('.frm-consumo').hide();
    if(module == 'consumo'){
        $('.frm-consumo').show(); 
        $('#adelantoForm').hide();
        $backButton.css("display", "flex");
    }else if(module == 'adelanto1'){
        $adelanto1Module.show();
        $backButton.css("display", "flex");
        $('#adelantoForm').show(); 
        $adelanto1Module.find('input, select').removeClass('ignore_validation');
        $adelanto2Module.find('input, select').addClass('ignore_validation');
        $adelanto3Module.find('input, select').addClass('ignore_validation');
    }else if(module == 'adelanto2'){
        $adelanto2Module.show();
        $('#adelantoForm').show();
        $backButton.css("display", "flex");
        $adelanto2Module.find('input, select').removeClass('ignore_validation');
        $adelanto1Module.find('input, select').addClass('ignore_validation');
        $adelanto3Module.find('input, select').addClass('ignore_validation');
    }else if(module == 'adelanto3'){
        $adelanto3Module.show();
        $('#adelantoForm').show();
        $backButton.css("display","flex");
        $adelanto3Module.find('input, select').removeClass('ignore_validation');
        $adelanto1Module.find('input, select').addClass('ignore_validation');
        $adelanto2Module.find('input, select').addClass('ignore_validation');
    } else if(module == 'home'){
        $selectionModule.show();
        $('#adelantoForm').hide();   
        $('.frm-consumo').hide();
        $backButton.hide();
    }
}
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
        }
    });
}
function menu_responsive_calc(){
    if( $(window).width() < 768 ){
        hamburger.closest('nav').find('.list-menu').find('li').eq(1).addClass('menu-responsive');
    }else{
        hamburger.closest('nav').find('.list-menu').find('li').eq(1).removeClass('menu-responsive');
    }
}
$(window).on('scroll', function () {
    console.log("scroll")
    if ($(window).scrollTop() > 50) {
        $(".headerLanding").addClass("header-scroll");
    } else {
        $(".headerLanding").removeClass("header-scroll");
    }
});
