//TODO: Falta que los select no entren dentro de la validacion.
// Falta que al pegar(ctrl v) se restrinja el tama�o y el regex

$(document).ready(function () {
    try {
        if ($.fn.datepicker && $.fn.datepicker.defaults) {
            $.fn.datepicker.defaults.language = 'es';
        }
        if (jQuery.validator) {
            jQuery.validator.addMethod(
                "regex",
                function (value, element, param) {
                    var re = new RegExp(param.regex);
                    return this.optional(element) || re.test(value);
                },
                function (param, element) {
                    return param.message;
                });
        }
    } catch (err) {
        console.log(err)
    }
});

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


function thousandWithSpace(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
}

function positionFooter(footerHeight, footerTop) {
    var $footer = $("#footer");
    footerHeight = $footer.height();
    footerTop = ($(window).scrollTop() + $(window).height() - footerHeight) + "px";

    if (($('.general-wrap').outerHeight(true) + footerHeight) < $(window).height()) {
        $footer.css({
            bottom: 0,
            left: 0,
            position: "absolute",
            right: 0
        });
    } else {
        $footer.css({
            position: "static"
        });
    }

}

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
        $(this).numeric({negative: false, decimal: false});

        if (restricted) {
            $(this).on("keypress", function (e) {
                if(e.charCode == 0){
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
    });
};

function formatCurrency(total) {
    var neg = false;
    if(total < 0) {
        neg = true;
        total = Math.abs(total);
    }
    return (neg ? '-S/ ' : 'S/ ') + parseFloat(total, 10).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1 ").toString();
}

function formatCurrency(total, currency) {
    var neg = false;
    if(total < 0) {
        neg = true;
        total = Math.abs(total);
    }
    return (neg ? currency + ' ' : currency + ' ') + parseFloat(total, 10).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1 ").toString();
}

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
        $(this).numeric({negative: false});

        if (restricted) {
            $(this).on("keypress", function (e) {
                if(e.charCode == 0){
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
    });
};

/** Allow only letters for an text box
 *
 * @param validRegex
 * @param restricted
 * @param minCharacters
 * @param maxCharacters
 * @returns {*}
 */
jQuery.fn.forceLettersOnly = function (validRegex, restricted, minCharacters, maxCharacters) {
    return this.each(function () {
        $(this).off("keypress");
        if (restricted) {
            $(this).on("keypress", function (e) {
                // Little fix for firefox(unfortunately firefox enters here, instead of chrome)
                if(e.charCode == 0){
                    return true;
                }

                var key = String.fromCharCode(!e.charCode ? e.which : e.charCode);
                var regex = new RegExp(validRegex);
                if (!regex.test(key)) {
                    e.preventDefault();
                    return false;
                }

                var charactersLength = $(this).val().length + 1;
                if (maxCharacters != null && charactersLength > maxCharacters) {

                    // Validate tha the text is not selected
                    // if($(this).data("text-selected") == window.getSelection().toString()){
                    //     return true;
                    // }

                    $(this).val($(this).val().substring(0, maxCharacters));
                    e.preventDefault();
                    return false;
                }
            });
            // $(this).select(function (e) {
            //     debugger;
            //     $(this).data("text-selected", window.getSelection().toString());
            // });
        }
    });
};

/** Create a hidden input element, and append it to the form:
 *
 * @param theForm
 * @param key
 * @param value
 */
function addHiddenToForm(theForm, key, value) {
    console.log('agregando ' + value)
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
function createFormValidationJson(jsonValidator, formElement) {
    var jsonFormValidation = {
        ignore: ".ignore_validation",
        rules: {},
        messages: {},
        restrictions: {},
        highlight: function (element) {
            // $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
            $(element).closest('.form-group').addClass('has-error');
        },
        unhighlight: function (element) {
            // $(element).closest('.form-group').removeClass('has-error').addClass('has-success');
            $(element).closest('.form-group').removeClass('has-error');
        },
        errorClass: "help-block help-block-error",
        errorElement: "span",
        errorPlacement: function (error, element) {
            error.appendTo(element.closest('.form-group').find(".errorContainer"));
        },
        submitHandler: function (form) {
            form.submit();
        },
        invalidHandler: function (event, validator) {
            $($(formElement).find('.has-error')[0]).find('.form-control').focus();
            $($(formElement).find('.has-error')[0]).find('.form-control').triggerHandler("focus");
        },
        formElement: formElement
    };

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
            if (jsonValidatorElement.validRegex != null) {
                jsonFormValidation.rules[stringKey].regex = {
                    regex: jsonValidatorElement.validRegex,
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
                $(jsonFormValidation.formElement).find('#' + stringKey).forceIntegerOnly(restriction.restricted, rule.min, rule.max);
            } else if (restriction.type == 'Double') {
                $(jsonFormValidation.formElement).find('#' + stringKey).forceDoubleOnly(restriction.restricted, rule.min, rule.max);
            } else if (restriction.type == 'String') {
                $(jsonFormValidation.formElement).find('#' + stringKey).forceLettersOnly(restriction.validRegex, restriction.restricted, rule.minlength, rule.maxlength);
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
            title: "",
            html: true,
            text: errorMessage,
            imageUrl: "/img/warning.png",
        });
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

    if(ajaxJson.showLoading == null){
        ajaxJson.showLoading = true;
    }

    // Array of erros
    var errorArray = [];

    if (ajaxJson.error != null) {
        var definedError = ajaxJson.error;
        errorArray[errorArray.length] = function (xhr) {
            console.log('code statatus: ' + xhr.status);
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
            ajaxError(xhr, $(ajaxJson.form).data('validator'), $(ajaxJson.form).data('validatorJson'))
        } else {
            ajaxError(xhr, ajaxJson.formValidation);
        }
    };

    // Completing the ajaxJson
    // ajaxJson.error = errorArray;
    ajaxJson.error = function (xhr) {
        if (ajaxJson.showLoading) {
            App.unblockUI();
        }

        for (i = 0; i < errorArray.length; i++) {
            var keep = errorArray[i](xhr);
            if (keep != null && !keep) {
                break;
            }
        }
    };

    // Success
    var definedSuccess = ajaxJson.success;
    ajaxJson.success = function (data, textStatus, jqXHR) {
        if (ajaxJson.showLoading) {
            App.unblockUI();
        }

        definedSuccess(data, textStatus, jqXHR);

        /*mask datatable*/
        var table = $('.table').each(function () {
            getDatatable($(this), $(this).hasClass('multiselect'));
        });
    };


    if (ajaxJson.data == null) {
        ajaxJson.data = {};
    }

    ajaxJson.headers = addCsrfToJsonHeaders({
        'x-request-sender-type': 'ajax'
    });

    if (ajaxJson.showLoading) {
        App.blockUI({
            boxed: true,
            zIndex: 999999
        });
    }
    $.ajax(ajaxJson);
}


function getDatatable(obj, isMultiselect = false) {
    if (obj.length) {
        obj.each(function (i) {
            var t = $(this);
            var hasThead = t.find('thead').length;
            var hasTbody = t.find('tbody').length;
            if (!t.hasClass('dataTable') && hasThead && hasTbody) {
                var options = {
                    responsive: true,
                    aLengthMenu: [
                        [25, 50, 100, 200, -1],
                        [25, 50, 100, 200, "Todos"]
                    ],
                    iDisplayLength: -1,
                    aaSorting: [],
                    bFilter: false,
                    bLengthChange: false,
                    bPaginate: false,
                    "autoWidth": false,
                    "showNEntries": false,
                    "language": {
                        "zeroRecords": "No hay resultado"
                    }
                };
                if (isMultiselect) {
                    options.buttons = [
                        'selectAll',
                        'selectNone'
                    ];
                    options.language.buttons = {
                        selectAll: "Seleccionar todos",
                        selectNone: "Deseleccionar todos"
                    };
                    options.columnDefs = [{
                        targets: 1,
                        data: null,
                        defaultContent: '',
                        orderable: false,
                        className: 'select-checkbox'
                    }];
                    options.select = {
                        style: 'multi',
                        selector: 'td:nth-child(2)',
                        blurable: false
                    };
                }
                t.DataTable(options);
            }
        });
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
function ajaxError(xhr, formValidation, validatorJson) {
    console.log("Llamando a funcion de error default: " + JSON.stringify(xhr));
    if ((xhr.status == 500 || xhr.status == 403) && isValidJson(xhr.responseText)) {
        var jsonError = JSON.parse(xhr.responseText);
        if (jsonError.type == 'message') {
            showErrorModal(jsonError.message);
        } else if (jsonError.type == 'formValidation' && formValidation != null) {
            delete jsonError.type;
            console.log("mostrando errores de validacion: " + JSON.stringify(jsonError));
            formValidation.showErrors(jsonError);

            if (validatorJson != null) {
                validatorJson.invalidHandler();
            }
        }
    } else if (xhr.status == 307 || xhr.status == 308) {
        console.log("redireccionando a: " + xhr.responseText);
        window.location.href = xhr.responseText;
    } else if (xhr.status == 409) {
        window.location.href = system.contextPath + "/500";
    } else if (xhr.status == 401) {
        window.location.href = system.contextPath + "/login";
    } else {
        showErrorModal('Se produjo un error inesperado, <br/>por favor int&eacute;ntalo nuevamente o cont&aacute;ctanos.');
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

jQuery.fn.loading = function (loadingText) {
    // Verify if it was initialized, else initialize
    var jsonLoading = null;
    if ($(this).data("loading") == null) {
        jsonLoading = {
            state: 'normal',
            loadingText: loadingText != null ? loadingText : 'Espera...'
        }
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
        var loadingDiv = $('<div></div>').addClass('loading-ajax-visible');
        loadingDiv.append($('<div class="sk-fading-circle"><div class="sk-circle1 sk-circle"><span></span></div>' +
            '<div class="sk-circle2 sk-circle"><span></span></div><div class="sk-circle3 sk-circle"><span></span></div>' +
            '<div class="sk-circle4 sk-circle"><span></span></div><div class="sk-circle5 sk-circle"><span></span></div>' +
            '<div class="sk-circle6 sk-circle"><span></span></div><div class="sk-circle7 sk-circle"><span></span></div>' +
            '<div class="sk-circle8 sk-circle"><span></span></div><div class="sk-circle9 sk-circle"><span></span></div>' +
            '<div class="sk-circle10 sk-circle"><span></span></div><div class="sk-circle11 sk-circle"><span></span></div>' +
            '<div class="sk-circle12 sk-circle"><span></span></div></div>'));
        $(this).append(loadingDiv)
        $(this).append($('<span class="button-inner-text">' + jsonLoading.loadingText + '</span>'))
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

function addCsrfToJsonHeaders(json){
    json['x-csrf-token'] = getCookie('fibonacci13');
    return json;
}

var UtilModule = (function () {
    return {
        reindexOnReload: function () {
            var tableForm =  $('#tableResults') ;
            var dataRows = $('td:first-child');
            tableForm.on('order.dt',function(){
                    var paginationFirstValue;
                    if($('.tablefilter').data('tablefilter') != null){
                        paginationFirstValue =  $('.tablefilter').data('tablefilter').offset + 1;
                    }else{
                        paginationFirstValue =  parseInt(dataRows[0].textContent,10);
                    }
                    var firstColumn = $('td:first-child');
                    for(var i = 0;i< firstColumn.length;i++)
                        firstColumn[i].textContent = (paginationFirstValue+i);
            });
        }
    };
})();