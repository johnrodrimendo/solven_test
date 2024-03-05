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
    shutDown                = $(".shutDown")
 
;


$(document).on('ready', function() {

    btn_down.click(function() {
        var cls = $(this).closest("main").next().offset().top - 50;
        $("html, body").animate({scrollTop: cls}, "slow");
    });

 

    var headerScroll = header.height();
   
    /*f (header.length) {
        $(window).on('scroll' , function(){
            var $scrollSize = $(window).scrollTop();
            if ($scrollSize >=  header.height() ) {
                header.addClass('header-scroll');
            } else {
                header.removeClass('header-scroll');
            }
        });
    }*/


    closes.click(function(event) {
        event.preventDefault();
        var t = $(this);
        t.closest('nav').find('.list-menu').find('li').eq(1).toggleClass('active-menu');
        console.log("class")
        //$("#hamburger").removeClass('hamburger-none');
        body.toggleClass('ov-hidden');
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
$(window).on('load', function() {
    check_if_in_view();
    $(window).on('scroll resize', check_if_in_view);
    $(window).trigger('scroll');
});
 

// $(window).on('resize', function(){
//     menu_responsive_calc();
// });

talkToUS.slick({
    autoplay: true,
    autoplaySpeed: 2000,
    dots: false,
    slidesToScroll: 5,
    slidesToShow: 5,
    responsive: [
        {
            breakpoint: 700,
            settings: {
                autoplaySpeed: 3000,
                slidesToScroll: 1,
                slidesToShow: 4
            }
        },
        {
            breakpoint: 600,
            settings: {
                autoplaySpeed: 3000,
                slidesToShow: 3,
                slidesToScroll: 1
            }
        },
        {
            breakpoint: 480,
            settings: {
                autoplaySpeed: 3000,
                slidesToShow: 2,
                slidesToScroll: 1
            }
        }
        // You can unslick at a given breakpoint now by adding:
        // settings: "unslick"
        // instead of a settings object
    ]

});

workerUs.slick({
    dots: false,
    autoplay: true,
    autoplaySpeed: 1000,
    slidesToScroll: 1,
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
 

//modal

shutDown[0];






// When the user clicks the button, open the modal 
// btn.onclick = function() {
//   modal.style.display = "block";
// }

// When the user clicks on <span> (x), close the modal
// span.onclick = function() {
//   modal.style.display = "none";
// }

// window.onclick = function(event) {
//   if (event.target == modal) {
//     modal.style.display = "none";
//   }
// }