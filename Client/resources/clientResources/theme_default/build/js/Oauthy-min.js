var oauthy=new Oauthy;function Oauthy(){var e,o={};this.setupNetwork=function(e,c,t,i){o[e]={clientId:c,redirectUrl:"/"==t.substring(0,1)?document.location.origin+t:t,defaultScopes:i}},this.login=function(c,t,i,r){var s;switch(e={network:c,scopes:null!=r?r:o[c].defaultScopes,onSuccess:t,onError:i},c){case"facebook":s="https://www.facebook.com/v2.8/dialog/oauth?client_id="+o[c].clientId+"&redirect_uri="+o[c].redirectUrl+(null!=e.scopes?"&scope="+e.scopes:"");break;case"linkedin":s="https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id="+o[c].clientId+"&redirect_uri="+o[c].redirectUrl+"&scope="+e.scopes;break;case"google":s="https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id="+o[c].clientId+"&redirect_uri="+o[c].redirectUrl+"&scope="+e.scopes+"&access_type=offline";break;case"windows":s="https://login.live.com/oauth20_authorize.srf?client_id="+o[c].clientId+"&redirect_uri="+o[c].redirectUrl+"&scope="+e.scopes+"&response_type=code";break;case"yahoo":s="https://api.login.yahoo.com/oauth2/request_auth?client_id="+o[c].clientId+"&redirect_uri="+o[c].redirectUrl+"&scope="+e.scopes+"&response_type=code";break;case"mercadolibre":s="http://auth.mercadolibre.com.ar/authorization?client_id="+o[c].clientId+"&redirect_uri="+o[c].redirectUrl+"&scope="+e.scopes+"&response_type=code"}console.log("opening: "+s),window.open(s,"","width=1000,height=800")},this.success=function(o){e.onSuccess(o)},this.error=function(){e.onError()}}