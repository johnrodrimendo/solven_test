function QuestionFramework(e) {
    this.TYPE_SELFEVALUATION = 1, this.TYPE_EVALUATION = 2, this.TYPE_COMPARISON = 3, this.SELFEVALUATION_URL = "autoevaluacion", this.EVALUATION_URL = "evaluacion", this.COMPARISON_URL = "compara";
    var t = this;
    this.token, this.type, this.currentQuestion, this.categoryUrl, this.agent, this.externalParams, this.questionContainer = e, this.errorContainer, this.urlUpdateAmountInstallments, this.loggable = !1, this.brandingPrimaryColor, this.brandingSecundaryColor, this.branding = !1, this.globalUserNameForZendesk = null, this.globalUseeEmailForZendesk = null, this.showZendeskChatTimeout = 12e4, this.pushNotificationUserId = null, this.pushNotificationsQuestionsToTrigger = [], this.goToQuestion = function (e) {
        t.currentQuestion = e;
        t.questionContainer.outerHeight() < 200 || t.questionContainer.outerHeight();
        t.questionContainer.html('<div class="process-loading-container circle-progress-question"> <div class="loading-content"> <div class="sk-fading-circle"> <div class="sk-circle1 sk-circle"><span></span></div><div class="loading-indicator sk-circle2 sk-circle"><span></span></div><div class="sk-circle3 sk-circle"><span></span></div><div class="sk-circle4 sk-circle"><span></span></div><div class="sk-circle5 sk-circle"><span></span></div><div class="sk-circle6 sk-circle"><span></span></div><div class="sk-circle7 sk-circle"><span></span></div><div class="sk-circle8 sk-circle"><span></span></div><div class="sk-circle9 sk-circle"><span></span></div><div class="sk-circle10 sk-circle"><span></span></div><div class="sk-circle11 sk-circle"></div></div><span class="loading-text">Cargando<span>.</span><span>.</span><span>.</span></span> </div></div>'), updateBranding(), setTimeout(function () {
            t.sendBeaconPushNotification(null)
        }, 15e3);
        var n = {
            url: system.contextPath + "/" + t.getUrlByType() + "/question/" + e,
            type: "GET",
            data: {},
            success: function (e) {
                t.replaceQuestionContent(e)
            },
            error: function (e, n) {
                return t.questionContainer.html(""), null != t.errorContainer && t.errorContainer.show(), !1
            }
        };
        null != t.externalParams && (n.data.externalParams = t.externalParams), null != t.token ? n.data.token = t.token : (null != t.agent && (n.data.agent = t.agent), null != t.categoryUrl && (n.data.categoryUrl = t.categoryUrl)), t.defaultProcessQuestionAjax(n)
    }, this.replaceQuestionContent = function (e) {
        null != t.errorContainer && t.errorContainer.hide(), t.questionContainer.html(e), $("#questionModals").html(""), t.questionContainer.find(".modal").each(function () {
            $(this).detach().appendTo("#questionModals")
        });
        var n = t.questionContainer.find("#questionConfiguration");
        null != n && (t.globalUserNameForZendesk = n.data("user-name"), t.globalUserEmailForZendesk = n.data("user-email"), t.showHideAgent(n.data("show-agent")), t.showHideUpdateModal(n.data("show-application-update-modal"), n.data("show-offer-update-modal")), t.showGoBack(n.data("show-go-back")), t.showPreApproved(n.data("show-pre-approved"), n.data("show-pre-approved-message")), t.showOfferMedal(n.data("show-offer-modal")), n.data("show-name-email") && t.showEmailName(n.data("user-email"), n.data("user-name")), null != n.data("percentage") ? t.paintProgress(n.data("percentage"), n.data("question-catgory")) : t.paintProgress(0, 1), null != n.data("force-vehicle-modal-show") && n.data("force-vehicle-modal-show") && t.openUpdateModal(), null != n.data("hotjar-id") && this.tagRecording(n.data("hotjar-id")), 50 === t.currentQuestion && 0 === $(".q50 .offer-container").length ? t.shouldShowZendeskChat(!1) : (null != n.data("showZendeskWidget") && (console.log("shouldShowZendeskChat", n.data("showZendeskWidget")), t.shouldShowZendeskChat(n.data("showZendeskWidget"))), null != n.data("showZendeskWidget") && !0 === n.data("showZendeskWidget") && (console.log("timeoutCountDown"), t.timeoutCountDown())), 50 === t.currentQuestion && 1 === $(".q50 .offer-container").length ? $("#processHeaderAmount").css("visibility", "visible") : $("#processHeaderAmount").css("visibility", "hidden"), (4 === n.data("question-catgory") && 50 === t.currentQuestion || 5 === n.data("question-catgory") || 6 === n.data("question-catgory") || 7 === n.data("question-catgory")) && t.pushNotificationsQuestionsToTrigger.push(t.currentQuestion), 7 === n.data("question-catgory") && (t.pushNotificationUserId = null), null !== t.isMobile.any() && setTimeout(function () {
            7 === n.data("question-catgory") && (t.pushNotificationUserId = null), t.sendBeaconPushNotification(t.pushNotificationUserId)
        }, 15e3), n.data("document-title") && (document.title = customDocumentTitle), n.remove()), t.initializeFormInputs(), 1 == t.questionContainer.find("form").find("input[type=text]").length && t.questionContainer.find("form").prepend($('<input type="text" name="dummyInput" style="display: none"/>'));
        var o = $("#offer-carousel");
        o.doOnce(function () {
            o.owlCarousel({
                items: 3,
                navText: ['<i class="icon icon-control-prev"></i>', '<i class="icon icon-control-next"></i>'],
                responsiveClass: !0,
                responsive: {0: {items: 1, nav: !0}, 568: {items: 2, nav: !0}, 1200: {items: 3, nav: !0}}
            })
        }), $("#reason-carousel").doOnce(function () {
            $("#reason-carousel").owlCarousel({
                items: 4,
                navText: ['<i class="icon icon-control-prev"></i>', '<i class="icon icon-control-next"></i>'],
                responsiveClass: !0,
                responsive: {0: {items: 1, nav: !0}, 600: {items: 1, nav: !0}, 1000: {items: 4, nav: !0}}
            })
        }), $("label.custom-check").on("change", ".real-check", function () {
            $(this).checked()
        }), $("label.h-input-check").on("change", ".checkbox", function () {
            $(this).checkbox()
        }), $("select").each(function () {
            $(this).select2({
                minimumResultsForSearch: -1,
                placeholder: $(this).data("placeholder"),
                width: $(this).data("width") + "px"
            })
        }), $(".input-radio, .h-input-radio").radio(), t.loggable && ($("#logDiv").remove(), $('<div id="logDiv" style="position: fixed;z-index: 99999999;right:0;bottom:0; font-size: 12px; background:#e9e9e9;">Pregunta: ' + t.currentQuestion + "</div>").appendTo("body")), updateBranding(), t.updateETAmessage(t.currentQuestion, n.data("question-catgory"))
    }, this.timeoutCountDown = function () {
        var e;

        function n() {
            clearTimeout(e), void 0 === sessionStorage.wasContacted && (e = setTimeout(t.contactZendesk, t.showZendeskChatTimeout))
        }

        n(), window.onloadstart = n, window.onmousemove = n, window.onmousedown = n, window.ontouchstart = n, window.onclick = n, window.onkeypress = n, window.addEventListener("scroll", n, !0)
    }, this.contactZendesk = function () {
        "undefined" != typeof zE && (console.log("contactZendesk"), sessionStorage.wasContacted = "true", zE("webWidget", "setLocale", "es"), zE("webWidget", "chat:send", "Solven, solicitud de asesoría"), zE("webWidget", "identify", {
            name: t.globalUserNameForZendesk,
            email: t.globalUserEmailForZendesk
        }), zE("webWidget", "open"))
    }, this.shouldShowZendeskChat = function (e) {
        "undefined" != typeof zE && (e ? (zE("webWidget", "setLocale", "es"), zE("webWidget", "close"), zE("webWidget", "updateSettings", {webWidget: {chat: {suppress: !1}}})) : zE("webWidget", "updateSettings", {webWidget: {chat: {suppress: !0}}}))
    }, this.updateETAmessage = function (e, t) {
        var n = "Estimado<br/>3 min.";
        83 === e ? n = "&iexcl;Con esto <br/>Finalizamos&excl;" : (null != t && t > 0 && (6 === t || 7 === t) || isEfectivoAlToque) && (n = ""), $(".estimated").eq(0).html(n)
    }, this.initializeFormInputs = function () {
        t.questionContainer.find(".input-outline, .selec2-item").on("keyup select2:select", function (e) {
            if (13 == e.keyCode) return $(this).closest(".questions-section").find(".button").click(), e.preventDefault(), !1
        })
    }, this.refreshQuestion = function () {
        t.goToQuestion(t.currentQuestion)
    }, this.backwards = function () {
        var e = {
            url: system.contextPath + "/" + t.getUrlByType() + "/backwards",
            type: "POST",
            data: {},
            error: function () {
                return showErrorModal("No puedes volver atrás"), !1
            }
        };
        null != t.token && (e.data.token = t.token), t.defaultProcessQuestionAjax(e)
    }, this.updateLoanApplication = function (e) {
        e.url = system.contextPath + "/" + t.getUrlByType() + "/updateLoanApplication", null == e.data && (e.data = {}), null != t.token && (e.data.token = t.token), t.defaultProcessQuestionAjax(e)
    }, this.updateLoanApplicationVehicle = function (e) {
        e.url = system.contextPath + "/" + t.getUrlByType() + "/updateLoanApplicationVehicle", null == e.data && (e.data = {}), null != t.token && (e.data.token = t.token), t.defaultProcessQuestionAjax(e)
    }, this.updateLoanOffer = function (e) {
        e.url = system.contextPath + "/" + t.getUrlByType() + "/updateLoanOffer", null == e.data && (e.data = {}), null != t.token && (e.data.token = t.token), t.defaultProcessQuestionAjax(e)
    }, this.ajaxToCurrentQuestionController = function (e, n) {
        e.url = system.contextPath + "/" + t.getUrlByType() + "/question/" + t.currentQuestion + "/" + n, null == e.data && (e.data = {}), null != t.token ? e.data.constructor == FormData ? e.data.append("token", t.token) : e.data.constructor == {}.constructor && (e.data.token = t.token) : null != t.externalParams && (e.data.constructor == FormData ? e.data.append("externalParams", t.externalParams) : e.data.constructor == {}.constructor && (e.data.externalParams = t.externalParams)), t.defaultProcessQuestionAjax(e)
    }, this.defaultProcessQuestionAjax = function (e) {
        (null == e.showLoadingBar || e.showLoadingBar) && (t.branding && null != t.brandingPrimaryColor ? addMask(t.brandingPrimaryColor, gradientOfHexaColors(t.brandingPrimaryColor, "#FFFFFF")) : addMask()), null != e.button && e.button.loading("", 2);
        var n = [];
        if (n[n.length] = function (n) {
            if (null != e.button) try {
                e.button.unloading()
            } catch (e) {
            }
            if (isJsonString(n)) {
                var o = JSON.parse(n);
                if (null != o.token && (t.token = o.token, window.history.pushState(null, null, system.contextPath + "/" + (t.type == t.TYPE_EVALUATION ? t.categoryUrl + "/" : "") + t.getUrlByType() + "/" + t.token)), null != o.type && "processQuestion" == o.type && null != o.goto) return null != r && r(n), t.goToQuestion(o.goto), !1
            }
            unMask()
        }, null != e.success) {
            var o = e.success;
            n[n.length] = function (e) {
                return o(e)
            }
        }
        var s, a, r = null;
        null != e.successBeforeChangeQuestion && (r = e.successBeforeChangeQuestion), e.success = function (e) {
            for (i = 0; i < n.length; i++) {
                var t = n[i](e);
                if (null != t && !t) break
            }
        }, null != e.error && (s = e.error), e.error = function (t, n) {
            if (unMask(), null != e.button) try {
                e.button.unloading()
            } catch (e) {
            }
            if (null != s) return s(t, n)
        }, null !== e.complete && (a = e.complete), e.complete = function () {
            if (setTimeout(function () {
                $(".circle-progress").hide()
            }, 1e3), null != a) return a()
        }, defaultAjax(e)
    }, this.init = function () {
        null != t.errorContainer && t.errorContainer.hide(), t.showHideAgent(!1), t.goToQuestion(t.currentQuestion)
    }, this.validateForm = function (e, t) {
        var n = createFormValidationJson(t, e, {
            errorPlacement: function (e, t) {
                t.closest(".field").find(".errorContainer").length ? e.appendTo(t.closest(".field").find(".errorContainer")) : e.appendTo(t.closest("form").find(".errorContainer"))
            }
        });
        e.validateForm(n)
    }, this.resetUrl = function () {
        window.history.pushState(null, null, system.contextPath + "/" + (t.type == t.TYPE_EVALUATION ? t.categoryUrl + "/" : "") + t.getUrlByType() + (null != t.token ? "/" + t.token : ""))
    }, this.setUrl = function (e) {
        "" !== e && (e += "/");
        var n = "";
        null != t.token && (n = t.token), path = system.contextPath + "/" + (t.type == t.TYPE_EVALUATION ? t.categoryUrl + "/" : "") + t.getUrlByType() + "/" + e + n, window.history.pushState(null, null, path)
    }, this.showHideAgent = function (e) {
        e ? $(".adviser-section").show() : $(".adviser-section").hide()
    }, this.showHideUpdateModal = function (e, n) {
        e || n ? (t.urlUpdateAmountInstallments = e ? system.contextPath + "/" + t.categoryUrl + "/" + t.EVALUATION_URL + "/" + t.token + "/updateLoanApplication" : system.contextPath + "/" + t.categoryUrl + "/" + t.EVALUATION_URL + "/" + t.token + "/updateLoanOffer", t.refreshAmountAndInstallments()) : $(".edit-amount-box").hide()
    }, this.showGoBack = function (e) {
        e ? $("#anchor-back").css("display", "flex") : $("#anchor-back").css("display", "none")
    }, this.showEmailName = function (e, t) {
        $("#contactFullName").val(t), $("#contactEmail").val(e)
    }, this.showHideToky = function (e, t) {
        e ? $("#toky-iframe-content").css({display: "block"}) : $("#toky-iframe-content").css({display: "none"})
    }, this.refreshAmountAndInstallments = function (e) {
        null != t.token && defaultAjax({
            url: system.contextPath + "/" + t.categoryUrl + "/" + t.EVALUATION_URL + "/" + t.token + "/amountAndInstallments",
            type: "GET",
            success: function (e) {
                $("#processHeaderAmount").html(e), $(".edit-amount-box").show(), $("#edit-loan").off("click"), $("#edit-loan").on("click", function (e) {
                    e.preventDefault(), t.openUpdateModal()
                }), updateBranding()
            }
        })
    }, this.openUpdateModal = function () {
        $("#edit-modal").modal({
            backdrop: "static",
            keyboard: !1,
            show: !0
        }), $("#edit-modal").html(""), $("#edit-modal").defaultLoad(t.urlUpdateAmountInstallments, null, function () {
            updateBranding()
        })
    }, this.getUrlByType = function () {
        return t.type == t.TYPE_SELFEVALUATION ? t.SELFEVALUATION_URL : t.type == t.TYPE_EVALUATION ? t.EVALUATION_URL : t.type == t.TYPE_COMPARISON ? t.COMPARISON_URL : void 0
    }, this.tagRecording = function (e) {
        t.isHotjarActive && (window.hj = window.hj || function () {
            (hj.q = hj.q || []).push(arguments)
        }, hj("tagRecording", [e]))
    }, this.showPreApproved = function (e, t) {
        e ? $("#pre-aproved-message").show().html(t) : $("#pre-aproved-message").hide()
    }, this.showOfferMedal = function (e) {
        e ? $(".sello").show() : $(".sello").hide()
    }, this.paintProgress = function (e, n) {
        if (t.type == t.TYPE_SELFEVALUATION) $(".steps-question.category-1").find(".progress-line").css("width", e + "%"), 100 == e ? $(".steps-question.category-1").addClass("active") : $(".steps-question.category-1").removeClass("active"); else for ($.inArray(n, [1, 2, 3, 4, 8]) >= 0 ? ($(".list-question.before-offer").show(), $(".list-question.after-offer").hide()) : ($(".list-question.before-offer").hide(), $(".list-question.after-offer").show()), i = 1; i <= 10; i++) i < n ? $(".steps-question.category-" + i).find(".progress-line").css("width", "100%") : i == n ? $(".steps-question.category-" + i).find(".progress-line").css("width", e + "%") : $(".steps-question.category-" + i).find(".progress-line").css("width", "0%"), i > 1 && (i <= n ? $(".steps-question.category-" + (i - 1)).addClass("active") : $(".steps-question.category-" + (i - 1)).removeClass("active"))
    }, this.generateURlToCurrentQuestionController = function (e) {
        return system.contextPath + "/" + t.getUrlByType() + "/question/" + t.currentQuestion + "/" + e
    }, this.isMobile = {
        Android: function () {
            return navigator.userAgent.match(/Android/i)
        }, BlackBerry: function () {
            return navigator.userAgent.match(/BlackBerry/i)
        }, iOS: function () {
            return navigator.userAgent.match(/iPhone|iPad|iPod/i)
        }, Opera: function () {
            return navigator.userAgent.match(/Opera Mini/i)
        }, Windows: function () {
            return navigator.userAgent.match(/IEMobile/i) || navigator.userAgent.match(/WPDesktop/i)
        }, any: function () {
            return this.Android() || this.BlackBerry() || this.iOS() || this.Opera() || this.Windows()
        }
    }, this.sendBeaconPushNotification = function (e) {
        if (null == e || -1 !== t.pushNotificationsQuestionsToTrigger.indexOf(t.currentQuestion)) try {
            if (OneSignal.sendTag("current_question", t.currentQuestion), "sendBeacon" in navigator) {
                var n = window.location.origin + "/loanapplication/updatePlayerId",
                    o = JSON.stringify({token: t.token, playerId: e});
                navigator.sendBeacon(n, o) ? console.log("sendBeaconPushNotification Beacon sent") : console.error("sendBeaconPushNotification Failed to send beacon")
            } else console.error("Browser does not support Beacon API")
        } catch (e) {
            console.error("OneSignal not available / Adblocker On?")
        } else console.log("sendBeaconPushNotification Not sending beacon")
    }
}