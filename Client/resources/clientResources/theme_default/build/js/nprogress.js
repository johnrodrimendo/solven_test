!function(e,n){"function"==typeof define&&define.amd?define(n):"object"==typeof exports?module.exports=n():e.NProgress=n()}(this,function(){var n,t,s={version:"0.2.0"},a=s.settings={minimum:.08,easing:"linear",positionUsing:"",speed:200,trickle:!0,trickleSpeed:200,showSpinner:!0,barSelector:'[role="bar"]',spinnerSelector:'[role="spinner"]',parent:"body",template:'<div class="bar" role="bar"><div class="peg"></div></div><div class="spinner" role="spinner"><div class="spinner-icon"></div></div>'};function u(e,n,t){return e<n?n:t<e?t:e}function c(e){return 100*(-1+e)}s.configure=function(e){var n,t;for(n in e)void 0!==(t=e[n])&&e.hasOwnProperty(n)&&(a[n]=t);return this},s.status=null,s.set=function(n){var e=s.isStarted();n=u(n,a.minimum,1),s.status=1===n?null:n;var t=s.render(!e),r=t.querySelector(a.barSelector),i=a.speed,o=a.easing;return t.offsetWidth,l(function(e){""===a.positionUsing&&(a.positionUsing=s.getPositioningCSS()),f(r,function(e,n,t){var r;r="translate3d"===a.positionUsing?{transform:"translate3d("+c(e)+"%,0,0)"}:"translate"===a.positionUsing?{transform:"translate("+c(e)+"%,0)"}:{"margin-left":c(e)+"%"};return r.transition="all "+n+"ms "+t,r}(n,i,o)),1===n?(f(t,{transition:"none",opacity:1}),t.offsetWidth,setTimeout(function(){f(t,{transition:"all "+i+"ms linear",opacity:0}),setTimeout(function(){s.remove(),e()},i)},i)):setTimeout(e,i)}),this},s.isStarted=function(){return"number"==typeof s.status},s.start=function(){s.status||s.set(0);var e=function(){setTimeout(function(){s.status&&(s.trickle(),e())},a.trickleSpeed)};return a.trickle&&e(),this},s.done=function(e){return e||s.status?s.inc(.3+.5*Math.random()).set(1):this},s.inc=function(e){var n=s.status;return n?1<n?void 0:("number"!=typeof e&&(e=0<=n&&n<.2?.1:.2<=n&&n<.5?.04:.5<=n&&n<.8?.02:.8<=n&&n<.99?.005:0),n=u(n+e,0,.994),s.set(n)):s.start()},s.trickle=function(){return s.inc()},t=n=0,s.promise=function(e){return e&&"resolved"!==e.state()&&(0===t&&s.start(),n++,t++,e.always(function(){0==--t?(n=0,s.done()):s.set((n-t)/n)})),this},s.render=function(e){if(s.isRendered())return document.getElementById("nprogress");v(document.documentElement,"nprogress-busy");var n=document.createElement("div");n.id="nprogress",n.innerHTML=a.template;var t,r=n.querySelector(a.barSelector),i=e?"-100":c(s.status||0),o=document.querySelector(a.parent);return f(r,{background:$cadena_colorweb,height:"3px",transition:"all 0 linear",transform:"translate3d("+i+"%,0,0)"}),a.showSpinner||(t=n.querySelector(a.spinnerSelector))&&b(t),o!=document.body&&v(o,"nprogress-custom-parent"),o.appendChild(n),n},s.remove=function(){y(document.documentElement,"nprogress-busy"),y(document.querySelector(a.parent),"nprogress-custom-parent");var e=document.getElementById("nprogress");e&&b(e)},s.isRendered=function(){return!!document.getElementById("nprogress")},s.getPositioningCSS=function(){var e=document.body.style,n="WebkitTransform"in e?"Webkit":"MozTransform"in e?"Moz":"msTransform"in e?"ms":"OTransform"in e?"O":"";return n+"Perspective"in e?"translate3d":n+"Transform"in e?"translate":"margin"};var r,l=(r=[],function(e){r.push(e),1==r.length&&i()});function i(){var e=r.shift();e&&e(i)}var o,d,f=(o=["Webkit","O","Moz","ms"],d={},function(e,n){var t,r,i=arguments;if(2==i.length)for(t in n)void 0!==(r=n[t])&&n.hasOwnProperty(t)&&p(e,t,r);else p(e,i[1],i[2])});function m(e){return e=function(e){return e.replace(/^-ms-/,"ms-").replace(/-([\da-z])/gi,function(e,n){return n.toUpperCase()})}(e),d[e]||(d[e]=function(e){var n=document.body.style;if(e in n)return e;for(var t,r=o.length,i=e.charAt(0).toUpperCase()+e.slice(1);r--;)if((t=o[r]+i)in n)return t;return e}(e))}function p(e,n,t){n=m(n),e.style[n]=t}function g(e,n){return 0<=("string"==typeof e?e:h(e)).indexOf(" "+n+" ")}function v(e,n){var t=h(e),r=t+n;g(t,n)||(e.className=r.substring(1))}function y(e,n){var t,r=h(e);g(e,n)&&(t=r.replace(" "+n+" "," "),e.className=t.substring(1,t.length-1))}function h(e){return(" "+(e&&e.className||"")+" ").replace(/\s+/gi," ")}function b(e){e&&e.parentNode&&e.parentNode.removeChild(e)}return s});