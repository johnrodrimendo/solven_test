/**
 * Created by jrodriguez on 08/09/16.
 */
$(document).ready(function () {
    $('.tablefilter').each(function () {
        $(this).data('tablefilter', new TableFilter($(this), 200));
    });
});

/**
 * Class to manage the filters established
 * @constructor
 */
function TableFilter(portlet, limit) {

    var self = this;
    this.portlet = portlet;
    this.filters = [];
    this.resultsContainer = null;
    this.limit = limit;
    this.offset = 0;

    //TAB CREDITS
    const TAB_MANAGEMENT = 1;
    const TAB_NO_MANAGEMENT = 2;
    const TAB_SALARY_ADVANCE = 3;

    this.init = function () {
        // Actions for filters
        self.portlet.find('.tablefilter-action-button').click(this.openFilters);
        self.portlet.find('.tablefilter-action-button-clean').click(this.removeFilters);

        // Actions for paginator
        // Creates the paginator elements
        self.tablePaginatorInfoDiv = $('<div class="table-paginator-info" style="display: inline-block;vertical-align: top;margin-top: .5em;margin-right: 1em;"></div>');
        self.tablePaginatorDiv = $('<div class="table-paginator paging_bootstrap_number" style="display: inline-block;vertical-align: top;">' +
            '<ul class="pagination pull-right" style="margin: 0px;">' +
            '<li class="page-first"><a href="#" class="page-link" title="Primero"><i class="fa fa-angle-double-left"></i></a></li>' +
            '<li class="page-pre"><a href="#" class="page-link" title="Previos"><i class="fa fa-angle-left"></i></a></li>' +
            '<li class="page-next"><a href="#" class="page-link" title="Siguientes"><i class="fa fa-angle-right"></i></a></li>' +
            '<li class="page-last"><a href="#" class="page-link" title="Último"><i class="fa fa-angle-double-right"></i></a></li></ul></div>');

        // Add the elements to the html
        var actionsDiv = self.portlet.find('.portlet-title').find('.actions');
        if (!actionsDiv.length) {
            actionsDiv = $('<div class="actions"></div>');
            self.portlet.find('.portlet-title').append(actionsDiv);
        }
        actionsDiv.append(self.tablePaginatorInfoDiv);
        actionsDiv.append(self.tablePaginatorDiv);

        // Define click events
        self.tablePaginatorDiv.find('.page-next > a').click(this.next);
        self.tablePaginatorDiv.find('.page-last > a').click(this.last);
        self.tablePaginatorDiv.find('.page-pre > a').click(this.pre);
        self.tablePaginatorDiv.find('.page-first > a').click(this.first);

        // Paint the info
        self.refreshInfo();
    };

    this.openFilters = function () {
        self.portlet.find('.tablefilter-filters').fadeIn(500);
        self.portlet.find('.actions').not('.tablefilter-actions').hide();
        self.portlet.find('.actions').parent().append(
            $('<div></div>')
                .fadeIn(500)
                .addClass('actions tablefilter-actions')
                .append($('<a href="javascript:;" id="apply-filter" class="btn btn-circle btn-default"><i class="fa fa-times"></i> Cancelar</a>')
                    .click(self.closeFilters)
                )
                .append($('<a href="javascript:;" id="cancel-filter" class="btn btn-circle btn-default"><i class="fa fa-check"></i> Aplicar Filtros</a>')
                    .click(self.applyFilters)
                )
        );

        var creationFromFilter = $('#creationFromFilter');
        var creationToFilter = $('#creationToFilter');

        if (window.datetimepickerFilters !== true) {
            creationFromFilter.val(moment().format('DD/MM/YYYY'));
            creationToFilter.val(moment().format('DD/MM/YYYY'));

            creationFromFilter.datepicker('endDate', moment().toDate());
            creationFromFilter.datepicker('update');
            creationToFilter.datepicker('endDate', moment().toDate());
            creationToFilter.datepicker('update');
        } else {
            creationFromFilter.datepicker({
                viewMode: 'days',
                format: 'DD/MM/YYYY'
            });
            creationToFilter.datepicker({
                viewMode: 'days',
                format: 'DD/MM/YYYY'
            });
        }
    };

    this.closeFilters = function () {
        self.portlet.find('.tablefilter-filters').hide();
        self.portlet.find('.tablefilter-actions').hide();
        self.portlet.find('.actions').not('.tablefilter-actions').fadeIn(500);
        self.portlet.find('.tablefilter-actions').remove();

        //Reset the values of all the inputs and selects
        self.portlet.find('.tablefilter-filters').find('form').find('select,input').each(function () {
            $(this).val('');
        });
    };
    this.applyFilters = function () {
        //Merge the old filters with the new ones
        var newFilters = self.getFormFilters();
        for(var i=0; i<newFilters.length; i++){
            // If its not multiple filter, find the same int he list that alredy exists and destroy it!
            if(!newFilters[i].multiple){
                self.filters = $.grep(self.filters, function(value) {
                    return value.name != newFilters[i].name;
                });
            }
        }
        self.filters = $.merge(self.filters, newFilters);

        self.loadData(0);
    };
    this.loadData = function(newOffset){
        //Create json of params
        var jsonParams = {};

        // Credits
        var creditsTab = $('#credits-tab');
        if (creditsTab) {
            var tabId = creditsTab.find('[aria-expanded=true]').data('tab');
            jsonParams['tabId'] = (tabId === undefined) ? TAB_MANAGEMENT : tabId;
        }

        for (var i = 0; i < self.filters.length; i++) {
            if (jsonParams[self.filters[i].name] == undefined) {
                jsonParams[self.filters[i].name] = [];
            }
            jsonParams[self.filters[i].name][jsonParams[self.filters[i].name].length] = self.filters[i].value;
        }

        //Call to the backend
        var $form = self.portlet.find('.tablefilter-filters').find('form');
        window.filters = self.filters;
        jsonParams["offset"] = newOffset;
        jsonParams["limit"] = self.limit;

        if (window.assistedProcess !== undefined) {
            jsonParams["assistedProcess"] = window.assistedProcess;
        }

        defaultAjax({
            url: $form.attr('action'),
            type: $form.attr('method'),
            data: jsonParams,
            success: function (data) {
                self.portlet.find('#table').html(data);
                self.paintFilters();
                self.closeFilters();
                self.offset = newOffset;
                self.refreshInfo();
            }
        });
    };
    this.getFormFilters = function () {
        var settedFilters = [];
        self.portlet.find('.tablefilter-filters').find('form').find('select,input').each(function () {
            if ($(this).val() != '') {
                var filter = {};
                // var filterLabel = $(this).data("filter-label") != null ? $(this).data("filter-label") : $(this).attr("name");
                if ($(this).prop("tagName").toLowerCase() == 'select') {
                    // filter.label = '<b>'+filterLabel+'</b>' +': '+$(this).find('option:selected').text();
                    filter.label = $(this).find('option:selected').text();
                    filter.value = $(this).val();
                    filter.name = $(this).attr("name");
                    filter.multiple = $(this).data("filter-multiple") != null ? $(this).data("filter-multiple") : true;
                    $(this).find('option:selected').prop('disabled', true);
                } else if ($(this).prop("tagName").toLowerCase() == 'input') {
                    // filter.label = '<b>'+filterLabel+'</b>' +': '+$(this).val();
                    filter.label = $(this).val();
                    filter.value = $(this).val();
                    filter.name = $(this).attr("name");
                    filter.multiple = $(this).data("filter-multiple") != null ? $(this).data("filter-multiple") : false;
                }
                settedFilters [settedFilters.length] = filter;
            }
        });

        return settedFilters;
    };
    this.paintFilters = function () {
        var filterTagsContainer = self.portlet.find('.portlet-body').find('#filterTagsContainer');
        if (filterTagsContainer.length > 0) {
            filterTagsContainer.remove();
        }

        if (self.filters.length > 0) {
            filterTagsContainer = $('<div id="filterTagsContainer" style="margin-bottom: 10px">Filtrado por </div>');
            for (var i = 0; i < self.filters.length; i++) {
                filterTagsContainer.append($('<span class="label label-info" style="margin-right: 4px;">' + self.filters[i].label + ' </span>')
                    .append($('<a href="javascript:;" data-filter-index="' + i + '"><i class="fa fa-times"  style="color:white"></i></a>')
                        .click(function () {
                            self.removeFilter($(this).attr("data-filter-index"));
                        })
                    )
                );
            }
            $(self.portlet.find('.portlet-body').get(0)).prepend(filterTagsContainer);
        }
    };
    this.removeFilters = function () {
        var filterTagsContainer = self.portlet.find('.portlet-body').find('#filterTagsContainer');
        if (filterTagsContainer.length > 0) {
            filterTagsContainer.remove();
        }
        var jsonParams = {};
        $('.nav.nav-tabs.pull-left').find('a').each(function(index, value){
            if($(this).attr('aria-expanded') == undefined ) jsonParams['collectionManagement'] = 'true';
            if($(this).attr('aria-expanded') == 'true'){
                jsonParams['collectionManagement'] = $(this).attr('data-management');
            }
        });
        var $form = self.portlet.find('.tablefilter-filters').find('form');
        defaultAjax({
            url: $form.attr('action'),
            type: $form.attr('method'),
            data: jsonParams,
            success: function (data) {
                if(self.resultsContainer != null){
                    self.resultsContainer.html(data);
                }else{
                    self.portlet.find('#tableResults tbody').html(data);
                }
            }
        });
    };
    this.removeFilter = function (filterIndex) {
        if (self.portlet.find('select[name="' + self.filters[filterIndex].name + '"]').length > 0) {
            self.portlet.find('select[name="' + self.filters[filterIndex].name + '"] option[value="' + self.filters[filterIndex].value + '"]').prop('disabled', false);
        }
        self.filters.splice(filterIndex, 1);
        self.loadData(0);
    };
    this.removeFilters = function () {
        var filterTagsContainer = self.portlet.find('.portlet-body').find('#filterTagsContainer');
        if (filterTagsContainer.length > 0) {
            filterTagsContainer.find('a').each(function(){
                var filterIndex = $(this).attr('data-filter-index');
                if (self.portlet.find('select[name="' + self.filters[filterIndex].name + '"]').length > 0) {
                    self.portlet.find('select[name="' + self.filters[filterIndex].name + '"] option[value="' + self.filters[filterIndex].value + '"]').prop('disabled', false);
                }
            });
            filterTagsContainer.remove();
        }
        self.filters = [];
        self.loadData(0);
    };

    // Pagination functions
    this.next = function () {
        var newOffset;
        if (self.offset + self.limit > self.total) {
            return;
        } else {
            newOffset = self.offset + self.limit;
        }

        self.loadData(newOffset);
    };

    this.first = function () {
        var newOffset;
        if(self.offset == 0) {
            return;
        }
        newOffset = 0;
        self.loadData(newOffset);
    };

    this.last = function () {
        var newOffset;
        if(self.offset + self.limit > self.total) {
            return;
        }

        newOffset = self.offset;

        while(newOffset + self.limit < self.total){
            newOffset += self.limit;
        }

        self.loadData(newOffset);
    };

    this.pre = function () {
        var newOffset;
        if (self.offset == 0) {
            return;
        } else if (self.offset - self.limit < 0) {
            newOffset = 0;
        } else {
            newOffset = self.offset - self.limit;
        }

        self.loadData(newOffset);
    };

    this.refreshInfo = function () {
        // Refresh the total
        var resultsBody = self.portlet.find('.table:first > tbody');

        if (resultsBody.data('table-paginator-total') !== undefined) {
            self.total = resultsBody.data('table-paginator-total');
        } else {
            self.total = 0;
        }

        // Paint the info
        if (self.total > 0) {
            self.tablePaginatorInfoDiv.html('<b>' + (self.offset + 1) + '</b>-<b>'
                + (self.offset + self.limit > self.total ? self.total : self.offset + self.limit)
                + '</b> de <b>' + self.total + '</b>');
        } else {
            self.tablePaginatorInfoDiv.html("No hay resultados");
            self.tablePaginatorDiv.find('button')
        }

        // invalidate buttons if necesary
        if (self.offset == 0) {
            self.tablePaginatorDiv.find('.page-pre').addClass('disabled');
            self.tablePaginatorDiv.find('.page-first').addClass('disabled');
        } else {
            self.tablePaginatorDiv.find('.page-pre').removeClass('disabled');
            self.tablePaginatorDiv.find('.page-first').removeClass('disabled');
        }

        if ((self.offset + self.limit) >= self.total) {
            self.tablePaginatorDiv.find('.page-next').addClass('disabled');
            self.tablePaginatorDiv.find('.page-last').addClass('disabled');
        } else {
            self.tablePaginatorDiv.find('.page-next').removeClass('disabled');
            self.tablePaginatorDiv.find('.page-last').removeClass('disabled');
        }
    };

    this.init();
}