//TODO: Falta que los select no entren dentro de la validacion.
// Falta que al pegar(ctrl v) se restrinja el tamaño y el regex

$(document).on('ready', function () {

    scanDisableButtons();

    // Sticky Header
    $(window).scroll(function () {
        $('.landing-header').doOnce(function () {
            var sticky = $('.landing-header'),
                scroll = $(window).scrollTop();

            if (scroll >= 100) sticky.addClass('sticky-header');
            else sticky.removeClass('sticky-header');
        });
    });

    try {
        if ($.fn.datepicker && $.fn.datepicker.defaults) {
            $.fn.datepicker.defaults.language = 'es';
        }
        if (jQuery.validator) {
            jQuery.validator.addMethod(
                "regex",
                function (value, element, param) {
                    var valid = true;
                    if (param.regex != null) {
                        for (var i = 0; i < param.regex.length; i++) {
                            if (param.regex[i] != null && !new RegExp(param.regex[i]).test(value)) {
                                valid = false;
                                break;
                            }
                        }
                    }
                    return this.optional(element) || valid;
                },
                function (param, element) {
                    return param.message;
                });
        }
    } catch (err) {
        console.log(err)
    }
});


function dataTable(obj, filtro, info_footer, sorting , pagination){

    if( obj.length ){
        obj.DataTable( {
            responsive: true,
            autoWidth: false,
            aLengthMenu: [
                [25, 50, 100, 200, -1],
                [25, 50, 100, 200, "Todos"]
            ],
            iDisplayLength: -1,
            retrieve: true,
            bFilter : filtro,
            paging:   pagination,
            info:     info_footer,
            ordering: sorting,
            language: {
                "sProcessing":     "Procesando...",
                "sLengthMenu":     "Mostrar _MENU_ registros",
                "sZeroRecords":    "No se encontraron resultados",
                "sEmptyTable":     "Ningún dato disponible en esta tabla",
                "sInfo":           "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
                "sInfoEmpty":      "Mostrando registros del 0 al 0 de un total de 0 registros",
                "sInfoFiltered":   "(filtrado de un total de _MAX_ registros)",
                "sInfoPostFix":    "",
                "sSearch":         "Buscar:",
                "sUrl":            "",
                "sInfoThousands":  ",",
                "sLoadingRecords": "Cargando...",
                "oPaginate": {
                    "sFirst":    "Primero",
                    "sLast":     "Último",
                    "sNext":     "Siguiente",
                    "sPrevious": "Anterior"
                },
                "oAria": {
                    "sSortAscending":  ": Activar para ordenar la columna de manera ascendente",
                    "sSortDescending": ": Activar para ordenar la columna de manera descendente"
                }
            }
        } );
    }
}

var tableresult = $('.table-responsive #tableResults');
dataTable( tableresult, false, false, true, false );

function scanDisableButtons(){
    $('.button').each(function(){
        if($(this).attr('data-disable')) {
            $(this).disableButton();
        }
    });
}

function functionalKeyPressed(e) {
    // Allow: backspace, delete, tab, escape, enter
    if ($.inArray(e.keyCode, [46, 8, 9, 27, 13]) !== -1 ||
        // Allow: Ctrl+A, Command+A
        (e.keyCode == 65 && ( e.ctrlKey === true || e.metaKey === true ) ) ||
        // Allow: home, end, left, right, down, up
        (e.keyCode >= 35 && e.keyCode <= 40)) {
        // let it happen, don't do anything
        return true;
    } else {
        return false;
    }
}

function counterUpInit() {
    $("[data-counter='counterup']").doOnce(function () {
        $("[data-counter='counterup']").counterUp({
            delay: 10,
            time: 1000
        });
    });
}

function successAjaxMessage(msg) {
    var element = '';
    if (msg != '') {
        element = "<span class='small-text alert successMessage'>";
        element += "<i class='icon icon-success'></i>";
        element += "<small>" + msg + "</small>";
        element += "</span>";
    }
    return element;
}

function errorAjaxMessage(msg) {
    var element = '';
    if (msg != '') {
        element = "<span class='small-text alert errorMessage'>";
        element += "<i class='icon icon-warning'></i>";
        element += "<small>" + msg + "</small>";
        element += "</span>";
    }
    return element;
}

function getFinishDate(ends, dateFormat) {
    moment.locale('es');
    if (dateFormat == 'day') {
        return moment().add(ends, dateFormat).format('DD MMMM YYYY');
    } else if (dateFormat == 'week') {
        return moment().add(ends, dateFormat).format('DD MMMM YYYY');
    }
    return moment().add(ends, dateFormat).format('MMMM YYYY');

}

/* Inputs validations */

// Email
function isEmail(email) {
    var pattern = /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;
    return pattern.test(email);
}

function formatDate(date, divisor) {
    if (!divisor) {
        divisor = '-';
    }
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [day, month, year].join(divisor);
}

