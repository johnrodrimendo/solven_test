function TableFilter(t){var e=this;this.portlet=t,this.filters=[],this.init=function(){e.portlet.find(".tablefilter-action-button").click(this.openFilters)},this.openFilters=function(){e.portlet.find(".tablefilter-filters").fadeIn(500),e.portlet.find(".actions").not(".tablefilter-actions").hide(),e.portlet.find(".actions").parent().append($("<div></div>").fadeIn(500).addClass("actions tablefilter-actions").append($('<a href="javascript:;" class="btn btn-circle btn-default"><i class="fa fa-times"></i> Cancelar</a>').click(e.closeFilters)).append($('<a href="javascript:;" class="btn btn-circle btn-default"><i class="fa fa-check"></i> Aplicar Filtros</a>').click(e.applyFilters))),$("#creationFromFilter").val(moment().format("DD/MM/YYYY")),$("#creationToFilter").val(moment().format("DD/MM/YYYY")),$("#creationFromFilter").datepicker("endDate",moment().toDate()),$("#creationFromFilter").datepicker("update"),$("#creationToFilter").datepicker("endDate",moment().toDate()),$("#creationToFilter").datepicker("update")},this.closeFilters=function(){e.portlet.find(".tablefilter-filters").hide(),e.portlet.find(".tablefilter-actions").hide(),e.portlet.find(".actions").not(".tablefilter-actions").fadeIn(500),e.portlet.find(".tablefilter-actions").remove(),e.portlet.find(".tablefilter-filters").find("form").find("select,input").each(function(){$(this).val("")})},this.applyFilters=function(){var t=e.getFormFilters();e.filters=$.merge(e.filters,t),e.callBackend()},this.callBackend=function(){var t={};$(".nav.nav-tabs.pull-left").find("a").each(function(e,i){null==$(this).attr("aria-expanded")&&(t.collectionManagement="true"),"true"==$(this).attr("aria-expanded")&&(t.collectionManagement=$(this).attr("data-management"))});for(var i=0;i<e.filters.length;i++)null==t[e.filters[i].name]&&(t[e.filters[i].name]=[]),t[e.filters[i].name][t[e.filters[i].name].length]=e.filters[i].value;var a=e.portlet.find(".tablefilter-filters").find("form");console.log("Se va a mandar: "+a.serialize()),defaultAjax({url:a.attr("action"),type:a.attr("method"),data:t,success:function(t){e.portlet.find("#tableResults tbody").html(t),e.paintFilters(),e.closeFilters()}})},this.getFormFilters=function(){var t=[];return e.portlet.find(".tablefilter-filters").find("form").find("select,input").each(function(){if(""!=$(this).val()){var e={};"select"==$(this).prop("tagName").toLowerCase()?(e.label=$(this).find("option:selected").text(),e.value=$(this).val(),e.name=$(this).attr("name"),$(this).find("option:selected").prop("disabled",!0)):"input"==$(this).prop("tagName").toLowerCase()&&(e.label=$(this).val(),e.value=$(this).val(),e.name=$(this).attr("name")),t[t.length]=e}}),console.log(t),t},this.paintFilters=function(){var t=e.portlet.find(".portlet-body").find("#filterTagsContainer");if(t.length>0&&t.remove(),e.filters.length>0){t=$('<div id="filterTagsContainer" style="margin-bottom: 10px">Filtrado por </div>');for(var i=0;i<e.filters.length;i++)t.append($('<span class="label label-info" style="margin-right: 4px;">'+e.filters[i].label+" </span>").append($('<a href="javascript:;" data-filter-index="'+i+'"><i class="fa fa-times"  style="color:white"></i></a>').click(function(){console.log($(this)),console.log($(this).attr("data-filter-index")),e.removeFilter($(this).attr("data-filter-index"))})));e.portlet.find(".portlet-body").prepend(t)}},this.removeFilter=function(t){e.portlet.find('select[name="'+e.filters[t].name+'"]').length>0&&e.portlet.find('select[name="'+e.filters[t].name+'"] option[value="'+e.filters[t].value+'"]').prop("disabled",!1),e.filters.splice(t,1),e.callBackend()},this.init()}$(document).ready(function(){$(".tablefilter").each(function(){$(this).data("tablefilter",new TableFilter($(this)))})});