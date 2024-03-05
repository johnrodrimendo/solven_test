$(document).on('ready', function () {
    $('select').select2();

    $('#registerForm').validateForm(createFormValidationJson(JSON.parse(updateFormValidator), $('#registerForm')));
    $("a[href*='" + location.pathname + "']").addClass("active");
    $('#registerBtn').click(function (e) {
        e.preventDefault();
        if ($('#registerForm').valid()) {
            $('#registerBtn').loading();
            $('#registerBtn').addClass('to-invest');
            defaultAjax({
                url: updateDataUrl,
                formValidation: $('#registerForm').data('validator'),
                data: $('#registerForm').serializeObject(),
                type: 'POST',
                success: function (data) {
                    $('#registerBtn').unloading();
                    swal({
                        title:"",
                        type: 'success',
                        text: "Los datos fueron registrados correctamente.",
                        html:true,
                        customClass: 'swal-width',
                        showConfirmButton: false, 
                        allowOutsideClick: true,
                        timer: 3000 
                    }); 
                },
                error: function (data) {
                    $('#registerBtn').unloading();
                },
                complete: function() {
                    $('#registerBtn').removeClass('to-invest');
                }
            });
        }
    });
    /*$(".statistics-payments").on("click",function(){
        $(".statistics-payments").on("click",function(){
            swal({
                title:"¡Proximante!",
                imageUrl:'/img/error-solvers.svg',
                text: "",
                html:true,
                customClass: 'swal-width',
                showConfirmButton: false, 
                allowOutsideClick: true,
                timer: 3000 
            });
        })
    })*/
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