function addMask(primaryColor, secundaryColor) {
    var firstColor = primaryColor != null ? primaryColor :"#CF2E38";
    var secondColor = secundaryColor != null ? secundaryColor :"#DE777F";

    var mask = $('<div class="mask"></div>');
    var hr = $('<div class="hr-container"><div class="wrapping hr-animation">' +
        '<hr class="colored" style="background: repeating-linear-gradient(-55deg, '+firstColor+', '+firstColor+' 10px, '+secondColor+' 10px, '+secondColor+' 15px)"/>' +
        '<hr class="colored" style="background: repeating-linear-gradient(-55deg, '+firstColor+', '+firstColor+' 10px, '+secondColor+' 10px, '+secondColor+' 15px)"/>' +
        '</div></div>');
    //$('body').prepend(hr);
    $('#question-replaceable').prepend(mask);

    NProgress.start();
}

function unMask() {
    $('.mask').remove();
    //$('body .hr-container').remove();
    NProgress.done();
}

function thousandWithSpaceDecimal(val) {
    return thousandWithSpace(parseFloat(val).toFixed(2));
}

function isValidCellphone(v) {
    var nine = String(v).charAt(0);
    var nine_as_number = Number(nine);

    if  (nine_as_number === 9) {
        return true;
    } else {
        return false;
    }

}
// Remove element from array
Array.prototype.remove = function(el) {
    return this.splice(this.indexOf(el), 1);
};



jQuery.fn.radio = function () {
    return this.each(function () {
        $(this).on('change', function () {
            if ($(this).attr('class') == 'input-radio') { // radio + icon + text
                $('.input-radio').removeClass('radio-selected');
                $(this).closest('.input-radio').addClass('radio-selected');
            } else if ($(this).attr('class') == 'real-radio') { //  just radio
                $('.custom-input-radio').removeClass('custom-input-radio-selected');
                $(this).closest('.custom-input-radio').addClass('custom-input-radio-selected');
            } else if ($(this).attr('class') == 'colorPicker') { //  just radio
                $('.box-color').removeClass('box-color-active');
                $(this).closest('.box-color').addClass('box-color-active');
            } else {
                $('.h-input-radio').removeClass('radio-selected'); // radio + text horizontal
                $(this).closest('.h-input-radio').addClass('radio-selected');
            }
        });
    });
};

jQuery.fn.checkbox = function () {
    var self = $(this);
    if (self.prop('checked')) {
        self.closest('.h-input-check').addClass('radio-selected');
    } else {
        self.closest('.h-input-check').removeClass('radio-selected');
    }
};


jQuery.fn.checked = function (type) {
    type = (type != null) ? type : null;
    var self = $(this);
    if (type == 'icon') {
        if (self.prop('checked')) {
            self.closest('.label-icon').addClass('label-icon-checked');
        } else {
            self.closest('.label-icon').removeClass('label-icon-checked');
        }
    } else {
        if (self.prop('checked')) {
            self.closest('.custom-check').addClass('checked');
        } else {
            self.closest('.custom-check').removeClass('checked');
        }
    }
};

jQuery.fn.doOnce = function (func) {
    this.length && func.apply(this);
    return this;
};

jQuery.fn.validateRegex = function (regex) {
    var r = new RegExp(regex);
    var value = $(this).val();
    return r.test(value);
};


/** Allow only numbers for a text box. No points either
 *
 * @param restricted
 * @param minValue
 * @param maxValue
 * @returns {*}
 */

jQuery.fn.forceIntegerOnly = function (restricted, minValue, maxValue) {
    return this.each(function () {
        $(this).off("keypress");
        $(this).off("keyup");
        $(this).numeric({negative: false, decimal: false});

        if (restricted) {
            var md = new MobileDetect(window.navigator.userAgent);
            if (md.mobile()) {
                $(this).on("keyup", function (e) {
                    var val = parseFloat($(this).val());
                    if (val > maxValue) {
                        $(this).val(maxValue);
                        e.preventDefault();
                        return false;
                    }
                });
            } else {
                $(this).on("keypress", function (e) {
                    // e.charCode == 0  fix for firefox and getSelectionStart == 0 means that the text is selected and to be replaced
                    if (e.charCode == 0 || getSelectionStart(this) == 0) {
                        return true;
                    }

                    var key = String.fromCharCode(!e.charCode ? e.which : e.charCode);
                    var val = parseFloat($(this).val() + key);
                    if (val > maxValue) {
                        $(this).val(maxValue);
                        e.preventDefault();
                        return false;
                    }
                });
            }
        }
    });

    //http://javascript.nwbox.com/cursor_position/
    function getSelectionStart(o) {
        if (o.createTextRange) {
            var r = document.selection.createRange().duplicate()
            r.moveEnd('character', o.value.length)
            if (r.text == '') return o.value.length
            return o.value.lastIndexOf(r.text)
        } else return o.selectionStart
    }
};

/**Allow only numbers for a text box. Allow points also
 *
 * @param restricted
 * @param minValue
 * @param maxValue
 * @returns {*}
 */
