window.fbAsyncInit = function () {
    FB.init({
        appId: '1704545486470868',
        xfbml: true,
        version: 'v2.6'
    });

    // FB.getLoginStatus(function(response) {
    // facebookStatusChangeCallback(response);
    // });
};

(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) {
        return;
    }
    js = d.createElement(s);
    js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));


//Calls the Facebook API 
function loginWithFacebook(formId) {
    FB.login(function (response) {
        if (response.status === 'connected') {
            // Logged into your app and Facebook.
            getFacebookData(formId);

        } else if (response.status === 'not_authorized') {
            // The person is logged into Facebook, but not your app.
            document.getElementById('status').innerHTML = 'Please log '
                + 'into this app.';
        } else {
            // The person is not logged into Facebook, so we're not sure if
            // they are logged into this app or not.
            document.getElementById('status').innerHTML = 'Please log '
                + 'into Facebook.';
        }
    }, {scope: 'public_profile,email,user_birthday,user_location'});
}

// Extract the information required from facebook
function getFacebookData(formId) {
    console.log('Welcome!  Fetching your information.... ');
    FB.api('/me?fields=email,name,first_name,last_name,age_range,link,gender,locale,picture,timezone,updated_time,verified,location,birthday',
        function (response) {
            console.log('Se obtuvo la info de face: '
                + JSON.stringify(response));

            var form = document.forms[formId];

            if (response.id != undefined)
                addHiddenToForm(form, 'facebookId', response.id);
            if (response.email != undefined)
                addHiddenToForm(form, 'facebookEmail', response.email);
            if (response.name != undefined)
                addHiddenToForm(form, 'facebookName', response.name);
            if (response.first_name != undefined)
                addHiddenToForm(form, 'facebookFirstName', response.first_name);
            if (response.last_name != undefined)
                addHiddenToForm(form, 'facebookLastName', response.last_name);
            if (response.age_range != undefined && response.age_range.max != undefined)
                addHiddenToForm(form, 'facebookAgeMax', response.age_range.max);
            if (response.age_range != undefined && response.age_range.min != undefined)
                addHiddenToForm(form, 'facebookAgeMin', response.age_range.min);
            if (response.link != undefined)
                addHiddenToForm(form, 'facebookLink', response.link);
            if (response.gender != undefined)
                addHiddenToForm(form, 'facebookGender', response.gender);
            if (response.locale != undefined)
                addHiddenToForm(form, 'facebookLocale', response.locale);
            if (response.picture != undefined && response.picture.data != undefined && response.picture.data.url != undefined)
                addHiddenToForm(form, 'facebookPicture', response.picture.data.url);
            if (response.timezone != undefined)
                addHiddenToForm(form, 'facebookTimeZone', response.timezone);
            if (response.updated_time != undefined)
                addHiddenToForm(form, 'facebookUppdatedTime', response.updated_time);
            if (response.verified != undefined)
                addHiddenToForm(form, 'facebookVerified', response.verified);
            if (response.location != undefined && response.location.name != undefined)
                addHiddenToForm(form, 'facebookLocation', response.location.name);
            if (response.birthday != undefined)
                addHiddenToForm(form, 'facebookBirthday', response.birthday);

            formSubmit();

        });
}