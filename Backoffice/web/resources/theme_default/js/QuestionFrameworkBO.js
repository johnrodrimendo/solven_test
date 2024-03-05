function QuestionCategoryFlow(categoryId, loanApplicationId, categoryNode, isEditable) {

    var self = this;
    this.questions = [];
    this.categoryId = categoryId;
    this.loanApplicationId = loanApplicationId;
    this.categoryNode = categoryNode;
    this.isEditable = isEditable != null ? isEditable : true;

    this.geQuestionByRandomId = function (randomId) {
        for (i = 0; i < self.questions.length; i++) {
            if (self.questions[i].randomId == randomId)
                return self.questions[i];
        }
        return null;
    };

    this.addQuestion = function (question) {
        self.questions[self.questions.length] = question;
    };

    this.loadQuestionsContent = function () {
        self.categoryNode.find('.question-node').each(function () {
            // If is empty, call for the controller
            if (!$.trim($(this).html())) {
                var baseUrl = system.contextPath + "/loanApplication/question/";
                var nodeQuestinoId = $(this).data('question-id');
                var questionNodeParams =  $(this).data('params');
                var answeredQuestion =  $(this).data('answered-question');

                $(this).defaultLoad(baseUrl + nodeQuestinoId, null, null, 'GET', {identifier: self.loanApplicationId, answeredQuestion: answeredQuestion, questionParams: questionNodeParams != null ? JSON.stringify(questionNodeParams) : null})
            }
        });
    };

    this.validateQuestions = function () {
        var questionFlowArray = [];
        self.categoryNode.find('.question-node').each(function () {
            var flow = $(this).data('question-flow');
            if (flow != null)
                questionFlowArray[questionFlowArray.length] = flow;
        });

        self.validateQuestionHelper(questionFlowArray, 0, function () {
            alert('todas las validaciones estan buenas');
        });
    };

    this.validateQuestionHelper = function (questionFlowArray, index, functionIfAllOk) {
        if (questionFlowArray.length == index + 1) {
            questionFlowArray[index].validate(functionIfAllOk);
        }else if (questionFlowArray.length > index) {
            questionFlowArray[index].validate(function () {
                self.validateQuestionHelper(questionFlowArray, index + 1, functionIfAllOk)
            });
        }
    };

    this.saveQuestions = function () {
        var questionFlowArray = [];
        var isSaved = false;
        self.categoryNode.find('.question-node').each(function () {
            if($(this).data('question-flow').canSave){
                var flow = $(this).data('question-flow');
                if (flow != null)
                    questionFlowArray[questionFlowArray.length] = flow;
            }
        });

        self.validateQuestionHelper(questionFlowArray, 0, function () {
            self.saveQuestionHelper(questionFlowArray, 0, function () {
                showAsistedModal(self.loanApplicationId);
            });
        });
    };

    this.saveQuestionHelper = function (questionFlowArray, index, functionIfAllOk) {
        if (questionFlowArray.length == index + 1) {
            questionFlowArray[index].saveData(functionIfAllOk);
        }else if (questionFlowArray.length > index) {
            questionFlowArray[index].saveData(function () {
                self.saveQuestionHelper(questionFlowArray, index + 1, functionIfAllOk)
            });
        }
    };

    // Add this object as data of the category-node
    self.categoryNode.data('question-category-flow', self);
    if(!self.isEditable){
        self.categoryNode.append('<div style="border-radius: 4px;width: 100%;height: 100%;position:absolute;top: 0px;left: 0px;background: #e8ebf1;opacity: .5;z-index: 999999;cursor: not-allowed;"></div>')
    }
}