jQuery.fn.forceDoubleOnly = function (restricted, minValue, maxValue) {
    return this.each(function () {
        $(this).off("keypress");
        $(this).off("keyup");
        $(this).numeric({negative: false, decimalPlaces: 2});

        if (restricted) {
            var md = new MobileDetect(window.navigator.userAgent);
            if (md.mobile()) {
                $(this).on("keyup", function (e) {
                    var val = parseFloat($(this).val());
                    if (val > maxValue) {
                        $(this).val($(this).val().substr(0, $(this).val().length - 1))
                        e.preventDefault();
                        return false;
                    }
                });
            } else {
                $(this).on("keypress", function (e) {
                    // e.charCode == 0  fix for firefox and getSelectionStart == 0 means that the text is selected and to be replaced
                    if (e.charCode == 0 || getSelectionStart(this) == 0) {
                        return true;
                    }

                    var key = String.fromCharCode(!e.charCode ? e.which : e.charCode);
                    var val = parseFloat($(this).val() + key);
                    if (val > maxValue) {
                        e.preventDefault();
                        return false;
                    }
                });
            }
        }
    });

    //http://javascript.nwbox.com/cursor_position/
    function getSelectionStart(o) {
        if (o.createTextRange) {
            var r = document.selection.createRange().duplicate()
            r.moveEnd('character', o.value.length)
            if (r.text == '') return o.value.length
            return o.value.lastIndexOf(r.text)
        } else return o.selectionStart
    }
};

/** Allow only letters for an text box
 *
 * @param validRegex
 * @param restricted
 * @param minCharacters
 * @param maxCharacters
 * @returns {*}
 */
jQuery.fn.  forceLettersOnly = function (validRegex, restricted, minCharacters, maxCharacters) {
    return this.each(function () {
        $(this).off("keypress");
        $(this).off("keyup");
        if (restricted) {
            var md = new MobileDetect(window.navigator.userAgent);
            if (md.mobile()) {
                $(this).on("keyup", function (e) {
                    debugger;


                    var key = $(this).val().substr($(this).val().length - 1, 1);
                    var regex = new RegExp(validRegex);
                    if (!regex.test(key)) {
                        $(this).val($(this).val().substr(0, $(this).val().length - 1))
                        e.preventDefault();
                        return false;
                    }

                    //debugger;
                    var charactersLength = $(this).val().length;
                    if (maxCharacters != null && charactersLength > maxCharacters) {
                        $(this).val($(this).val().substring(0, maxCharacters));
                        e.preventDefault();
                        return false;
                    }
                });
            } else {
                $(this).on("keypress", function (e) {
                    // e.charCode == 0  fix for firefox and getSelectionStart == 0 means that the text is selected and to be replaced
                    if (e.charCode == 0 || getSelectionStart(this) == 0) {
                        return true;
                    }

                    var key = String.fromCharCode(!e.charCode ? e.which : e.charCode);
                    var regex = new RegExp(validRegex);
                    if (!regex.test(key)) {
                        e.preventDefault();
                        return false;
                    }

                    //debugger;
                    var charactersLength = $(this).val().length + 1;
                    if (maxCharacters != null && charactersLength > maxCharacters) {
                        $(this).val($(this).val().substring(0, maxCharacters));
                        e.preventDefault();
                        return false;
                    }
                });
            }
        }
    });

    //http://javascript.nwbox.com/cursor_position/
    function getSelectionStart(o) {
        if (o.createTextRange) {
            var r = document.selection.createRange().duplicate()
            r.moveEnd('character', o.value.length)
            if (r.text == '') return o.value.length
            return o.value.lastIndexOf(r.text)
        } else return o.selectionStart
    }
};

/** Create a hidden input element, and append it to the form:
 *
 * @param theForm
 * @param key
 * @param value
 */
function addHiddenToForm(theForm, key, value) {
    //console.log('agregando ' + value)
    if (value == null || value == undefined) {
        return;
    }
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = key;
    if (typeof value === 'object') {
        input.value = JSON.stringify(value);
    } else {
        input.value = value;
    }
    theForm.appendChild(input);
}

/** Verifies if the string is a valid json
 *
 * @param str
 * @returns {boolean}
 */
function isJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

/** Creates the json to be used by the JsonValidationPlugin from the jsonValidator of the Java Form Object Class
 *
 * @param jsonValidator
 * @returns {{debug: boolean, rules: {}, messages: {}, restrictions: {}, errorClass: string, errorElement: string, highlight: jsonFormValidation.highlight, success: jsonFormValidation.success, errorPlacement: jsonFormValidation.errorPlacement, submitHandler: jsonFormValidation.submitHandler}}
 */
