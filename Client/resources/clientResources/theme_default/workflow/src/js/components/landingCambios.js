var rextieUrl = rextieUri + '/api/v1/fxrates/rate/?utm_source=solven';

var doItOnce = true;
var canDoAjax = true;

var
    $changeSource = $('#changeSource'),
    $changeTarget = $('#changeTarget'),
    $changeAhorro = $('#changeAhorro'),
    $cambiosForm = $('#cambiosForm'),
    $switchCurrency = $('.switch-currency'),
    $labelSource = $('.source-currency'),
    $labelTarget = $('.target-currency'),
    $labelBuyRate = $('.buyRate'),
    $labelSellRate = $('.sellRate')
;

$(document).ready(function () {
    requestRextie($changeSource.data('currency'), $changeTarget.data('currency'), $changeSource);
});


// Formulario
if (rextieForm != null) {
    var jsonValidator = JSON.parse(rextieForm);
    $cambiosForm.validateForm(createFormValidationJson(jsonValidator, $cambiosForm));

    $changeSource.forceDoubleOnly(false, $cambiosForm.data('validatorJson').rules.sourceCurrencyAmount.min, $cambiosForm.data('validatorJson').rules.sourceCurrencyAmount.max);
    $changeTarget.forceDoubleOnly(false, $cambiosForm.data('validatorJson').rules.targetCurrencyAmount.min, $cambiosForm.data('validatorJson').rules.targetCurrencyAmount.max);
}


// Funciones
$changeSource.on('keyup', function () {
    $changeTarget.val('');
    $changeAhorro.val('');
    delay(function () {
        requestRextie($changeSource.data('currency'), $changeTarget.data('currency'), $changeSource);
    }, 1 * 1000);
});

$changeTarget.on('keyup', function () {
    $changeSource.val('');
    $changeAhorro.val('');
    delay(function () {
        requestRextie($changeSource.data('currency'), $changeTarget.data('currency'), $changeTarget);
    }, 1 * 1000);
});

$switchCurrency.on('click', switchCurrencyButton);

function switchCurrencyButton() {
    var currentSourceCurrency = $changeSource.data('currency');
    var currentSourceCurrencyLabel = $labelSource.text();
    var currentSourceCurrencySymbol = $changeSource.parent().attr('class');
    var currentTargetCurrency = $changeTarget.data('currency');
    var currentTargetCurrencyLabel = $labelTarget.text();
    var currentTargetCurrencySymbol = $changeTarget.parent().attr('class');

    $changeSource.data('currency', currentTargetCurrency);
    $labelSource.text(currentTargetCurrencyLabel);
    $changeSource.parent().attr('class', currentTargetCurrencySymbol);
    $changeTarget.data('currency', currentSourceCurrency);
    $labelTarget.text(currentSourceCurrencyLabel);
    $changeTarget.parent().attr('class', currentSourceCurrencySymbol);

    requestRextie($changeSource.data('currency'), $changeTarget.data('currency'), $changeSource);
}



// example
// requestRextie('USD', 'PEN', $changeSource);
function requestRextie(source_currency, target_currency, elem) {
    var amount = elem.val();

    if (amount === null || amount.trim() === '') {
        return;
    }

    if(!canDoAjax) {
        console.log('Another ajax is running');
        return;
    }

    // console.log('from: ' + source_currency);
    // console.log('to: ' + target_currency);
    // console.log('amount: ' + amount);

    var body = {
        source_currency: source_currency,
        target_currency: target_currency
    };

    if (elem.attr('id') === 'changeSource') {
        body.source_amount = amount;
    } else if (elem.attr('id') === 'changeTarget') {
        body.target_amount = amount;
    }

    var headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append('Accept', 'application/json');

    var options = {method: 'POST', headers: headers, body: JSON.stringify(body)};

    canDoAjax = false;

    fetch(rextieUrl, options)
        .then(function (response) {
            return response.json();
        })
        .then(function (response) {
            paintRextieResult(elem, response);
        })
        .catch(function (err) {
            console.error(err);
        })
        .finally(function() {
            canDoAjax = true;
        });
}

function paintRextieResult(elem, result) {
    if (elem.attr('id') === 'changeSource') {
        // console.log('print in target: ' + result.target_amount);
        $changeTarget.val(result.target_amount);
        if(parseFloat($changeSource.val()) < parseFloat(result.source_amount)) {
            $changeSource.val(result.source_amount);
        }
    } else if (elem.attr('id') === 'changeTarget') {
        // console.log('print in target: ' + result.source_amount);
        $changeSource.val(result.source_amount);
        if(parseFloat($changeTarget.val()) < parseFloat(result.target_amount)) {
            $changeTarget.val(result.target_amount);
        }
    }

    $changeAhorro.val(result.saved_bank_amount);

    // console.log(result.valid_until);
    // console.log(new Date(result.valid_until).toLocaleString());

    if(doItOnce) {
        $labelBuyRate.html(result.fx_rate_buy);
        $labelSellRate.html(result.fx_rate_sell);
        doItOnce = false;
    }
}


// https://neilcamm.com/blog/javascript/delay-the-keyup-event-until-the-user-stops-typing/
var delay = (function () {
    var timer = 0;
    return function (callback, ms) {
        clearTimeout(timer);
        timer = setTimeout(callback, ms);
    };
})();