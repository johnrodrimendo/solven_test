$(document).on('ready', function () {
    $('select').select2();
    var clipboard = new ClipboardJS('#copyTextBtn');
    // $('#copyTextBtn').click(function(){
    //     var copyText = document.getElementById("textToCopy");
    //     copyText.select();
    //     copyText.setSelectionRange(0, 99999);
    //     document.execCommand("copy");
    // });
    $("a[href*='" + location.pathname + "']").addClass("active");
    const content = document.querySelectorAll('.accordion__content');

    content.forEach(content => {
        if (content && content.style.maxHeight) {
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + 'px';
        }
});
  

});


$(window).on("scroll", function () {
    if ($(window).scrollTop() > 50) {
        console.log($(window).scrollTop());
        $(".header").addClass("header-scroll");
        $(".header .menu-desktop li .active-color_white").addClass("active-color_gray");
        $(".header .menu-desktop li .active-color_white").removeClass("active-color_white");
    } 
    else {
        $(".header").removeClass("header-scroll");
        $(".header .menu-desktop li .active-color_gray").addClass("active-color_white");
        $(".header .menu-desktop li .active-color_gray").removeClass("active-color_gray");
 
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


//-------LOGOUT--------
$(document).on('ready', function () {
    $('.log-out-btn').click(function (e) {
        e.preventDefault();
        $('.log-out-btn').loading();
        defaultAjax({
            url: logoutUrl,
            type: 'POST',
            success: function (data) {
            },
            error: function (data) {
                $('.log-out-btn').unloading();
            }
        });
    });
});