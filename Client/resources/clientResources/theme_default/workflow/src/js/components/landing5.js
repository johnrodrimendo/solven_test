var select                  = $('select'),
    lazy                    = $('.lazy'),
    animation_elements      = $('.toload'),
    sec_news                = $('.sec-news'),
    sec_benefits            = $('.sec-benefits'),
    input                   = $('input'),
    option_select2          = $('body').find('.select2-container li'),
    btn_down                = $('.btn-down'),
    favicon                 = $("#favicon"),
    theme_color             = $('meta[name="theme-color"]'),
    owl                     = $('.owl-carousel'),
    talkToUS                = $('.talk-to-us'),
    workerUsPE              = $('.worker-us-pe'),
    workerUsAR              = $('.worker-us-ar'),
    noModifyIframe          = document.querySelector('main.no-modify-iframe')
;

function validateIframe() {
    if ( window.location !== window.parent.location && !noModifyIframe)
    {
        $("body").addClass("is-iframe");
        $("img.logo").hide();
        $("a.logoLinkUrl").hide();
        $("div.logo a.logo").hide();
    }
}

function removeIframeCss(){
    $("body").removeClass("is-iframe");
    //$("img.logo").show();
    //$("a.logoLinkUrl").show();
    //$("div.logo a.logo").show();
}

validateIframe();

$(document).on('ready', function() {

    validateIframe();

    if(!$('#docType').val()) {
        $('#docType').val($('#docType').children('option:nth-child(2)').val());
    }

    setTimeout(() => {
        if($("#loading-screen").length){
            $("#loading-screen").css("display","none");
            $("body").css("overflow-y", "auto");
        }
    },500);

    var lastHeight = null;
    select.select2();
    if($( "#docType" ).prop( "disabled" )){
        $("#docType").select2({disabled : true});
    }

    if(document.querySelector(".landing-banbif-main") && !document.querySelector(".challenger-b")){
        let height = window.innerHeight;
        $(".landing-banbif-main").innerHeight(height);
        if(window.innerWidth < 1023){
            if(height < 578) height = 578;
            $(".landing-banbif-main").css('cssText', 'min-height:'+height+'px !important');
            $(".landing-banbif-main").innerHeight(height);
        }
        lastHeight = height;
        $(window).resize(function(){
            if(window.innerWidth > 1023){
                $(".landing-banbif-main").css('min-height', '');
                let height = window.innerHeight;
                if(height < 670) height = 670;
                lastHeight = height;
                $(".landing-banbif-main").innerHeight(height);
            }else{
                if(window.innerHeight > lastHeight) {
                    lastHeight = window.innerHeight;
                    if(lastHeight < 578) lastHeight = 578;
                    $(".landing-banbif-main").css('cssText', 'min-height:'+lastHeight+'px !important');
                    $(".landing-banbif-main").innerHeight(lastHeight);
                }
            }
        });
    }
    //CHROME NO ACEPTA DOS REL=ICON. DEBO ELIMINAR EL PRIMERO
    $('link[rel="shortcut icon"]').remove();

    if (isBranded) {

        favicon.attr("href",fav_icon);

        theme_color.attr("content",primaryColor);

        $(document).on("mouseleave", ".select2-results__option", function() {
            $(this).css({
                background: "#FFFFFF",
                color: "#34465d"
            }).siblings().css({
                background: "#FFFFFF",
                color: "#34465d"
            })
        });
        $(document).on("mouseenter", ".select2-results__option", function() {
            $(this).css({
                background: primaryColor,
                color: '#FFF'
            }).siblings().css({
                background: '#FFFFFF',
                color: '#34465d'
            })
        });

        input.focus(function() {
            $(this).css("border-color", primaryColor);
        });
        input.blur(function() {
            $(this).css("border-color", "#D6D8DD");
        });

        $("#openModalAzteca").click(function() {
            $('#modalAzteca').modal('show');
        })
    }


    btn_down.click(function() {
        var cls = $(this).closest("main").next().offset().top;
        $("html, body").animate({scrollTop: cls}, "slow");
    });


    if( $(window).width() > 768 ){
        owl.owlCarousel({
            items: 5,
            smartSpeed: 600,
            autoplay:true,
            autoplayTimeout:5500,
            mouseDrag:true,
            lazyLoad:true,
            loop:true,
            margin:10,
            responsive : {
                0 : {
                    items: 1
                },
                480 : {
                    items: 1
                },
                601 : {
                    items: 3
                },
                768 : {
                    items: 5
                }
            }
        });
    }

    $('#loanForm input').on('blur', function() {
        if (new RegExp(/^\s|\s$/g).test($(this).val())) {
            $(this).val($(this).val().trim());

            $('#loanForm').valid();
        }
    });
});

