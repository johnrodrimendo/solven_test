/*
 * Affirm js 1.0
 * javiereat@gmail.com
 * MIT licensed
 *
 * Copyright (C) 2016 javiereat@gmail.com
 */


/*  Affirm section1 Carousel
 * --------------------------------------- */
$(document).ready(function () {

    var clickEvent = false;
    $('#aff-section1-carousel').carousel({
        interval: 4000
    }).on('click', '.list-group li', function () {
        clickEvent = true;
        $('.list-group li').removeClass('active');
        $(this).addClass('active');
    }).on('slid.bs.carousel', function (e) {
        if (!clickEvent) {
            var count = $('.list-group').children().length - 1;
            var current = $('.list-group li.active');
            current.removeClass('active').next().addClass('active');
            var id = parseInt(current.data('slide-to'));
            if (count == id) {
                $('.list-group li').first().addClass('active');
            }
        }
        clickEvent = false;
    });
})

$(window).load(function () {
    var boxheight = $('#aff-section1-carousel .carousel-inner').innerHeight();
    var itemlength = $('#aff-section1-carousel .item').length;
    var triggerheight = Math.round(boxheight / itemlength + 1);
    $('#aff-section1-carousel .list-group-item').outerHeight(triggerheight);
});


/*  Affirm section3 Items
 * --------------------------------------- */
$(document).ready(function () {
    console.log("ClubItem " + $(".club-item").length)
    $(".club-item").hover(
        function () {
            console.log("onHover");
            $(this).find(".club-item-desc").animate({bottom: -35}, 300);
            $(this).find(".club-item-desc-cont").animate({display: 'none'}, 300);
            $(this).find(".club-item-desc").addClass("hoverred");
            $(this).find('i').toggleClass("fa-chevron-up fa-chevron-right");
        },
        function () {
            $(this).find(".club-item-desc").animate({bottom: '-155px'}, 300);
            $(this).find(".club-item-desc-cont").animate({padding: '0 0 0 0px'}, 300);
            $(this).find(".club-item-desc").removeClass("hoverred");
            $(this).find('i').toggleClass("fa-chevron-up fa-chevron-right");
        }
    );
});


/* --------------------------------------- */


$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
    var target = $(this).attr('href');

    $(target).css('right', '-' + $(window).width() + 'px');
    var right = $(target).offset().right;
    $(target).css({right: right}).animate({"right": "0px"}, "10");
})