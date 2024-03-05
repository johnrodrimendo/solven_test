/**
 * Created by jrodriguez on 08/09/16.
 */
$(document).ready(function () {

    $('.tablefilter').each(function () {
        $(this).data('tablefilter', new TableFilter($(this)));
    });

    // $('.tablefilter-action-button').click(function () {
    //     $(this).closest('.portlet').find('.tablefilter-filters').fadeIn(500);
    //     $(this).closest('.actions').not('.tablefilter-actions').hide();
    //     $(this).closest('.actions').parent().append(
    //         $('<div></div>')
    //             .fadeIn(500)
    //             .addClass('actions tablefilter-actions')
    //             .append($('<a href="javascript:;" class="btn btn-circle btn-default"><i class="fa fa-times"></i> Cancelar</a>')
    //                 .click(function () {
    //                     $(this).closest('.portlet').find('.tablefilter-filters').hide();
    //                     $(this).closest('.portlet').find('.tablefilter-actions').hide();
    //                     $(this).closest('.portlet').find('.actions').not('.tablefilter-actions').fadeIn(500);
    //                     $(this).closest('.portlet').find('.tablefilter-actions').remove();
    //                 })
    //             )
    //             .append($('<a href="javascript:;" class="btn btn-circle btn-default"><i class="fa fa-check"></i> Aplicar Filtros</a>')
    //                 .click(function () {
    //
    //                 })
    //             )
    //     );
    // });

});

/**
 * Class to manage the filters established
 * @constructor
 */
function TableFilter(portlet) {
    console.log("Filtros habilitados");
    var self = this;
    this.portlet = portlet;
    this.filters = [];


    this.init = function () {
        self.portlet.find('.tablefilter-action-button').click(this.openFilters);
    };

    this.openFilters = function () {
        self.portlet.find('.tablefilter-filters').fadeIn(500);
        self.portlet.find('.actions:not(#downloadButtonContainer)').not('.tablefilter-actions').hide();
        self.portlet.find('.actions').first().after(
            $('<div></div>')
                .fadeIn(500)
                .addClass('actions tablefilter-actions')
                .append($('<a href="javascript:;" class="btn btn-circle btn-default" id="cancel-filter"><i class="fa fa-times"></i> Cancelar</a>')
                    .click(self.closeFilters)
                )
                .append($('<a href="javascript:;" class="btn btn-circle btn-default" id="apply-filter" ><i class="fa fa-check"></i> Aplicar Filtros</a>')
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

        //Reset the values of all the inputs and selects
        self.portlet.find('.tablefilter-filters').find('form').find('select,input').each(function () {
            $(this).val('');
        });
    };
    this.applyFilters = function () {
        //Merge the old filters with the new ones
        var newFilters = self.getFormFilters();
        self.filters = $.merge(self.filters, newFilters);

        self.callBackend();
    };
    this.callBackend = function(){
        //Create json of params
        var jsonParams = {};

        $('.nav.nav-tabs.pull-left').find('a').each(function(index, value){
            if($(this).attr('aria-expanded') == undefined ) jsonParams['collectionManagement'] = 'true';
            if($(this).attr('aria-expanded') == 'true'){
                jsonParams['collectionManagement'] = $(this).attr('data-management');
            }
        });

        for (var i = 0; i < self.filters.length; i++) {
            if (jsonParams[self.filters[i].name] == undefined) {
                jsonParams[self.filters[i].name] = [];
            }
            jsonParams[self.filters[i].name][jsonParams[self.filters[i].name].length] = self.filters[i].value;
        }

        //Call to the backend
        var $form = self.portlet.find('.tablefilter-filters').find('form');
        console.log("Se va a mandar : " + $form.serialize());
        defaultAjax({
            url: $form.attr('action'),
            type: $form.attr('method'),
            data: jsonParams,
            success: function (data) {
                self.portlet.find('#tableResults').DataTable().clear();
                self.portlet.find('#tableResults tbody').html(data);
                self.paintFilters();
                self.closeFilters();
                setTimeout(()=>{
                    // self.portlet.find('#tableResults').DataTable()
                    //     .search("")
                    //     .draw();
                },500)

            }
        });
    };
    this.getFormFilters = function () {
        var settedFilters = [];
        self.portlet.find('.tablefilter-filters').find('form').find('select,input').each(function () {
            if ($(this).val() != '') {
                var filter = {};
                if ($(this).prop("tagName").toLowerCase() == 'select') {
                    filter.label = $(this).find('option:selected').text();
                    filter.value = $(this).val();
                    filter.name = $(this).attr("name");
                    $(this).find('option:selected').prop('disabled', true);
                } else if ($(this).prop("tagName").toLowerCase() == 'input') {
                    filter.label = $(this).val();
                    filter.value = $(this).val();
                    filter.name = $(this).attr("name");
                }
                settedFilters [settedFilters.length] = filter;
            }
        });
        console.log(settedFilters);
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
                            console.log($(this));
                            console.log($(this).attr("data-filter-index"));
                            self.removeFilter($(this).attr("data-filter-index"));
                        })
                    )
                );
            }
            self.portlet.find('.portlet-body').prepend(filterTagsContainer);
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