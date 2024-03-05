/**
 * Created by jrodriguez on 31/07/16.
 */
var pageSummaries = [];

function initSummaryPanel() {
    // Configure that the first panels open and the focus in the first element
    if (pageSummaries.length > 0) {
        for (i = 0; i < pageSummaries.length; i++) {
            for (j = 0; j < pageSummaries[i].elements.length; j++) {
                if (pageSummaries[i].elements[j].is(':enabled')) {
                    openSummaryPanel(pageSummaries[i].panel, pageSummaries[i].elements)
                    return;
                }
            }
        }
    }
}

function configureSummaryPanel(arrayElements) {
    // Find the summaryPanel and configure it
    var $summaryPanel = arrayElements[0].closest(".summarypanel");
    $summaryPanel.attr("tabindex", -1).focus(function () {
        console.log("Focus on sumarypanel");
    });
    $summaryPanel.find(".summarypanel-tittle, .summarypanel-summary").click(function () {
        openSummaryPanel($summaryPanel, arrayElements);
    });
    $(document).mouseup(function (e) {
        // If the click is outside the panel
        if (!$summaryPanel.is(e.target) && $summaryPanel.has(e.target).length === 0 && $(e.target).parents('.datepicker').length === 0) {
            // Close the summaryPanel
            closeSummaryPanel($summaryPanel, arrayElements);
        }
    });

    var summaryPanelJson = {};
    summaryPanelJson.panel = $summaryPanel;
    summaryPanelJson.elements = arrayElements;
    pageSummaries[pageSummaries.length] = summaryPanelJson;

    closeSummaryPanel($summaryPanel, arrayElements, false)
}

function openSummaryPanel($summaryPanel, arrayElements) {
    // Open the summaryPanel
    $summaryPanel.find(".summarypanel-summary").hide(300);
    $summaryPanel.find(".summarypanel-body").show(300, function () {

        //Focus on the first element that is not disabled, is visible and is empty
        for (i = 0; i < arrayElements.length; i++) {
            if (arrayElements[i].is(':enabled') && (arrayElements[i].val() == '' || arrayElements[i].val() == null) && arrayElements[i].is(':visible')) {
                console.log(arrayElements[i])
                arrayElements[i].focus();
                return;
            }
        }
    });
}

function closeSummaryPanel($summaryPanel, arrayElements, validate) {
    console.log('Closing the summaryPanel');
    var showSummary = false;
    var validElements = true;
    for (i = 0; i < arrayElements.length; i++) {
        var $element = arrayElements[i];
        // Fill the element summary
        var elementSummary = $element.is("select") ? $element.find('option:selected').text() : $element.val();
        $('#' + $element.attr('id') + 'SummaryPanel').html(elementSummary);
        // Check if it has value
        if ($.trim(elementSummary) != '') {
            showSummary = true;
            $('#' + $element.attr('id') + 'SummaryPanelContainer').show();
        } else {
            $('#' + $element.attr('id') + 'SummaryPanelContainer').hide();
        }

        // console.log("Validando que el elemento sea valido");
        // console.log($element);
        // console.log($element.valid());
        if (validate == null || validate) {
            if (!$element.valid()) {
                validElements = false;
            }
        }
    }

    // $summaryPanel.find('.summarypanel-tittle').show(300);
    if (showSummary) {
        $summaryPanel.find('.summarypanel-summary').show(300);
    } else {
        $summaryPanel.find('.summarypanel-summary').hide(300);
    }
    $summaryPanel.find('.summarypanel-body').hide(300);

    if (validate == null || validate) {
        if (validElements) {
            $summaryPanel.removeClass('panel-has-success-failed');
            // $summaryPanel.addClass('panel-has-success-done');
        } else {
            // $summaryPanel.removeClass('panel-has-success-done');
            $summaryPanel.addClass('panel-has-success-failed');
        }
    }
}

//Utilitarios

function openSummaryPanelByIndex(index){
    openSummaryPanel(pageSummaries[index].panel, pageSummaries[index].elements)
}
