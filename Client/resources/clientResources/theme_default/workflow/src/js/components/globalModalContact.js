var contactForm = $('#contactForm');
var sendMessage = $('#sendMessage');
var btn_call_modalContact = $('#btnContact');
var modalContact = $('#contact-modal');
var responseBlock = $('#response-block');

$(document).on('ready', function() {
    if (globalModalContact != null) {
        contactForm.validateForm(createFormValidationJson(JSON.parse(globalModalContact.ajaxValidator), contactForm));
    }

    sendMessage.on('click', function (event) {
        event.preventDefault();
        if (contactForm.valid()) {
            grecaptcha.reset();
            grecaptcha.execute();
        }
    });

    btn_call_modalContact.on('click', function (event) {
        event.preventDefault();
        modalContact.addClass('fade in');
    });

    modalContact.find('.icon-close').on('click', function(event){
        event.preventDefault();
        modalContact.removeClass('fade');
        setTimeout(function() {
            modalContact.removeClass('in');
        }, 200);
    });
});

function submitGlobalModalContact() {
    sendMessage.loading('', 2);
    defaultAjax({
        url: globalModalContact.ajaxUrl,
        type: 'POST',
        formValidation: contactForm.data('validator'),
        data: contactForm.serializeObject(),
        success: function (data) {
            sendMessage.unloading();
            contactForm.data('validator').resetForm();
            contactForm[0].reset();
            responseBlock.html(successAjaxMessage('Gracias, te responderemos a la brevedad.'));
        },
        error: function () {
            sendMessage.unloading();
        }
    });
}