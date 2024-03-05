function Tp(options) {

    var settings = $.extend({
        // These are the defaults.
        container: '#camera',
        userFileConfig: [],
        showThumbnails: true,
        thumbnails: [], // To do: if image is uploaded
        numPics: 1,
        msgBottom: '',
        messages: [],
        presignedUrl : null,
        extraFilename : null
    }, options);

    var md = new MobileDetect(window.navigator.userAgent);

    var current_document_index = 0;
    var binded = false;
    var IMG_WIDTH = 1600;
    var IMG_HEIGHT = 1600;
    var IMG_WIDTH_UPLOAD = 1600;
    var IMG_HEIGHT_UPLOAD = 1600;
    var base64Img;
    var mediaRecorder = null;
    var blobsRecorded = [];
    var videoRecordedLinkGenerated = false;
    var MAX_BLOBS_VIDEO_SIZE = 10;
    var finalBlobData = null;
    var recordingUploaded = false;
    var deviceName;
    let streamData = null;
    let validateImageInProgress = false;
    let intervalReference = null;
    let identityDocumentsIds = [25,26,56,57];
    let docType = null;
    let boxDocumentReferenceDefaultPercentage = 0.95;
    let firstLabelRequest = null;
    let maxMillisecondsForLabelsRequest = 20000;
    let lastFrameContainerWidth = null;
    let lastFrameContainerWidthValidatorInterval = null;

    this.init = function () {

        resetComponent();
        if (!options.notAskingPermission) {
            checkCompatibility();
            console.log('Compatibility: ' + checkCompatibility());
        } else console.log("Not asking permission");

    };

    this.uploadPictures = function (finishFunction) {
        uploadPictures(finishFunction);
    };

    $('#nextButton').on('click', function () {
        current_document_index++;

        $("#nextButton").addClass("block-hidden");
        $("#newUpload").addClass("block-hidden");
        $("#sendButtonButton").addClass("block-hidden");
        $("#skipButton").addClass("block-hidden");
        resetComponent();
        if (video) {
            stopStream();
        }
        if (video) {
            stopStream();
        }
        updateBranding();
    });

    $('#newUpload').on('click', function (e) {
        e.preventDefault();
        $(".delete-pic").click();
    });

    // Reset component to initial state
    function resetComponent() {
        if ((checkCompatibility() || md.mobile() || md.os() == "AndroidOS" || md.os() == "iOS") && !alwaysHideTakeButton())
            $("#takeButton").removeClass('block-hidden');
        else
            $("#takeButton").addClass('block-hidden');
        // $("#uploadButton").removeClass('block-hidden');
        // lastDegree = null;
        if (settings.userFileConfig.length > 0 && settings.userFileConfig[current_document_index].skipAskPermission) $("#takeButton").addClass('block-hidden');
        // General component
        var wrapComponent = $('<div class="tp-wrap"></div>');
        var buttons = $('#buttons');
        var filenameToShow = settings.userFileConfig[current_document_index].name;
        var imgThumbnail = "<i class=\"icon icon-image-outline\"></i>";

        if (settings.userFileConfig[current_document_index].thumbnail != null && settings.userFileConfig[current_document_index].thumbnail) {
            imgThumbnail = `<img src="${'/img/' + settings.userFileConfig[current_document_index].thumbnail}" alt="" class="preview-thumbnail"/>`;
        }


        // var frameContainer = "<div class='frame-container'><div class='frame-border'> <img src='/img/marco-validacion_sombra.svg' alt=''/></div></div>";
        var frameContainer = "<div class='frame-container'><div class='frame-border'> </div></div>";
        var barMsg = (settings.msgBottom) ? $('<div class="msg-block"><p id="msg">' + settings.msgBottom + '</p></div>') : '';
        var barMsgSecondary = '';
        var initialMessage = "Sube el archivo de tu ";
        if(isBDS) initialMessage = "Subí el archivo del ";
        // var barMsg = (settings.msgBottom) ? $('<div class="msg-block msg-bottom"><p id="msg">' + '</p></div>') : '';
        var uploadBox = settings.userFileConfig[current_document_index].onlyImageFormat ? $('<div class="upload-block"><label for="upload" class="upload-box"><span class="box">' + imgThumbnail + '<span class="text-white">'+initialMessage+'<strong>' + filenameToShow + '</strong> aquí</span></span><input type="file" id="upload" accept="image/*" ' + (settings.userFileConfig[current_document_index].id == 23 ? 'capture="camera' : '') + ' multiple="true"/></label></div>') : $('<div class="upload-block"><label for="upload" class="upload-box"><span class="box">' + imgThumbnail + '<span class="text-white">'+initialMessage+'<strong>' + filenameToShow + '</strong> aquí</span></span><input type="file" id="upload" accept="image/*, application/pdf, .pdf, application/msword, .doc, application/vnd.openxmlformats-officedocument.wordprocessingml.document, .docx" ' + (settings.userFileConfig[current_document_index].id == 23 ? 'capture="camera' : '') + ' multiple="true"/></label></div>');
        var cameraBox = $('<div class="camera-block">' + frameContainer + '<video id="camera-stream" autoplay loop muted playsinline style=""></video><canvas id="principalCanvas"></canvas><canvas id="overlayCanvas" witdth="100%" style="display:block;position:absolute;top:0;min-widht:100%;widht:100%;height:100%;left:0;"></canvas><img id="snap"/><div class="controls-widgets"><a href="#" id="take-photo" title="Tomar foto" data-toggle="tooltip" data-placement="top" title="Tómate una foto"><i class="icon icon-camera"></i></a></div></div>');
        var radios = '',
            component = '';

        // Add document type radios
        var radioItems = $('<ul></ul>');

        if (settings.userFileConfig.length > 1) {
            settings.userFileConfig.sort(compare);

            for (var i = 0; i < settings.userFileConfig.length; i++) {
                settings.userFileConfig[i].index = i + 1;
            }

            for (var i = 0; i < settings.userFileConfig.length; i++) {
                var imgSize = getUserFiles(settings.userFileConfig[i].id);

                if (current_document_index == i) {
                    docType = settings.userFileConfig[i].id;
                }

                var items =
                    '<li>' +
                    '<label for="documentType-' + settings.userFileConfig[i].id + '" class="h-input-radio ' + (i == current_document_index ? 'radio-selected' : '') + '">' +
                    '<input type="radio" name="documentType" class="documentType" id="documentType-' + settings.userFileConfig[i].id + '" value="' + settings.userFileConfig[i].id + '"/>' +
                    '<div class="option-wrapper">' +
                    '<div class="iconic-option-inner">' +
                    '<div class="radio inline"></div>' +
                    // '<h5 class="inline">' + settings.userFileConfig[i].index + '</h5>' +
                    '</div>' +
                    '</div>' +
                    '</label>' +
                    '</li>';
                radioItems.append(items);
                wrapComponent.addClass(settings.userFileConfig[current_document_index].backgroundClass);
            }
        }

        $("#nextButton").addClass("block-hidden");
        $("#newUpload").addClass("block-hidden");
        $("#sendButtonButton").addClass("block-hidden");
        if (!settings.userFileConfig[current_document_index].required) {
            $("#skipButton").removeClass("block-hidden");
        }
        //console.info(settings.userFileConfig);
        var msg = settings.userFileConfig[current_document_index].id == 23 ? settings.messages["selfie"] : settings.messages["uploadDoc"];
        var tooltip = "";

        if (settings.userFileConfig[current_document_index].showTooltip) {
            tooltip = '<a href="#" id="open-modal-question60"> aquí</a>';
            $("#modalTituloQuestion_60").text(settings.userFileConfig[current_document_index].name);
            $("#modalMessageQuestion_60").html(settings.userFileConfig[current_document_index].tooltip);
        }

        barMsg = $('<div class="msg-block"><p id="msg">' + '</p></div>');
        wrapComponent.append(barMsg);

        if (settings.userFileConfig[current_document_index].showImage) {
            wrapComponent.append('<div class="image"><img src= "' + system.contextPath + '/img/' + settings.userFileConfig[current_document_index].thumbnail + '"></div>');
        }

        if (settings.userFileConfig[current_document_index].id != 23) {
            var addRequiredSpan = !settings.userFileConfig[current_document_index].required ? '<span required="">(no obligatorio)</span>' : '';
            wrapComponent.append('<div class="status">' + (settings.userFileConfig[current_document_index].name).toUpperCase() + addRequiredSpan + '</div>');
        }

        radios = $('<div class="main-inputs"></div>').append(radioItems);

        // Generate the component
        // if (!checkCompatibility()) {
        //     component = wrapComponent.append(radios);
        // } else {
        //     component = wrapComponent.append(radios, barMsg);
        // }

        $(settings.container).html(wrapComponent);

        if (!checkCompatibility()) {
            $(settings.container).append(radios);
        } else {
            $(settings.container).append(radios, barMsg);
        }

        // Check if compactibility
        $('.documentType').on('change', function () {
            $(this).closest('.main-inputs').addClass('block-hidden');
            //docType = $(this).val();
            if (checkCompatibility() || md.mobile() || md.os() == "AndroidOS" || md.os() == "iOS") {
                wrapComponent.html(uploadBox);
                $('#upload').on('change', function () {

                    readURL($(this)[0], docType);
                    if (current_document_index + 1 == settings.userFileConfig.length) {
                        $('#nextButton').addClass('block-hidden');
                        $('#newUpload').removeClass('block-hidden');
                        $('#sendButton').removeClass('block-hidden');
                    } else {
                        $('#nextButton').removeClass('block-hidden');
                        $('#newUpload').removeClass('block-hidden');
                        $('#sendButton').addClass('block-hidden');
                    }
                });
            } else {
                buttons.append('<a href="#" class="anchor-button bg-red tpAction" data-target="take">Tomar una foto</a><a href="#" class="anchor-button bg-white tpAction" data-target="upload">Subir foto</a>');
            }
            var msg, i;

            if (settings.userFileConfig.length != null) {
                for (i = 0; i < settings.userFileConfig.length; i++) {
                    if (settings.userFileConfig[i].id == docType) {
                        msg = "Sube o toma una foto de " + (settings.userFileConfig[i].name).toLowerCase();
                    }
                }
            }
            barMsg = $('<div class="msg-block"><p id="msg">' + msg + '</p><span></span></div>');
            wrapComponent.append(barMsg);
        });

        $('#upload').on('change', function () {
            readURL($(this)[0], docType);
            if (current_document_index + 1 == settings.userFileConfig.length) {
                $('#nextButton').addClass('block-hidden');
                $('#newUpload').removeClass('block-hidden');
                $('#sendButton').removeClass('block-hidden');
            } else {
                $('#nextButton').removeClass('block-hidden');
                $('#newUpload').removeClass('block-hidden');
                $('#sendButton').addClass('block-hidden');
            }
        });

        $('.main-buttons').unbind('click');

        $('.main-buttons').on('click', '.tpAction', function (e) {
            e.preventDefault();
            switch ($(this).data('target')) {
                case 'take':
                    $("#uploadButton").addClass("c-hidden");
                    $("#takeButton").addClass("c-hidden");
                    wrapComponent.html(cameraBox);
                    let principalMessage = null;
                    if(settings.userFileConfig[current_document_index].principalMessage) principalMessage = settings.userFileConfig[current_document_index].principalMessage;
                    var msg = principalMessage || (settings.userFileConfig[current_document_index].id == 23 ? settings.messages["selfie"] : (settings.messages["uploadDoc-"+settings.userFileConfig[current_document_index].id] || settings.messages["uploadDoc"]));
                    if(settings.userFileConfig.length > 0 && settings.userFileConfig[current_document_index].enableValidationModels){
                        if((images || []).find(x => x.userFileType == settings.userFileConfig[current_document_index].id)){
                            if(settings.userFileConfig[current_document_index].id == 23) msg = "¡Excelente! Si la foto está OK,<br>haz click en continuar";
                            else if(identityDocumentsIds.indexOf(settings.userFileConfig[current_document_index].id) != -1) msg = "¡Excelente! Si la foto de tu DNI está OK,<br>haz click en continuar";
                        }
                    }
                    barMsg = $('<div class="msg-block"><p id="msg">' + msg + '</p><span></span></div>');
                    wrapComponent.append(barMsg);
                    let hasBarMsgSecondary = false;
                    if(settings.userFileConfig[current_document_index].secondaryMessage){
                        hasBarMsgSecondary = true;
                        barMsgSecondary = $('<div class="msg-block-secondary" style="display: none"><p id="msg-secondary">' + settings.userFileConfig[current_document_index].secondaryMessage + '</p><span></span></div>');
                        wrapComponent.append(barMsgSecondary);
                    }

                    requestCamera(settings.userFileConfig[current_document_index].id == 23);
                    let enableValidationModels = settings.userFileConfig.length > 0 && settings.userFileConfig[current_document_index].enableValidationModels;
                    if(enableValidationModels) $('#take-photo').hide();
                    base64Img = null;
                    $('#take-photo').on('click', function () {
                        e.preventDefault();
                        var snap = takeSnapshot();
                        base64Img = snap;
                        resizeImageFromCamera(snap, IMG_WIDTH, IMG_HEIGHT, false);

                    });
                    updateBranding();
                    break;
                case 'upload':
                    if (video) {
                        stopStream();
                    }
                    uploadFile(wrapComponent, uploadBox, settings, docType);
                    break;

            }
        });

        if (settings.userFileConfig[current_document_index].showTooltip) {
            $('#open-modal-question60').on('click', function (event) {
                event.preventDefault();
                self.openMsg1Modal();
                $("#modal_msg_question60").modal('show');
            });
            this.openMsg1Modal = function () {
                $("#modal_msg_question60").modal('show');
            };
        }

        if (video) {
            stopStream();
        }
        let userFileTypeIds = settings.userFileConfig.filter(x => !x.skipAskPermission).map(x => x.id);
        userFileTypeIds.push(23);
        if (userFileTypeIds.indexOf(settings.userFileConfig[current_document_index].id) != -1) {
            if (checkCompatibility() || md.mobile() || md.os() == "AndroidOS" || md.os() == "iOS")
                $("#takeButton").click();
            else if(settings.userFileConfig[current_document_index].skipAskPermission) $("#uploadButton").click();
            else $("#takeButton").click();
            updateBranding();
        } else {
            $("#uploadButton").click();
            updateBranding();
        }
        updateBranding();
    }

    function uploadFile(wrapComponent, uploadBox, settings, docType) {
        $("#uploadButton").addClass("c-hidden");
        $("#takeButton").removeClass("c-hidden");
        wrapComponent.html(uploadBox);
        let initialMessage = "Sube la foto de tu ";
        if(isBDS) initialMessage = "Subí el archivo del ";

        let IdUserFileConfig = settings.userFileConfig[current_document_index].id;

        var msgExtra="";

        switch (IdUserFileConfig){
            case 23 : var msg = settings.messages["selfie"];
                break;
            case 25 : var msg =  initialMessage + settings.userFileConfig[current_document_index].name + " aquí";
                msgExtra = "Recuerda tomar una foto del celular, no se admiten copias o imágenes en blanco y negro."
                break;
            case 26 : var msg =  initialMessage + settings.userFileConfig[current_document_index].name + " aquí";
                msgExtra = "Recuerda tomar una foto del celular, no se admiten copias o imágenes en blanco y negro."
                break;
            default : var msg =  settings.messages["uploadDoc"];
                break;
        }

        /* if(settings.userFileConfig[current_document_index].id == 23) {
             var msg = settings.messages["selfie"];
         }

         if(settings.userFileConfig[current_document_index].id == 25){
             var msg =  initialMessage + settings.userFileConfig[current_document_index].name + " aquí";
         }

         if(settings.userFileConfig[current_document_index].id == 26){
             var msg =  initialMessage + settings.userFileConfig[current_document_index].name + " aquí";
         }*/

        //var msg = settings.userFileConfig[current_document_index].id == 23 ? settings.messages["selfie"] : settings.messages["uploadDoc"];

        //console.log("mensaje: "+msg+" doctype: "+settings.userFileConfig[current_document_index].id);

        barMsg = $('<div style="display: block!important" class="msg-block"><p id="msg">' + msg + '</p>' +
            '<p>'+ msgExtra+ '</p><span></span></div>');
        wrapComponent.append(barMsg);
        $('#upload').on('change', function () {

            //console.log("Tamanio: "+images.length);
            if (this.files[0].size < 1024 * 1024 * 10) {
                //console.log("Tamanio correcto");
                readURL($(this)[0], docType);
            } else {
                showErrorModal('El archivo tiene un tamaño más grande que el permitido');
                //console.log("Tamano maximo de archivo sobrepasado "+this.files[0].size);
                //console.log("nombre del archivo "+this.files[0].name);
            }
            //console.log("Subiendo archivo "+ $(this)[0] + " del tipo "+docType);

        });
        updateBranding();
    }

    function resizeImageFromUpload(snap, wantedWidth, wantedHeight, docType, skipResize) {
        if(typeof skipResize == "undefined") skipResize = false;
        if(skipResize == null) skipResize = false;
        var i = new Image();
        i.onload = function () {
            ratio = i.height / i.width;

            if(!skipResize){
                if (i.width < wantedWidth) {
                    wantedWidth = i.width;
                    wantedHeight = i.height;
                } else {
                    wantedHeight = wantedWidth * ratio;
                }
            }
            else{
                wantedWidth = i.width;
                wantedHeight = i.height;
            }

            var img = document.createElement('img');
            var canvas;
            var ctx;
            img.onload = function () {
                canvas = document.createElement('canvas');
                ctx = canvas.getContext('2d');
                canvas.width = wantedWidth;
                canvas.height = wantedHeight;
                ctx.drawImage(this, 0, 0, wantedWidth, wantedHeight);
                var resizeImage = canvas.toDataURL("image/jpeg", 0.9);
                saveImage(resizeImage, docType);
                if (current_document_index + 1 == settings.userFileConfig.length) {
                    $('#nextButton').addClass('block-hidden');
                    $('#newUpload').removeClass('block-hidden');
                    $('#sendButton').removeClass('block-hidden');
                } else {
                    if (errorSaving == false) {
                        $('#nextButton').removeClass('block-hidden');
                        $('#newUpload').removeClass('block-hidden');
                    } else {
                        $('#nextButton').addClass('block-hidden');
                        $('#newUpload').addClass('block-hidden');
                    }
                    $('#sendButton').addClass('block-hidden');
                }
                $("#uploadButton").removeClass("c-hidden");

                if (checkCompatibility() || md.mobile() || md.os() == "AndroidOS" || md.os() == "iOS")
                    $("#takeButton").removeClass("c-hidden");
                else
                    $("#takeButton").addClass("c-hidden");
            }
            img.src = snap;
        };
        i.src = snap;

    }

    $('#skipButton').on('click', function () {
        $("#nextButton").addClass("block-hidden");
        $("#sendButtonButton").addClass("block-hidden");
        $("#skipButton").addClass("block-hidden");
        current_document_index++;
        if (current_document_index == settings.userFileConfig.length) {
            $("#sendButton").click();
            return;
        }
        if (video) {
            stopStream();
        }
        resetComponent();
        updateBranding();
    });

    var localStream,
        video = '',
        images = [];

    var errorSaving;

    // Images storage
    function saveImage(snap, docType) {

        errorSaving = false;
        docType = (docType == null) ? settings.userFileConfig[0].id : docType;
        var files = getUserFiles(docType);
        var docTypeSettings = getUserFilesConfig(docType);
        var imgJson = {
            userFileType: docType,
            image: snap,
            fileName: ''
        };
        //debugger;

        // Validate the extension of the file
        var extension = snap.split(';')[0].split('/')[1];
        if ($.inArray(extension, ["jpg", "jpeg", "png", "pdf", "JPG", "JPEG", "PNG", "PDF", "msword", "vnd.openxmlformats-officedocument.wordprocessingml.document"]) === -1) { // TODO HARD
            showErrorModal('El formato del archivo no es v&aacute;lido');
            errorSaving = true;
            return;
        }

        // Validate the max amount of files
        if (files.length >= docTypeSettings.maxFiles) {
            showErrorModal('Maximo de im&aacute;genes');
            errorSaving = true;
            return;
        }

        images.push(imgJson);

        resetComponent();
        paintThumbnails();
    }

    function paintThumbnails() {
        var thumbnails = $('<div id="thumbs-block" class="thumbs-block"></div>');
        var thumbsContent = $('<ul></ul>');
        var thumb = '',
            thumbs;

        for (var i = 0; i < images.length; i++) {

            var imageData = images[i];
            //DEFAULT PDF IMAGE FOR MARIO
            var imageToShow = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAU8AAAFOCAYAAAAGkZ9XAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTM4IDc5LjE1OTgyNCwgMjAxNi8wOS8xNC0wMTowOTowMSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTcgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6QzYwMTdDNkY0N0UxMTFFOEFGRjhCQUJEQjU4N0U2NjkiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6QzYwMTdDNzA0N0UxMTFFOEFGRjhCQUJEQjU4N0U2NjkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpDNjAxN0M2RDQ3RTExMUU4QUZGOEJBQkRCNTg3RTY2OSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpDNjAxN0M2RTQ3RTExMUU4QUZGOEJBQkRCNTg3RTY2OSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Pp36gEUAAA1USURBVHja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';
            var ext = imageData.image.split(';')[0].split('/')[1];
            if (ext !== 'pdf' && ext !== 'msword' && ext !== 'vnd.openxmlformats-officedocument.wordprocessingml.document') { // TODO HARDCODED
                imageToShow = imageData.image;
                thumb =
                    '<li id="thumb-' + i + '" data-degress="0">' +
                    '<div class="circle">' +
                    '<img src=" ' + imageToShow + ' "/>' +
                    '<button class="delete-pic" style="display: none;" data-index="' + i + '"><i class="icon icon-close"></i></button>' +
                    '<button class="rotate-pic-left" data-index="' + i + '" data-degrees="0" data-rotate="L"><i class="icon icon-close"></i></button>' +
                    '<button class="rotate-pic" data-index="' + i + '" data-degrees="0" data-rotate="R"><i class="icon icon-close"></i></button>' +
                    '</div>' +
                    '</li>';
            } else {
                thumb =
                    '<li id="thumb-' + i + '" data-degress="0">' +
                    '<div class="circle">' +
                    '<img class="pdf-result" src=" ' + imageToShow + ' "/>' +
                    '<button class="delete-pic" data-index="' + i + '"><i class="icon icon-close"></i></button>' +
                    '</div>' +
                    '</li>';
            }


            thumbsContent.html(thumb);
        }

        $("#takeButton").addClass('block-hidden');
        $("#uploadButton").addClass('block-hidden');

        thumbs = thumbnails.html(thumbsContent);
        $(settings.container).find('#thumbs-block').remove();
        $(settings.container).append(thumbs);

        $('.delete-pic').on('click', function (e) {
            e.preventDefault();
            removeImage($(this).data('index'));
            $(`#thumb-${$(this).data('index')}`).data('degrees', 0);
            base64Img = undefined;
            if (video) {
                stopStream();
            }
            resetComponent();
            let userFileTypeIdsOnlyTakePhoto = settings.userFileConfig.filter(x => !x.skipAskPermission).map(x => x.id);
            userFileTypeIdsOnlyTakePhoto.push(23);
            if (userFileTypeIdsOnlyTakePhoto.indexOf(settings.userFileConfig[current_document_index].id) != -1) {
                $("#takeButton").click();
            }
            else $("#uploadButton").click();
            updateBranding();
        });


        $('.rotate-pic,.rotate-pic-left').on('click', function (e) {
            e.preventDefault();

            removeImage($(this).data('index')); // remove previous image

            var degrees;
            if ($(this).data('rotate')) {
                degrees = $(this).data('rotate') == 'L' ? Number($(this).data('degrees')) - 90 : Number($(this).data('degrees')) + 90;
            } else {
                degrees = $(this).data('degrees') ? Number($(this).data('degrees')) + 90 : Number($(this).data('degrees')) - 90;
            }

            let lastDegree = $(`#thumb-${$(this).data('index')}`).data('degrees') ? Number($(`#thumb-${$(this).data('index')}`).data('degrees')) : null;

            if (lastDegree == null) lastDegree = 0 + degrees;
            else degrees = lastDegree + degrees;

            degrees = degrees >= 360 || degrees <= -360 ? 0 : degrees;
            lastDegree = 0 + degrees;

            var snap = base64Img; // get stored image
            var idx = $(this).data('index');

            $(`#thumb-${idx}`).data('degrees', lastDegree);

            var wantedWidth = IMG_WIDTH;
            var wantedHeight = IMG_HEIGHT;

            // copy paste
            var img = document.createElement('img');
            var canvas;
            var ctx;
            var lastElementRotate = $(this);
            img.onload = function () {
                canvas = document.createElement('canvas');
                ctx = canvas.getContext('2d');

                var width = this.width, // snap real width
                    height = this.height; // snap real height
                var ratio = height / width;

                if (width < wantedWidth) {
                    wantedWidth = width;
                    wantedHeight = height;
                } else {
                    wantedHeight = wantedWidth * ratio;
                }

                canvas.width = wantedWidth;
                canvas.height = wantedHeight;

                drawImage(ctx, this, 0, 0, wantedWidth, wantedHeight, degrees);

                var resizeImage = canvas.toDataURL("image/jpeg", 0.9);
                docType = settings.userFileConfig[idx].id;

                saveImage(resizeImage, docType);

                $(`#thumb-${idx}`).data('degrees', lastDegree);

                // lastElementRotate.data('degrees',  degrees);

                if (current_document_index + 1 === settings.userFileConfig.length) {
                    $('#nextButton').addClass('block-hidden');
                    $('#newUpload').removeClass('block-hidden');
                    $('#sendButton').removeClass('block-hidden');
                } else {
                    $('#nextButton').removeClass('block-hidden');
                    $('#sendButton').addClass('block-hidden');
                    $('#newUpload').removeClass('block-hidden');
                }
                if (checkCompatibility() || md.mobile() || md.os() === "AndroidOS" || md.os() === "iOS")
                    $("#takeButton").removeClass("c-hidden");
                else
                    $("#takeButton").addClass("c-hidden");

                $("#uploadButton").removeClass("c-hidden");
            };

            img.src = snap;
            // copy paste
        });
    }

    function compare(a, b) {
        // Use toUpperCase() to ignore character casing
        var genreA = a.order;
        var genreB = b.order;

        var comparison = 0;
        if (genreA > genreB) {
            comparison = 1;
        } else if (genreA < genreB) {
            comparison = -1;
        }
        return comparison;
    }

    function readURL(input, docType) {


        if (input.files && input.files[0]) {

            for (var i = 0; i < input.files.length; i++) {
                var reader = new FileReader();

                reader.onload = function (e) {
                    // console.log("result "+e.target.result);
                    base64Img = e.target.result; // store current uploaded image
                    var ext = e.target.result.split(';')[0].split('/')[1];
                    //console.log("result files type "+ext);
                    if (ext !== 'pdf' && ext !== 'msword' && ext !== 'vnd.openxmlformats-officedocument.wordprocessingml.document') { // TODO HARDCODED
                        let skipResize = false;
                        //settings.userFileConfig[current_document_index].skipResize == true ? true : false;
                        resizeImageFromUpload(e.target.result, IMG_WIDTH_UPLOAD, IMG_HEIGHT_UPLOAD, docType, skipResize);
                    } else {
                        saveImage(e.target.result, docType);
                        if (current_document_index + 1 == settings.userFileConfig.length) {
                            $('#nextButton').addClass('block-hidden');
                            $('#newUpload').removeClass('block-hidden');
                            $('#sendButton').removeClass('block-hidden');
                        } else {
                            if (errorSaving == false) {
                                $('#nextButton').removeClass('block-hidden');
                                $('#newUpload').removeClass('block-hidden');
                            } else {
                                $('#nextButton').addClass('block-hidden');
                                $('#newUpload').addClass('block-hidden');
                            }

                            $('#sendButton').addClass('block-hidden');
                        }
                        $("#uploadButton").removeClass("c-hidden");

                        if (checkCompatibility() || md.mobile() || md.os() == "AndroidOS" || md.os() == "iOS")
                            $("#takeButton").removeClass("c-hidden");
                        else
                            $("#takeButton").addClass("c-hidden");
                    }
                };
                reader.readAsDataURL(input.files[i]);

            }

        }
    }

    function removeImage(index) {
        images.splice(index, 1);
        $('#sendButton').addClass('block-hidden');
        $('#nextButton').addClass('block-hidden');
        $('#newUpload').addClass('block-hidden');

        for (var i = 0; i < images.length; i++) {
            if (images[i] === index) {
                images.splice(i, 1);
            }
        }
    }

    function getUserFilesConfig(docType) {

        for (var i = 0; i < settings.userFileConfig.length; i++) {
            if (docType == settings.userFileConfig[i].id) {
                return settings.userFileConfig[i];
            }
        }
    }

    function getUserFiles(docType) {

        var files = [];
        for (var i = 0; i < images.length; i++) {
            if (docType == images[i].userFileType) {
                files.push(images[i]);
            }
        }
        return files;
    }

    function checkCompatibility() {
        if (!options.notAskingPermission) {
            if (settings.userFileConfig.length > 0) {
                if (settings.userFileConfig[current_document_index].skipAskPermission) return false;
                return navigatorMedia();
            } else return navigatorMedia();
        }
        return false;
    }

    function navigatorMedia() {
        navigator.getMedia = (navigator.getUserMedia ||
            navigator.webkitGetUserMedia ||
            navigator.mozGetUserMedia ||
            navigator.msGetUserMedia);
        return (navigator.getMedia);
    }

    function alwaysHideTakeButton() {
        if (!options.hideTakeButton) return false;
        return true;
    }

    async function getDeviceName(deviceId) {
        console.log('getDeviceName: ' + deviceId)
        navigator.mediaDevices.enumerateDevices().then((value) => {
            for(i=0;i<value.length;i++){
                if(value[i].deviceId == deviceId){
                    deviceName =  value[i].label;
                    break;
                }
            }
        });
    }

    function requestLabelsInterval(){
        clearIntervalReference();
        setTimeout(function(){
            clearIntervalReference();
            if(!validateImageInProgress) requestLabels();
            intervalReference = setInterval(function(){
                if(!validateImageInProgress) requestLabels();
            }, 3000);
        },4000)
    }

    function clearIntervalReference(){
        if(intervalReference != null) {
            clearInterval(intervalReference);
            intervalReference = null;
        }
    }

    function resetRequestLabelsVariable(){
        firstLabelRequest = null;
        validateImageInProgress = false;
    }

    function saveImageAndChangeContainer(resizeImage, docType){
        saveImage(resizeImage, docType);
        if (current_document_index + 1 == settings.userFileConfig.length) {
            $('#nextButton').addClass('block-hidden');
            $('#newUpload').removeClass('block-hidden');
            $('#sendButton').removeClass('block-hidden');
        } else {
            $('#nextButton').removeClass('block-hidden');
            $('#newUpload').removeClass('block-hidden');
            $('#sendButton').addClass('block-hidden');
        }
        if ((checkCompatibility() || md.mobile() || md.os() == "AndroidOS" || md.os() == "iOS") && !alwaysHideTakeButton())
            $("#takeButton").removeClass("c-hidden");
        else
            $("#takeButton").addClass("c-hidden");

        $("#uploadButton").removeClass("c-hidden");
        $("#camera-stream").hide();
    }

    function validateIfHasSecondaryMessage(){
        let secondaryMessage = $(".msg-block-secondary");
        if(secondaryMessage.length && secondaryMessage.is(":hidden")){
            if((images || []).find(x => x.userFileType == settings.userFileConfig[current_document_index].id)) return;
            $(".msg-block").hide();
            $(".msg-block-secondary").show();
        }
    }

    function requestLabels() {
        var snap = takeSnapshot();
        base64Img = snap;
        resizeImageFromCamera(snap, IMG_WIDTH, IMG_HEIGHT, true, getImageLabels);
    }

    async function requestCamera(frontCamera) {
        const userMediaDevices = await navigator.mediaDevices.getUserMedia({video: true});
        let frontCamerasPossibleLabels = ["front","frontal","avant","delante"];
        let rearCamerasPossibleLabels = ["rear","back","trasera","atras"];
        let enableValidationModels = settings.userFileConfig.length > 0 && settings.userFileConfig[current_document_index].enableValidationModels;
        if(enableValidationModels) $('#take-photo').hide();
        $("#camera-stream").show();
        try{
            stopStream()
        }
        catch (errr){
            console.log(errr)
        }
        navigator.mediaDevices.enumerateDevices().then((value) => {
            for(i=0;i<value.length;i++){
                if(value[i].kind == 'videoinput'){
                    if(frontCamera ? frontCamerasPossibleLabels.findIndex(x => value[i].label.toLowerCase().indexOf(x) != -1) !== -1 : rearCamerasPossibleLabels.findIndex(x => value[i].label.toLowerCase().indexOf(x) != -1) !== -1) {
                        console.log('deviceName: ' + value[i].label + ' | deviceId: ' + value[i].deviceId);
                        deviceName =  value[i].label;
                        try{
                            userMediaDevices.getTracks().forEach(track => track.stop());
                        }catch (userMediaDeviceErr){
                            console.log(userMediaDeviceErr);
                        }
                        setTimeout(()=>{
                            startStream(value[i].deviceId, identityDocumentsIds.indexOf(settings.userFileConfig[current_document_index].id) != -1 && enableValidationModels);
                            if(enableValidationModels){
                                if(settings.userFileConfig[current_document_index].id == 23){
                                    initFaceDetectionControls()
                                    run()
                                }else if(identityDocumentsIds.indexOf(settings.userFileConfig[current_document_index].id) != -1) requestLabelsInterval();
                            }
                        },500)
                        return;
                    }
                }
            }
            if(!frontCamera && navigator.userAgent.toLowerCase().indexOf('iphone') != -1){
                for(i=0;i<value.length;i++){
                    if(value[i].kind == 'videoinput' && i > 0){
                        console.log('deviceName: ' + value[i].label + ' | deviceId: ' + value[i].deviceId);
                        deviceName =  value[i].label;
                        try{
                            userMediaDevices.getTracks().forEach(track => track.stop());
                        }catch (userMediaDeviceErr){
                            console.log(userMediaDeviceErr);
                        }
                        setTimeout(()=>{
                            startStream(value[i].deviceId, identityDocumentsIds.indexOf(settings.userFileConfig[current_document_index].id) != -1 && enableValidationModels);
                            if(enableValidationModels){
                                if(settings.userFileConfig[current_document_index].id == 23){
                                    initFaceDetectionControls()
                                    run()
                                }else if(identityDocumentsIds.indexOf(settings.userFileConfig[current_document_index].id) != -1) requestLabelsInterval();
                            }
                        },500)
                        return;
                    }
                }
            }
            for(i=0;i<value.length;i++){
                if(value[i].kind == 'videoinput'){
                    console.log('deviceName: ' + value[i].label + ' | deviceId: ' + value[i].deviceId);
                    deviceName =  value[i].label;
                    try{
                        userMediaDevices.getTracks().forEach(track => track.stop());
                    }catch (userMediaDeviceErr){
                        console.log(userMediaDeviceErr);
                    }
                    setTimeout(()=>{
                        startStream(value[i].deviceId, identityDocumentsIds.indexOf(settings.userFileConfig[current_document_index].id) != -1 && enableValidationModels);
                        if(enableValidationModels){
                            if(settings.userFileConfig[current_document_index].id == 23){
                                initFaceDetectionControls()
                                run()
                            }else if(identityDocumentsIds.indexOf(settings.userFileConfig[current_document_index].id) != -1) requestLabelsInterval();
                        }
                    },500)
                    return;
                }
            }
        });
    }

    function resizeImageFromCamera(snap, wantedWidth, wantedHeight, excludeResize, functionToSendImage) {
        if(excludeResize) excludeResize = false;
        var hidden_canvas = $('canvas#principalCanvas')[0],
            context = hidden_canvas.getContext('2d');
        var width = video.videoWidth,
            height = video.videoHeight;
        var ratio = height / width;
        if(excludeResize){
            wantedWidth = video.videoWidth;
            wantedHeight = video.videoHeight;
        }else{
            if (width < wantedWidth) {
                wantedWidth = width;
                wantedHeight = height;
            } else {
                wantedHeight = wantedWidth * ratio;
            }
        }

        // We create an image to receive the Data URI
        var img = document.createElement('img');
        var canvas;
        var ctx;
        // When the event "onload" is triggered we can resize the image.
        img.onload = function () {
            // We create a canvas and get its context.
            canvas = document.createElement('canvas');
            ctx = canvas.getContext('2d');

            // set the dimensions at the wanted size.
            canvas.width = wantedWidth;
            canvas.height = wantedHeight;

            ctx.fillStyle = "transparent";
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            // We resize the image with the canvas method drawImage();
            ctx.drawImage(this, 0, 0, wantedWidth, wantedHeight);
            var resizeImage = canvas.toDataURL("image/jpeg", 0.7);

            if(functionToSendImage != null){
                functionToSendImage(resizeImage, docType)
            }else{
                saveImageAndChangeContainer(resizeImage, docType);
            }
        }

        img.src = snap;

    }

    function startStream(deviceId, printDocumentBox){
        console.log('stream camera')
        try{
            stopStream()
        }
        catch (errr){
            console.log(errr)
        }
        navigator
            .mediaDevices
            .getUserMedia({
                video: {
                    deviceId: deviceId
                }
            })
            .then(function (stream) {
                console.log('Starting stream')
                $(".camera-block .frame-container").show();
                video = document.querySelector('#camera-stream');
                video.srcObject = stream;
                streamData = stream;
                video.loop = true;
                localStream = stream;
                try {
                    mediaRecorder = new MediaRecorder(stream, { mimeType: 'video/webm' });
                }   catch (err1) {
                    try {
                        mediaRecorder = new MediaRecorder(stream, { mimeType: 'video/mp4' });
                    }   catch (err2) {
                        // If fallback doesn't work either. Log / process errors.
                        console.error({err1});
                        console.error({err2})
                    }
                }
                video.play();
                if(printDocumentBox) {
                    resetRequestLabelsVariable();
                    setTimeout(function () {
                        predictionBox(video, document.querySelector('canvas#overlayCanvas'), null)
                        if(lastFrameContainerWidthValidatorInterval != null) clearIntervalReference(lastFrameContainerWidthValidatorInterval);
                        lastFrameContainerWidthValidatorInterval = setInterval(function(){
                            resizeForPredictionBox();
                        }, 1000)
                        setTimeout(function(){
                            validateIfHasSecondaryMessage();
                        }, 4000)
                    }, 800)
                }
                try{
                    mediaRecorder.addEventListener('dataavailable', function(e) {
                        if(blobsRecorded.length < MAX_BLOBS_VIDEO_SIZE) blobsRecorded.push(e.data);
                        else if(!videoRecordedLinkGenerated) stopMediaRecorder();
                    });
                    mediaRecorder.addEventListener('stop', function(e) {
                        if(!videoRecordedLinkGenerated){
                            videoRecordedLinkGenerated = true;
                            let videoBlob = new Blob(blobsRecorded.length > MAX_BLOBS_VIDEO_SIZE ? blobsRecorded.slice(0, MAX_BLOBS_VIDEO_SIZE) : blobsRecorded, { type: 'video/webm' });
                            if(settings.presignedUrl){
                                finalBlobData = videoBlob;
                                uploadRecordingAndRegister(settings.presignedUrl, finalBlobData.slice(), settings.extraFilename);
                                //uploadVideoMediaRecorder(videoBlob, settings.presignedUrl)
                            }
                        }
                    });
                    mediaRecorder.start(1000);
                }
                catch (errMediaRecorder){

                }
            })
            .catch(function (err) {
                showErrorModal("Oops, parece que no tenemos permiso a tu cámara. Revisa la configuración de tu navegador.");
                $(".upload-box").remove();
                console.log(err);
            });
    }

    function resizeForPredictionBox(){
        if(document.getElementById("camera-stream") && document.querySelector('canvas#overlayCanvas')) {
            let cameraClientWidth = document.getElementById("camera-stream").clientWidth;
            if(lastFrameContainerWidth == null) lastFrameContainerWidth = cameraClientWidth + 0;
            if(cameraClientWidth != lastFrameContainerWidth){
                predictionBox(document.getElementById("camera-stream"), document.querySelector('canvas#overlayCanvas'))
                lastFrameContainerWidth = cameraClientWidth + 0;
            }
        }
    }

    // Take picture
    function takeSnapshot() {
        var hidden_canvas = $('canvas#principalCanvas')[0],
            context = hidden_canvas.getContext('2d');

        var width = video.videoWidth,
            height = video.videoHeight;

        if (width && height) {

            // Setup a canvas with the same dimensions as the video.
            hidden_canvas.width = width;
            hidden_canvas.height = height;

            // Make a copy of the current frame in the video on the canvas.
            context.drawImage(video,  0, 0, width, height);
            // Turn the canvas image into a dataURL that can be used as a src for our photo.
            return hidden_canvas.toDataURL('image/jpeg');
        }
    }

    function dataURItoBlob(dataURI) {
        // convert base64/URLEncoded data component to raw binary data held in a string
        var byteString;
        if (dataURI.split(',')[0].indexOf('base64') >= 0)
            byteString = atob(dataURI.split(',')[1]);
        else
            byteString = unescape(dataURI.split(',')[1]);

        // separate out the mime component
        var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

        // write the bytes of the string to a typed array
        var ia = new Uint8Array(byteString.length);
        for (var i = 0; i < byteString.length; i++) {
            ia[i] = byteString.charCodeAt(i);
        }

        return new Blob([ia], {
            type: mimeString
        });
    }

    function stopStream() {
        if(video){
            video.pause();
            video.src = '';
            video.srcObject = undefined;
        }
        if(localStream){
            localStream.getVideoTracks().forEach(function (track) {
                track.stop();
            });
        }
        stopMediaRecorder();
    }
    function stopMediaRecorder(){
        if(mediaRecorder){
            try {
                mediaRecorder.stop();
            }
            catch (errorRecorder){
                console.error(errorRecorder)
            }
        }
    }

    function uploadPictures(finishFunction) {
        // Validate the required files
        for (var i = 0; i < settings.userFileConfig.length; i++) {
            if (settings.userFileConfig[i].required) {
                var userFileImages = getUserFiles(settings.userFileConfig[i].id);
                if (userFileImages.length == 0) {
                    showErrorModal("Falta subir documentacion obligatoria para " + settings.userFileConfig[i].name);
                    return;
                }
            }
        }

        var filesToUpload = [];
        for (var i = 0; i < images.length; i++) {
            if (images[i].uploaded == null) {
                images[i].uploaded = false;
            }
            if (!images[i].uploaded) {
                filesToUpload.push(i);
            }
        }

        if(settings.presignedUrl){
            if(recordingUploaded){
                uploadPicture(filesToUpload, finishFunction);
                base64Img = undefined;
            }
            else{
                $.ajax({
                    url: settings.presignedUrl,
                    type: 'PUT',
                    async: true,
                    cache: false,
                    contentType: 'binary/octet-stream',
                    processData: false,
                    data: finalBlobData,
                    success: function (data) {
                        let filenameFD = new FormData();
                        filenameFD.append("filename", settings.extraFilename);
                        filenameFD.append("deviceName", deviceName);
                        questionFw.ajaxToCurrentQuestionController({
                            type: "POST",
                            data: filenameFD,
                            cache: false,
                            contentType: false,
                            processData: false,
                            success: function (data) {
                                uploadPicture(filesToUpload, finishFunction);
                                base64Img = undefined;
                            },
                            error: function(xhr, status, error) {
                                showErrorModal("Ha ocurrido un error realizando la operación, intente nuevamente");
                                return;
                            }
                        }, "registerfile");
                    },
                    error: function(xhr, status, error) {
                        showErrorModal("Ha ocurrido un error, intente nuevamente");
                        return;
                    }
                });
            }
        }
        else{
            uploadPicture(filesToUpload, finishFunction);
            base64Img = undefined;
        }
    }

    function uploadRecordingAndRegister(url, blob, filename){
        if(url){
            let filenameCloned = filename ? ""+filename : null;
            let deviceNameCloned = deviceName ? ""+deviceName : null;
            $.ajax({
                url: url,
                type: 'PUT',
                async: true,
                cache: false,
                contentType: 'binary/octet-stream',
                processData: false,
                data: blob,
                success: function (data) {
                    let filenameFD = new FormData();
                    filenameFD.append("filename", filenameCloned);
                    filenameFD.append("deviceName", deviceNameCloned);
                    questionFw.ajaxToCurrentQuestionController({
                        type: "POST",
                        data: filenameFD,
                        cache: false,
                        contentType: false,
                        processData: false,
                        success: function (_data) {
                            recordingUploaded = true;
                        },
                        error: function(xhr, status, error) {
                            return;
                        }
                    }, "registerfile");
                },
                error: function(xhr, status, error) {
                    return;
                }
            });
        }
    }

    function uploadPicture(filesToUpload, finishFunction) {
        if (filesToUpload.length > 0) {
            //debugger;
            var fileToUpload = images[filesToUpload[0]];
            var data = new FormData();
            data.append("file", fileToUpload.image);
            data.append("userFileType", fileToUpload.userFileType);

            questionFw.ajaxToCurrentQuestionController({
                type: "POST",
                data: data,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    filesToUpload.splice(0, 1);
                    uploadPicture(filesToUpload, finishFunction);
                }
            }, "userfile");
        } else if (finishFunction != null) {
            finishFunction();
        }
    }

    function getImageLabels(image, docType) {
        if (image != null) {
            if((images || []).find(x => x.userFileType == docType)) return;
            if(firstLabelRequest == null) firstLabelRequest = new Date().getTime();
            else if( Math.abs(new Date().getTime() - firstLabelRequest) >= maxMillisecondsForLabelsRequest){
                swal({
                    title: "Oops...",
                    text: "No pudimos obtener la foto de tu DNI, <br> colócalo nuevamente",
                    imageUrl: "/img/icon-modal-error.svg",
                    html: true,
                    confirmButtonText : "Volver a tomar",
                    confirmButtonColor: '#F7323F',
                    cancelButtonColor: '#3F3B3B',
                    imageSize: "66x66"
                }, function (ok) {
                    firstLabelRequest = null;
                    validateImageInProgress = false;
                });
                return;
            }
            validateImageInProgress = true;
            //debugger;
            var data = new FormData();
            data.append("file", image);

            questionFw.ajaxToCurrentQuestionController({
                type: "POST",
                data: data,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    if(data == null || data.length == 0) validateImageInProgress = false;
                    else{
                        let result = JSON.parse(data);
                        if(result.instances.length) {
                            let isMatching = printBoundingBox(document.querySelector('#camera-stream'), document.querySelector('canvas#overlayCanvas'), boxDocumentReferenceDefaultPercentage, result.instances[0].boundingBox)
                            if(isMatching) {
                                setTimeout(function (){
                                    saveImageAndChangeContainer(image, docType);
                                    resetRequestLabelsVariable();
                                    clearIntervalReference();
                                }, 1000)
                                return;
                            }
                            else validateImageInProgress = false;
                        }
                        else validateImageInProgress = false;
                    }
                },
                error : function (data){
                    validateImageInProgress = false;
                }
            }, "identifyLabels");
        }
    }

    function uploadVideoMediaRecorder(blob, url){
        $.ajax({
            url: url,
            type: 'PUT',
            async: true,
            cache: false,
            contentType: 'binary/octet-stream',
            processData: false,
            data: blob,
            success: function (data) {

            },
            error: function(xhr, status, error) {
                console.log(error);
            }
        });
    }

    // https://stackoverflow.com/a/43927355
    function drawImage(ctx, image, x, y, w, h, degrees) {
        ctx.save();
        ctx.translate(x + w / 2, y + h / 2);
        ctx.rotate(degrees * Math.PI / 180.0);
        ctx.translate(-x - w / 2, -y - h / 2);
        ctx.drawImage(image, x, y, w, h);
        ctx.restore();
    }

    const SSD_MOBILENETV1 = 'ssd_mobilenetv1'
    const TINY_FACE_DETECTOR = 'tiny_face_detector'

    let selectedFaceDetector = TINY_FACE_DETECTOR
    //FACE-API.js
    // ssd_mobilenetv1 options
    let minConfidence = 0.5
    // tiny_face_detector options
    let inputSize = 512
    let scoreThreshold = 0.5

    let forwardTimes = []

    function getFaceDetectorOptions() {
        return selectedFaceDetector === SSD_MOBILENETV1
            ? new faceapi.SsdMobilenetv1Options({ minConfidence })
            : new faceapi.TinyFaceDetectorOptions({ inputSize, scoreThreshold })
    }

    function changeInputSize(size) {
        inputSize = parseInt(size)
    }

    function getCurrentFaceDetectionNet() {
        return faceapi.nets.tinyFaceDetector
        if (selectedFaceDetector === SSD_MOBILENETV1) {
            return faceapi.nets.ssdMobilenetv1
        }
        if (selectedFaceDetector === TINY_FACE_DETECTOR) {
            return faceapi.nets.tinyFaceDetector
        }
    }

    function isFaceDetectionModelLoaded() {
        return !!getCurrentFaceDetectionNet().params
    }

    async function changeFaceDetector(detector) {
        selectedFaceDetector = detector

        if (!isFaceDetectionModelLoaded()) {
            await getCurrentFaceDetectionNet().load('https://solven-public.s3.amazonaws.com/external/face-api/')
        }

    }

    function initFaceDetectionControls() {
    }

    async function onPlay(videoEl) {
        let withFaceLandmarks = false
        let withBoxes = true

        if(!videoEl.currentTime || videoEl.paused || videoEl.ended || !isFaceDetectionModelLoaded())
            return setTimeout(() => onPlay(videoEl))


        const options = getFaceDetectorOptions()

        const ts = Date.now()

        let task = faceapi.detectAllFaces(videoEl, options)
        task = withFaceLandmarks ? task.withFaceLandmarks() : task
        const results = await task

        updateTimeStats(Date.now() - ts)

        const canvas = $('canvas#overlayCanvas').get(0)
        const dims = faceapi.matchDimensions(canvas, videoEl, true)
        resizeCanvasFromVideo(videoEl, canvas)

        const takePhotoElement = $('#take-photo').get(0)

        const resizedResults = faceapi.resizeResults(results, dims)
        let isMatching = faceapi.draw.drawDetections(canvas, resizedResults, {
            boxPercentage : 0.70
        }, null, true, md.mobile() || md.os() == "AndroidOS" || md.os() == "iOS")
        if(isMatching){
            setTimeout(function (){
                if((images || []).find(x => x.userFileType == settings.userFileConfig[current_document_index].id)) return;
                let snapShot = takeSnapshot();
                resizeImageFromCamera(snapShot, IMG_WIDTH, IMG_HEIGHT, true);
            },1000);
            return;
        }

        setTimeout(() => onPlay(videoEl))
    }

    async function run() {
        // load face detection and face landmark models
        $('#take-photo').get(0).style.display = 'none';
        await changeFaceDetector(TINY_FACE_DETECTOR)
        await faceapi.loadFaceLandmarkModel('https://solven-public.s3.amazonaws.com/external/face-api/')
        changeInputSize(512)
        onPlay($('#camera-stream').get(0))
        setTimeout(function(){
            validateIfHasSecondaryMessage();
        }, 4000)
    }

    function updateTimeStats(timeInMs) {
        forwardTimes = [timeInMs].concat(forwardTimes).slice(0, 30)
        const avgTimeInMs = forwardTimes.reduce((total, t) => total + t) / forwardTimes.length
    }

    //COCO-SSD
    let model = undefined;

    function cocoSsdLoad(hideButton){
        if(!model) cocoSsd.load({
            base: 'lite_mobilenet_v2'
        }).then(function (loadedModel) {
            model = loadedModel;
        });
        if(hideButton) $('#take-photo').get(0).style.display = 'none';
    }

    function predictWebcam(video, container, takeButtonElement) {
        if(!video.currentTime || video.paused || video.ended || !model)
            return setTimeout(() => predictWebcam(video, container, takeButtonElement))
        resizeCanvasFromVideo(video, container)
        let boxSizePercentage = .75;
        printPredictionBox(video, container, boxSizePercentage, null, takeButtonElement)
        model.detect(video, 5, 0.5).then(function (predictions) {
            let element = getPossiblePrediction(predictions);
            if(element && element.score > 0.66){
                let isMatching = printPredictionBox(video, container, boxSizePercentage, {
                    _x : element.bbox[0],
                    _y : element.bbox[1],
                    _width : element.bbox[2],
                    _height : element.bbox[3]
                }, takeButtonElement)
            }
            setTimeout(() => predictWebcam(video, container, takeButtonElement))
        });
    }

    function predictionBox(video, container){
        resizeCanvasFromVideo(video, container)
        printPredictionBox(video, container, boxDocumentReferenceDefaultPercentage, null, null)

    }

    function resizeCanvasFromVideo(video, canvas){
        canvas.style.width  = null;
        canvas.style.left = null;
        const dims = faceapi.matchDimensions(canvas, video, true)
        if(canvas.clientWidth > video.clientWidth){
            canvas.style.width = video.clientWidth+"px";
            canvas.style.left = "0px";
        }
        let difference = video.clientWidth - canvas.clientWidth;
        if(difference > 0){
            let leftPosition = difference/2;
            if(canvas.style.left != leftPosition+"px"){
                canvas.style.left = leftPosition+"px";
            }
        }else if(difference < 0){
            canvas.style.width = video.clientWidth+"px";
            canvas.style.left = "0px";
        }
    }

    function printPredictionBox(video, input, boxPercentage, boxProperties,  takeButtonElement) {
        let isMatching = false;
        if(input){
            let cocoSsdContainerProperties = getPropertiesForCocoSsdBox(video, input, boxPercentage, md.mobile() || md.os() == "AndroidOS" || md.os() == "iOS")
            let ctx = input.getContext("2d");
            ctx.clearRect(0, 0, input.width, input.height);
            if(boxProperties){
  /*              ctx.beginPath();
                ctx.lineWidth = 5;
                ctx.strokeStyle = '#1e88e5';
                ctx.strokeRect(boxProperties._x, boxProperties._y, boxProperties._width, boxProperties._height);
                ctx.stroke();*/
            }
            ctx.beginPath();
            ctx.lineWidth = 5;
            if(boxProperties) isMatching = compareProperties(boxProperties, cocoSsdContainerProperties)
            ctx.strokeStyle = isMatching ? "#66bb6a" : '#ffee58';
            ctx.strokeRect(cocoSsdContainerProperties._x, cocoSsdContainerProperties._y, cocoSsdContainerProperties._width, cocoSsdContainerProperties._height);
            ctx.stroke();
            if(takeButtonElement){
                if(isMatching) takeButtonElement.style.display = 'block'
                else takeButtonElement.style.display = 'none'
            }
        }
        return isMatching;
    }

    function printBoundingBox(video, input, boxPercentage, boundingBoxProperties) {
        let isMatching = false;
        if(input && boundingBoxProperties){
            let _xPx = input.width*boundingBoxProperties.left;
            let _yPx = input.height*boundingBoxProperties.top;
            let _heightPx = input.height*boundingBoxProperties.height;
            let _widthPx = input.width*boundingBoxProperties.width;
            isMatching = printPredictionBox(video, input, boxPercentage, {
                _x : _xPx,
                _y : _yPx,
                _width : _widthPx,
                _height : _heightPx
            }, null)
        }
        return isMatching;
    }

    function gcd (a, b) {
        return (b == 0) ? a : gcd (b, a%b);
    }

    function getPropertiesForCocoSsdBox(video, input, boxPercentage, isMobile){
        let minorValue = Math.min(input.width,input.height)
        let baseValue = input.width*boxPercentage;
        let heightValue = baseValue/(1.60);
        let videoRatio = gcd(video.videoWidth, video.videoHeight);
        let widthRatio = input.width/videoRatio;
        let heightRatio = input.height/videoRatio;
        let xAxisDifference = null;
        let yAxisDifference = null;
        if(isMobile){
            baseValue = input.width*boxPercentage;
            heightValue = input.clientWidth < 300 ? input.height*.45 : input.height/1.6;
            xAxisDifference = input.width-baseValue;
            yAxisDifference = input.height-heightValue;
        }else{
            baseValue = input.width*boxPercentage;
            heightValue = input.clientWidth < 300 ? input.height*.45 : input.height/1.6;
            xAxisDifference = input.width-baseValue;
            yAxisDifference = input.height-heightValue;
        }
        return {
            _x : xAxisDifference/2,
            _y : yAxisDifference/2,
            _width : baseValue,
            _height : heightValue
        }
    }

    function compareProperties(lastBox, boxProperties){
        let matching = false;
        if(lastBox._width < boxProperties._width && lastBox._width >= (boxProperties._width*.65)){
            let principalBoxXLastPoint = boxProperties._x + boxProperties._width;
            let principalBoxYLastPoint = boxProperties._y + boxProperties._height;
            let boxXLastPoint = lastBox._x + lastBox._width;
            let boxYLastPoint = lastBox._y + lastBox._height;
            matching = boxXLastPoint <= principalBoxXLastPoint &&  boxYLastPoint <= principalBoxYLastPoint && lastBox._x >= boxProperties._x &&  lastBox._y >= boxProperties._y;
        }
        return matching;
    }

    function getPossiblePrediction(predictions){
        let possibleValue = ["snowboard",
            "skateboard",
            "surfboard",
            "sandwich",
            "laptop",
            "remote",
            "keyboard",
            "book",
            "cell phone",
            "microwave"];
        let predictionsFiltered = predictions.filter(x => possibleValue.indexOf(x.class) != -1).sort((a, b) => b.score-a.score);
        if(predictionsFiltered?.length) return predictionsFiltered.pop();
        return null;
    }

    function chargeCocoSsdModel(){
        if(settings.userFileConfig && settings.userFileConfig.length){
            let filterData = settings.userFileConfig.find(x => x.enableValidationModels && x.id != 23)
//                    if(filterData) cocoSsdLoad(false);
        }
    }

    if(settings.userFileConfig && settings.userFileConfig.length) chargeCocoSsdModel();


}