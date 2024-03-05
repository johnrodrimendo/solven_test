var storage = window.localStorage;

if(storage){
    if(!storage.getItem("landing_type")){
        assignLandingType();
    }
    else if(storage.getItem("landing_type_setted")){
        if(Math.abs(parseInt(storage.getItem("landing_type_setted")) - new Date().getTime()) > 10*60000) assignLandingType();
    }
    assignClasses();
}

function assignLandingType() {
    storage.clear();
    storage.setItem("landing_type","A");
    // storage.setItem("landing_type",Math.random() < 0.5 ? "A" : "B");
    storage.setItem("landing_type_setted", `${new Date().getTime()}`);
    assignClasses();
}

function assignClasses(){
    let element = document.querySelector(".landing-default.landing-banbif-main");
    if(element){
        let linkA = 'https://solven-public.s3.amazonaws.com/img/banbif/landing_23/Banbif-landing-3-min-1.png';
        let linkB = linkA;
        switch(storage.getItem("landing_type")){
            case "A":
                // element.classList.add("yellow-landing");
                // if(document.querySelector(".inner-contain-banner > picture#banbif-picture:nth-child(1) > source:nth-child(1)")) document.querySelector(".inner-contain-banner > picture#banbif-picture:nth-child(1) > source:nth-child(1)").srcset = 'https://solven-public.s3.amazonaws.com/img/banbif/bnn_puntostc.png';
                // if(document.querySelector("#bgHome.banbif")) document.querySelector("#bgHome.banbif").src = 'https://solven-public.s3.amazonaws.com/img/banbif/bnn_puntostc.png';
                element.classList.add("green-landing");
                // if(document.querySelector(".inner-contain-banner > picture#banbif-picture:nth-child(1) > source:nth-child(1)")) document.querySelector(".inner-contain-banner > picture#banbif-picture:nth-child(1) > source:nth-child(1)").srcset = linkA;
                // if(document.querySelector("#bgHome.banbif")) document.querySelector("#bgHome.banbif").src = linkA;
                break;
            case "B":
                element.classList.add("yellow-landing");
                // if(document.querySelector(".inner-contain-banner > picture#banbif-picture:nth-child(1) > source:nth-child(1)")) document.querySelector(".inner-contain-banner > picture#banbif-picture:nth-child(1) > source:nth-child(1)").srcset = 'https://solven-public.s3.amazonaws.com/img/banbif/bnn_puntostc.png';
                // if(document.querySelector("#bgHome.banbif")) document.querySelector("#bgHome.banbif").src = 'https://solven-public.s3.amazonaws.com/img/banbif/bnn_puntostc.png';
                // if(document.querySelector(".inner-contain-banner > picture#banbif-picture:nth-child(1) > source:nth-child(1)")) document.querySelector(".inner-contain-banner > picture#banbif-picture:nth-child(1) > source:nth-child(1)").srcset = linkB;
                // if(document.querySelector("#bgHome.banbif")) document.querySelector("#bgHome.banbif").src = linkB;
                break;
        }
    }
}