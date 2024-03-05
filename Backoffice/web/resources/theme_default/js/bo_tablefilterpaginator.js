jQuery.fn.portletTableFilterPaginator = function (url, method, limit) {
    new TableFilterPaginator($(this), url, method, limit, 0);
};

$(document).ready(function () {
    console.log("bo_tablefilteraginator");
});

function TableFilterPaginator(portlet, url, method, limit, offset) {
    var self = this;
    this.portlet = portlet;
    this.filters = [];
    this.url = url;
    this.method = method;
    this.limit = limit;
    this.offset = offset;
    this.total = $(".table:first > tbody > tr:first").length ? $(".table:first > tbody > tr:first").attr('data-total') : 0;

    this.tablePaginatorDiv = null;
    this.actionsDiv = null;

    var $form = $('.tablefilter-filters').find('form');

    this.init = function () {
        self.tablePaginatorDiv = $('<div class="table-paginator paging_bootstrap_number pull-right" style="display: inline-block;vertical-align: top;">' +
            '<ul class="pagination pull-right" style="margin: 0px;">' +
            '<li class="page-item page-first"><a href="#" class="page-link" title="Primero"><i class="fa fa-angle-double-left"></i></a></li>' +
            '<li class="page-item page-pre"><a href="#" class="page-link" title="Previos"><i class="fa fa-angle-left"></i></a></li>' +
            '<li class="page-item page-next"><a href="#" class="page-link" title="Siguientes"><i class="fa fa-angle-right"></i></a></li>' +
            '<li class="page-item page-last"><a href="#" class="page-link" title="Último"><i class="fa fa-angle-double-right"></i></a></li></ul></div>');

        self.actionsDiv = self.portlet.find('.portlet-title').find('.paginator');

        if (!self.actionsDiv.length) {
            self.actionsDiv = $('<div class="paginator"></div>');
            self.portlet.find('.portlet-title').append(self.actionsDiv);
        }

        self.actionsDiv.append(self.tablePaginatorDiv);

        self.portlet.find('.tablefilter-action-button').click(self.openFilters);
        self.portlet.find('.page-next > a').click(self.next);
        self.portlet.find('.page-pre > a').click(self.pre);
        self.portlet.find('.page-first > a').click(self.first);
        self.portlet.find('.page-last > a').click(self.last);
    };

    this.openFilters = function () {
        self.portlet.find('.tablefilter-filters').fadeIn(500);
        self.portlet.find('.actions').not('.tablefilter-actions').hide();
        self.portlet.find('.actions').parent().append(
            $('<div></div>')
                .fadeIn(500)
                .addClass('actions tablefilter-actions')
                .append($('<a href="javascript:;" class="btn btn-circle btn-default"><i class="fa fa-times"></i> Cancelar</a>')
                    .click(self.closeFilters)
                )
                .append($('<a href="javascript:;" class="btn btn-circle btn-default"><i class="fa fa-check"></i> Aplicar Filtros</a>')
                    .click(self.applyFilters)
                )
        );
        $('#creationFromFilter').val(moment().format('DD/MM/YYYY'));
        $('#creationToFilter').val(moment().format('DD/MM/YYYY'));

        $('#creationFromFilter').datepicker('endDate', moment().toDate());
        $('#creationFromFilter').datepicker('update');
        $('#creationToFilter').datepicker('endDate', moment().toDate());
        $('#creationToFilter').datepicker('update');
    };

    this.closeFilters = function () {
        self.portlet.find('.tablefilter-filters').hide();
        self.portlet.find('.tablefilter-actions').hide();
        self.portlet.find('.actions').not('.tablefilter-actions').fadeIn(500);
        self.portlet.find('.tablefilter-actions').remove();
    };

    this.getFilters = function () {
        var settedFilters = [];

        $form.find('select,input').each(function () {
            if ($(this).val() != '') {
                var filter = {};
                if ($(this).prop("tagName").toLowerCase() == 'select') {
                    filter.label = $(this).find('option:selected').text();
                    filter.value = $(this).val();
                    filter.name = $(this).attr("name");
                    filter.multiple = $(this).data("filter-multiple") != null ? $(this).data("filter-multiple") : true;
                    $(this).find('option:selected').prop('disabled', true);
                } else if ($(this).prop("tagName").toLowerCase() == 'input') {
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

    this.first = function () {
        var newOffset;
        if(self.offset == 0) {
            console.log('You are already in the first page');
            return;
        }
        newOffset = 0;
        self.loadData(newOffset, self.filters);
    };

    this.last = function () {
        var newOffset;
        if(self.offset + self.limit >= self.total) {
            console.log('You are already in the last page');
            return;
        }

        newOffset = self.offset;

        while(newOffset + self.limit < self.total){
            newOffset += self.limit;
        }

        self.loadData(newOffset, self.filters);
    };

    this.pre = function () {
        console.log('--- pre ---');
        console.log('offset', self.offset);
        console.log('limit', self.limit);
        console.log('totla', self.total);
        var newOffset;
        if (self.offset == 0) {
            console.log('No pre is available');
            return;
        } else if (self.offset - self.limit < 0) {
            newOffset = 0;
        } else {
            newOffset = self.offset - self.limit;
        }

        self.loadData(newOffset, self.filters);
    };

    this.next = function () {
        console.log('--- next ---');
        console.log('offset', self.offset);
        console.log('limit', self.limit);
        console.log('totla', self.total);
        var newOffset;
        if (self.offset + self.limit >= self.total) {
            console.log('No next is available');
            return;
        } else {
            newOffset = self.offset + self.limit;
        }

        self.loadData(newOffset, self.filters);
    };

    this.applyFilters = function() {
        var newFilters = self.getFilters();

        for(var i=0; i<newFilters.length; i++){
            // If its not multiple filter, find the same int he list that alredy exists and destroy it!
            if(!newFilters[i].multiple){
                self.filters = $.grep(self.filters, function(value) {
                    return value.name != newFilters[i].name;
                });
            }
        }
        newFilters = $.merge(self.filters, newFilters);

        self.loadData(0, newFilters);
    };

    this.resetFilters = function () {
        self.filters = [];
    };

    this.refreshInfo = function () {
        console.log('new total', self.total);
        console.log('new offset', self.offset);
    };

    this.loadData = function (newOffset, filters) {
        console.log(newOffset);

        var jsonParams = {};

        $('.nav.nav-tabs.pull-left').find('a').each(function(index, value){
            if($(this).attr('aria-expanded') == undefined ) jsonParams['collectionManagement'] = 'true';
            if($(this).attr('aria-expanded') == 'true'){
                jsonParams['collectionManagement'] = $(this).attr('data-management');
            }
        });

        for (var i = 0; i < filters.length; i++) {
            if (jsonParams[filters[i].name] == undefined) {
                jsonParams[filters[i].name] = [];
            }
            jsonParams[filters[i].name][jsonParams[filters[i].name].length] = filters[i].value;
        }

        jsonParams["offset"] = newOffset;
        jsonParams["limit"] = self.limit;

        console.log(jsonParams);

        defaultAjax({
            url: self.url,
            type: self.method,
            data: jsonParams,
            success: function (data) {
                self.portlet.find('.table:first > tbody').html($(data));
                self.offset = newOffset;
                self.filters = filters;
                self.total = $(".table:first > tbody > tr:first").length ? $(".table:first > tbody > tr:first").attr('data-total') : 0;
                self.refreshInfo();
                self.paintFilters();
                self.closeFilters();

            }
        });

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
                            console.log($(this));
                            console.log($(this).attr("data-filter-index"));
                            self.removeFilter($(this).attr("data-filter-index"));
                        })
                    )
                );
            }
            $(self.portlet.find('.portlet-body').get(0)).prepend(filterTagsContainer);
        }
    };

    this.removeFilter = function (filterIndex) {
        if (self.portlet.find('select[name="' + self.filters[filterIndex].name + '"]').length > 0) {
            self.portlet.find('select[name="' + self.filters[filterIndex].name + '"] option[value="' + self.filters[filterIndex].value + '"]').prop('disabled', false);;
        }
        self.filters.splice(filterIndex, 1);
        self.callBackend();
    };

    this.init();
}