function createFormValidationJson(jsonValidator, formElement, jsonFormValidationCustom) {
    var jsonFormValidation = {
        ignore: ".ignore_validation",
        rules: {},
        messages: {},
        restrictions: {},
        highlight: function (element) {
            // $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
            $(element).closest('.form-group, .field-wrap, .field').addClass('has-error field-error');
        },
        unhighlight: function (element) {
            //console.log("????");
            // $(element).closest('.form-group').removeClass('has-error').addClass('has-success');
            $(element).closest('.form-group, .field-wrap, .field').removeClass('has-error field-error');
        },
        errorClass: "help-block help-block-error",
        errorElement: "span",
        errorPlacement: function (error, element) {
            console.log(element,element.closest('form'), element.closest('.input-material'));
            if (element.closest('.form-group').find(".errorContainer").length) {
                error.appendTo(element.closest('.form-group').find(".errorContainer"));
            } else if (element.closest('.field').find(".errorContainer").length) {
                error.appendTo(element.closest('.field').find(".errorContainer"));
            }
            else if (element.closest('.input-material').find(".errorContainer").length) {
                error.appendTo(element.closest('.input-material').find(".errorContainer"));
            }
            else {
                error.appendTo(element.closest('.form').find(".errorContainer"));
            }
        },
        submitHandler: function (form) {
            form.submit();
        },
        invalidHandler: function (event, validator) {
            //console.log('invalidHandler')
            $($(formElement).find('.has-error')[0]).find('.form-control').focus();
            $($(formElement).find('.has-error')[0]).find('.form-control').triggerHandler("focus");
        },
        formElement: formElement
    };

    if (jsonFormValidationCustom != null) {
        if (jsonFormValidationCustom.errorPlacement != null) {
            jsonFormValidation.errorPlacement = jsonFormValidationCustom.errorPlacement;
        }
    }

    for (i = 0; i < Object.keys(jsonValidator).length; i++) {
        var stringKey = Object.keys(jsonValidator)[i];
        var jsonValidatorElement = jsonValidator[stringKey];

        if (isJsonString(jsonValidatorElement)) {
            continue;
        }

        jsonFormValidation.rules[stringKey] = {};
        jsonFormValidation.messages[stringKey] = {};
        jsonFormValidation.restrictions[stringKey] = {};

        if (jsonValidatorElement.required) {
            jsonFormValidation.rules[stringKey].required = true;
            jsonFormValidation.messages[stringKey].required = jsonValidatorElement.requiredErrorMsg;
        }

        if (jsonValidatorElement.type == 'Integer') {
            jsonFormValidation.restrictions[stringKey].type = jsonValidatorElement.type;
            jsonFormValidation.rules[stringKey].number = true;
            jsonFormValidation.messages[stringKey].number = jsonValidatorElement.integerErrorMsg;
            if (jsonValidatorElement.maxValue != null) {
                jsonFormValidation.rules[stringKey].max = jsonValidatorElement.maxValue;
                jsonFormValidation.messages[stringKey].max = jsonValidatorElement.maxValueErrorMsg;
            }
            if (jsonValidatorElement.minValue != null) {
                jsonFormValidation.rules[stringKey].min = jsonValidatorElement.minValue;
                jsonFormValidation.messages[stringKey].min = jsonValidatorElement.minValueErrorMsg;
            }
            if (jsonValidatorElement.restricted != null) {
                jsonFormValidation.restrictions[stringKey].restricted = jsonValidatorElement.restricted;
            }
        } else if (jsonValidatorElement.type == 'Double') {
            jsonFormValidation.restrictions[stringKey].type = jsonValidatorElement.type;
            jsonFormValidation.rules[stringKey].number = true;
            jsonFormValidation.messages[stringKey].number = jsonValidatorElement.doubleErrorMsg;
            if (jsonValidatorElement.maxValue != null) {
                jsonFormValidation.rules[stringKey].max = jsonValidatorElement.maxValue;
                jsonFormValidation.messages[stringKey].max = jsonValidatorElement.maxValueErrorMsg;
            }
            if (jsonValidatorElement.minValue != null) {
                jsonFormValidation.rules[stringKey].min = jsonValidatorElement.minValue;
                jsonFormValidation.messages[stringKey].min = jsonValidatorElement.minValueErrorMsg;
            }
            if (jsonValidatorElement.restricted != null) {
                jsonFormValidation.restrictions[stringKey].restricted = jsonValidatorElement.restricted;
            }
        } else if (jsonValidatorElement.type == 'String') {
            jsonFormValidation.restrictions[stringKey].type = jsonValidatorElement.type;
            if (jsonValidatorElement.maxCharacters != null) {
                jsonFormValidation.rules[stringKey].maxlength = jsonValidatorElement.maxCharacters;
                jsonFormValidation.messages[stringKey].maxlength = jsonValidatorElement.maxCharactersErrorMsg;
            }
            if (jsonValidatorElement.minCharacters != null) {
                jsonFormValidation.rules[stringKey].minlength = jsonValidatorElement.minCharacters;
                jsonFormValidation.messages[stringKey].minlength = jsonValidatorElement.minCharactersErrorMsg;
            }
            if (jsonValidatorElement.emailFormat) {
                jsonFormValidation.rules[stringKey].email = true;
                jsonFormValidation.messages[stringKey].email = jsonValidatorElement.emailFormatErrorMsg;
            }
            if (jsonValidatorElement.restricted != null) {
                jsonFormValidation.restrictions[stringKey].restricted = jsonValidatorElement.restricted;
            }
            if (jsonValidatorElement.validRegex != null || jsonValidatorElement.validPattern != null) {
                jsonFormValidation.rules[stringKey].regex = {
                    regex: [jsonValidatorElement.validRegex, jsonValidatorElement.validPattern],
                    message: jsonValidatorElement.validRegexErrorMsg
                };
                jsonFormValidation.restrictions[stringKey].validRegex = jsonValidatorElement.validRegex;
            }
        }
    }
    return jsonFormValidation;
}

