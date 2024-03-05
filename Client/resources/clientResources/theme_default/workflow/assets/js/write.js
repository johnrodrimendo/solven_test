$.fn.write = function (onFinish, delayOnFinish) {
    // writeFor(this, this.text(), 0, onFinish, delayOnFinish);
    var text = this.text();
    this.html('');
    $(this).typed({
        strings: [text],
        showCursor: false,
        typeSpeed: 0,
        callback: function () {
            if (onFinish != null) {
                if (delayOnFinish != null) {
                    setTimeout(onFinish, delayOnFinish);
                } else {
                    onFinish();
                }
            }
        }
    })
};

function writeFor(captionEl, caption, captionLength, onFinish, delayOnFinish) {
    captionEl.html(caption.substr(0, captionLength++));
    if (captionLength < caption.length + 1) {
        setTimeout(function () {
            writeFor(captionEl, caption, captionLength, onFinish, delayOnFinish);
        }, 60);
    } else {
        captionLength = 0;
        caption = '';
        if (onFinish != null) {
            if (delayOnFinish != null) {
                setTimeout(onFinish, delayOnFinish);
            } else {
                onFinish();
            }
        }
    }
}


function cursorAnimation() {
    $('#cursor').animate({
        opacity: 0
    }, 'fast', 'swing').animate({
        opacity: 1
    }, 'fast', 'swing');
}