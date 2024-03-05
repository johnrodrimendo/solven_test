package com.affirm.common.service;

import com.affirm.common.model.transactional.UserFile;
import com.affirm.common.service.impl.FileServiceImpl;
import com.amazonaws.services.s3.AmazonS3URI;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * @author jrodriguez
 */
public interface FileService {

    byte[] getPublicFile(String path, String filename);

    byte[] getUserFileById(int userFileId, boolean thumbnail) throws Exception;

    byte[] getUserFile(int userId, String filename, boolean thumbnail) throws Exception;

    String writeUserFile(byte[] bytes, Integer userId, String fileName) throws Exception;

    String writeWebServiceFile(byte[] bytes, Integer webServiceLogId, String fileName) throws Exception;

    byte[] getWebServiceFile(int loanId, String filename, boolean thumbnail);

    String getWebServiceFilePath(int loanId, String filename);
    String getUserFilePath(Integer userId, String fileName);
    ByteBuffer webServiceFileToByteBuffer(int loanId, String filename);

    byte[] getUserEmployerAvatar(int userEmployerId, String filename, boolean thumbnail) throws Exception;

    String writeUserEmployerAvatar(byte[] avatarFile, int userEmployerId, String filename) throws Exception;

    boolean isImage(String fileName);

    String writeUserFile(byte[] bytes, Integer userId, String fileName, boolean aSync) throws Exception;

    byte[] getSysUserAvatar(int sysuserId, String filename, boolean thumbnail) throws Exception;

    String setSysUserAvatar(byte[] avatarFile, int sysuserId, String filename) throws Exception;

    String generateUserFileEncrypt(int userId, String fileName, int userFileId);

    String generateEntityWebServiceFileEncrypt(int loanId, String fileName);

    String generateEntityWebServiceFileUrl(int loanId, String fileName, HttpServletRequest request, boolean thumbnail);

    String generateUserFileUrl(int userId, int fileId, String fileName, HttpServletRequest request, boolean thumbnail);

    String generateUserFileUrl(Integer userFileId, HttpServletRequest request, boolean thumbnail) throws Exception;

    String generateUserFileUrl(UserFile userFile, boolean thumbnail) throws Exception;

    String generateUserPdfSplittedUrl(UserFile userFile, int startPage, int endPage) throws Exception;

    String generateSysUserAvatarUrl(String fileName, ServletRequest request, boolean thumbnail);

    ByteBuffer userFileToByteBuffer(UserFile userFile);

    byte[] getContract(String filename) throws Exception;

    byte[] getAssociatedFile(String filename) throws Exception;

    String generatePublicUserFileUrl(int userFileId, boolean download) throws Exception;

    String writeReport(byte[] reportFile, String fileName) throws Exception;

    String writeEntity(byte[] file, int entityId, String fileName) throws Exception;

    void getReportFile(String filename, OutputStream outputStream) throws Exception;

    String bucketSafeToPublicBucket(String bucketPath, String fileName, byte[] bytes) throws Exception;

    void makeFilePrivate(FileServiceImpl.S3Folder folder, String fileName) throws Exception;

    String getS3FileUrl(FileServiceImpl.S3Folder folder, String fileName) throws Exception;

    String getS3FilePublicTemporalUrl(FileServiceImpl.S3Folder folder, String fileName, int secondsToExpiration) throws Exception;

    boolean fileExistsInFolder(FileServiceImpl.S3Folder folder, String fileName) throws Exception;

    void renameFile(FileServiceImpl.S3Folder folder, String fileName, String newFileName) throws Exception;

    byte[] getFileAsByteArray(FileServiceImpl.S3Folder folder, String fileName) throws Exception;

    void getFileToStream(FileServiceImpl.S3Folder folder, String fileName, OutputStream outputStream) throws Exception;

    void useFileInputStream(FileServiceImpl.S3Folder folder, String fileName, Function<InputStream, Exception> function) throws Exception;

    String CustomBucketOverridePublic(String bucketName, String bucketPath,String fileName, String string, Boolean isTest) throws Exception;

    String generateSplitUserFileUrl(int userId, int fileId, String fileName, HttpServletRequest request, boolean thumbnail);

    byte[] getRotateImageBytes(int angle, byte[] bytes);

    String overrideUserFile(byte[] bytes, Integer userId, String fileName) throws Exception;

    String uploadFileToCustomBucket(String bucket, String bucketPath, final String fileName, byte[] bytes, boolean isPublic) throws Exception;

    byte[] downloadFile(final String bucket, final String keyName) throws IOException;

    byte[] downloadFile(String url) throws IOException;

    File convertByteArrayToFile(byte[] arrayData, String filename) throws IOException;
    
    String bucketOverrideCustomBucket(String bucket, String bucketPath, final String fileName, byte[] bytes, boolean isPublic) throws Exception;

    byte[] getRemoteExternFile(String location) throws Exception;

    String getPresignedUrl( String keyName, long timeMillis)throws IOException;

    String getSharingObjectWithPresignedUrl(String path, String keyName, long timeMillis)throws IOException;

    String generatePresignedUrl(String filePath, String contentType, String filename, long timeMillis);

}