package com.affirm.entityExt.controller;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.BinaryOutputWrapper;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.TrackingAction;
import com.affirm.common.model.catalog.UserFileType;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationUserFiles;
import com.affirm.common.model.transactional.UserFile;
import com.affirm.common.service.FileService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.RekognitionService;
import com.affirm.common.service.UserService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class EntityFileController {


    @Autowired
    private FileService fileService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private RekognitionService rekognitionService;
    @Autowired
    private CreditDAO creditDAO;

    private PdfReader pdfReader;

    private static final String TMP_PDF = "tmp.pdf";
    private static final String RIGHT = "R";
    private static final String LEFT = "L";

    @RequestMapping(value = "/file/userFile/{jsonEncrypt}/image.jpg", method = RequestMethod.GET)
//    @RequiresPermissionOr403(permissions = "person:documentationTab:view", type = RequiresPermissionOr403.Type.AJAX)
    @ResponseBody
    public ResponseEntity getUserFile(
            @PathVariable("jsonEncrypt") String jsonEncrypt,
            @RequestParam(required = false) boolean thumbnail) {
        try {
            JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
            byte[] file = fileService.getUserFileById(jsonDecrypt.getInt("fileid"), thumbnail);
            if (file != null) {
                HttpHeaders responseHeaders = new HttpHeaders();
                String extension = FilenameUtils.getExtension(jsonDecrypt.getString("file"));
                switch (extension.toLowerCase()) {
                    case "pdf":
                        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
                        break;
                    case "png":
                        responseHeaders.setContentType(MediaType.IMAGE_PNG);
                        break;
                    case "gif":
                        responseHeaders.setContentType(MediaType.IMAGE_GIF);
                        break;
                    case "wav":
                        responseHeaders.setContentType(MediaType.valueOf("audio/wav"));
                        break;
                    case "mp3":
                        responseHeaders.setContentType(MediaType.valueOf("audio/mpeg"));
                        break;
                    default:
                        responseHeaders.setContentType(MediaType.IMAGE_JPEG);
                        break;
                }
                return new ResponseEntity(file, responseHeaders, HttpStatus.OK);
            }
            return null;
        } catch (Exception ex) {
            //logger.error("Error", ex);
            return null;
        }
    }

    @RequestMapping(value = "/file/userFile", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:update:file:upload", type = RequiresPermissionOr403.Type.AJAX)
    @ResponseBody
    public ResponseEntity<String> registerUserFile(
            @RequestParam("userId") Integer userId,
            @RequestParam("file") MultipartFile[] file,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("userFileType") Integer userFileType,
            Locale locale) {
        try {
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
            switch (userFileType) {
                case UserFileType.DNI_ANVERSO:
                case UserFileType.DNI_FRONTAL:
                case UserFileType.CEDULA_CIUDADANIA_FRONTAL:
                case UserFileType.CEDULA_CIUDADANIA_ANVERSO:
                case UserFileType.SELFIE:
                    List<LoanApplicationUserFiles> loanApplicationFiles = personDAO.getLoanApplicationFiles(loanApplicationId, loanApplication.getPersonId(), locale);
                    if (loanApplicationFiles != null && !loanApplicationFiles.isEmpty()) {
                        LoanApplicationUserFiles lauf = loanApplicationFiles.get(0);
                        List<UserFile> luf = lauf.getUserFileList();
                        for (UserFile uf : luf) {
                            if (uf.getFileType().getId() == userFileType.intValue())
                                userDao.validateUserFile(uf.getId(), false);
                        }
                    }
                    break;
            }
            userService.registerUserFiles(file, loanApplicationId, userId, userFileType);
            //use rekognition for new image
            switch (userFileType) {
                case UserFileType.DNI_ANVERSO:
                case UserFileType.DNI_FRONTAL:
                case UserFileType.CEDULA_CIUDADANIA_FRONTAL:
                case UserFileType.CEDULA_CIUDADANIA_ANVERSO:
                case UserFileType.SELFIE:
                    rekognitionService.updateCompareOneDniVsSelfieNotYetCompared(loanApplication.getId(), loanApplication.getPersonId(), loanApplication.getUserId(), locale, userFileType);
                    break;
            }

            return ResponseEntity.ok(null);
        } catch (Exception ex) {
            //logger.error("Error", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @RequestMapping(value = "/file/userFile/{userFileId}/validate", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:update:file:validation", type = RequiresPermissionOr403.Type.AJAX)
    @ResponseBody
    public ResponseEntity<String> validateUserFile(
            Locale locale,
            @PathVariable("userFileId") Integer userFileId,
            @RequestParam("validated") Boolean validated) {
        try {
            if (validated) {
//                if (!SecurityUtils.getSubject().isPermitted("person:documentation:validate")) {
//                    return AjaxResponse.errorForbidden();
//                }
            } else {
//                if (!SecurityUtils.getSubject().isPermitted("person:documentation:refuse")) {
//                    return AjaxResponse.errorForbidden();
//                }
            }

            UserFile userFile = userDao.getUserFile(userFileId);
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(userFile.getLoanApplicationId(), locale);

            userDao.validateUserFile(userFileId, validated);
            return AjaxResponse.ok("");
        } catch (SqlErrorMessageException sqlErrorMsgEx) {
            return AjaxResponse.errorMessage(messageSource.getMessage(sqlErrorMsgEx.getMessageKey(), null, locale));
        } catch (Exception ex) {
            //logger.error("Error", ex);
            return AjaxResponse.errorMessage(messageSource.getMessage("system.error.default", null, locale));
        }
    }

    @RequestMapping(value = "/file/userFile/all/validate", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:update:file:validation", type = RequiresPermissionOr403.Type.AJAX)
    @ResponseBody
    public ResponseEntity<String> validateAllUserFiles(
            Locale locale,
            @RequestParam("loanApplicationId") Integer laId) {
        try {
//            if (!SecurityUtils.getSubject().isPermitted("person:documentation:validate")) {
//                System.out.println("No tienes permiso para validar documentos");
//                return AjaxResponse.errorForbidden();
//            }
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(laId, locale);
            userDao.validateAllUserFile(loanApplication.getUserId(), laId);
            return AjaxResponse.ok(null);
        } catch (SqlErrorMessageException sqlErrorMsgEx) {
            return AjaxResponse.errorMessage(messageSource.getMessage(sqlErrorMsgEx.getMessageKey(), null, locale));
        } catch (Exception ex) {
            //logger.error("Error", ex);
            return AjaxResponse.errorMessage(messageSource.getMessage("system.error.default", null, locale));
        }
    }

    @RequestMapping(value = "/file/userFile/requestMissingDocumentation", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:update:file:missing_documentation", type = RequiresPermissionOr403.Type.AJAX)
    @ResponseBody
    public ResponseEntity<String> requestMissingDocumentation(
            Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId
    ) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loanApplication != null && (loanApplication.getStatus().getId() == LoanApplicationStatus.WAITING_APPROVAL || loanApplication.getStatus().getId() == LoanApplicationStatus.APPROVED)) {
            //loanApplicationDao.registerTrackingAction(loanApplicationId, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(), TrackingAction.MISSING_DOCUMENTATION, null, null);
            loanApplicationDao.reportMissingDocumentation(loanApplicationId);
            loanApplicationService.sendMissingDocumentation(loanApplication.getUserId(), loanApplicationId, locale);
            return AjaxResponse.ok(null);
        } else {
            return AjaxResponse.errorMessage("La loan application aún está en proceso.");
        }
    }

    @RequestMapping(value = "/file/userFile/{userFileId}/updateUserFileType", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:update:file:upload", type = RequiresPermissionOr403.Type.AJAX)
    @ResponseBody
    public ResponseEntity<String> updateUserFileType(
            Locale locale,
            @PathVariable("userFileId") Integer userFileId,
            @RequestParam("userFileTypeId") Integer userFileTypeId) {
        try {
            userDao.updateUserFileType(userFileId, userFileTypeId);
            return AjaxResponse.ok(null);
        } catch (SqlErrorMessageException sqlErrorMsgEx) {
            return AjaxResponse.errorMessage(messageSource.getMessage(sqlErrorMsgEx.getMessageKey(), null, locale));
        } catch (Exception ex) {
            //logger.error("Error", ex);
            return AjaxResponse.errorMessage(messageSource.getMessage("system.error.default", null, locale));
        }
    }

    @RequestMapping(value = "/file/userFile/{jsonEncrypt}/rotate/{orientation}", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:update:file:rotate", type = RequiresPermissionOr403.Type.AJAX)
    @ResponseBody
    public ResponseEntity<String> rotateFileImage(
            Locale locale,
            @PathVariable("jsonEncrypt") String jsonEncrypt,
            @PathVariable("orientation") String orientation) {
        try {

            JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
            int userId = jsonDecrypt.getInt("user");
            String filename = jsonDecrypt.getString("file");
            String filenameThumbnail = Configuration.APP_FILE_THUMBANIL_PREFIX + filename;

            String extension = FilenameUtils.getExtension(filename);

            if (!Arrays.asList("png", "jpeg", "jpg").contains(extension)) {
                return AjaxResponse.errorMessage(messageSource.getMessage("documentation.rotate.document.error", null, locale));
            }

            byte[] file = fileService.getUserFile(userId, filename, false);
            byte[] fileThumbnail = fileService.getUserFile(userId, filenameThumbnail, false);

            int angle;

            switch (orientation) {
                case RIGHT:
                    angle = 90;
                    break;
                case LEFT:
                    angle = -90;
                    break;
                default:
                    angle = 0;
            }

            fileService.overrideUserFile(fileService.getRotateImageBytes(angle, file), userId, filename);
            if (fileThumbnail != null) {
                fileService.overrideUserFile(fileService.getRotateImageBytes(angle, fileThumbnail), userId, filenameThumbnail);
            }

            return AjaxResponse.ok(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return AjaxResponse.errorMessage(messageSource.getMessage("system.error.default", null, locale));
        }
    }

    @RequestMapping(value = "/file/sysuser/avatar/{filename}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public byte[] getSysUserFile(@RequestParam(required = false) boolean thumbnail) throws Exception {
        try {
            SysUser sysuser = (SysUser) SecurityUtils.getSubject().getPrincipal();
            if (sysuser.getAvatar() != null) {
                return fileService.getSysUserAvatar(sysuser.getId(), sysuser.getAvatar(), thumbnail);
            }
        } catch (Exception ex) {
            //logger.error("Error", ex);
        }
        return null;
    }

    @RequestMapping(value = "/file/assign/documentsMissingFlag", method = RequestMethod.POST)
//    @RequiresPermissionOr403(permissions = "loanApplication:refuse", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object markAsDocumentsMissing(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId
    ) throws Exception {
        loanApplicationDao.updateLoanApplicationFilesUploaded(loanApplicationId, false);
        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/file/sysuser/avatar/{filename}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateSysUserAvatar(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
            Integer sysUserId = sysUser.getId();
            if (file != null) {
                // for (int i = 0; i < files.length; i++) {
                if (file.getSize() > 0) {
                    String avatarName = sysUser.getAvatar().split("\\.")[0];
                    //logger.debug("Avatar name: " + avatarName);
                    String fileExtension = file.getOriginalFilename().split("\\.")[1];
                    String filename = avatarName + "." + fileExtension;
                    String newFilename = fileService.setSysUserAvatar(
                            file.getBytes(),
                            sysUserId,
                            filename
                    );
                    sysUser.setAvatar(newFilename);
                    return ResponseEntity.ok("Avatar guardado");
                }
            }
        } catch (Exception ex) {
            //logger.error("Error", ex);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir archivo");
    }

    @RequestMapping(value = "/file/splitUserFile/{jsonEncrypt}/image.jpg")
//    @RequiresPermissionOr403(permissions = "person:documentationTab:view", type = RequiresPermissionOr403.Type.AJAX)
    public ResponseEntity<?> getSpliteUserFile(@PathVariable("jsonEncrypt") String jsonEncrypt,
                                               @RequestParam(required = false) boolean thumbnail,
                                               @RequestParam(required = false) Integer creditId) {
        BinaryOutputWrapper output = new BinaryOutputWrapper();
        try {
            JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
            byte[] bytes = fileService.getUserFileById(jsonDecrypt.getInt("fileid"), thumbnail);

            if (bytes != null) {
                OutputStream out = new FileOutputStream(TMP_PDF);
                out.write(bytes);
                out.close();

                pdfReader = new PdfReader(TMP_PDF);
                List<String> fileNames = new ArrayList<>();

                String solicitudCreditoPersonaNatural = "Solicitud de Credito Persona Natural.pdf";
                generatePdfPartOfTheWhole(solicitudCreditoPersonaNatural, 1, 4);
                fileNames.add(solicitudCreditoPersonaNatural);

                String contratoCredito = "Contrato de Credito.pdf";
                generatePdfPartOfTheWhole(contratoCredito, 5, 16);
                fileNames.add(contratoCredito);

                String pagare = "Pagare.pdf";
                generatePdfPartOfTheWhole(pagare, 17, 19);
                fileNames.add(pagare);

                String hojaResumenInformativa = "Hoja Resumen (Informativa).pdf";
                generatePdfPartOfTheWhole(hojaResumenInformativa, 20, 22);
                fileNames.add(hojaResumenInformativa);

                String cronogramaPagosSoles = creditId != null ? "Cronograma de Pagos Soles - " + creditId + ".pdf" : "Cronograma de Pagos Soles.pdf";
                generatePdfPartOfTheWhole(cronogramaPagosSoles, 23, 27);
                fileNames.add(cronogramaPagosSoles);

                output = prepDownloadAsZIP(fileNames, creditId);
            }
        } catch (Exception ex) {
            //logger.error("Error", ex);
        }

        return new ResponseEntity<>(output.getData(), output.getHeaders(), HttpStatus.OK);
    }

    private void generatePdfPartOfTheWhole(String fileName, int startPage, int endPage) throws Exception {
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, new FileOutputStream(fileName));
        document.open();

        for (int i = startPage; i <= endPage; i++)
            copy.addPage(copy.getImportedPage(pdfReader, i));

        document.close();
    }

    private BinaryOutputWrapper prepDownloadAsZIP(List<String> fileNames, Integer creditId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        String outputFilename = creditId != null ? "partes_contrato_acceso - " + creditId + ".zip" : "partes_contrato_acceso.zip";
        headers.setContentDispositionFormData(outputFilename, outputFilename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteOutputStream);

        for (String fileName : fileNames) {
            File file = new File(fileName);
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            FileInputStream fileInputStream = new FileInputStream(file);
            IOUtils.copy(fileInputStream, zipOutputStream);
            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        zipOutputStream.close();
        return new BinaryOutputWrapper(byteOutputStream.toByteArray(), headers);
    }

    @RequestMapping(value = "/file/entityWebServiceFile/{jsonEncrypt}/image.jpg", method = RequestMethod.GET)
//    @RequiresPermissionOr403(permissions = "person:documentationTab:view", type = RequiresPermissionOr403.Type.AJAX)
    @ResponseBody
    public ResponseEntity getEntityWebServiceFile(
            @PathVariable("jsonEncrypt") String jsonEncrypt,
            @RequestParam(required = false) boolean thumbnail) {
        try {
            JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
            byte[] file = fileService.getWebServiceFile(jsonDecrypt.getInt("loanId"), jsonDecrypt.getString("file"), thumbnail);
            if (file != null) {
                HttpHeaders responseHeaders = new HttpHeaders();
                String extension = FilenameUtils.getExtension(jsonDecrypt.getString("file"));
                switch (extension.toLowerCase()) {
                    case "pdf":
                        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
                        break;
                    case "png":
                        responseHeaders.setContentType(MediaType.IMAGE_PNG);
                        break;
                    case "gif":
                        responseHeaders.setContentType(MediaType.IMAGE_GIF);
                        break;
                    case "wav":
                        responseHeaders.setContentType(MediaType.valueOf("audio/wav"));
                        break;
                    case "mp3":
                        responseHeaders.setContentType(MediaType.valueOf("audio/mpeg"));
                        break;
                    default:
                        responseHeaders.setContentType(MediaType.IMAGE_JPEG);
                        break;
                }
                return new ResponseEntity(file, responseHeaders, HttpStatus.OK);
            }
            return null;
        } catch (Exception ex) {
            //logger.error("Error", ex);
            return null;
        }
    }

}