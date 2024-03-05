package com.affirm.client.controller;

import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.UserFileType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.FileService;
import com.affirm.common.util.CryptoUtil;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
@RequestMapping("/public")
public class PublicController {

    private static Logger logger = Logger.getLogger(PublicController.class);

    @Autowired
    private FileService fileService;
    @Autowired
    private ReportsDAO reportsDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UserDAO userDao;

    @RequestMapping(value = "/userFile/{jsonEncrypt}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    public ResponseEntity publicUserFile(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("jsonEncrypt") String jsonEncrypt) throws Exception {
        try {
            JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
            byte[] file = fileService.getUserFile(jsonDecrypt.getInt("user"), jsonDecrypt.getString("file"), false);
            if (file != null) {
                MediaType contentType = null;
                HttpHeaders responseHeaders = new HttpHeaders();
                String extension = FilenameUtils.getExtension(jsonDecrypt.getString("file"));
                switch (extension.toLowerCase()) {
                    case "pdf":
                        contentType = MediaType.APPLICATION_PDF;
                        break;
                    case "png":
                        contentType = MediaType.IMAGE_PNG;
                        break;
                    case "gif":
                        contentType = MediaType.IMAGE_GIF;
                        break;
                    case "wav":
                        contentType = MediaType.valueOf("audio/wav");
                        break;
                    case "mp3":
                        contentType = MediaType.valueOf("audio/mpeg");
                        break;
                    default:
                        contentType = MediaType.IMAGE_JPEG;
                        break;
                }

                responseHeaders.setContentType(contentType);
                return new ResponseEntity(file, responseHeaders, HttpStatus.OK);
            }
            return null;
        } catch (Exception ex) {
            logger.error("Error", ex);
            return null;
        }
    }

