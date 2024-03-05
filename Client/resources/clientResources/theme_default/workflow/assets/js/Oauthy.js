var oauthy = new Oauthy();

function Oauthy() {
    var self = this;
    var currentRequest;
    var networkConfig = {};
    this.setupNetwork = function (network, clientId, redirectUrl, defaultScopes) {
        networkConfig[network] = {
            clientId: clientId,
            redirectUrl: redirectUrl.substring(0, 1) == '/' ? document.location.origin + redirectUrl : redirectUrl,
            defaultScopes: defaultScopes
        }
    };
    this.login = function (network, onSuccess, onError, scopes) {
        currentRequest = {
            network: network,
            scopes: scopes != null ? scopes : networkConfig[network].defaultScopes,
            onSuccess: onSuccess,
            onError: onError
        };

        // Open popup
        var url;
        switch (network) {
            case 'facebook':
                url = "https://www.facebook.com/v2.8/dialog/oauth?client_id=" + networkConfig[network].clientId + "&redirect_uri=" + networkConfig[network].redirectUrl + (currentRequest.scopes != null ? "&scope=" + currentRequest.scopes : '');
                break;
            case 'linkedin':
                url = "https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=" + networkConfig[network].clientId + "&redirect_uri=" + networkConfig[network].redirectUrl + "&scope=" + currentRequest.scopes;
                break;
            case 'google':
                url = "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=" + networkConfig[network].clientId + "&redirect_uri=" + networkConfig[network].redirectUrl + "&scope=" + currentRequest.scopes+"&access_type=offline";
                break;
            case 'windows':
                url = "https://login.live.com/oauth20_authorize.srf?client_id=" + networkConfig[network].clientId + "&redirect_uri=" + networkConfig[network].redirectUrl + "&scope=" + currentRequest.scopes + "&response_type=code";
                break;
            case 'yahoo':
                url = "https://api.login.yahoo.com/oauth2/request_auth?client_id=" + networkConfig[network].clientId + "&redirect_uri=" + networkConfig[network].redirectUrl + "&scope=" + currentRequest.scopes + "&response_type=code";
                break;
            case 'mercadolibre':
                url = "http://auth.mercadolibre.com.ar/authorization?client_id=" + networkConfig[network].clientId + "&redirect_uri=" + networkConfig[network].redirectUrl + "&scope=" + currentRequest.scopes + "&response_type=code";
                break;
        }
        console.log("opening: "+url);
        window.open(url, "", "width=1000,height=800");
    };
    this.success = function (code) {
        currentRequest.onSuccess(code);
    }
    this.error = function () {
        currentRequest.onError();
    }
}