$(window).on('scroll', function () {
    if ($(window).scrollTop() > 50) {
        console.log($(window).scrollTop());
        $("header").addClass("header-scroll");
    } else {
        $("header").removeClass("header-scroll");
    }
});
/* --------- Carrusell ------- */
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
        // You can unslick at a given breakpoint now by adding:
        // settings: "unslick"
        // instead of a settings object
    ]

});
workerUsPE.slick({
    dots: false,
    autoplay: true,
    autoplaySpeed: 1000,
    slidesToScroll: 1,
    slidesToShow: 5,
    responsive: [{
            breakpoint: 885,
            settings: {
                autoplay: true,
                slidesToScroll: 1,
                slidesToShow: 4,
            }
        },
        {
            breakpoint: 720,
            settings: {
                autoplay: true,
                slidesToScroll: 1,
                slidesToShow: 3,
            }
        },
        {
            breakpoint: 480,
            settings: {
                autoplay: true,
                slidesToScroll: 1,
                slidesToShow: 2,
            }
        }

        // You can unslick at a given breakpoint now by adding:
        // settings: "unslick"
        // instead of a settings object
    ]

});

workerUsAR.slick({
    dots: false,
    autoplay: true,
    autoplaySpeed: 1000,
    slidesToScroll: 4,
    slidesToShow: 4,
    responsive: [
        {
            breakpoint: 720,
            settings: {
                autoplay: true,
                slidesToScroll: 1,
                slidesToShow: 3,
            }
        },
        {
            breakpoint: 480,
            settings: {
                autoplay: true,
                slidesToScroll: 1,
                slidesToShow: 2,
            }
        }

        // You can unslick at a given breakpoint now by adding:
        // settings: "unslick"
        // instead of a settings object
    ]

});

/* --------- Formulario loan application ------- */
var oldDocumenNumber=-1;

