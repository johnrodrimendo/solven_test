/**
 * Created by jrodriguez on 30/12/16.
 */

jQuery.fn.portletTablePaginator = function (url, limit) {
    new TablePaginator($(this), url, limit, 0);
};

function TablePaginator(portlet, url, limit, offset) {

    var self = this;
    this.url = url;
    this.limit = limit;
    this.offset = offset;
    this.total = 0;
    this.portlet = portlet;
    this.tablePaginatorInfoDiv;
    this.tablePaginatorDiv;

    this.init = function () {
        // Creates the paginator elements
        self.tablePaginatorInfoDiv = $('<div class="table-paginator-info" style="display: inline-block;vertical-align: top;margin-top: .5em;margin-right: 1em;"></div>');
        self.tablePaginatorDiv = $('<div class="table-paginator paging_bootstrap_number" style="display: inline-block;vertical-align: top;">' +
            '<ul class="pagination pull-right" style="margin: 0px;">' +
            '<li class="page-pre"><a href="#" title="Previos"><i class="fa fa-angle-left"></i></a></li>' +
            '<li class="page-next"><a href="#" title="Siguientes"><i class="fa fa-angle-right"></i></a></li></ul></div>');

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
        self.tablePaginatorDiv.find('.page-pre > a').click(this.pre);

        // Paint the info
        self.refreshInfo();
    };

    this.next = function () {
        var newOffset;
        if (self.offset + self.limit > self.total) {
            console.log('No next is available');
            return;
        } else {
            newOffset = self.offset + self.limit;
        }

        self.loadData(newOffset);
    };

    this.pre = function () {
        var newOffset;
        if (self.offset == 0) {
            console.log('No pre is available');
            return;
        } else if (self.offset - self.limit < 0) {
            newOffset = 0;
        } else {
            newOffset = self.offset - self.limit;
        }

        self.loadData(newOffset);
    };

    this.loadData = function (newOffset) {
        defaultAjax({
            url: self.url,
            type: 'GET',
            data: {
                offset: newOffset,
                limit: self.limit
            },
            success: function (data) {
                self.portlet.find('.table:first > tbody').replaceWith($(data));
                self.offset = newOffset;
                self.refreshInfo();
            }
        })
    };

    this.refreshInfo = function () {
        // Refresh the total
        if (self.portlet.find('.table:first > tbody[data-table-paginator-total]').length) {
            self.total = self.portlet.find('.table:first > tbody[data-table-paginator-total]').data('table-paginator-total');
            self.portlet.find('.table:first > tbody[data-table-paginator-total]').removeAttr('data-table-paginator-total');
        }

        // Paint the info
        self.tablePaginatorInfoDiv.html('<b>' + (self.offset + 1) + '</b>-<b>'
            + (self.offset + self.limit > self.total ? self.total : self.offset + self.limit) + '</b> de <b>' + self.total + '</b>');

        // invalidate buttons if necesary
        if (self.offset == 0) {
            self.tablePaginatorDiv.find('.page-pre').addClass('disabled')
        } else {
            self.tablePaginatorDiv.find('.page-pre').removeClass('disabled')
        }

        if (self.offset + self.limit > self.total) {
            self.tablePaginatorDiv.find('.page-next').addClass('disabled')
        } else {
            self.tablePaginatorDiv.find('.page-next').removeClass('disabled')
        }
    };

    this.init();
}