    @RequestMapping(value = "/userFile/{jsonEncrypt}/download", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public void publicUserFileDownload(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("jsonEncrypt") String jsonEncrypt) throws Exception {
        try {
            JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
            byte[] file = fileService.getUserFile(jsonDecrypt.getInt("user"), jsonDecrypt.getString("file"), false);
            if (file != null) {
                MediaType contentType = null;
                HttpHeaders responseHeaders = new HttpHeaders();
                String extension = FilenameUtils.getExtension(jsonDecrypt.getString("file"));
                switch (extension.toLowerCase()) {
                    case "pdf":
                        contentType = MediaType.APPLICATION_PDF;
                        break;
                    case "png":
                        contentType = MediaType.IMAGE_PNG;
                        break;
                    case "gif":
                        contentType = MediaType.IMAGE_GIF;
                        break;
                    default:
                        contentType = MediaType.IMAGE_JPEG;
                        break;
                }

                response.setHeader("Content-disposition", "attachment; filename= " + jsonDecrypt.getString("file"));
                response.setContentType(contentType.getType());
                response.getOutputStream().write(file);
            }
        } catch (Exception ex) {
            logger.error("Error", ex);
        }
    }

    @RequestMapping(value = "/report/download/{jsonEncrypt}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public void downloadReport(
            Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("jsonEncrypt") String reportToken) throws Exception {

        try {
            String decrypted = CryptoUtil.decrypt(reportToken);
            JSONObject jsonDecrypted = new JSONObject(decrypted);
            ReportProces reportProces = reportsDao.getReportProces(jsonDecrypted.getInt("reportProcesId"));

            String url = fileService.getPresignedUrl(reportProces.getUrl(), 108000);

            response.sendRedirect(url);
            // response.setHeader("Content-disposition", "attachment; filename=" + reportProces.getUrl());
            //fileService.getReportFile(reportProces.getUrl(), response.getOutputStream());
            // response.setContentType(MediaType.valueOf("application/vnd.ms-excel").getType());
        } catch (Exception ex) {
            logger.error("Error", ex);
        }
    }

    @RequestMapping(value = "/pdfSplit/{jsonEncrypt}/download", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public void publicPdfSplitDownload(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("jsonEncrypt") String jsonEncrypt) throws Exception {
        try {
            JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
            byte[] file = fileService.getUserFile(jsonDecrypt.getInt("user"), jsonDecrypt.getString("file"), false);
            if (file != null) {
                response.setHeader("Content-disposition", "attachment; filename= " + jsonDecrypt.getString("file"));
                response.setContentType(MediaType.APPLICATION_PDF.getType());

                int startPage = jsonDecrypt.getInt("startPage");
                int endPage = jsonDecrypt.getInt("endPage");


                Document document = new Document();
                PdfCopy copy = new PdfCopy(document, response.getOutputStream());
                document.open();

                PdfReader pdfReader = new PdfReader(file);

                for (int i = startPage; i <= endPage; i++)
                    copy.addPage(copy.getImportedPage(pdfReader, i));

                document.close();

            }
        } catch (Exception ex) {
            logger.error("Error", ex);
        }
    }

    @RequestMapping(value = "/zipAllLoanDocuments/{jsonEncrypt}/download", produces = "application/zip", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public byte[] zipAllLoanDocuments(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("jsonEncrypt") String jsonEncrypt) throws Exception {

        JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
        Credit credit = creditDao.getCreditByID(jsonDecrypt.getInt("creditId"), locale, false, Credit.class);
        List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(credit.getLoanApplicationId());
        if (userFiles == null) {
            return null;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
        response.addHeader("Content-Disposition", "attachment; filename=\"Documentos_" + person.getDocumentNumber() + ".zip\"");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
        for (UserFile userFile : userFiles.stream().filter(f -> f.getFileType().getId() != UserFileType.DNI_MERGED && f.getFileType().getId() != UserFileType.ELIMINADOS && f.getFileType().getId() != UserFileType.SELFIE_RECORDING).collect(Collectors.toList())) {
            String extension = "";
            String newFileName = "";
            String oldFileName = userFile.getFileName();
            int i = oldFileName.lastIndexOf('.');
            if (i > 0) {
                extension = "." + oldFileName.substring(i + 1);
                newFileName = oldFileName.substring(0, i);
            } else {
                newFileName = oldFileName;
            }
            if (credit.getEntity().getId() == Entity.MULTIFINANZAS) {
                switch (userFile.getFileType().getId()) {
                    case UserFileType.HOJA_RESUMEN:
                        newFileName = "Cronograma";
                        break;
                    case UserFileType.CONTRATO_SOLICITUD:
                        newFileName = "Solicitud_definitiva";
                        break;
                    case UserFileType.BUREAU_RESULT:
                        newFileName = "Consulta Nosis";
                        break;
                }
            } else {
                switch (userFile.getFileType().getId()) {
                    case UserFileType.RIPLEY_FINAL_SCHEDULE:
                        newFileName = "Cronograma_Final";
                        break;
                }
            }
            newFileName = newFileName + "_" + person.getDocumentNumber() + extension;

            File outputFile = new File(newFileName);
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(fileService.getUserFile(userDao.getUserIdByPersonId(credit.getPersonId()), userFile.getFileName(), false));

                zipOutputStream.putNextEntry(new ZipEntry(outputFile.getName()));
                FileInputStream fileInputStream = new FileInputStream(outputFile);
                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @RequestMapping(value = "/zipAllLoanApplicationDocuments/{jsonEncrypt}/download", produces = "application/zip", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public byte[] zipAllLoanDocumentsFromLoan(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("jsonEncrypt") String jsonEncrypt) throws Exception {

        JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypt.getInt("loanId"), locale);
        if (loanApplication == null) {
            return null;
        }
        List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplication.getId());
        if (userFiles == null) {
            return null;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        response.addHeader("Content-Disposition", "attachment; filename=\"Documentos_" + person.getDocumentNumber() + ".zip\"");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
        for (UserFile userFile : userFiles.stream().filter(f -> f.getFileType().getId() != UserFileType.DNI_MERGED && f.getFileType().getId() != UserFileType.ELIMINADOS && f.getFileType().getId() != UserFileType.SELFIE_RECORDING).collect(Collectors.toList())) {
            String extension = "";
            String newFileName = "";
            String oldFileName = userFile.getFileName();
            int i = oldFileName.lastIndexOf('.');
            if (i > 0) {
                extension = "." + oldFileName.substring(i + 1);
                newFileName = oldFileName.substring(0, i);
            } else {
                newFileName = oldFileName;
            }
            newFileName = newFileName + "_" + person.getDocumentNumber() + extension;

            File outputFile = new File(newFileName);
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(fileService.getUserFile(userDao.getUserIdByPersonId(loanApplication.getPersonId()), userFile.getFileName(), false));

                zipOutputStream.putNextEntry(new ZipEntry(outputFile.getName()));
                FileInputStream fileInputStream = new FileInputStream(outputFile);
                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