$(window).on('load', function() {

    check_if_in_view();
    $(window).on('scroll resize', check_if_in_view);
    $(window).trigger('scroll');

    var isAuthenticationRequired = false;

    if (loanFormValidator != null) {
        var jsonValidator = JSON.parse(loanFormValidator);
        $('#loanForm').validateForm(createFormValidationJson(jsonValidator, $('#loanForm')));
    }

    if (typeof authenticationFormValidator !== 'undefined'&& authenticationFormValidator != null) {
        var jsonAuthenticationValidator = JSON.parse(authenticationFormValidator);
        $('#authenticationForm').validateForm(createFormValidationJson(jsonAuthenticationValidator, $('#authenticationForm')));
    }

    var docType = $('#docType'),
        documentNumber = $('#documentNumber'),
        email = $('#email'),
        birthdayDay = $('#birthDay'),
        birthdayMonth = $('#birthMonth'),
        birthdayYear = $('#birthYear'),
        firstOnChange = true;

    docType.on('change', function () {
        if (!firstOnChange) {
            documentNumber.val('');
        } else {
            firstOnChange = false;
        }
        console.log($(this).val(), "valor de doctype");
        if ($(this).val() == 2) {
            $('#documentNumber').removeClass('document-input').addClass('document-input-ce');
            $('#loanForm').data('validatorJson').rules.documentNumber.minlength = 8;
            $('#loanForm').data('validatorJson').rules.documentNumber.maxlength = 9;

        } else if ($(this).val() == 1) {
            $('#documentNumber').removeClass('document-input-ce').addClass('document-input');
            $('#loanForm').data('validatorJson').rules.documentNumber.minlength = 8;
            $('#loanForm').data('validatorJson').rules.documentNumber.maxlength = 8;
        } else if ($(this).val() == 8) { // COLOMBIA
            $('#loanForm').data('validatorJson').rules.documentNumber.minlength = 5;
            $('#loanForm').data('validatorJson').rules.documentNumber.maxlength = 11;
        } else if ($(this).val() == 9) { // COLOMBIA
            $('#loanForm').data('validatorJson').rules.documentNumber.minlength = 5;
            $('#loanForm').data('validatorJson').rules.documentNumber.maxlength = 11;
        } else {
            $('#documentNumber').removeClass('document-input-ce').addClass('document-input');
            $('#loanForm').data('validatorJson').rules.documentNumber.minlength = 11;
            $('#loanForm').data('validatorJson').rules.documentNumber.maxlength = 11;
        }
        $('#loanForm').validateForm($('#loanForm').data('validatorJson'));
        $('#loanForm').data('validator').resetForm();
    });
    docType.change();

    $('#documentNumber, #phone').on('input', function () {
        $(this).val($(this).val().trim());
    });

    $('#documentNumber').focusout(function () {
        if (oldDocumenNumber != $('#documentNumber').val() && ($('#documentNumber').val().length == 8 || $('#documentNumber').val().length == 9)) {
            oldDocumenNumber = $('#documentNumber').val();
            defaultAjax({
                url: '/credito-de-consumo-5/synthesized',
                data: {documentNumber: $('#documentNumber').val()},
                type: 'POST',
                encryptEnabled : true,
                success: function (data) {
                },
                error: function (data) {
                }
            });
        }
    });

    birthdayDay.forceIntegerOnly(true, 1, 31);

    birthdayMonth.forceIntegerOnly(true, 1, 12);

    birthdayYear.forceIntegerOnly(true, yearFrom, yearTo);

    birthdayDay.keyup(function (event) {
        if ($(this).val().length == 2) {
            birthdayMonth.focus();
        }
    });

    birthdayMonth.keyup(function (event) {
        if ($(this).val().length == 2) {
            birthdayYear.focus();
        }
    });

    // COLOMBIA ONLY
    
    //
    if (countryId === 57) {
        if ($('.amount-colombia')) {
             $('.amount-colombia').maskNumber({
                thousands: '.',
                integer: true
            });
        }
        if ($('#loanForm').data('validatorJson').rules.timeLimit) {
            $('#timeLimit').forceIntegerOnly(true, $('#loanForm').data('validatorJson').rules.timeLimit.min, $('#loanForm').data('validatorJson').rules.timeLimit.max);
        }
    }

    $('#btnReview, #btnEnter').click(function(e){
        e.preventDefault();
        var birthday = moment(birthdayDay.val() + "/" + birthdayMonth.val() + "/" + birthdayYear.val(), "DD/MM/YYYY");
        var birthdayIsRequired = $('#loanForm').data('validatorJson').rules.birthday.required != undefined;
        var amountRequired = $('#loanForm').data('validatorJson').rules.amount.required != undefined;
        var amount;
        if(amountRequired){
            var amountMask =  $('.amount-mask').val() || $('#amount').val();
            var amountVal = $('#amount').val(amountMask);
            amount = amountVal.val().replace(/(\.|\,)+\d{1,2}$/g, '').replace(/\.|\,/g, '');
        }
        var isValid = $('#loanForm').valid();

        var authenticationFormIsValid = true;

        if(isAuthenticationRequired) authenticationFormIsValid = $('#authenticationForm').valid();

        if (birthdayIsRequired && !birthday.isValid() || birthday.get('years') > yearTo || birthday.get('years') < yearFrom) {
            $('.errorContainer.birthday').html('<span id="name-error" class="help-block help-block-error">La fecha es incorrecta</span>');
            birthdayDay.closest('.field').addClass('field-error');
        }
        if (isValid && authenticationFormIsValid) {
            $('#btnReview').loading('', 2);
            $('#btnReview').prop('disabled', true);
            $('#btnEnter').loading('', 2);
            $('#btnEnter').prop('disabled', true);
            let landingType = window.localStorage.getItem("landing_type");
            if(landingType == null) landingType = "A";
            var enableDocType = false;
            if($( "#docType" ).prop( "disabled" )) $( "#docType"  ).prop( "disabled", false );
            let formDataJson = $.extend($('#loanForm').serializeObject(), {
                amount,
                source: readCookie("utm_source"),
                medium: readCookie("utm_medium"),
                campaign: readCookie("utm_campaign"),
                term: readCookie("utm_term"),
                content: readCookie("utm_content"),
                gclid: readCookie('gclid'),
                gaClientID: localStorage.getItem('ga:clientId'),
                birthday: birthday.isValid() ? birthday.format("DD/MM/YYYY") : null,
                externalParams: externalParams,
                banbifLandingABTesting : landingType,
                marketing_campaign: readCookie("marketing_campaign"),
            });
            if(formDataJson.acceptAgreement == "on") formDataJson.acceptAgreement = true;
            else formDataJson.acceptAgreement = false;
            if(formDataJson.acceptAgreement2 == "on") formDataJson.acceptAgreement2 = true;
            else formDataJson.acceptAgreement2 = false;
            if(formDataJson.conditionsPolicy == "on") formDataJson.conditionsPolicy = true;
            else formDataJson.conditionsPolicy = false;

            if(isAuthenticationRequired){
                let authenticationFormData = $('#authenticationForm').serializeObject();
                formDataJson.email = authenticationFormData.email;
                formDataJson.phone = authenticationFormData.phone;
            }

            defaultAjax({
                url: registerLoanUrl,
                formValidation: $('#loanForm').data('validator'),
                data: formDataJson,
                type: 'POST',
                encryptEnabled : true,
                success: function (data) {
                    console.log("Enviado correctamente ");
                    $('#btnReview').prop('disabled', false);
                    $('#btnReview').unloading();
                    $('#btnEnter').prop('disabled', false);
                    $('#btnEnter').unloading();
                },
                error: function (data) {
                    $('#btnReview').unloading();
                    $('#btnReview').prop('disabled', false);
                    $('#btnEnter').prop('disabled', false);
                    $('#btnEnter').unloading();
                    if ((data.status == 500 || data.status == 403) && isValidJson(data.responseText)) {
                        let jsonErrorAjx = JSON.parse(data.responseText);
                        if (jsonErrorAjx.message == "AUTHENTICATION_REQUIRED") {
                            $("#loanForm").hide();
                            isAuthenticationRequired = true;
                            $("#authenticationForm").show();
                            return false;
                        }
                    }
                }
            });

        }

        else {
            console.log("ERROR: ", $(".field").hasClass("field-error"))
            if ($(".field").hasClass("field-error") === true) {
                console.log("Entro al IF")
                $(".contain-form").addClass("contaim-form_error");
            }
        }
    });

    $("#loanForm").find(".field input[type='text']").on('keyup', function (event) {
        if (event.keyCode == 13) {
            if ($('#loanForm').valid()) {
                $("#btnReview").click();
            }
            event.preventDefault();
            return false;
        }
    });

    if(countryId == 54){
        var staticPhoneNumberMaxlength = $('#loanForm').data('validatorJson').rules.phone.maxlength;
        $('body').on('keyup', '#code', function () {
            $('#loanForm').data('validatorJson').rules.phone.maxlength = staticPhoneNumberMaxlength - $(this).val().length;
            $('#loanForm').data('validatorJson').rules.phone.minlength = staticPhoneNumberMaxlength - $(this).val().length;
            $('#loanForm').validateForm($('#loanForm').data('validatorJson'));
            $('#loanForm').data('validator').resetForm();
        });

        $('#phone').mask('000-#');
        $('body').on('keyup', '#code', function () {
            var actualCodeLength = $(this).val().length;
            if (actualCodeLength == 2) {
                $('#phone').mask('0000-#');
            } else if (actualCodeLength > 2) {
                $('#phone').mask('000-#');
            }
        });
    }
});

/* --------- Funciones ------- */

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

