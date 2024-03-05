(function(){
    var $cuerpo = $('body');
    var $ventana = $(window);
    var $widthVentana = $ventana.width();
    var $htmlcuerpo = $('html, body');
    var $banner = $('.main-banner');
    var $owlTestimonial = $("#slideTestimonial");
    var $owlPress = $("#slidePress");
    var $wrap_languages = $('.wrap-languages');
    var $img_lazy = $('.lazy');
    var $hamburger = $('#hamburger');
    var $boxWhite  = $('.box-trans');
    var $header     = $('header');
    var $animation_elements = $('.toload');
    var $close      = $("#close");
    var $q130       =$("#q130");
    /*----------------------------------------------------------------------------------*/
    /* Header
    /*------------------------------------------------p----------------------------------*/
    /*----------------------------------------------------------------------------------*/
    /* Lazy Images
    /*----------------------------------------------------------------------------------*/
    lazy();

    function lazy() {
        $img_lazy.unveil(0, function () {
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
    $(document).on('ready', function() {

        $(window).on('scroll', function () {
            if ($(window).scrollTop() > 50) {
                console.log($(window).scrollTop());
                $("header").addClass("header-scroll");
            } else {
                $("header").removeClass("header-scroll");
            }
            console.log("scrool")
        });

        menu_responsive_calc();

       $hamburger.click(function(event) {
            event.preventDefault();
            var t = $(this);
            t.closest('nav').find('.list-menu').find('li').eq(1).toggleClass('active-menu');
            console.log("class")
            $(this).addClass('hamburger-none');
            $cuerpo.toggleClass('ov-hidden');
        });
        $close .click(function(event) {
            event.preventDefault();
            var t = $(this);
            t.closest('nav').find('.list-menu').find('li').eq(1).toggleClass('active-menu');
            console.log("class")
            $hamburger.removeClass('hamburger-none');
            $cuerpo.toggleClass('ov-hidden');
        });

    });



   $ventana.on('resize', function(){
        menu_responsive_calc();
    });


   $ventana.on('load', function() {
        check_if_in_view();
        $ventana.on('scroll resize', check_if_in_view);
        $ventana.trigger('scroll');
   });

    function check_if_in_view() {
        var window_height = $ventana.height(),
            window_top_position = $ventana.scrollTop(),
            window_bottom_position = (window_top_position + window_height) - 50;

        $.each($animation_elements, function() {
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

    function menu_responsive_calc(){
        if( $ventana.width() < 768 ){
            $hamburger.closest('nav').find('.list-menu').find('li').eq(1).addClass('menu-responsive');
        }else{
            $hamburger.closest('nav').find('.list-menu').find('li').eq(1).removeClass('menu-responsive');
        }
    }

    

    $('.link-adelanto-sueldo').on('click', function(){
        $('#docNumber').focus();
    });

    $('.check-inner').on('click',function(){
        $(this).toggleClass('checked');
    });

    $ventana.on('load', function () {
        apareceImagen();
    });
    $ventana.resize(function() {
        apareceImagen();
    });

    /*----------------------------------------------------------------------------------*/
    /* Menu Responsive
    /*----------------------------------------------------------------------------------*/
    /*$hamburger.click(function(event) {
        $(this).toggleClass('opened').parent().parent().find('nav').addClass('active-menu');
        console.log('abrí menu responsive');
        //if( !$('#hamburger').hasClass('opened') ){
            cerrar_nav();
            console.log('esta cerrado');
        }
    });
    function cerrar_nav() {
        //$('.active-menu').removeClass();
        $hamburger.removeClass('opened');
    }
    $( window ).resize(function() {
        cerrar_nav();
    });*/

    /*----------------------------------------------------------------------------------*/
    /* Submenu hover
    /*----------------------------------------------------------------------------------*/
    $('.current-menu').hover(function() {
        $('header').addClass('hover');
    }, function() {
        $('header').removeClass('hover');
    });

    /*----------------------------------------------------------------------------------*/
    /* Next Section Button
    /*----------------------------------------------------------------------------------*/
    $('#down').on('click', function() {
        scrollNexSection( $('.sec-press') );
    });
    $('#downe').on('click', function() {
        scrollNexSection( $('.sec-benefits') );
    });
    function scrollNexSection($nextSection){
        $('html, body').animate({
            scrollTop: $nextSection.offset().top - 60
        }, 750);
    }


    /*----------------------------------------------------------------------------------*/
    /* Tabs
    /*----------------------------------------------------------------------------------*/
    $('.head-tab ul li a').click(function(event){
        event.preventDefault();
        $('.tabs a').removeClass('head-active');
        $(this).addClass('head-active');
        var id = $(this).data('id');
        $('.content-tabs .item-tab').removeClass('tab-active');
        $('#'+id).addClass('tab-active');
    });

    $('.head-tab ul li .head-item').on('click', function(event){
        event.preventDefault();
        $('html, body').animate({
            scrollTop: $('.tabs').offset().top
        }, 1050);
    });

    /*-----------------------------------------------------------------------------------*/
    /* Acordeon Expand
     /*-----------------------------------------------------------------------------------*/
    function acordeonExpand(element,parent){
        var btn_item = $(element);
        var condition = false;
        btn_item.click(function(event) {
            event.preventDefault();
            $(this).addClass('activar');
            if(condition != true){
                $(this).addClass('activar');
                condition = true;
            }
            else{
                btn_item.removeClass('activar');
                condition = false;
            }
            $(this).parent().find(parent).stop(false).slideToggle();
            $(this).parent().find(parent).toggleClass('actived');
        });
    }
    acordeonExpand('.accordion dt','dd');


    $('.tabs-lists .item-tab').click(function() {
        if(location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'')&& location.hostname == this.hostname) {
            var $target = $(this.hash);
            $target = $target.length && $target || $('[name=' + this.hash.slice(1) +']');
            if ($target.length) {
                $('.tabs-lists li').removeClass('active');
                $(this).parent().addClass('active');
                //var targetOffset = $target.offset().top - 60;
                if( $(window).width() < 768 ){
                    var targetOffset = $target.offset().top - 270;
                }else{
                    var targetOffset = $target.offset().top - 60;
                }
                $('html,body').animate({scrollTop: targetOffset}, 800);
                return false;
            }
        }
    });

    //primer acordion
    var primerAccordion= $('#about').find('.accordion').eq(0).find('dt').addClass('activar');
    $('.h1').on('click', function(event){
        var li = $(this).parent();
        $(this).parent().addClass('active');

        if($('.p1').parent().hasClass('actived')){
            console.log('algun li tiene actived - contenido');
            $(this).parent().removeClass('active');
            $(this).parent().removeClass('actived');
            $('.p1').parent().removeClass('actived');
        }else{
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
    $('.h2').on('click', function(event){
        var li = $(this).parent();
        $(this).parent().addClass('active');

        if($('.p2').parent().hasClass('actived')){
            console.log('algun li tiene actived - contenido');
            $(this).parent().removeClass('active');
            $(this).parent().removeClass('actived');
            $('.p2').parent().removeClass('actived');
        }else{
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
    $('.h3').on('click', function(event){
        var li = $(this).parent();
        $(this).parent().addClass('active');

        if($('.p3').parent().hasClass('actived')){
            console.log('algun li tiene actived - contenido');
            $(this).parent().removeClass('active');
            $(this).parent().removeClass('actived');
            $('.p3').parent().removeClass('actived');
        }else{
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
    $('.h4').on('click', function(event){
        var li = $(this).parent();
        $(this).parent().addClass('active');

        if($('.p4').parent().hasClass('actived')){
            console.log('algun li tiene actived - contenido');
            $(this).parent().removeClass('active');
            $(this).parent().removeClass('actived');
            $('.p4').parent().removeClass('actived');
        }else{
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
    $('.h5').on('click', function(event){
        var li = $(this).parent();
        $(this).parent().addClass('active');

        if($('.p5').parent().hasClass('actived')){
            console.log('algun li tiene actived - contenido');
            $(this).parent().removeClass('active');
            $(this).parent().removeClass('actived');
            $('.p5').parent().removeClass('actived');
        }else{
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
    $('.h6').on('click', function(event){
        var li = $(this).parent();
        $(this).parent().addClass('active');

        if($('.p6').parent().hasClass('actived')){
            console.log('algun li tiene actived - contenido');
            $(this).parent().removeClass('active');
            $(this).parent().removeClass('actived');
            $('.p6').parent().removeClass('actived');
        }else{
            console.log('ningun li tiene active');
            $(this).parent().parent().find('li').removeClass('active');
            $(this).parent().parent().find('li').removeClass('actived');
            $(this).parent().addClass('active');
            $('.p6').parent().addClass('actived');
        }
        $('html, body').animate({
            scrollTop: $(".p6").parent().offset().top- 400
        }, 1050);
    });
    $('.h7').on('click', function(event){
        var li = $(this).parent();
        $(this).parent().addClass('active');

        if($('.p7').parent().hasClass('actived')){
            console.log('algun li tiene actived - contenido');
            $(this).parent().removeClass('active');
            $(this).parent().removeClass('actived');
            $('.p7').parent().removeClass('actived');
        }else{
            console.log('ningun li tiene active');
            $(this).parent().parent().find('li').removeClass('active');
            $(this).parent().parent().find('li').removeClass('actived');
            $(this).parent().addClass('active');
            $('.p7').parent().addClass('actived');
        }
        $('html, body').animate({
            scrollTop: $(".p7").parent().offset().top- 400
        }, 1050);
    });

    /*-----------------------------------------------------------------------------------*/
    /* Banner Images
     /*-----------------------------------------------------------------------------------*/
    function apareceImagen(){
        if( $(window).width() < 768){
            var $laImagen = $('.main-banner').find('#bannerPrincipal');
            var $laImagenResponsive = $('.main-banner').find('#bannerResponsive');
            var $srcOriginal = $laImagen.attr('data-src');
            console.log($srcOriginal);
            var $srcOriginalResponsive = $laImagenResponsive.attr('data-src');
            if ($srcOriginal !== undefined) {
                $laImagen.removeAttr('src');
                $laImagenResponsive.attr('src', $srcOriginalResponsive);
                console.log($srcOriginalResponsive);
            }
            else{
                if ($srcOriginal !== undefined) {
                    $laImagen.attr('src', $srcOriginal);
                }
            }
        }
        else{
            var $laImagen = $('.main-banner').find('#bannerPrincipal');
            var $laImagenResponsive = $('.main-banner').find('#bannerResponsive');
            var $srcOriginal = $laImagen.attr('data-src');
            var $srcOriginalResponsive = $laImagenResponsive.attr('data-src');
            if ($srcOriginal !== undefined) {
                $laImagenResponsive.removeAttr('src');
                $laImagen.attr('src', $srcOriginal);
            }
            else{
                if ($srcOriginal !== undefined) {
                    $laImagen.attr('src', $srcOriginal);
                }
            }
        }
    }

    /*-----------------------------------------------------------------------------------*/
    /* Input Focus
     /*-----------------------------------------------------------------------------------*/
    var $label = $('form .gfield_label');
    var $input = $('form input, form textarea');
    $label.on('click', function(){
        $(this).parent().parent().find('input').focus();
    });
    $input.on("focus",function(event){
        if($(this).length && $(this).val().length){
        }else{
            $(this).parent().parent().addClass('focus');
        }
    }).on("blur", function(event){
        if($(this).length && $(this).val().length){
            $(this).parent().parent().addClass('focus');
        }else{
            $(this).parent().parent().removeClass('focus');
        }
    });
    /*End focus*/


    /*-----------------------------------------------------------------------------------*/
    /* Select
     /*-----------------------------------------------------------------------------------*/

    var $cboTypeDoc = $('#docType');
    var $cboOcupation = $('#ocupation');
    var $cboReason = $('#reason');
    setSelect($cboTypeDoc , 'DNI', 80);
    setSelect($cboOcupation , 'Cargo', 403);
    setSelect($cboReason , 'Motivo', 403);

    function setSelect( $this , $placeholder, $width ){
        $this.select2({
            placeholder: $placeholder,
            allowClear: true,
            width: $width
        });
    }

    /*-----------------------------------------------------------------------------------*/
    /* Go Footer Button - Empresas
     /*-----------------------------------------------------------------------------------*/
    $('.go-contact').click(function(){
        $('html, body').animate({
            scrollTop: $( $(this).attr('href') ).offset().top
        }, 800);
        return false;
    });

    $('.callForm').on( 'click', function(even){
        even.preventDefault();
        $('#contact-modal').addClass('fade').addClass('in');
    });

    $('#contact-modal').find('.icon-close').on( 'click' , function() {
        $('#contact-modal').removeClass('fade').removeClass('in');
    });

    $('#confirmation-button').on( 'click', function(even){
        even.preventDefault();
        $('#confirmation-modal').addClass('fade').addClass('in');
    });

    $('#confirmation-modal').find('.icon-close').on( 'click' , function() {
        $('#confirmation-modal').removeClass('fade').removeClass('in');
    });


    /*-----------------------------------------------------------------------------------*/
    /* Sticky Header
     /*-----------------------------------------------------------------------------------*/
    var $headerSite = $("#header");
    if ($headerSite.length) {
        $ventana.on('scroll' , function(){
            var $scrollSize = $ventana.scrollTop();
            if ($scrollSize >  100) {
                $headerSite.addClass('sticky-header');
            } else {
                $headerSite.removeClass('sticky-header');
            }
        });
    }

    /*-----------------------------------------------------------------------------------*/
    /* Sticky Faqs aside
     /*-----------------------------------------------------------------------------------*/
    var $wfaqs = $('.w-faqs');
    if ($wfaqs.length) {
        $ventana.on('scroll' , function(){
            var $scrollSize = $ventana.scrollTop();
            if ($scrollSize >  380) {
                $wfaqs.addClass('sticky-faqs');
            } else {
                $wfaqs.removeClass('sticky-faqs');
            }
        });
    }

    /*-----------------------------------------------------------------------------------*/
    /* variables
     /*-----------------------------------------------------------------------------------*/
    var $cuerpo = $('body');
    var element1 = $('.main-banner'); //sección slider
    var element2 = $('.sec-press');
    var element3 = $('.sec-benefits');
    var element4 = $('.sec-youneedhelp');

    $ventana.on('load', function() {
        if( $owlTestimonial.length ){
            $owlTestimonial.owlCarousel({
                items: 1,
                loop: true,
                smartSpeed: 600,
                autoplay:true,
                autoplayTimeout:5500,
                mouseDrag:true,
                center: true
            });
        }


        $('#available-languages').find('li').find('button').on('click', function(){
            $(this).parent().addClass('active').siblings('li').removeClass('active');
        });

        var $animation_elements = $('.toload');
        var $header = $('#header');
        var $banner = $('.main-banner');
        $header.addClass('loaded');
        $banner.addClass('loaded');


        function check_if_in_view() {
            var window_height = $ventana.height();
            var window_top_position = $ventana.scrollTop();
            var window_bottom_position = (window_top_position + window_height) - 50;

            $.each($animation_elements, function() {
                var $element = $(this);
                var element_height = $element.outerHeight();
                var element_top_position = $element.offset().top;
                var element_bottom_position = (element_top_position + element_height);
                if ((element_bottom_position >= window_top_position) && (element_top_position < window_bottom_position)) {
                    $element.addClass('loaded').removeClass('toload');
                    if( element1.hasClass('loaded') ){
                        apareceImagen();
                    }
                    if( element2.hasClass('loaded') ){
                        /*
                        $owlPress.owlCarousel({

                            items: 3,
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
                                    items: 3
                                }
                            }
                        });
                        */
                    }

                } else {
                    $element.removeClass('loaded');
                }
            });
        }
        $ventana.on('scroll resize', check_if_in_view);
        $ventana.trigger('scroll');


    });
    /*-----------------------------------------------------------------------------------*/
    /* Landing Forms
     /*-----------------------------------------------------------------------------------*/

    $ventana.on('load', function() {



        /*-----------------------------------------------------------------------------------*/
        /* Validation Modal Form
         /*-----------------------------------------------------------------------------------*/
        var $sendMessage = $('#sendMessage');
        var $contactForm = $('#contactForm');
        var $responseBlock = $('#response-block');
        //debugger;
        if (ajaxValidator != null) {
            var jsonValidator = JSON.parse(ajaxValidator);
            $contactForm.validateForm(createFormValidationJson(jsonValidator, $contactForm));
        }
        $sendMessage.on('click', function (event) {
            event.preventDefault();
            validate();
        });

        function validate() {
            if ($contactForm.valid()) {
                grecaptcha.reset();
                grecaptcha.execute();
            }
        }

        function submit() {
            $sendMessage.loading('', 2);
            defaultAjax({
                url: ajaxContact,
                type: 'POST',
                formValidation: $contactForm.data('validator'),
                data: $contactForm.serializeObject(),
                success: function (data) {
                    $sendMessage.unloading();
                    $contactForm.data('validator').resetForm();
                    $contactForm[0].reset();
                    $responseBlock.html(successAjaxMessage('Gracias, te responderemos a la brevedad.'));
                },
                error: function () {
                    $sendMessage.unloading();
                }
            });
        }



        /*-----------------------------------------------------------------------------------*/
        /* Validation Advance Form
         /*-----------------------------------------------------------------------------------*/


        $('#subscribe').on('click', function(e){
            e.preventDefault();
            var responseBlock = $('#response-block'),
                btn = $(this);
            if(!isEmail($('#email').val())){
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
        });
    });

    $('#action_preEvaluation_open').on('click', function(){
        $(this).closest('.credito-consumo').addClass('main-open');
    });

    $('#action_preEvaluation').on('click', function(){
        $(this).closest('.credito-consumo').removeClass('main-open');
    });

    $('#form_preEvaluation #amount').focus();

    $('#amount').focus();

    $('#form_preEvaluation #amount').keydown(function (e) {
        if (e.which == 9) {
            $('#reason').select2('open');
        }
    });

    var changeTypeDoc = function() {
        $('#docNumber').focus();
    };

    $('#reason').on('select2:select change', changeTypeDoc);


}());

$(document).on('ready', function() {
    var idAelantoSueldo = $('#adelantoSueldo');
    if( idAelantoSueldo.length ){
        $('#scrollTop').on('click', function (e) {
            e.preventDefault();
            var body = $("html, body");
            body.stop().animate({scrollTop:0}, 500, 'swing');
        });
        $('#landingSubmit').doOnce(function(){
            $('#landingSubmit').on('click', function(){
                letMeKnow();
            });
        });
        $('#docType').doOnce(function(){
            $('#docType').select2({
                minimumResultsForSearch: -1,
                width: '80px'
            });
        });
        var responseBlock = $('#response-block'), buttonLanding = $('.button-landing');
        $('#docNumber').forceLettersOnly('^[0-9]+$', true, 8, 8);
        $('#cellphone').forceLettersOnly('^[0-9]+$', true, 9, 9);
        $('#authToken').forceLettersOnly('^[0-9]+$', true, 4, 4);
        $('#docType').on('change', function(){
            responseBlock.html('');
            $('#docNumber').val('');
            if ( $(this).val() == 1 ) {
                $('#docNumber').removeClass('document-input-landing-ce').addClass('document-input-landing');
                $('#docNumber').forceLettersOnly('^[0-9]+$', true, 8, 8);
            } else {
                $('#docNumber').removeClass('document-input-landing').addClass('document-input-landing-ce');
                $('#docNumber').forceLettersOnly('^[0-9]+$', true, 9, 9);
            }
        });
        $('.form-container').find('input').on('keyup', function(e){
            if(e.keyCode == 13) {
                $(this).closest('.block').find('.button-landing:enabled')[0].click();
            }
        });
        $('.display-target').on('click', function (event) {
            event.preventDefault();
            switch ($(this).data('target')) {
                case '#document-block':
                    responseBlock.html('');
                    $('#docNumber').val('');
                    $(this).closest('.block').addClass('block-hidden');
                    $($(this).data('target')).removeClass('block-hidden');
                    break;
                case '#email-block':
                    responseBlock.html('');
                    $('#email').val('');
                    $(this).closest('.block').addClass('block-hidden');
                    $($(this).data('target')).removeClass('block-hidden');
                    break;
                case 'resendToken':
                    var resendButtonToken = $(this);
                    defaultAjax({
                        url: url_loanApplication + "/"+ loanApplicationToken +"/resendSmsToken",
                        type: 'POST',
                        success: function (data) {
                            resendButtonToken.fadeOut();
                            setTimeout(function () {
                                resendButtonToken.fadeIn();
                            }, 30000);
                        }
                    });
                    break;
            }
        });

        function readCookie(name) {
            var n = name + "=";
            var cookie = document.cookie.split(';');
            console.log('ejecutando function readCookie');
            for(var i=0;i < cookie.length;i++) {
                var c = cookie[i];
                while (c.charAt(0)==' '){c = c.substring(1,c.length);}
                if (c.indexOf(n) == 0){return c.substring(n.length,c.length);}
            }
            return null;
        }

        $('.button-landing').doOnce(function(){
            $('.button-landing').on('click', function(e){
                e.preventDefault();
                var btn = $(this);
                switch(btn.data('target')) {
                    case '#document-block':
                        var v = false,
                            msgError = 'N&uacute;mero de documento inv&aacute;lido.',
                            docNumber = $('#docNumber');
                        if (docNumber.val().length == 8 && $('#docType').val() == 1) {
                            v = true;
                        }
                        if (docNumber.val().length == 9 && $('#docType').val() == 2){
                            v = true;
                        }
                        if (v) {
                            responseBlock.html('');
                            btn.loading('', 2);
                            defaultAjax({
                                url: url_salaryAdvance,
                                type: 'POST',
                                form: $('#frmAdelanto'),
                                data: {
                                    docType: $('#docType').val(),
                                    docNumber: $('#docNumber').val(),
                                    source: readCookie("utm_source"),
                                    medium: readCookie("utm_medium"),
                                    campaign: readCookie("utm_campaign"),
                                    term: readCookie("utm_term"),
                                    content: readCookie("utm_content"),
                                    gclid: readCookie('gclid'),
                                    gaClientID: localStorage.getItem('ga:clientId')
                                },
                                success: function (data) {
                                    btn.unloading();

                                    var json = JSON.parse(data);
                                    loanApplicationToken = json.loanApplicationToken;

                                    $('#document-block').addClass('block-hidden');
                                    $('#cellphone-block').removeClass('block-hidden');

                                },
                                error: function (xhr, data) {
                                    btn.unloading();
                                    if(data.type == "message" && data.message != null && isJsonString(data.message)){
                                        var errorJson = JSON.parse(data.message);
                                        if(errorJson.type == "noEmployee"){
                                            swal({title: "", text: errorJson.msg, confirmButtonText: 'Ok'});
                                            $('.sweet-alert > p').html(errorJson.msg);
                                            $('.sweet-alert > p > a').css({color: '#26cad3'})
                                            return false;
                                        }
                                    }
                                }
                            });

                        } else {
                            responseBlock.html(errorAjaxMessage(msgError));
                        }
                        break;
                    case '#email-block':
                        if (!isEmail($('#email').val())) {
                            responseBlock.html(errorAjaxMessage('Ingresa un email v&aacute;lido.'));
                        } else {
                            responseBlock.html('');
                            btn.loading('', 2);
                            defaultAjax({
                                url: url_salaryAdvance,
                                type: 'POST',
                                form: $('#frmAdelanto'),
                                data: {
                                    email: $('#email').val()
                                },
                                success: function (data) {
                                    btn.unloading();
                                    $('#email-block').addClass('block-hidden');
                                    responseBlock.html(successAjaxMessage('Te enviamos un email con el link para acceder a tu crédito. Puede tardar unos segundos.'));
                                },
                                error: function () {
                                    btn.unloading();
                                }
                            });
                        }
                        break;
                    case '#cellphone-block':
                        var cell = $('#cellphone').validateRegex('^(9)[0-9]{8}?$');
                        if ($('#cellphone').val().length == 9 && cell) {
                            responseBlock.html('');
                            btn.loading('', 2);
                            defaultAjax({
                                url: url_salaryAdvanceCellphone,
                                type: 'POST',
                                form: $('#frmAdelanto'),
                                data: {
                                    loanApplicationToken: loanApplicationToken,
                                    cellphone: $('#cellphone').val()
                                },
                                success: function (data) {
                                    btn.unloading();

                                    $('#cellphone-block').addClass('block-hidden');
                                    $('#confirmationPin-block').removeClass('block-hidden');
                                },
                                error: function () {
                                    btn.unloading();
                                }
                            });
                        } else {
                            responseBlock.html(errorAjaxMessage('Ingresa un n&uacute;mero v&aacute;lido.'));
                        }
                        break;
                    case '#confirmationPin-block':
                        if ($('#authToken').val().length === 4) {
                            responseBlock.html('');
                            btn.loading('', 2);
                            defaultAjax({
                                url: url_salaryAdvanceCellphoneValid,
                                type: 'POST',
                                form: $('#frmAdelanto'),
                                data: {
                                    loanApplicationToken: loanApplicationToken,
                                    authToken: $('#authToken').val()
                                },
                                success: function (data) {
                                    btn.unloading();
                                },
                                error: function () {
                                    btn.unloading();
                                }
                            });
                        } else {
                            responseBlock.html(errorAjaxMessage('El PIN no es v&aacute;lido.'));
                        }

                        break;
                }
            });
        });
    }

});
function letMeKnow() {
    if (!isEmail($('#earlyEmail').val())) {
        $('#response-block').html(errorAjaxMessage('Ingresa un email v&aacute;lido.'));
    } else {
        $('#response-block').html('');
        $('#landingSubmit').loading();
        defaultAjax({
            url: baseUrl,
            type: 'POST',
            data: $('#frmEarlyAccess').serializeObject(),
            success: function (data) {
                $('#landingSubmit').unloading();
                $('#early-block').addClass('block-hidden');
                $('#response-block').html(successAjaxMessage('Te avisaremos apenas estemos listos.'));
            },
            error: function () {
                $('#landingSubmit').unloading();
            }
        });
    }
}

$(window).on('load', function () {
    /*si va*/
    $('#form_preEvaluation #amount').focus();

    setTimeout(function () {
        $('#estilos').remove();
    }, 900);
});
