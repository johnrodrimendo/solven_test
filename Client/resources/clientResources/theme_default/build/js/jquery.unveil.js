!function(a){a.fn.unveil=function(t,i){var e,o=a(window),s=t||0,n=1<window.devicePixelRatio?"data-src-retina":"data-src",r=this;function u(){var t=r.filter(function(){var t=a(this);if(!t.is(":hidden")){var i=o.scrollTop(),e=i+o.height(),n=t.offset().top,r=n+t.height();return i-s<=r&&n<=e+s}});e=t.trigger("unveil"),r=r.not(e)}return this.one("unveil",function(){var t=this.getAttribute(n);(t=t||this.getAttribute("data-src"))&&(this.setAttribute("src",t),"function"==typeof i&&i.call(this))}),o.scroll(u),o.resize(u),u(),this}}(window.jQuery||window.Zepto);