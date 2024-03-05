package com.affirm.common.service.impl;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.SysUserDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.transactional.UserFile;
import com.affirm.common.service.FileService;
import com.affirm.common.util.CryptoUtil;
import com.affirm.system.configuration.Configuration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.*;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

/**
 * @author jrodriguez
 */

@Service("fileService")
public class FileServiceImpl implements FileService {

    private static Logger logger = Logger.getLogger(FileServiceImpl.class);

    public static final String AELU_DOCUMENTACION_PRELIMINAR = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String ENTITY_WEBSERVICE_BUCKET = "entity_web_service";

    public enum S3Folder {
        PREAPPROVED_BASE_FOLDER("bases_preaprovadas"),
        SMS_BULK_FOLDER("sms_bulk");

        public String path;

        //
        S3Folder(String path) {
            this.path = path;
        }
    }

    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private Configuration configuration;
    @Autowired
    private SysUserDAO sysUserDAO;

    AmazonS3 s3client = new AmazonS3Client(
            new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));

    String getBucket() {
        return System.getenv("AWS_S3_BUCKET");
    }

    @Override
    public byte[] getPublicFile(String path, String filename) {
        return getFromPublicBucket(path, filename);
    }

    @Override
    public byte[] getUserFileById(int userFileId, boolean thumbnail) throws Exception {
        UserFile userFile = userDao.getUserFile(userFileId);
        return getUserFile(userFile.getUserId(), userFile.getFileName(), thumbnail);
    }

    @Override
    public byte[] getUserFile(int userId, String filename, boolean thumbnail) {
        if (thumbnail)
            return getThumbnailFromBucket("user/" + userId + "/", filename);
        else
            return getFromBucket("user/" + userId + "/", filename);
    }

    @Override
    public String writeUserFile(byte[] bytes, Integer userId, String fileName) throws Exception {
        return bucketSafeWrite("user/" + userId + "/", fileName, bytes, true, false);
    }

    @Override
    public String writeWebServiceFile(byte[] bytes, Integer webServiceLogId, String fileName) throws Exception {
        return bucketSafeWrite( FileServiceImpl.ENTITY_WEBSERVICE_BUCKET+ "/" + webServiceLogId + "/", fileName, bytes, false, false);
    }

    @Override
    public byte[] getWebServiceFile(int loanId, String filename, boolean thumbnail) {
        if (thumbnail)
            return getThumbnailFromBucket(FileServiceImpl.ENTITY_WEBSERVICE_BUCKET+"/" + loanId + "/", filename);
        else
            return getFromBucket(FileServiceImpl.ENTITY_WEBSERVICE_BUCKET+ "/" + loanId + "/", filename);
    }

    @Override
    public String getWebServiceFilePath(int loanId, String filename) {
        return FileServiceImpl.ENTITY_WEBSERVICE_BUCKET+"/" + loanId + "/"+ filename;
    }

    @Override
    public String getUserFilePath(Integer userId, String fileName){
        return "user/" + userId + "/"+ fileName;
    }

    @Override
    public String writeUserFile(byte[] bytes, Integer userId, String fileName, boolean aSync) throws Exception {
        return bucketSafeWrite("user/" + userId + "/", fileName, bytes, aSync, false);
    }

    @Override
    public byte[] getSysUserAvatar(int sysuserId, String filename, boolean thumbnail) throws Exception {
        if (thumbnail)
            return getThumbnailFromBucket("sysuser/" + sysuserId + "/", filename);
        else
            return getFromBucket("sysuser/" + sysuserId + "/", filename);
    }

    @Override
    public String setSysUserAvatar(byte[] avatarFile, int sysuserId, String filename) throws Exception {
        String extension = getExtensionLowerCase(filename);
        byte[] squareImage = cropCenterSquare(avatarFile, extension);
        String finalFilename = bucketOverride("sysuser/" + sysuserId + "/", filename, squareImage, false);
        if (isPresent("sysuser/" + sysuserId + "/" + Configuration.APP_FILE_THUMBANIL_PREFIX + filename)) {
            s3client.deleteObject(getBucket(), "sysuser/" + sysuserId + "/" + Configuration.APP_FILE_THUMBANIL_PREFIX + filename);
        }
        sysUserDAO.updateAvatar(sysuserId, finalFilename);
        return finalFilename;
    }

    @Override
    public byte[] getUserEmployerAvatar(int userEmployerId, String filename, boolean thumbnail) throws Exception {
        if (thumbnail)
            return getThumbnailFromBucket("employeruser/" + userEmployerId + "/", filename);
        else
            return getFromBucket("employeruser/" + userEmployerId + "/", filename);
    }

    @Override
    public String writeUserEmployerAvatar(byte[] avatarFile, int userEmployerId, String filename) throws Exception {
        String extension = getExtensionLowerCase(filename);
        byte[] squareImage = cropCenterSquare(avatarFile, extension);
        String finalFilename = bucketSafeWrite("employeruser/" + userEmployerId + "/", filename, squareImage, true, false);
        if (isPresent("employeruser/" + userEmployerId + "/" + Configuration.APP_FILE_THUMBANIL_PREFIX + filename)) {
            s3client.deleteObject(getBucket(), "employeruser/" + userEmployerId + "/" + Configuration.APP_FILE_THUMBANIL_PREFIX + filename);
        }
        return finalFilename;
    }

    @Override
    public boolean isImage(String fileName) {
        final String[] imagesFormats = {"png", "jpg", "jpeg", "gif"};
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        for (int i = 0; i < imagesFormats.length; i++) {
            if (extension.equals(imagesFormats[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String generateUserFileEncrypt(int userId, String fileName, int userFileId) {
        JSONObject jsonToEncrypt = new JSONObject();
        jsonToEncrypt.put("user", userId);
        jsonToEncrypt.put("file", fileName);
        jsonToEncrypt.put("fileid", userFileId);
        return CryptoUtil.encrypt(jsonToEncrypt.toString());
    }

    @Override
    public String generateEntityWebServiceFileEncrypt(int loanId, String fileName) {
        JSONObject jsonToEncrypt = new JSONObject();
        jsonToEncrypt.put("loanId", loanId);
        jsonToEncrypt.put("file", fileName);
        return CryptoUtil.encrypt(jsonToEncrypt.toString());
    }

    @Override
    public String generateUserFileUrl(int userId, int fileId, String fileName, HttpServletRequest request, boolean thumbnail) {
        return request.getContextPath() + "/file/userFile/" + generateUserFileEncrypt(userId, fileName, fileId) + "/image.jpg?thumbnail=" + thumbnail
                + "&t=" + new Date().getTime(); // Avoid cache image refresh
    }

    @Override
    public String generateEntityWebServiceFileUrl(int loanId, String fileName, HttpServletRequest request, boolean thumbnail) {
        return request.getContextPath() + "/file/entityWebServiceFile/" + generateEntityWebServiceFileEncrypt(loanId, fileName) + "/image.jpg?thumbnail=" + thumbnail
                + "&t=" + new Date().getTime(); // Avoid cache image refresh
    }

    @Override
    public String generateSplitUserFileUrl(int userId, int fileId, String fileName, HttpServletRequest request, boolean thumbnail) {
        return request.getContextPath() + "/file/splitUserFile/" + generateUserFileEncrypt(userId, fileName, fileId) + "/image.jpg?thumbnail=" + thumbnail;
    }

    @Override
    public String generateUserFileUrl(Integer userFileId, HttpServletRequest request, boolean thumbnail) throws Exception {
        if (userFileId == null)
            return "";

        UserFile userFile = userDao.getUserFile(userFileId);
        if (userFile == null)
            return "";
        return request.getContextPath() + "/file/userFile/" + generateUserFileEncrypt(userFile.getUserId(), userFile.getFileName(), userFile.getId()) + "/image.jpg?thumbnail=" + thumbnail;
    }

    @Override
    public String generateUserFileUrl(UserFile userFile, boolean thumbnail) throws Exception {
        if (userFile == null)
            return "";
        return Configuration.getClientDomain(CountryParam.COUNTRY_PERU) + "/file/userFile/" + generateUserFileEncrypt(userFile.getUserId(), userFile.getFileName(), userFile.getId()) + "/image.jpg?thumbnail=" + thumbnail;
    }

    @Override
    public String generateUserPdfSplittedUrl(UserFile userFile, int startPage, int endPage) throws Exception {
        if (userFile == null)
            return "";

        JSONObject jsonToEncrypt = new JSONObject();
        jsonToEncrypt.put("user", userFile.getUserId());
        jsonToEncrypt.put("file", userFile.getFileName());
        jsonToEncrypt.put("fileid", userFile.getId());
        jsonToEncrypt.put("startPage", startPage);
        jsonToEncrypt.put("endPage", endPage);
        return Configuration.getClientDomain(CountryParam.COUNTRY_PERU) + "/public/pdfSplit/" + CryptoUtil.encrypt(jsonToEncrypt.toString()) + "/download";
    }

    @Override
    public String generateSysUserAvatarUrl(String fileName, ServletRequest request, boolean thumbnail) {
        return ((HttpServletRequest) request).getContextPath() + "/file/sysuser/avatar/" + fileName + "?thumbnail=" + thumbnail;
    }

    @Override
    public ByteBuffer userFileToByteBuffer(UserFile userFile) {
        byte[] bytes = getUserFile(userFile.getUserId(), userFile.getFileName(), false);
        return ByteBuffer.wrap(bytes);
    }

    @Override
    public ByteBuffer webServiceFileToByteBuffer(int loanId, String filename) {
        byte[] bytes = getWebServiceFile(loanId, filename, false);
        return ByteBuffer.wrap(bytes);
    }

    @Override
    public byte[] getContract(String filename) throws Exception {
        return getFromBucket("contracts/", filename);
    }

    @Override
    public byte[] getAssociatedFile(String filename) throws Exception {
        return getFromBucket("entity/", filename);
    }

    @Override
    public String generatePublicUserFileUrl(int userFileId, boolean download) throws Exception {
        UserFile userFile = userDao.getUserFile(userFileId);
        if (userFile == null)
            return "";
        return Configuration.getClientDomain() + "/public/userFile/" + generateUserFileEncrypt(userFile.getUserId(), userFile.getFileName(), userFile.getId())
                + (download ? "/download" : "");
    }

    @Override
    public String writeReport(byte[] reportFile, String fileName) throws Exception {
        return bucketSafeWrite("reports/", fileName, reportFile, true, false);
    }

    @Override
    public String writeEntity(byte[] file, int entityId, String fileName) throws Exception {
        return uploadFileToCustomBucket(getBucket(),"entity/"+ entityId + "/", fileName , file ,true);
    }

    @Override
    public void getReportFile(String filename, OutputStream outputStream) throws Exception {
        getFromBucketToStream("reports/", filename, outputStream);
    }

    private static String getExtensionLowerCase(String filename) {
        String[] splitted = filename.split("\\.");
        return splitted[splitted.length - 1].toLowerCase();
    }

    private byte[] getThumbnailFromBucket(String path, String filename) {
        String extension = getExtensionLowerCase(filename);
        if (!extension.equals("mp3") && !extension.equals("wav")) {
            boolean thumbnailIsPresent = isPresent(path + Configuration.APP_FILE_THUMBANIL_PREFIX + filename);
            //abort
            if (extension.equals("pdf")) {
                //deliver pdf image
                return getFromPublicBucket("img/", "adobe-reader-pdf.png");
            }
            //fetch generated
            if (thumbnailIsPresent) {
                //fetch thumbnail
                return getFromBucket(path, Configuration.APP_FILE_THUMBANIL_PREFIX + filename);
            }
            //generate
            else {
                //fetch full Image
                byte[] fullImage = getFromBucket(path, filename);
                if (fullImage != null) {
                    InputStream inputStream = new ByteArrayInputStream(fullImage);
                    try {
                        BufferedImage thumbBufferedImage = Thumbnails
                                .of(inputStream)
                                .outputFormat(extension)
                                .crop(Positions.CENTER)
                                .size(100, 100)
                                .asBufferedImage();
                        //get byte[]
                        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                        ImageIO.write(thumbBufferedImage, extension, byteOutputStream);
                        byteOutputStream.flush();
                        byte[] thumbImage = byteOutputStream.toByteArray();
                        byteOutputStream.close();
                        //save to bucket
                        bucketOverride(path, Configuration.APP_FILE_THUMBANIL_PREFIX + filename, thumbImage, false);
                        return thumbImage;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }
        }
        return null;
    }

    private byte[] getFromPublicBucket(String path, String filename) {
        GetObjectRequest rangeObjectRequest = new GetObjectRequest("solven-public", path + filename);
        try {
            try (S3Object objectPortion = s3client.getObject(rangeObjectRequest)) {
                InputStream objectData = objectPortion.getObjectContent();
                return IOUtils.toByteArray(objectData);
            }
        } catch (AmazonS3Exception amazonS3Exception) {
            logger.error("No existe imagen de avatar con el nombre : " + path + filename);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getFromBucket(String path, String filename) {
        GetObjectRequest rangeObjectRequest = new GetObjectRequest(getBucket(), path + filename);
        try {
            try (S3Object objectPortion = s3client.getObject(rangeObjectRequest)) {
                InputStream objectData = objectPortion.getObjectContent();
                return IOUtils.toByteArray(objectData);
            }
        } catch (AmazonS3Exception amazonS3Exception) {
            logger.error("No existe imagen de avatar con el nombre : " + path + filename);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void getFromBucketToStream(String path, String filename, OutputStream outputStream) {
        GetObjectRequest rangeObjectRequest = new GetObjectRequest(getBucket(), path + filename);
        try {
            try (S3Object objectPortion = s3client.getObject(rangeObjectRequest)) {
                InputStream objectData = objectPortion.getObjectContent();

                byte[] read_buf = new byte[1024];
                int read_len = 0;
                while ((read_len = objectData.read(read_buf)) > 0) {
                    outputStream.write(read_buf, 0, read_len);
                }
            }
        } catch (AmazonS3Exception amazonS3Exception) {
            logger.error("No existe imagen de avatar con el nombre : " + path + filename);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether an image or folder exists or not in the input path.
     */
    private boolean isPresent(String path) {
        return s3client.doesObjectExist(getBucket(), path);
    }

    /**
     * Create folder if it doesn't exist
     */
    private void initFolder(String path) throws Exception {
        if (!isPresent(path)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(0);
            InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
            s3client.putObject(new PutObjectRequest(getBucket(), path, emptyContent, metadata));
        }
    }

    @Override
    public String bucketSafeToPublicBucket(String bucketPath, String fileName, byte[] bytes) throws Exception {
        initFolder(bucketPath);
        String filenameAndSuffix = fileName;
        int cont = 0;
        while (isPresent(bucketPath + filenameAndSuffix)) {
            cont++;
            filenameAndSuffix = FilenameUtils.getBaseName(fileName) + "(" + cont + ")." + FilenameUtils.getExtension(fileName);
        }

        return bucketOverridePublic(bucketPath, filenameAndSuffix, bytes);
    }

    private String bucketOverridePublic(String bucketPath, String fileName, byte[] bytes) throws Exception {
        initFolder(bucketPath);
        // send the file to amazon
        new Thread(() -> {
            try {
                // Write into the file
                s3client.putObject(new PutObjectRequest("solven-public", bucketPath + fileName, new ByteArrayInputStream(bytes), null).withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (Exception ex) {
                logger.error("Error uploading file to S3", ex);
            }
        }
        ).start();
        return fileName;
    }

    private String bucketSafeWrite(String bucketPath, String fileName, byte[] bytes, boolean aSync, boolean isPublic) throws Exception {
        initFolder(bucketPath);
        //Create  the file name verifying that the fi doesnt already exists
        String filenameAndSuffix = fileName;
        int cont = 0;
        while (isPresent(bucketPath + filenameAndSuffix)) {
            cont++;
            filenameAndSuffix = FilenameUtils.getBaseName(fileName) + "(" + cont + ")." + FilenameUtils.getExtension(fileName);
        }
        if (aSync)
            return bucketOverride(bucketPath, filenameAndSuffix, bytes, isPublic);
        return bucketOverrideSync(bucketPath, filenameAndSuffix, bytes, isPublic);
    }

    private String bucketOverrideSync(String bucketPath, final String fileName, byte[] bytes, boolean isPublic) throws Exception {
        initFolder(bucketPath);
        // send the file to amazon
        PutObjectRequest request = new PutObjectRequest(getBucket(), bucketPath + fileName, new ByteArrayInputStream(bytes), null);
        if (isPublic)
            request.withCannedAcl(CannedAccessControlList.PublicRead);
        s3client.putObject(request);

        return fileName;
    }


    private String bucketOverride(String bucketPath, final String fileName, byte[] bytes, boolean isPublic) throws Exception {
        initFolder(bucketPath);
        // send the file to amazon
        new Thread(() -> {
            try {
                PutObjectRequest request = new PutObjectRequest(getBucket(), bucketPath + fileName, new ByteArrayInputStream(bytes), null);
                if (isPublic)
                    request.withCannedAcl(CannedAccessControlList.PublicRead);
                s3client.putObject(request);
            } catch (Exception ex) {
                logger.error("Error uploading file to S3", ex);
            }
        }
        ).start();
        return fileName;
    }

    public String bucketOverrideCustomBucket(String bucket, String bucketPath, final String fileName, byte[] bytes, boolean isPublic) throws Exception {
        initFolder(bucketPath);
        // send the file to amazon
        try {
            PutObjectRequest request = new PutObjectRequest(bucket, bucketPath + fileName, new ByteArrayInputStream(bytes), null);
            if (isPublic)
                request.withCannedAcl(CannedAccessControlList.PublicRead);
            s3client.putObject(request);
        } catch (Exception ex) {
            logger.error("Error uploading file to S3", ex);
        }
        return fileName;
    }


    private byte[] cropCenterSquare(byte[] input, String extension) throws Exception {
        //reduce
        InputStream inputStream = new ByteArrayInputStream(input);
        //extract smaller size to make square
        BufferedImage buf = ImageIO.read(inputStream);
        int height = buf.getHeight();
        int width = buf.getWidth();
        int smaller = height < width ? height : width;
        //crop
        BufferedImage thumbBufferedImage = Thumbnails
                .of(buf)
                .outputFormat(extension)
                .crop(Positions.CENTER)
                .size(smaller, smaller)
                .asBufferedImage();
        //get byte[] newImage
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ImageIO.write(thumbBufferedImage, extension, byteOutputStream);
        byteOutputStream.flush();
        byte[] newImage = byteOutputStream.toByteArray();
        byteOutputStream.close();
        return newImage;
    }

    //--------------------------
    // New methods using the enum
    //--------------------------

    @Override
    public void makeFilePrivate(S3Folder folder, String fileName) throws Exception {
        s3client.setObjectAcl(getBucket(), folder.path + "/" + fileName, CannedAccessControlList.Private);
    }

    @Override
    public String getS3FileUrl(S3Folder folder, String fileName) throws Exception {
        return s3client.getUrl(getBucket(), folder.path + "/" + fileName).toExternalForm();
    }

    @Override
    public String getS3FilePublicTemporalUrl(S3Folder folder, String fileName, int secondsToExpiration) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, secondsToExpiration);
        return s3client.generatePresignedUrl(getBucket(), folder.path + "/" + fileName, calendar.getTime()).toExternalForm();
    }

    @Override
    public boolean fileExistsInFolder(S3Folder folder, String fileName) throws Exception {
        return s3client.doesObjectExist(getBucket(), folder.path + "/" + fileName);
    }

    @Override
    public void renameFile(S3Folder folder, String fileName, String newFileName) throws Exception {

        CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                getBucket(), folder.path + "/" + fileName,
                getBucket(), folder.path + "/" + newFileName);
        s3client.copyObject(copyObjRequest);
        s3client.deleteObject(new DeleteObjectRequest(getBucket(), folder.path + "/" + fileName));
    }

    @Override
    public byte[] getFileAsByteArray(S3Folder folder, String fileName) throws Exception {
        return getFromBucket(folder.path + "/", fileName);
    }

    @Override
    public void getFileToStream(S3Folder folder, String fileName, OutputStream outputStream) throws Exception {
        getFromBucketToStream(folder.path + "/", fileName, outputStream);
    }

    @Override
    public void useFileInputStream(S3Folder folder, String fileName, Function<InputStream, Exception> function) throws Exception {
        GetObjectRequest rangeObjectRequest = new GetObjectRequest(getBucket(), folder.path + "/" + fileName);
        try (S3Object objectPortion = s3client.getObject(rangeObjectRequest)) {
            InputStream inputStream = objectPortion.getObjectContent();
            Exception ex = function.apply(inputStream);
            if (ex != null)
                throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String CustomBucketOverridePublic(String bucketName, String bucketPath,String fileName, String string, Boolean isTest) throws Exception {

        if(isTest != null && isTest == true){
            s3client.putObject(new PutObjectRequest(bucketName, bucketPath+"/"  +fileName, new ByteArrayInputStream(string.getBytes()), null).withCannedAcl(CannedAccessControlList.PublicRead));
        }

        if(null == isTest){
            initFolder(bucketPath);
            new Thread(() -> {
                try {
                    // Write into the file
                    s3client.putObject(new PutObjectRequest(bucketName, bucketPath+"/"  +fileName, new ByteArrayInputStream(string.getBytes()), null).withCannedAcl(CannedAccessControlList.PublicRead));
                } catch (Exception ex) {
                    logger.error("Error uploading file to S3", ex);
                }
            }
            ).start();

        }
        return fileName;
    }

    @Override
    public byte[] getRotateImageBytes(int angle, byte[] bytes) {
        if (angle == 0 || angle % 360 == 0)
            return bytes;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        try {
            Thumbnails.of(in)
                    .scale(1)
                    .rotate(angle)
                    .toOutputStream(baos);
        } catch (IOException e) {
            logger.error("Error rotating file", e);
        }
        return baos.toByteArray();
    }

    @Override
    public String overrideUserFile(byte[] bytes, Integer userId, String fileName) throws Exception {
        return bucketOverrideSync("user/" + userId + "/", fileName, bytes, false);
    }
    //private String bucketOverrideSync(String bucketPath, final String fileName, byte[] bytes, boolean isPublic) throws Exception {

    @Override
    public String uploadFileToCustomBucket(String bucket, String bucketPath, final String fileName, byte[] bytes, boolean isPublic) throws Exception {
        initFolder(bucketPath);
        try {
            PutObjectRequest request = new PutObjectRequest(bucket, bucketPath + fileName, new ByteArrayInputStream(bytes), null);
            if (isPublic)
                request.withCannedAcl(CannedAccessControlList.PublicRead);
            s3client.putObject(request);
            return s3client.getUrl(bucket, bucketPath + fileName).toString();
        } catch (Exception ex) {
            logger.error("Error uploading file to S3", ex);
            throw ex;
        }
    }

    @Override
    public byte[] downloadFile(final String url) throws IOException {
        AmazonS3URI amazonS3URI = new AmazonS3URI(url);
        return downloadFile(amazonS3URI.getBucket(),amazonS3URI.getKey());
    }

    @Override
    public byte[] downloadFile(final String bucket, final String keyName) throws IOException {
        byte[] content = null;
        final S3Object s3Object = s3client.getObject(bucket, keyName);
        final S3ObjectInputStream stream = s3Object.getObjectContent();
        try {
            content = com.amazonaws.util.IOUtils.toByteArray(stream);
            s3Object.close();
            System.out.println("Finished download file");
        } catch(final IOException ex) {
            System.out.println("Error download file");
            throw ex;
        }
        return content;
    }

    @Override
    public File convertByteArrayToFile(byte[] arrayData, String filename) throws IOException {
        final File file = File.createTempFile(filename,null);
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(arrayData);
        } catch (final IOException ex) {
           ex.printStackTrace();
           throw new RuntimeException("ERROR CONVERTING FILE");
        }
        return file;
    }

    @Override
    public byte[] getRemoteExternFile(String location) throws Exception {
        URL url = new URL(location);
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = url.openStream ();
            bytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
            //handle errors
        }
        finally {
            if (is != null) is.close();
        }
        return bytes;
    }

    @Override
    public String getPresignedUrl( String keyName, long timeMillis)throws IOException {
        return getSharingObjectWithPresignedUrl("reports/",keyName,timeMillis);
    }


    @Override
    public String getSharingObjectWithPresignedUrl(String path, String keyName, long timeMillis)throws IOException {

        // Set the presigned URL to expire after one hour.
        Date expiration = new Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis += timeMillis;
        expiration.setTime(expTimeMillis);

        // Generate the presigned URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(getBucket(), path + keyName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    @Override
    public String generatePresignedUrl(String filePath, String contentType, String filename, long timeMillis){
        // Set the pre-signed URL to expire after one hour.
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime() + timeMillis;
        expiration.setTime(expTimeMillis);
        String objectKey = filename;

        // Generate the pre-signed URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(getBucket(), String.format("%s%s",filePath,filename))
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        if(contentType != null && !contentType.trim().isEmpty()) generatePresignedUrlRequest.setContentType(contentType);
        URL url = this.s3client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }
}