function QuestionFlow(questionId, randomId) {

    var self = this;
    this.questionId = questionId;
    this.randomId = randomId;
    this.loanApplicationId = null;
    this.questionNode = null;
    this.answeredQuestion = null;
    this.canSave = true;

    this.getFormValuesJson = function () {
    };

    this.initializeFormValidation = function (validator) {
        var form = self.getFormElement();
        form.validateForm(createFormValidationJson(JSON.parse(validator), form));
    };

    this.getFormElement = function () {
        return self.find('.question-form');
    };

    this.getQuestionNodeElement = function () {
        return self.questionNode;
        // return $('.question-node[random-id='+self.randomId+']');
    };

    this.getCategoryflow = function () {
        return self.getQuestionNodeElement().closest('.category-node').data('question-category-flow');
    };

    this.find = function (selector) {
        return self.getQuestionNodeElement().find(selector);
    };

    this.hideQuestionNode = function (selector) {
        return self.getQuestionNodeElement().hide();
    };

    this.loadNextQuestions = function () {
        if(!self.isEditable())
            return;

        var formValues = self.getFormValuesJson();
        var ajaxParamsJson = {
            loanApplicationId: self.loanApplicationId,
            questionId: self.questionId,
            jsonParams: formValues != null ? JSON.stringify(formValues) : null
        };

        defaultAjax({
            url: system.contextPath + "/loanApplication/questionProcess/refresh/modal",
            type: 'POST',
            data: ajaxParamsJson,
            form: self.getFormElement(),
            success: function (data) {
                if (data != null && data == 'showRegister') {
                    // Show the register button

                } else {
                    var questionNode = self.getQuestionNodeElement();
                    questionNode.nextAll().remove();
                    questionNode.after(data);

                    self.getCategoryflow().loadQuestionsContent();
                }

            }
        })
    };

    this.validate = function (onSuccess) {
        var formValues = self.getFormValuesJson();
        var ajaxParamsJson = {
            identifier: self.loanApplicationId,
            jsonParams: formValues != null ? JSON.stringify(formValues) : null
        };

        var isValid = true;
        if (self.getFormElement() != null && self.getFormElement().length && self.getFormElement().data('validator') != null) {
            isValid = self.getFormElement().valid();
        }

        if (isValid) {
            defaultAjax({
                url: system.contextPath + "/loanApplication/question/" + self.questionId + "/validate",
                type: 'POST',
                data: ajaxParamsJson,
                form: self.getFormElement(),
                success: function () {
                    if (onSuccess != null) {
                        onSuccess();
                    }
                }
            });
        }
    };

    this.saveData = function (onSuccess) {

        var formValues = self.getFormValuesJson();
        var ajaxParamsJson = {
            identifier: self.loanApplicationId,
            jsonParams: formValues != null ? JSON.stringify(formValues) : null
        };

        defaultAjax({
            url: system.contextPath + "/loanApplication/question/" + self.questionId + "/save",
            type: 'POST',
            data: ajaxParamsJson,
            form: self.getFormElement(),
            success: function () {
                if (onSuccess != null) {
                    onSuccess();
                }
            }
        });
    };

    this.callCustomMethod = function (customMetod, jsonParams, onSuccess, onError) {
        var ajaxParamsJson = {
            loanApplicationId: self.loanApplicationId,
            questionId: self.questionId,
            jsonParams: jsonParams != null ? JSON.stringify(jsonParams) : null
        };

        var baseUrl = system.contextPath + "/loanApplication/questionProcess/customMethod/";
        defaultAjax({
            url: baseUrl + customMetod,
            type: 'POST',
            data: ajaxParamsJson,
            success: function (data) {
                if (onSuccess != null)
                    onSuccess(data);
            },
            error: function (data) {
                if (onError != null)
                    onError(data)
            }
        })
    };

    // this.skipQuestionIfCan = function () {
    //
    //     var formValues = self.getFormValuesJson();
    //     var ajaxParamsJson = {
    //         identifier: self.loanApplicationId
    //     };
    //
    //     defaultAjax({
    //         url: system.contextPath + "/loanApplication/question/" + self.questionId + "/canSkip",
    //         type: 'POST',
    //         data: ajaxParamsJson,
    //         success: function (data) {
    //             if (data != null && data == 'true') {
    //                 debugger;
    //                 self.loadNextQuestions();
    //             }
    //         }
    //     });
    // };

    this.isEditable = function () {
        return self.getCategoryflow().isEditable;
    };

    // In the init, add this question to the array of the categoryFlow
    self.questionNode = $('.question-form[data-random-id=' + self.randomId + ']').closest('.question-node').data('random-id', self.randomId);
    self.questionNode.data('random-id', self.randomId);
    self.questionNode.data('question-flow', self);
    self.answeredQuestion = self.questionNode.data('answered-question');

    var categoryFlow = self.getCategoryflow();
    categoryFlow.addQuestion(self);
    self.loanApplicationId = categoryFlow.loanApplicationId;

    // if(self.isEditable() && !self.answeredQuestion){
    //     self.skipQuestionIfCan();
    // }
}


// Util method to get the Question Object by the randomId
function getQuestionFlowByRandomId(randomId) {
    return $('.question-form[data-random-id=' + randomId + ']').closest('.question-node').data('question-flow');
}

// Util method to get the Category Object by the categoryId
function getQuestionCategoryFlowByCategorId(categoryId) {
    return $('.category-node[data-category-id=' + categoryId + ']').data('question-category-flow');
}

function showAsistedModal(loanApplicationId) {
    var url = system.contextPath + "/loanApplication/questionProcess/modal";
    $('#asistedQuestionModal').modal();
    $('#asistedQuestionModal .loading').show();
    $('#asistedQuestionModal .content').hide();
    $('#asistedQuestionModal .content').load(url + "?loanApplicationId=" + loanApplicationId, null, function () {
        $('#asistedQuestionModal .loading').hide(300);
        $('#asistedQuestionModal .content').show(300);
    });
}