!function(e){e(window).scroll(function(){var t,n,o=(t=window.innerHeight,!(n=document.compatMode)&&e.support.boxModel||(t="CSS1Compat"==n?document.documentElement.clientHeight:document.body.clientHeight),t),i=document.documentElement.scrollTop?document.documentElement.scrollTop:document.body.scrollTop,c=[];e.each(e.cache,function(){this.events&&this.events.inview&&c.push(this.handle.elem)}),c.length&&e(c).each(function(){var t=e(this),n=t.offset().top,c=t.height(),d=t.data("inview")||!1;i>n+c||i+o<n?d&&(t.data("inview",!1),t.trigger("inview",[!1])):i<n+c&&(d||(t.data("inview",!0),t.trigger("inview",[!0])))})}),e(function(){e(window).scroll()})}(jQuery);