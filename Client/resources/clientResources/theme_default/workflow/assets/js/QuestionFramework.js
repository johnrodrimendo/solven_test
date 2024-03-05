function QuestionFramework(questionContainer) {

    this.TYPE_SELFEVALUATION = 1;
    this.TYPE_EVALUATION = 2;
    this.TYPE_COMPARISON = 3;

    this.SELFEVALUATION_URL = "autoevaluacion";
    this.EVALUATION_URL = "evaluacion";
    this.COMPARISON_URL = "compara";

    var self = this;
    this.token;
    this.type;
    this.currentQuestion;
    this.categoryUrl;
    this.agent;
    this.externalParams;
    this.questionContainer = questionContainer;
    this.errorContainer;
    this.errorContainerRetry;
    // Variables internas
    this.urlUpdateAmountInstallments;
    this.loggable = false;
    this.brandingPrimaryColor;
    this.brandingSecundaryColor;
    this.branding = false;
    this.globalUserNameForZendesk = null;
    this.globalUseeEmailForZendesk = null;
    this.showZendeskChatTimeout = 2 * 60 * 1000;
    this.pushNotificationUserId = null;
    this.pushNotificationsQuestionsToTrigger = [];
    this.showCustomOffer = false;
    this.showLabelInformation = true;

    this.goToQuestion = function (questionId) {
        self.currentQuestion = questionId;
        self.questionContainer.html('<div class="process-loading-container circle-progress-question question-waiting"><div class="loading-content"><div class="sk-circle"><div class="sk-circle1 sk-child"></div><div class="sk-circle2 sk-child"></div><div class="sk-circle3 sk-child"></div><div class="sk-circle4 sk-child"></div><div class="sk-circle5 sk-child"></div><div class="sk-circle6 sk-child"></div><div class="sk-circle7 sk-child"></div><div class="sk-circle8 sk-child"></div></div> <span class="loading-text">Cargando<span>.</span><span>.</span><span>.</span></span></div></div>');
        updateBranding();
        setTimeout(function () {
            self.sendBeaconPushNotification(null);
        }, 15 * 1000);
        var json = {
            url: system.contextPath + "/" + self.getUrlByType() + "/question/" + questionId,
            type: 'GET',
            data: {},
            success: function (data) {
                self.replaceQuestionContent(data);

            },
            error: function (xhr, data) {
                let showPrincipalContainer = true;
                self.questionContainer.html("");
                if (xhr.status == 510 && self.errorContainerRetry != null){
                    self.errorContainerRetry.show();
                    showPrincipalContainer = false;
                }
                if (self.errorContainer != null && showPrincipalContainer)
                    self.errorContainer.show();
                return false;
            }
        };
        if (self.externalParams != null)
            json.data.externalParams = self.externalParams;

        if (self.token != null)
            json.data.token = self.token;
        else {
            if (self.agent != null)
                json.data.agent = self.agent;
            if (self.categoryUrl != null)
                json.data.categoryUrl = self.categoryUrl;
        }

        self.defaultProcessQuestionAjax(json);

        self.sendGAClientId();

    };

    this.replaceQuestionContent = function (data) {


        if (self.errorContainer != null)
            self.errorContainer.hide();
        if (self.errorContainerRetry!= null)
            self.errorContainerRetry.hide();
        try {
            self.questionContainer.html(data);
        } catch (e) {
            console.error(e)
        }
        // var loanProcessTop =Number($(".loan-process").css("top").replace("px",""));
        // var loanProccesContainer = $(".loan-process .container").height();
        // var windowInnerHeight = window.innerHeight / 2;
        // var loanProcessSum = loanProcessTop + loanProccesContainer;
        // console.log("loanProcessTop", loanProcessTop);
        // console.log("loanProccesContainer", loanProccesContainer);
        // console.log("windowInnerHeight", windowInnerHeight);
        // console.log("loanProcessSum", loanProcessSum);
        //
        // if (loanProcessSum > windowInnerHeight) {
        //     console.log("entro al IF")
        //   $(".loan-process .bottom-footer").css("position", "relative");
        // }
        // else {
        //     console.log("entro al ELSE")
        //    $(".loan-process .bottom-footer").css("position", "fixed");
        // }

        // Put modals outside the container
        $('#questionModals').html('');
        self.questionContainer.find('.modal').each(function () {
            $(this).detach().appendTo('#questionModals');
        });

        // Process configuration
        var configurationElement = self.questionContainer.find("#questionConfiguration");
        if (configurationElement != null) {
            self.globalUserNameForZendesk = configurationElement.data("user-name");
            self.globalUserEmailForZendesk = configurationElement.data("user-email");
            self.showCustomOffer = configurationElement.data("show-custom-offer");
            self.showLabelInformation = configurationElement.data("show-label-information");
            self.showHideAgent(configurationElement.data("show-agent"));
            self.showHideUpdateModal(configurationElement.data("show-application-update-modal"), configurationElement.data("show-offer-update-modal"));
            //self.showHideToky(configurationElement.data("show-toky"));
            // self.paintLateralSection(configurationElement.data("section"));
            self.showGoBack(configurationElement.data("show-go-back"));
            self.showPreApproved(configurationElement.data("show-pre-approved"), configurationElement.data("show-pre-approved-message"));
            self.showOfferMedal(configurationElement.data("show-offer-modal"));

            if (configurationElement.data("show-name-email")) {
                self.showEmailName(configurationElement.data("user-email"), configurationElement.data("user-name"));
            }

            if (configurationElement.data("is-banbif") != null) {
                self.paintProgressBanBif(configurationElement.data("question-catgory"));
            } else {
                self.getEvaluationPercentageAsync(configurationElement.data("question-catgory"));
                // if (configurationElement.data("percentage") != null)
                //     self.paintProgress(configurationElement.data("percentage"), configurationElement.data("question-catgory"));
                // else
                //     self.paintProgress(0, 1);
            }
            if (configurationElement.data("force-vehicle-modal-show") != null && configurationElement.data("force-vehicle-modal-show"))
                self.openUpdateModal();
            if (configurationElement.data("hotjar-id") != null)
                this.tagRecording(configurationElement.data("hotjar-id"));

            if (self.currentQuestion === 50 && $('.q50 .offer-container').length === 0) {
                self.shouldShowZendeskChat(false);
            } else {
                if (configurationElement.data("showZendeskWidget") != null) {
                    console.log('shouldShowZendeskChat', configurationElement.data("showZendeskWidget"));
                    self.shouldShowZendeskChat(configurationElement.data("showZendeskWidget"));
                }
                if (configurationElement.data("showZendeskWidget") != null && configurationElement.data("showZendeskWidget") === true) {
                    console.log('timeoutCountDown');
                    self.timeoutCountDown();
                }
            }
            if (self.currentQuestion === 86) {
                var q86LoanProcess = $('.loan-process');
                var q86Logo = $('.logo');
                console.log(q86Logo);
                q86LoanProcess.addClass('q86-loan-process');
                q86Logo.addClass('q86-logo');
            } else {
                var q86LoanProcess = $('.loan-process');
                var q86Logo = $('.logo');
                q86LoanProcess.removeClass('q86-loan-process');
                q86Logo.removeClass('q86-logo');

            }
            if (self.currentQuestion === 95) {
                var q95LoanProcess = $('.loan-process');
                q95LoanProcess.addClass('q95-loan-process');
            } else {
                var q95LoanProcess = $('.loan-process');
                q95LoanProcess.removeClass('q95-loan-process');

            }

            if (self.currentQuestion === 135) {
                $(".loan-process").addClass("q135-loan-process");
                $(".bottom-footer").addClass("bottom-q-135");
                $(".bottom-q-135").css("display", "none");
            } else {
                $(".bottom-footer").removeClass("bottom-q-135");
                $(".loan-process").removeClass("q135-loan-process");
            }
            if (self.currentQuestion === 137) {
                $(".bottom-footer").addClass("bottom-q-137");
            } else {
                $(".bottom-footer").removeClass("bottom-q-137");
            }
            if (self.currentQuestion === 138) {
                $(".bottom-footer").addClass("bottom-q-138");
            } else {
                $(".bottom-footer").removeClass("bottom-q-138");
            }

            // show offer update modal only in q50 AND only after calendar
            if (self.currentQuestion === 50 && $('.q50 .offer-container').length === 1) {
                $(".edit-amount-box").show();
            } else {
                $(".edit-amount-box").hide();
            }

            // Push notifications
            if ((configurationElement.data("question-catgory") === 4 && self.currentQuestion === 50) || configurationElement.data("question-catgory") === 5 || configurationElement.data("question-catgory") === 6 || configurationElement.data("question-catgory") === 7) {
                self.pushNotificationsQuestionsToTrigger.push(self.currentQuestion); // agrego dinamicamente las preguntas de ciertas categorias
            }
            if (configurationElement.data("question-catgory") === 7) {
                self.pushNotificationUserId = null;
            }
            if (self.isMobile.any() !== null) {
                setTimeout(function () {
                    if (configurationElement.data("question-catgory") === 7) {
                        self.pushNotificationUserId = null;
                    }
                    self.sendBeaconPushNotification(self.pushNotificationUserId);
                }, 15 * 1000);
            }

            if (configurationElement.data("document-title")) {
                document.title = customDocumentTitle;
            }
            configurationElement.remove();
        }
        // Enter to submit
        self.initializeFormInputs();

        if (self.questionContainer.find('form').find('input[type=text]').length == 1) {
            self.questionContainer.find('form').prepend($('<input type="text" name="dummyInput" style="display: none"/>'))
        }


        // Initialize components
        var $offer_carousel = $("#offer-carousel");
        $offer_carousel.doOnce(function () {
            $offer_carousel.owlCarousel({
                items: 3,
                navText: ['<i class="icon icon-control-prev"></i>', '<i class="icon icon-control-next"></i>'],
                responsiveClass: true,
                responsive: {
                    0: {
                        items: 1,
                        nav: true
                    },
                    568: {
                        items: 2,
                        nav: true
                    },
                    1200: {
                        items: 3,
                        nav: true
                    }
                }
            });
        });

        $("#reason-carousel").doOnce(function () {
            $('#reason-carousel').owlCarousel({
                items: 4,
                navText: ['<i class="icon icon-control-prev"></i>', '<i class="icon icon-control-next"></i>'],
                responsiveClass: true,
                responsive: {
                    0: {
                        items: 1,
                        nav: true
                    },
                    600: {
                        items: 1,
                        nav: true
                    },
                    1000: {
                        items: 4,
                        nav: true
                    }
                }
            });
        });
        $('label.custom-check').on('change', '.real-check', function () {
            $(this).checked();
        });

        $('label.h-input-check').on('change', '.checkbox', function () {
            $(this).checkbox();
        });

        $("select").each(function () {
            $(this).select2({
                minimumResultsForSearch: -1,
                placeholder: $(this).data('placeholder'),
                width: $(this).data('width') + 'px'
            });
        });

        $('.input-radio, .h-input-radio').radio();

        if (self.loggable) {
            $('#logDiv').remove();
            $('<div id="logDiv" style="position: fixed;z-index: 99999999;right:0;bottom:0; font-size: 12px; background:#e9e9e9;">Pregunta: ' + self.currentQuestion + '</div>').appendTo('body');
        }
        updateBranding();
        self.updateETAmessage(self.currentQuestion, configurationElement.data("question-catgory"));
    };
    this.timeoutCountDown = function () {
        var t;
        resetTimer();
        window.onloadstart = resetTimer;
        window.onmousemove = resetTimer;
        window.onmousedown = resetTimer;
        window.ontouchstart = resetTimer;
        window.onclick = resetTimer;
        window.onkeypress = resetTimer;
        window.addEventListener('scroll', resetTimer, true);


        function resetTimer() {
            clearTimeout(t);
            if (sessionStorage['wasContacted'] === undefined)
                t = setTimeout(self.contactZendesk, self.showZendeskChatTimeout);

        }
    };

    this.contactZendesk = function () {
        if (typeof zE !== 'undefined') {
            console.log('contactZendesk');
            sessionStorage['wasContacted'] = 'true';
            zE('webWidget', 'setLocale', 'es');
            zE('webWidget', 'chat:send', 'Solven, solicitud de asesoría');
            zE('webWidget', 'identify', {
                name: self.globalUserNameForZendesk,
                email: self.globalUserEmailForZendesk
            });
            zE('webWidget', 'open');
        }
    };

    this.shouldShowZendeskChat = function (shouldShow) {
        if (typeof zE !== 'undefined') {
            if (shouldShow) {
                zE('webWidget', 'setLocale', 'es');
                zE('webWidget', 'close');
                zE('webWidget', 'updateSettings', {
                    webWidget: {
                        chat: {
                            suppress: false
                        }
                    }
                });
            } else {
                zE('webWidget', 'updateSettings', {
                    webWidget: {
                        chat: {
                            suppress: true
                        }
                    }
                });
            }
        }
    };


    this.updateETAmessage = function (pageN, category) {
        console.log("category - ", category);
        console.log("pageN - ", pageN);
        //var message = 'Quedan<br/>3 min.';
        var message;
        /*if(pageN === 83)
            message = '&iexcl;Con esto <br/>Finalizamos&excl;';*/
        if ((category != null && category > 0) && (category === 6) || isEfectivoAlToque) {
            message = '';
        } else if ((category === 1 || category === 2)) {
            if(self.showLabelInformation){
                if(isBancoAztecaEntity) message = 'Quedan<br/>3 min.';
                else message = 'Quedan<br/>5 min.';
            }
            else  message = '';
        } else if ((category === 3)) {
            if(self.showLabelInformation) message = 'Quedan<br/>4 min.';
            else  message = '';
        } else if ((category === 4)) {
            if(self.showLabelInformation){
                if(isBancoAztecaEntity) message = 'Quedan<br/>2 min.';
                else message = 'Quedan<br/>3 min.';
            }
            else  message = '';
        } else if ((category === 5)) {
            if(self.showLabelInformation){
                if(pageN === 175){
                    message = '';
                }
                else if (pageN === 83 || pageN === 135) {
                    message = 'Quedan<br/>1 min.';
                } else {
                    if(isBancoAztecaEntity) message = 'Quedan<br/>1 min.';
                    else message = 'Quedan<br/>2 min.';
                }
            }
            else  message = '';
        } else if ((category === 7)) {
            if(self.showLabelInformation){
                if (pageN === 62) {
                    if ($(".q62").hasClass("q62-paso1")) {
                        console.log("paso1")
                        message = '&iexcl;Con esto <br/>terminamos&excl;';
                    } else {
                        console.log("paso2")
                        message = '&iexcl;Listo&excl;';
                    }
                } else {
                    message = '';
                }
            }
            else  message = '';
        } else if ((category === 8)) {
            if(self.showLabelInformation){
                if (pageN === 9) {
                    if(isBancoAztecaEntity) message = 'Quedan<br/>2 min.';
                    else message = 'Quedan<br/>3 min.';
                } else {
                    message = '';
                }
            }
            else  message = '';
        }
        $('.estimated').eq(0).html(message);
    }

    this.initializeFormInputs = function () {
        // Enter to submit
        self.questionContainer.find('.input-outline, .selec2-item').on('keyup select2:select', function (event) {
            if (event.keyCode == 13) {
                $(this).closest('.questions-section').find('.button').click();
                event.preventDefault();
                return false;
            }
        });
    };

    this.refreshQuestion = function () {
        self.goToQuestion(self.currentQuestion);
    };

    this.backwards = function () {
        var json = {
            url: system.contextPath + "/" + self.getUrlByType() + "/backwards",
            type: 'POST',
            data: {},
            error: function (xhr, errorJson) {
                if (errorJson != null && errorJson.constructor == {}.constructor && errorJson.type == "reloadPage") {
                    return true;
                }else{
                    showErrorModal("No puedes volver atrás");
                    return false;
                }
            }
        };
        if (self.token != null)
            json.data.token = self.token;

        self.defaultProcessQuestionAjax(json);
    };

    this.updateLoanApplication = function (ajaxJson) {
        ajaxJson.url = system.contextPath + "/" + self.getUrlByType() + "/updateLoanApplication";

        if (ajaxJson.data == null)
            ajaxJson.data = {};
        if (self.token != null)
            ajaxJson.data.token = self.token;

        self.defaultProcessQuestionAjax(ajaxJson);
    };

    this.updateLoanApplicationVehicle = function (ajaxJson) {
        ajaxJson.url = system.contextPath + "/" + self.getUrlByType() + "/updateLoanApplicationVehicle";

        if (ajaxJson.data == null)
            ajaxJson.data = {};
        if (self.token != null)
            ajaxJson.data.token = self.token;

        self.defaultProcessQuestionAjax(ajaxJson);
    };

    this.updateLoanOffer = function (ajaxJson) {
        ajaxJson.url = system.contextPath + "/" + self.getUrlByType() + "/updateLoanOffer";

        if (ajaxJson.data == null)
            ajaxJson.data = {};
        if (self.token != null)
            ajaxJson.data.token = self.token;

        self.defaultProcessQuestionAjax(ajaxJson);
    };

    this.ajaxToCurrentQuestionController = function (ajaxJson, urlMethod) {
        ajaxJson.url = system.contextPath + "/" + self.getUrlByType() + "/question/" + self.currentQuestion + "/" + urlMethod;

        if (ajaxJson.data == null)
            ajaxJson.data = {};
        if (self.token != null) {
            if (ajaxJson.data.constructor == FormData) {
                ajaxJson.data.append("token", self.token);
            } else if (ajaxJson.data.constructor == {}.constructor) {
                ajaxJson.data.token = self.token;
            }
        } else if (self.externalParams != null) {
            if (ajaxJson.data.constructor == FormData) {
                ajaxJson.data.append("externalParams", self.externalParams);
            } else if (ajaxJson.data.constructor == {}.constructor) {
                ajaxJson.data.externalParams = self.externalParams;
            }
        }

        //let encryptedData = encryptData(ajaxJson);
        //ajaxJson.isEncrypted = encryptedData.encrypted;
        //if(encryptedData.encrypted) ajaxJson.data = encryptedData.data;

        self.defaultProcessQuestionAjax(ajaxJson);
    };

    this.defaultProcessQuestionAjax = function (ajaxJson) {
        if (ajaxJson.showLoadingBar == null || ajaxJson.showLoadingBar)
            if (self.branding && self.brandingPrimaryColor != null)
                addMask(self.brandingPrimaryColor, gradientOfHexaColors(self.brandingPrimaryColor, '#FFFFFF'));
            else
                addMask();

        //Activate loading button
        if (ajaxJson.button != null) {
            ajaxJson.button.loading('', 2);
        }

        var successArray = [];
        successArray[successArray.length] = function (data) {

            if (ajaxJson.button != null) {
                try {
                    ajaxJson.button.unloading();
                } catch (e) {}
            }

            if (isJsonString(data)) {
                var jsonData = JSON.parse(data);
                if (jsonData.token != undefined) {
                    self.token = jsonData.token;
                    window.history.pushState(null, null, system.contextPath + "/" + (self.type == self.TYPE_EVALUATION ? self.categoryUrl + "/" : "") + self.getUrlByType() + "/" + self.token);
                }
                if (jsonData.type != undefined && jsonData.type == 'processQuestion') {
                    if (jsonData.goto != undefined) {

                        if (successBeforeChangeQuestion != null) {
                            successBeforeChangeQuestion(data);
                        }

                        self.goToQuestion(jsonData.goto);
                        return false;
                    }
                }
            }
            unMask();
        };

        if (ajaxJson.success != null) {
            var definedSuccess = ajaxJson.success;
            successArray[successArray.length] = function (data) {
                return definedSuccess(data);
            }
        }

        var successBeforeChangeQuestion = null;
        if (ajaxJson.successBeforeChangeQuestion != null) {
            successBeforeChangeQuestion = ajaxJson.successBeforeChangeQuestion;
        }

        ajaxJson.success = function (data) {
            for (i = 0; i < successArray.length; i++) {
                var keep = successArray[i](data);
                if (keep != null && !keep) {
                    break;
                }
            }
        };

        var definederror;
        if (ajaxJson.error != null) {
            definederror = ajaxJson.error;
        }
        ajaxJson.error = function (xhr, data) {
            unMask();
            if (ajaxJson.button != null) {
                try {
                    ajaxJson.button.unloading();
                } catch (e) {}
            }
            // If its timeout, show custom message
            if (xhr.status == 503){
                showErrorModal("Hubo un error, por favor recarga la pagina e int&eacute;ntalo nuevamente.");
                if (definederror != null)
                    definederror(xhr, data);
                return false;
            }else{
                if (definederror != null)
                    return definederror(xhr, data);
            }
        };

        // This is for the first circle progress showed
        var definedComplete;
        if (ajaxJson.complete !== null) {
            definedComplete = ajaxJson.complete;
        }

        ajaxJson.complete = function () {
            setTimeout(function () {
                $(".circle-progress").hide();
            }, 1000);
            if (definedComplete != null)
                return definedComplete();
        };

        defaultAjax(ajaxJson);
    };

    this.init = function () {
        if (self.errorContainer != null)
            self.errorContainer.hide();
        if (self.errorContainerRetry != null)
            self.errorContainerRetry.hide();

        self.showHideAgent(false);

        self.goToQuestion(self.currentQuestion);
    };

    this.validateForm = function (questionForm, serverJsonValidator) {
        var jsonValidator = createFormValidationJson(serverJsonValidator, questionForm, {
            errorPlacement: function (error, element) {
                if (element.closest('.field').find(".errorContainer").length) {
                    error.appendTo(element.closest('.field').find(".errorContainer"));
                } else {
                    error.appendTo(element.closest('form').find(".errorContainer"));
                }
            }
        });
        questionForm.validateForm(jsonValidator);
    };

    this.resetUrl = function () {
        window.history.pushState(null, null, system.contextPath + "/" + (self.type == self.TYPE_EVALUATION ? self.categoryUrl + "/" : "") + self.getUrlByType() + (self.token != null ? "/" + self.token : ""));
    };

    this.setUrl = function (evalType) {
        if (evalType !== "") {
            evalType = evalType + "/";
        }
        var tok = ""
        if (self.token != null) {
            tok = self.token
        }
        path = system.contextPath + "/" + (self.type == self.TYPE_EVALUATION ? self.categoryUrl + "/" : "") + self.getUrlByType() + "/" + evalType + tok;
        window.history.pushState(null, null, path);
    };

    this.showHideAgent = function (show) {
        if (show)
            $(".adviser-section").show();
        else
            $(".adviser-section").hide();
    };

    this.showHideUpdateModal = function (updateLoanApp, updateLoanOffer) {
        if (!updateLoanApp && !updateLoanOffer) {
            $(".edit-amount-box").hide();
        } else {
            if (updateLoanApp)
                self.urlUpdateAmountInstallments = system.contextPath + "/" + self.categoryUrl + "/" + self.EVALUATION_URL + "/" + self.token + "/updateLoanApplication"
            else
                self.urlUpdateAmountInstallments = system.contextPath + "/" + self.categoryUrl + "/" + self.EVALUATION_URL + "/" + self.token + "/updateLoanOffer"

            self.refreshAmountAndInstallments();

        }
    };

    this.showGoBack = function (show) {
        if (show)
            $("#anchor-back").css('visibility', 'visible');
        else
            $("#anchor-back").css('visibility', 'hidden');
    };

    this.showEmailName = function (email, fullName) {
        $("#contactFullName").val(fullName);
        $("#contactEmail").val(email);
    };

    this.showHideToky = function (show, loopTry) {
        if (show)
            $("#toky-iframe-content").css({
                'display': "block"
            });
        else
            $("#toky-iframe-content").css({
                'display': "none"
            });
    };

    // this.paintLateralSection = function (sectionId) {
    //     if (sectionId == null)
    //         $("#sectionSidebar").hide();
    //     else
    //         $("#sectionSidebar").show();
    //
    //     $("#sectionSidebar > ul > li").each(function (index) {
    //         if ($(this).data('section') <= sectionId) {
    //             $(this).addClass('active');
    //         } else {
    //             $(this).removeClass('active');
    //         }
    //     });
    // };

    this.refreshAmountAndInstallments = function (success) {
        if (self.token != null) {
            defaultAjax({
                url: system.contextPath + "/" + self.categoryUrl + "/" + self.EVALUATION_URL + "/" + self.token + "/amountAndInstallments",
                type: "GET",
                success: function (data) {
                    $('#processHeaderAmount').html(data);

                    $(".edit-amount-box").show();

                    $('#edit-loan').off('click');
                    $('#edit-loan').on('click', function (event) {
                        event.preventDefault();
                        self.openUpdateModal();
                    });

                    updateBranding();
                }
            });
        }
    };

    this.openUpdateModal = function () {
        $('#edit-modal').modal({
            backdrop: 'static',
            keyboard: false,
            show: true
        });
        $('#edit-modal').html('');
        $('#edit-modal').defaultLoad(self.urlUpdateAmountInstallments, null, function () {
            updateBranding();
        });
    };

    this.getUrlByType = function () {
        if (self.type == self.TYPE_SELFEVALUATION)
            return self.SELFEVALUATION_URL;
        if (self.type == self.TYPE_EVALUATION)
            return self.EVALUATION_URL;
        if (self.type == self.TYPE_COMPARISON)
            return self.COMPARISON_URL;
    };

    this.tagRecording = function (value) {
        if (self.isHotjarActive) {
            window.hj = window.hj || function () {
                (hj.q = hj.q || []).push(arguments)
            };
            hj('tagRecording', [value]);
        }
    };

    this.showPreApproved = function (show, message) {
        if (show)
            $("#pre-aproved-message").show().html(message)
        else
            $("#pre-aproved-message").hide()
    };

    this.showOfferMedal = function (show) {
        if (show) {
            $('.sello').show();
        } else {
            $('.sello').hide();
        }
    };

    this.paintProgress = function (percentage, category) {
        if (self.type == self.TYPE_SELFEVALUATION) {
            $(".steps-question.category-1").find('.progress-line').css("width", percentage + "%");
            if (percentage == 100) {
                $(".steps-question.category-1").addClass('active');
            } else {
                $(".steps-question.category-1").removeClass('active');
            }
        } else {
            if(self.showCustomOffer){
                $(".list-question.after-offer").hide();
                $(".list-question.before-offer").hide();
                $(".list-question.custom-offer").show();
            }
            else{
                $(".list-question.custom-offer").hide();
                if ($.inArray(category, [1, 2, 3, 4, 8]) >= 0) {
                    $(".list-question.before-offer").show();
                    $(".list-question.after-offer").hide();
                } else {
                    $(".list-question.before-offer").hide();
                    $(".list-question.after-offer").show();
                }
            }

            for (i = 1; i <= 12; i++) {
                // Paint of the percentage
                if (i < category) {
                    $(".steps-question.category-" + i).find('.progress-line').css("width", 100 + "%");
                } else if (i == category) {
                    $(".steps-question.category-" + i).find('.progress-line').css("width", percentage + "%");
                } else {
                    $(".steps-question.category-" + i).find('.progress-line').css("width", 0 + "%");
                }

                // Paint of the icon
                if(self.showCustomOffer){
                    if (i <= category) {
                        $(".steps-question.category-" + i).addClass('active');
                    }
                    else $(".steps-question.category-" + i).removeClass('active');
                }else{
                    if (i > 1) {
                        if (i <= category) {
                            $(".steps-question.category-" + (i - 1)).addClass('active');
                        } else {
                            $(".steps-question.category-" + (i - 1)).removeClass('active');
                        }
                    }
                }
            }

            for (i = 1; i <= 12; i++) {
                if(!$(".steps-question.category-" + i).hasClass('active')){
                    $(".steps-question.category-"+ i).find('.w').css("background", "");
                    $(".steps-question.category-"+ i).find('.list-question-text').css("color", "");
                }
            }
        }
    };

    this.paintProgressBanBif = function (category) {
        // show verification step in evaluation
        if (category == 8) {
            category = 1;
        }

        for (i = 1; i <= 10; i++) {
            // Paint of the percentage
            if (i <= category) {
                $(".steps-question.category-" + i).find('.progress-line').css("width", 100 + "%");
                $(".steps-question.category-" + i).addClass('active');
            } else {
                $(".steps-question.category-" + i).find('.progress-line').css("width", 0 + "%");
                $(".steps-question.category-" + i).removeClass('active');
            }
        }
    };

    this.getEvaluationPercentageAsync = function(category){
        if (self.token != null) {
            defaultAjax({
                url: system.contextPath + "/" + self.getUrlByType() + "/percentage",
                type: "GET",
                data:{
                    token : self.token
                },
                success: function (data) {
                    if(data != null) data = JSON.parse(data);
                    self.paintProgress(data.percentage || 0, category);
                    updateBranding();
                }, error: function(data){
                    self.paintProgress(0, category);
                    updateBranding();
                }
            });
        }
    }

    this.generateURlToCurrentQuestionController = function (urlMethod) {
        return system.contextPath + "/" + self.getUrlByType() + "/question/" + self.currentQuestion + "/" + urlMethod;
    };

    this.isMobile = {
        Android: function () {
            return navigator.userAgent.match(/Android/i);
        },
        BlackBerry: function () {
            return navigator.userAgent.match(/BlackBerry/i);
        },
        iOS: function () {
            return navigator.userAgent.match(/iPhone|iPad|iPod/i);
        },
        Opera: function () {
            return navigator.userAgent.match(/Opera Mini/i);
        },
        Windows: function () {
            return navigator.userAgent.match(/IEMobile/i) || navigator.userAgent.match(/WPDesktop/i);
        },
        any: function () {
            return (this.Android() || this.BlackBerry() || this.iOS() || this.Opera() || this.Windows());
        }
    };

    this.sendBeaconPushNotification = function (userId) {
        if (userId != null && self.pushNotificationsQuestionsToTrigger.indexOf(self.currentQuestion) === -1) {
            console.log('sendBeaconPushNotification Not sending beacon');
            return;
        }
        try {
            OneSignal.sendTag("current_question", self.currentQuestion);

            if ('sendBeacon' in navigator) {
                var url = window.location.origin + '/loanapplication/updatePlayerId';
                var data = JSON.stringify({
                    token: self.token,
                    playerId: userId
                });

                var result = navigator.sendBeacon(url, data);
                if (result) {
                    console.log('sendBeaconPushNotification Beacon sent');
                } else {
                    console.error('sendBeaconPushNotification Failed to send beacon');
                }
            } else {
                console.error('Browser does not support Beacon API');
            }
        } catch (error) {
            console.error('OneSignal not available / Adblocker On?')
        }
    };

    this.sendGAClientId = function(){
        try{
            var gaClientId;
            for(var i=0; i<dataLayer.length; i++){
                if(dataLayer[i].g != null && dataLayer[i].g.event == 'gtagApiGet'){
                    gaClientId = dataLayer[i].g.gtagApiResult.client_id;
                }
            }
            if(gaClientId != null){
                defaultAjax({
                    url: system.contextPath + "/" + self.getUrlByType() + "/gaclientid",
                    type: "POST",
                    data:{
                        token : self.token,
                        clientId: gaClientId
                    },
                    success: function (data) {
                    }, error: function(data){
                        console.error(error);
                    }
                });
            }
        } catch (error) {
            console.error(error);
        }
    };

}