/** Set the validations and restrictions of the form
 *
 * @param jsonFormValidation
 */
jQuery.fn.validateForm = function (jsonFormValidation) {
    for (i = 0; i < Object.keys(jsonFormValidation.rules).length; i++) {
        var stringKey = Object.keys(jsonFormValidation.rules)[i];
        var rule = jsonFormValidation.rules[stringKey];
        var restriction = jsonFormValidation.restrictions[stringKey];

        if (restriction != null) {
            if (restriction.type == 'Integer') {
                if ($(jsonFormValidation.formElement).find('#' + stringKey).length) {
                    $(jsonFormValidation.formElement).find('#' + stringKey).forceIntegerOnly(restriction.restricted, rule.min, rule.max);
                } else {
                    $(jsonFormValidation.formElement).find('[name=' + stringKey + ']').forceIntegerOnly(restriction.restricted, rule.min, rule.max);
                }
            } else if (restriction.type == 'Double') {
                if ($(jsonFormValidation.formElement).find('#' + stringKey).length) {
                    $(jsonFormValidation.formElement).find('#' + stringKey).forceDoubleOnly(restriction.restricted, rule.min, rule.max);
                } else {
                    $(jsonFormValidation.formElement).find('[name=' + stringKey + ']').forceDoubleOnly(restriction.restricted, rule.min, rule.max);
                }
            } else if (restriction.type == 'String') {
                if ($(jsonFormValidation.formElement).find('#' + stringKey).length) {
                    $(jsonFormValidation.formElement).find('#' + stringKey).forceLettersOnly(restriction.validRegex, restriction.restricted, rule.minlength, rule.maxlength);
                } else {
                    $(jsonFormValidation.formElement).find('[name=' + stringKey + ']').forceLettersOnly(restriction.validRegex, restriction.restricted, rule.minlength, rule.maxlength);
                }
            }
        }
    }

    $(this).removeData('validator');
    $(this).data('validatorJson', jsonFormValidation);

    return $(this).validate(jsonFormValidation);
};

/**
 * Return true if the json is a valid one
 * @param text
 * @returns {boolean}
 */
