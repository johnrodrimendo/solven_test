$(document).on('ready', function () {
    updateBranding();
});
function updateBranding(){
    if(!isBranded){
        return;
    }
    $('#favicon').attr('href', $fav_icon);
    $('#noDebtsButton').css({
        'color' : $cadena_colorweb,
        'border-color' : $cadena_colorweb
    });
    $('#noDebtsButton').hover(function() {
        $(this).css("background-color", $cadena_colorweb);
        $(this).css('color','#FFFFFF');
    }, function() {
        $(this).css({
            'background-color': '#FFFFFF',
            'color' : $cadena_colorweb,
            'border-color' : $cadena_colorweb
        });
    });
    $('.item-consolidation-detail .no-consolidate-button').css("background-color", $cadena_colorwebsecond);
    $('.item-consolidation-detail form .w-buttons .close-button').css("background-color", $cadena_colorwebsecond);
    $('.item-consolidation-detail form .w-buttons .close-button').hover(function() {
        $(this).css("background-color", $cadena_colorwebsecond);
    }, function() {
        $(this).css("background-color", $cadena_colorwebsecond);
    });
    $('.modal .wrap-form .button.bg-transparent').css({
        'color' : $cadena_colorweb,
        'border-color' : $cadena_colorweb
    });
    $('.modal .wrap-form .wrap-field .input-outline').css("border-color", $cadena_colorgris);
    $('.modal .wrap-form .wrap-field .input-outline').hover(function() {
        $(this).css("border-color", $cadena_colorweb);
    }, function() {
        $(this).css("border-color", $cadena_colorgris);
    });
    $('.modal .wrap-form .wrap-field .input-outline').delegate( "*", "focus", function() {
        $(this).css("border-color", $cadena_colorweb);
    });
    $('.brand-txt-colorweb').css("color", $cadena_colorweb);
    $('.text-red.text-bold').css("color", $cadena_colorweb);
    $('.brand-txt-white').css("color", $cadena_colorwhite);
    $('.choose-agent .underline').css("background", "#e8e8e8");
    $('body.brand .brand-footer .underline').css("background", $cadena_colorweb);
    $('.imgProgress-svg').css("background", $cadena_colorgris);
    $('.imgProgress-svg').css("border-color", $cadena_colorgris);
    $('.imgProgress-svg .imgProgress-circle').css("fill", $cadena_colorgris);
    $('.imgProgress-svg .imgProgress-circle').css("stroke", $cadena_colorweb);
    $('.brand .divisor-bottom').css("color", $cadena_colorweb);
    $('.bg-black').css("background", $cadena_colorwebsecond);
    $('.bg-red').css("background", $cadena_colorweb);
    $('.bg-red-outline').css("background", 'white');
    $('.bg-red-outline').css("border", `1px solid ${$cadena_colorweb}`);
    $('.bg-red-outline').css("color", `${$cadena_colorweb}`);
    $('.bg-lightblue').css("background", $cadena_colorweb);
    $('.divisor-bottom').css('background', $cadena_colorweb );
    $('body.brand .modal .modal-dialog .modal-content .modal-top .corner-close  ').css("background", $cadena_colorweb);
    $('body').find('.sweet-alert button').css("background", $cadena_colorweb);
    $('body').find('.car-model').css("color", $cadena_colorweb);
    $('body').find('.currency').css("color", $cadena_colorweb);
    $('body').find('.text-lightblue').css("color", $cadena_colorweb);
    $('small a').css("color", $cadena_colorweb);
    $('#open-modal-question1').css("color", $cadena_colorweb);
    $('.modal .icon-close').css("background", $cadena_colorweb);
    $('body').find('.select2-selection:focus').css("border-color", $cadena_colorweb);
    $('body').find('b[role=presentation]').css("border-color", $cadena_colorweb+' transparent transparent transparent');
    $('#documents .tp-wrap .msg-block span').css("background", $cadena_colorweb);
    $('.sidebar-breadcrumb ul li .circle').css("background", "#e2e2e2");
    $('.sidebar-breadcrumb ul li span').css("color", "#e2e2e2");
    $('.sidebar-breadcrumb ul li .bullets').css("background", "#e2e2e2");
    $('.sidebar-breadcrumb ul li.active .circle').css("background", $cadena_colorwebsecond);
    $('.sidebar-breadcrumb ul li.active span').css("color", $cadena_colorwebsecond);
    $('.sidebar-breadcrumb ul li.active .bullets').css("background", $cadena_colorwebsecond);
    $('body').find('.progressBar .bar').css("background", $cadena_colorweb);
    $('body').find('#nprogress .peg').css("box-shadow", `0 0 10px ${$cadena_colorweb}, 0 0 5px ${$cadena_colorweb}`);
    let styleBranding = document.createElement('style');
    styleBranding.type = 'text/css';
    styleBranding.innerHTML = `#nprogress .peg{
        box-shadow : 0 0 10px ${$cadena_colorweb}, 0 0 5px ${$cadena_colorweb} !important;
    }`;
    document.getElementsByTagName('head')[0].appendChild(styleBranding);
    $('body').find('#question-replaceable').find('.positive').css("color", $cadena_colorwebsecond);
    $('body').find('#question-replaceable').find('.icon-star').css("color", $cadena_colorwebsecond);
    $('.brand .bottom-footer.brand-footer ul li span').css("color", "#7A808D");
    $('.brand .bottom-footer.brand-footer ul li a').css("color", $cadena_colorweb);
    $('body').find('.icon-edit').css("color", $cadena_colorweb);
    $('#edit-loan').css("color", $cadena_colorweb);
    $('.brand .datepicker table tr td.day').css("background-color", "#e2e2e2" );
    $('.brand .loan-process .datepicker table tr td.disabled, .loan-process .datepicker table tr td.new, .loan-process .datepicker table tr td.old').css("background-color", "#FFFFFF" );
    $('.brand .datepicker table tr td.day.active').css("background-color", $cadena_colorweb );
    $('#datetimepicker12').on('click',function(){
        $('.brand .datepicker table tr td.day').css("background-color", "#e2e2e2" );
        $('.brand .loan-process .datepicker table tr td.disabled, .loan-process .datepicker table tr td.new, .loan-process .datepicker table tr td.old').css("background-color", "#FFFFFF" );
        $('.brand .datepicker table tr td.day.active').css("background-color", $cadena_colorweb );
    });
    $('#datetimepicker12').delegate( "*", "focus", function() {
        $('.brand .datepicker table tr td.day').css("background-color", "#e2e2e2" );
        $('.brand .loan-process .datepicker table tr td.disabled, .loan-process .datepicker table tr td.new, .loan-process .datepicker table tr td.old').css("background-color", "#FFFFFF" );
        $('.brand .datepicker table tr td.day.active').css("background-color", $cadena_colorweb );
    });
    $('.brand .loan-process .datepicker table tr td').on('click', function(){
        $('#datetimepicker12').focus();
        $('#datetimepicker12').trigger('click');
    });
    $('body').find('.bootstrap-datetimepicker-widget .list-unstyled li .datepicker .datepicker-days .table-condensed tr td.day.active').on('click', function(){
        $('body').find('.bootstrap-datetimepicker-widget .list-unstyled li .datepicker .datepicker-days .table-condensed tr td.day.active').css("background-color", $cadena_colorweb );
    });
    $('#edit-loan').on('click', function(){
        console.log('open modal');
        $('.modal .icon-close').css("background", $cadena_colorweb);
        $('.bg-red').css("background", $cadena_colorweb);
    });
    $( ".input-radio" ).delegate( "*", "focus", function() {
        var elem = $( this );
        elem.addClass('hover-input');
        elem.css("border-color", $cadena_colorweb);
        $('body').find('b[role=presentation]').css("border-color", $cadena_colorweb +' transparent');
        $('body').find('li[aria-selected=true]').css("background", $cadena_colorweb);
    });
    $( ".input-radio" ).delegate( "*", "blur", function() {
        var elem = $( this );
        elem.removeClass('hover-input');
        elem.css("border-color", $cadena_colorgris);
    });
    $( ".field" ).delegate( "*", "focus", function() {
        var elem = $( this );
        elem.css("border-color", $cadena_colorweb);
        $('body').find('b[role=presentation]').css("border-color", $cadena_colorweb +' transparent');
        $('body').find('li[aria-selected=true]').css("background", $cadena_colorweb);
    });
    $( ".field" ).delegate( "*", "blur", function() {
        var elem = $( this );
        elem.css("border-color", $cadena_colorgris);
    });
    $('body').find('.custom-check').on('click', function(){
        if( !$(this).hasClass('checked') ){
            $(this).find('.check-inner').css({
                'background' : $cadena_colorweb,
                'border-color' : $cadena_colorweb
            });
        }
        else{
            $(this).find('.check-inner').css({
                'background' : 'inherit',
                'border-color' : $cadena_colorgris
            });
        }
    });
    $('.help-block-error').css("color", $cadena_colorweb);
    $('.offer-title .line').css("background", $cadena_colorweb);
    $('body').find('.select2').on('click', function(){
        $('body').find('.select2-container').find('.select2-results__group').css("color", $cadena_colorweb);
        $('body').find('.select2-results__option').css({
            'background' : $cadena_colorwhite,
            'color' : '#34465d'
        });
        $('body').find('.select2-results__option.select2-results__option--highlighted').css({
            'background' : $cadena_colorweb,
            'color' : $cadena_colorwhite,
            'border-color': $cadena_colorweb
        });
    });
    $('.horizontal-line .circle').css({
        'border' : '3px solid '+$cadena_colorweb,
        'color' : $cadena_colorweb
    });
    $('.offer-container ul .best-offer').eq(0).hover(function() {
        offer_color_hover( $(this), $cadena_colorweb );
    }, function() {
        offer_color_hover( $(this),  "#D6D8DD" );
    });
    $('.offer-container ul .best-offer').eq(1).hover(function() {
        offer_color_hover( $(this), gradientOfHexaColors(questionFw.brandingPrimaryColor, '#FFFFFF' , 0.7) );
    }, function() {
        offer_color_hover( $(this),  "#D6D8DD" );
    });
    $('.offer-container ul .best-offer').eq(2).hover(function() {
        offer_color_hover( $(this), gradientOfHexaColors(questionFw.brandingPrimaryColor, '#FFFFFF' , 0.4) );
    }, function() {
        offer_color_hover( $(this),  "#D6D8DD" );
    });
    function offer_color_hover( e , c ){
        e.find('.bar-offer').css("background", c);
        e.find('.box-c').css("background", c);
        e.find('.btn-offer').css("border-color", c);
        e.find('.btn-offer').find('.circle-inner').css("color", c);
        e.find('.box-b').find("p").find("span").css("color", c);
        e.find('.icon-check').css({
            'background-color' : c,
            'border-color' : c
        });
        e.css("border-color",c);
    }
    $(".input-radio, .h-input-radio").hover(function() {
        var $this = $(this);
        if( $this.hasClass('click') ){
            if( !$this.hasClass('hover') ){
            }else{
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });
                $(".input-radio, .h-input-radio").removeClass('hover') ;
                $this.addClass('hover');
            }
            $(".input-radio, .h-input-radio").removeClass('hover') ;
            $this.addClass('click').siblings().removeClass('hover') ;
        }else{
            if( !$this.hasClass('hover') ){
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });
                $this.addClass('hover').siblings().removeClass('hover') ;
            }
        }

    }, function() {
        var $this = $(this);
        if( $this.hasClass('click') ){
            if( $this.hasClass('hover') ){
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });
                $(".input-radio, .h-input-radio").removeClass('hover') ;
                $this.removeClass('hover');
                $this.addClass('click');
            }else{
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });

                $(".input-radio, .h-input-radio").removeClass('hover') ;
                $this.removeClass('hover');
                $this.addClass('click');
            }
        }else{
            if( !$this.hasClass('hover') ){
                $this.css({ 'background' : '#FFFFFF', 'border-color' : '#d6d8dd' });
                $this.find('.radio').css({ 'border' : '2px solid #D6D8DD', 'background' : "#FFFFFF" });
            }
            else{
                $this.removeClass('hover');
            }
        }

    });

    $(".chooseCredit .input-radio").hover(function() {
        var $this = $(this);
        if( $this.hasClass('click') ){
            if( !$this.hasClass('hover') ){
            }else{
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });
                $(".input-radio").removeClass('hover') ;
                $this.addClass('hover');
            }
            $(".chooseCredit  .input-radio").removeClass('hover') ;
            $this.addClass('click').parent().siblings().find('.input-radio').removeClass('hover') ;
        }else{
            if( !$this.hasClass('hover') ){
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });
                $this.addClass('hover').parent().siblings().find('.input-radio').removeClass('hover') ;
            }
        }

    }, function() {
        var $this = $(this);
        if( $this.hasClass('click') ){
            if( $this.hasClass('hover') ){
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });
                $(".chooseCredit .input-radio").removeClass('hover') ;
                $this.removeClass('hover');
                $this.addClass('click');
            }else{
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });

                $(".chooseCredit .input-radio").removeClass('hover') ;
                $this.removeClass('hover');
                $this.addClass('click');
            }
        }else{
            if( !$this.hasClass('hover') ){
                $this.css({ 'background' : '#FFFFFF', 'border-color' : '#d6d8dd' });
                $this.find('.radio').css({ 'border' : '2px solid #D6D8DD', 'background' : "#FFFFFF" });
            }
            else{
                $this.removeClass('hover');
            }
        }

    });

    $(".input-radio, .h-input-radio").on('change' , function(){
        var $this = $(this);
        var status = true;
        $this.addClass('click').siblings().removeClass('click') ;
        $(".input-radio, .h-input-radio").css({
            'background' : '#FFFFFF',
            'border-color' : '#d6d8dd'
        });
        $(".input-radio, .h-input-radio").find('.radio').css({
            'border' : '2px solid #D6D8DD',
            'background' : "#FFFFFF"
        });
        if( $this.hasClass('click') ){
            $this.css({
                'background' : '#F9F9FB',
                'border-color' : $cadena_colorweb
            });
            $this.find('.radio').css({
                'border' : '5px solid white',
                'background' : $cadena_colorweb
            });
        }else{
            console.log('no class click');
        }
        $(".input-radio, .h-input-radio").removeClass('hover');
    });
    $(window).load(function() {
        $('#edit-loan').css("color", $cadena_colorweb);
        $('.car-model').css("color", $cadena_colorweb);
        $('body').find('.corner-close').each(function(i){
            $('.corner-close').css("background", $cadena_colorweb);
        });
        $('body').find('.brandSelected').each(function(i){
            $(this).on('click' , function(){
                $(this).addClass('test');
                $('body').find('.bg-red').css("background", $cadena_colorweb);
                console.log('click');
            });
        });
        $('body').find('#edit-loan').on('click', function(){
            $('#edit-modal').find('.bg-red').css("background", $cadena_colorweb);
            $('#edit-modal').find('.corner-close').css("color", $cadena_colorweb);
            console.log('open modal');
        });
    });
    if( $('.choose-agent').hasClass('brand') ){
        $('.choose-agent').find('.brand-footer').find('span').css("color", "#e8e8e8");
        $('.choose-agent').find('.brand-footer').find('a').css("color", "#e8e8e8");
    }
    $('.offer-container .offer-title .call-offer-modal a').css("color", $cadena_colorweb);
    //$('#take-photo').css("background", $cadena_colorweb);
    $('.main-buttons .bg-t').css({ 'background' : '#FFFFFF', 'border-color' : $cadena_colorweb , 'color': $cadena_colorweb });
    $('.main-buttons .bg-t').hover(function() {
        $(this).css({ 'background' : $cadena_colorweb, 'border-color' : $cadena_colorweb , 'color': '#FFFFFF' });
    }, function() {
        $(this).css({ 'background' : '#FFFFFF', 'border-color' : $cadena_colorweb , 'color': $cadena_colorweb });
    });
    $('body').find('.sweet-alert button').css("background-color", $cadena_colorweb);
    $('#open-modal-question60').css("color", $cadena_colorweb);
    $('.loan-process .container .adviser-section .horizontal-line .line').css("background-color", $cadena_colorweb);
    $('.schedule-modal-accept-offer').css({ 'background' : $cadena_colorweb, 'color': '#fff' });
    $('.main-choose-agent').css("background", $backgroundColorAgent);
    $('.choose-agent.brand.brand-bg-colorweb').css("background", $backgroundColorAgent);
    $('.horizontal-line .brand-bg-colorweb').css("background", $cadena_colorweb);
    $('body.main-choose-agent .hero-content p').css("color", $textColorAgent);
    $('body.main-choose-agent .hero-content ul li .circle h4').css("color", $textColorAgent);
    $('.choose-agent.brand .bottom-footer.brand-footer ul li a, .choose-agent.brand .bottom-footer.brand-footer ul li span').css("color", $textColorAgent);

    $('.loading-red .sk-fading-circle .sk-circle span').css("background", $cadena_colorweb);
    $('.loan-process .msg-loaded span .sk-fading-circle .sk-circle span').css("background", $cadena_colorweb);
    $('.sk-fading-circle .sk-circle span').css("background", $cadena_colorweb);

    $('.w-lines .color-one').css("background", $color_line_one);
    $('.w-lines .color-two').css("background", $color_line_two);
    $('.w-lines .color-three').css("background", $color_line_three);
    $('#loanNumberButton').css("background", $cadena_colorweb);
    $(".card-branded-detail .box-content").css({
        'border-color': $cadena_colorweb
    });
    $(".card-branded-detail .box-header").css({
        'background': $cadena_colorweb
    });

    $(document).on("mouseenter"," body .select2-container .select2-dropdown .select2-results .select2-results__options .select2-results__option--highlighted",function() {
        var $this = $(this);
        $this.css({ 'background' : gradientOfHexaColors(questionFw.brandingPrimaryColor, '#FFFFFF' , 0.4), 'color' : "#FFFFFF" });
    }, function() {
        var $this = $(this);
        $this.css({ 'background' :  gradientOfHexaColors(questionFw.brandingPrimaryColor, '#FFFFFF' , 0.96) , 'color' : '#FFFFFF '}); /*ternary color*/
    });
    $(document).on("mouseleave"," body .select2-container .select2-dropdown .select2-results .select2-results__options .select2-results__option--highlighted",function() {
        var $this = $(this);
        $this.css({ 'background' : '#FFFFFF' , 'color' : '#34465d'});
    });
    $(document).find('.select2-container .select2-dropdown .select2-results .select2-results__options .select2-results__option--highlighted').css({ 'background' : '#FFFFFF' , 'color' : '#34465d'});
    $('.line.brand-bg-colorweb').css("background", $cadena_colorweb);
    $('.brand .bottom-footer.brand-footer.sullana-footer ul li a').css("color", $textColorAgent);
    $('.brand .bottom-footer.brand-footer.sullana-footer ul li .underline').css("background", $textColorAgent);
    $(window).on('load', function(){
        $('.circle figure').addClass('loaded');
    });
    $('.pdfViewer .pdf-bar').css("background", $cadena_colorweb);
    $('body .brand .pdfViewer.bg-black').css('background','#34465d');
    $('.sk-circle span').css("background", $cadena_colorweb);
    $('.pdf-container .sk-circle span').css("background","#FFFFFF");
    $('.pdf-header .container .row .column-2 .help .circle').css("color", $cadena_colorwebsecond);

    $('.sidebar-cajasullana ul li.active .circle').css("background",$cadena_colorweb);
    $('.sidebar-cajasullana ul li.active span').css("color",$cadena_colorweb);
    $('.sidebar-cajasullana ul li.active .bullets').css("background",$cadena_colorweb);


    $(".modal .h-input-radio").hover(function() {
        var $this = $(this);
        if( $this.hasClass('click') ){
            if( !$this.hasClass('hover') ){
            }else{
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });
                $(".h-input-radio").removeClass('hover') ;
                $this.addClass('hover');
            }
            $(".modal .h-input-radio").removeClass('hover') ;
            $this.addClass('click').parent().siblings().find('.input-radio').removeClass('hover') ;
        }else{
            if( !$this.hasClass('hover') ){
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });
                $this.addClass('hover').parent().siblings().find('.input-radio').removeClass('hover') ;
            }
        }

    }, function() {
        var $this = $(this);
        if( $this.hasClass('click') ){
            if( $this.hasClass('hover') ){
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });
                $(".modal .h-input-radio").removeClass('hover') ;
                $this.removeClass('hover');
                $this.addClass('click');
            }else{
                $this.css({ 'background' : '#F9F9FB', 'border-color' : $cadena_colorweb });
                $this.find('.radio').css({ 'border' : '5px solid white', 'background' : $cadena_colorweb });

                $(".modal .h-input-radio").removeClass('hover') ;
                $this.removeClass('hover');
                $this.addClass('click');
            }
        }else{
            if( !$this.hasClass('hover') ){
                $this.css({ 'background' : '#FFFFFF', 'border-color' : '#d6d8dd' });
                $this.find('.radio').css({ 'border' : '2px solid #D6D8DD', 'background' : "#FFFFFF" });
            }
            else{
                $this.removeClass('hover');
            }
        }
    });
}