$.fn.standOut = function() {
    var arrayParents = $(this).parents().get().reverse();
    if(arrayParents.length > 1){
        for(var i=1; i<=arrayParents.length; i++){
            if(i == arrayParents.length){
                $(arrayParents[i-1]).children().not($(this)).addClass("blurStandOutMe");
            }else{
                $(arrayParents[i-1]).children().not(arrayParents[i]).addClass("blurStandOutMe");
            }
        }
    }
}

$.fn.standOff = function() {
    $('.blurStandOutMe').removeClass("blurStandOutMe");
}

$.fn.standOutMe = function() {
    $(this).focus(function(){
        $(this).standOut();
    }).blur(function(){
        $(this).standOff();
    });
};