function isValidJson(text) {
    return (/^[\],:{}\s]*$/.test(text.replace(/\\["\\\/bfnrtu]/g, '@').replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').replace(/(?:^|:|,)(?:\s*\[)+/g, '')));
}

function showErrorModal(errorMessage) {
    if (errorMessage != null) {
        // $('#errorModal').find('p').html(errorMessage);
        // $('#errorModal').modal({
        //     show: true,
        //     backdrop: false
        // });
        swal({
            title: "Oops...",
            text: errorMessage,
            imageUrl: "/img/icon-modal-error.svg",
            html: true,
            confirmButtonColor: '#F7323F',
            cancelButtonColor: '#3F3B3B',
            imageSize: "66x66"
        });
    }
}

function showErrorModalWithRedirectLink(errorMessage,redirectLink) {
    if (errorMessage != null) {
        swal({
            title: "Oops...",
            text: errorMessage,
            imageUrl: "/img/icon-modal-error.svg",
            html: true,
            confirmButtonColor: '#F7323F',
            cancelButtonColor: '#3F3B3B',
            imageSize: "66x66"
        });
        $("div.sweet-alert button.confirm").click(function (e){
            window.location.href = redirectLink;
        })
    }
}

function showErrorModalWithRechargeButton(errorMessage) {
    if (errorMessage != null) {
        swal({
            title: "Oops...",
            text: errorMessage,
            imageUrl: "/img/icon-modal-error.svg",
            html: true,
            confirmButtonColor: '#F7323F',
            cancelButtonColor: '#3F3B3B',
            imageSize: "66x66"
        });
        $("div.sweet-alert button.confirm").click(function (e){
            location.reload();
        })
    }
}

function showMessageModal(message) {
    if (message != null) {
        sweetAlert("", message);
    }
}

/**
 * Method that have the default behavior of the $.ajax()
 * - When response code == (307 || 308) -> Redirect to url
 * - When response code == 500 && data.type == message -> show error message
 *
 * If the json contains "formValidation" (Object from the jqueryvalidation plugin), the default behavour will be executed
 */
function defaultAjax(ajaxJson) {
    // Array of erros
    var errorArray = [];

    if (ajaxJson.error != null) {
        var definedError = ajaxJson.error;
        errorArray[errorArray.length] = function (xhr) {
            //console.log('code statatus: ' + xhr.status);

            if (xhr.status != 307 && xhr.status != 308) {
                if (xhr.status == 500 && isValidJson(xhr.responseText)) {
                    return definedError(xhr, JSON.parse(xhr.responseText));
                } else {
                    return definedError(xhr);
                }
            }
        }
    }
    errorArray[errorArray.length] = function (xhr) {
        if (ajaxJson.form != undefined) {
            ajaxError(xhr, $(ajaxJson.form).data('validator'), $(ajaxJson.form).data('validatorJson'), ajaxJson.redirectInNewTab)
        } else {
            ajaxError(xhr, ajaxJson.formValidation, null, ajaxJson.redirectInNewTab);
        }
    };

    // Completing the ajaxJson
    // ajaxJson.error = errorArray;
    ajaxJson.error = function (xhr) {
        for (i = 0; i < errorArray.length; i++) {
            var keep = errorArray[i](xhr);
            if (keep != null && !keep) {
                break;
            }
        }
    };

    if (ajaxJson.data == null) {
        ajaxJson.data = {};
    }

    let encryptedKey = null;
    let isIframe = false;

    try {
        if ( window.location !== window.parent.location )
        {
            isIframe = true;
        }
    }
    catch (errorexception){

    }

    let encryptDataResult = encryptData(ajaxJson);

    encryptedKey = encryptDataResult.encrypted;

    if(encryptDataResult.encrypted) ajaxJson.data = encryptDataResult.data;

    ajaxJson.headers = addCsrfToJsonHeaders(Object.assign(
        { 'x-request-sender-type': 'ajax' },
        encryptedKey ? {'x-request-encrypted-type': 'true'} : {},
        isIframe ? { 'EQmsmpULSd' : '11RDSCiEUPCPJFebUY914bgbcc8qw8drlhyXxPhc' } : {}
        ));
 
    $.ajax(ajaxJson);

}

function utilSecurity(message = '', key = ''){
    var keyUTF8 = CryptoJS.enc.Utf8.parse(key);
    var srcs = CryptoJS.enc.Utf8.parse(message);
    var message = CryptoJS.AES.encrypt(srcs, keyUTF8,{mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
    return message.toString();
}

function encryptData(ajaxJson){

    let encryptedKey = null;
    if (typeof Yv4vDHAmzdWv !== 'undefined') {
        if(Yv4vDHAmzdWv != null && ajaxJson.type == "POST" && ajaxJson.encryptEnabled) encryptedKey = `${Yv4vDHAmzdWv}`;
    }
    let keyToEncryptedValue = ajaxJson.encryptKey;
    if(!keyToEncryptedValue) keyToEncryptedValue = "sOTRIWxTDVs";

    if(encryptedKey) return {
        encrypted : true,
        data :  {[keyToEncryptedValue] : utilSecurity(JSON.stringify(ajaxJson.data), encryptedKey), token : ajaxJson.data != null ? (ajaxJson.data.token || null) : null}
    }

    return {
        encrypted : false,
        data : ajaxJson.data
    }
}

jQuery.fn.defaultLoad = function (url, selector, success, requestType, data, error) {
    var self = $(this);
    defaultAjax({
        url: url,
        type: requestType != null ? requestType : 'GET',
        data: data,
        success: function (data) {
            self.html(selector ? $("<div>").append($.parseHTML(data)).find(selector) : data);
            if (success != null)
                success(data);
        },
        error: error
    });
};

/**
 * Method that interprets the response object and show the error if there is
 * @param xhr ajax response object
 * @param formValidation JqueryValidation object to show the errors
 */
function ajaxError(xhr, formValidation, validatorJson, redirectInNewTab) {
    //console.log("Llamando a funcion de error default: " + JSON.stringify(xhr));
    if ((xhr.status == 500 || xhr.status == 403) && isValidJson(xhr.responseText)) {
        var jsonError = JSON.parse(xhr.responseText);
        if (jsonError.type == 'message') {
            showErrorModal(jsonError.message);
        }
        if (jsonError.type == 'recharge') {
            showErrorModalWithRechargeButton(jsonError.message);
        }
        else if (jsonError.type == 'reloadPage') {
            location.reload();
        }
        else if (jsonError.type == 'errorWithLink') {
            showErrorModalWithRedirectLink(jsonError.message,jsonError.redirectLink);
        }
        else if (jsonError.type == 'formValidation' && formValidation != null) {
            delete jsonError.type;
            //console.log("mostrando errores de validacion: " + JSON.stringify(jsonError));
            formValidation.showErrors(jsonError);

            if (validatorJson != null) {
                validatorJson.invalidHandler();
            }
        }
    } else if (xhr.status == 307 || xhr.status == 308) {
        //console.log("redireccionando a: " + xhr.responseText);
        if(redirectInNewTab == null || !redirectInNewTab)
            window.location.href = xhr.responseText;
        else
            window.open(xhr.responseText,'_blank');
    } else if (xhr.status == 409) {
        window.location.href = system.contextPath + "/500";
    } else if (xhr.status == 401) {
        window.location.href = system.contextPath + "/login";
    } else {
        showErrorModal("Hubo un error, por favor int&eacute;ntalo nuevamente.");
    }
}

function clearElementsInContainer($container) {
    $container.find(':input').each(function () {
        switch (this.type) {
            case 'password':
            case 'text':
            case 'textarea':
            case 'number':
            case 'file':
            case 'select-one':
            case 'select-multiple':
                $(this).val('');
                $(this).blur();
                break;
            case 'checkbox':
            case 'radio':
                this.checked = false;
        }
    });
}

function disableElementsInContainer($container) {
    $container.find(':input').each(function () {
        $(this).prop('disabled', true);
        $(this).addClass('input-disabled');
    })
}

function browserUpdate() {

    var objappVersion = navigator.appVersion,
        objAgent = navigator.userAgent,
        objbrowserName = navigator.appName,
        objfullVersion = '' + parseFloat(navigator.appVersion),
        objBrMajorVersion = parseInt(navigator.appVersion, 10),
        objOffsetName, objOffsetVersion, ix,
        d = document.getElementById('browserOutdate');

    // In Chrome
    if ((objOffsetVersion = objAgent.indexOf("Chrome")) != -1) {
        objbrowserName = "Chrome";
        objfullVersion = objAgent.substring(objOffsetVersion + 7);
    }
    // In Microsoft internet explorer
    else if ((objOffsetVersion = objAgent.indexOf("MSIE")) != -1) {
        objbrowserName = "Microsoft Internet Explorer";
        objfullVersion = objAgent.substring(objOffsetVersion + 5);
    }

    // In Firefox
    else if ((objOffsetVersion = objAgent.indexOf("Firefox")) != -1) {
        objbrowserName = "Firefox";
    }
    // In Safari
    else if ((objOffsetVersion = objAgent.indexOf("Safari")) != -1) {
        objbrowserName = "Safari";
        objfullVersion = objAgent.substring(objOffsetVersion + 7);
        if ((objOffsetVersion = objAgent.indexOf("Version")) != -1)
            objfullVersion = objAgent.substring(objOffsetVersion + 8);
    }
    // For other browser "name/version" is at the end of userAgent
    else if ((objOffsetName = objAgent.lastIndexOf(' ') + 1) <
        (objOffsetVersion = objAgent.lastIndexOf('/'))) {
        objbrowserName = objAgent.substring(objOffsetName, objOffsetVersion);
        objfullVersion = objAgent.substring(objOffsetVersion + 1);
        if (objbrowserName.toLowerCase() == objbrowserName.toUpperCase()) {
            objbrowserName = navigator.appName;
        }
    }
    // trimming the fullVersion string at semicolon/space if present
    if ((ix = objfullVersion.indexOf(";")) != -1)
        objfullVersion = objfullVersion.substring(0, ix);
    if ((ix = objfullVersion.indexOf(" ")) != -1)
        objfullVersion = objfullVersion.substring(0, ix);

    objBrMajorVersion = parseInt('' + objfullVersion, 10);
    if (isNaN(objBrMajorVersion)) {
        objfullVersion = '' + parseFloat(navigator.appVersion);
        objBrMajorVersion = parseInt(navigator.appVersion, 10);
    }

    /*console.log(''
     +'Browser name  = '+objbrowserName+'<br>'
     +'Full version  = '+objfullVersion+'<br>'
     +'Major version = '+objBrMajorVersion+'<br>'
     +'navigator.appName = '+navigator.appName+'<br>'
     +'navigator.userAgent = '+navigator.userAgent+'<br>'
     )*/
    var version = objfullVersion;

    if (objfullVersion.length > 1) {
        version = objfullVersion.substring(0, objfullVersion.indexOf('.'));
    }

    if (objbrowserName == "Microsoft Internet Explorer") {
        if (version <= "9") {
            d.className += ' browserOutdate-active';
        }
    }
    if (objbrowserName == "Safari") {
        if (version <= "6") {
            d.className += ' browserOutdate-active';
        }
    }
    if (objbrowserName == "Firefox") {

        if (version <= "4") {
            d.className += ' browserOutdate-active';
        }
    }
    if (objbrowserName == "Chrome") {
        if (version <= "46") {
            d.className += ' browserOutdate-active';
        }
    }
    if (objbrowserName == "Opera") {
        if (version <= "11") {
            d.className += ' browserOutdate-active';
        }
    }
}

$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (this.value == '') {
            return;
        }
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

var createCookie = function (name, value, days) {
    var expires;
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toGMTString();
    }
    else {
        expires = "";
    }
    document.cookie = name + "=" + value + expires + "; path=/";
}

function getCookie(c_name) {
    if (document.cookie.length > 0) {
        c_start = document.cookie.indexOf(c_name + "=");
        if (c_start != -1) {
            c_start = c_start + c_name.length + 1;
            c_end = document.cookie.indexOf(";", c_start);
            if (c_end == -1) {
                c_end = document.cookie.length;
            }
            return unescape(document.cookie.substring(c_start, c_end));
        }
    }
    return "";
}

jQuery.fn.enableButton = function () {
    var self = $(this);
    self.removeClass('btn-disabled').attr('disabled', false);
    return false;
};

jQuery.fn.disableButton = function () {
    var self = $(this);
    self.addClass('btn-disabled').attr('disabled', true);
    return false;
};

jQuery.fn.loading = function (loadingText, type, lock) {
    // Verify if it was initialized, else initialize
    var jsonLoading = null;
    if ($(this).data("loading") == null) {
        jsonLoading = {
            state: 'normal',
            loadingText: loadingText != null ? loadingText : 'Espera...',
            type: (type != null) ? type : 1
        };
        $(this).data("loading", jsonLoading);
    } else {
        jsonLoading = $(this).data("loading");
    }

    // Change to loading only if the state is in normal
    if (jsonLoading.state == 'normal') {
        jsonLoading.state = 'loading';
        // Save the current innerhtml
        jsonLoading.html = $(this).html();
        // Remove current innerHtml
        $(this).empty();
        // Disable the button
        $(this).prop('disabled', true);
        // Append the loading div
        if (jsonLoading.type == 1) {
            var loadingDiv = $('<div></div>').addClass('loading-ajax-visible');
            loadingDiv.append($('<div class="sk-fading-circle"><div class="sk-circle1 sk-circle"><span></span></div>' +
                '<div class="sk-circle2 sk-circle"></div><div class="sk-circle3 sk-circle"><span></span></div>' +
                '<div class="sk-circle4 sk-circle"></div><div class="sk-circle5 sk-circle"><span></span></div>' +
                '<div class="sk-circle6 sk-circle"></div><div class="sk-circle7 sk-circle"><span></span></div>' +
                '<div class="sk-circle8 sk-circle"></div><div class="sk-circle9 sk-circle"><span></span></div>' +
                '<div class="sk-circle10 sk-circle"></div><div class="sk-circle11 sk-circle"><span></span></div>' +
                '<div class="sk-circle12 sk-circle"></div></div>'));
            $(this).append(loadingDiv);
            $(this).append($('<span class="button-inner-text">' + jsonLoading.loadingText + '</span>'))
        } else {
            var loadingDivBounce = $('<div></div>').addClass('loading-ajax-visible');
            loadingDivBounce.append(
                $('<div class="spinner">' +
                    '<div class="bounce1"></div>' +
                    '<div class="bounce2"></div>' +
                    '<div class="bounce3"></div>' +
                    '</div>')
            );
            $(this).append(loadingDivBounce);
        }
    }
    $(this).data("loading", jsonLoading);
};

jQuery.fn.unloading = function () {
    var jsonLoading = $(this).data("loading");
    if (jsonLoading && jsonLoading.state == 'loading') {
        jsonLoading.state = 'normal';
        // Enable the button
        $(this).prop('disabled', false);
        // Remove the loading div and append saved html
        $(this).html(jsonLoading.html);
    }
    $(this).data("loading", jsonLoading);
};

jQuery.fn.loadingContent = function () {
    var self = $(this);
    self.show();
    self.html($('<div class="loading-red"><div class="sk-fading-circle"><div class="sk-circle1 sk-circle"><span></span></div>' +
        '<div class="sk-circle2 sk-circle"></div><div class="sk-circle3 sk-circle"><span></span></div>' +
        '<div class="sk-circle4 sk-circle"></div><div class="sk-circle5 sk-circle"><span></span></div>' +
        '<div class="sk-circle6 sk-circle"></div><div class="sk-circle7 sk-circle"><span></span></div>' +
        '<div class="sk-circle8 sk-circle"></div><div class="sk-circle9 sk-circle"><span></span></div>' +
        '<div class="sk-circle10 sk-circle"></div><div class="sk-circle11 sk-circle"><span></span></div>' +
        '<div class="sk-circle12 sk-circle"></div><span></span></div></div>'));
};

jQuery.fn.unloadingContent = function () {
    var self = $(this);
    self.html('').hide();
};

function addCsrfToJsonHeaders(json) {
    json['x-csrf-token'] = getCookie('fibonacci13');
    return json;
}

function gradientOfHexaColors(color1Param, color2Param, ratioParam){
    var color1 = color1Param.replace('#', '');
    var color2 = color2Param.replace('#', '');
    var ratio = ratioParam != null ? ratioParam : 0.5;
    var hex = function(x) {
        x = x.toString(16);
        return (x.length == 1) ? '0' + x : x;
    };

    var r = Math.ceil(parseInt(color1.substring(0,2), 16) * ratio + parseInt(color2.substring(0,2), 16) * (1-ratio));
    var g = Math.ceil(parseInt(color1.substring(2,4), 16) * ratio + parseInt(color2.substring(2,4), 16) * (1-ratio));
    var b = Math.ceil(parseInt(color1.substring(4,6), 16) * ratio + parseInt(color2.substring(4,6), 16) * (1-ratio));

    return '#' + hex(r) + hex(g) + hex(b);
}

