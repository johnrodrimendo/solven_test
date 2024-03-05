package com.affirm.common.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * All credits for http://www.memorylack.com/2011/06/apache-commons-vfs-for-sftp.html
 * <p>
 * nestoru - 2012/01/24: Small changes:
 * 1. Using logging instead of System.out.println()
 * 2. The initial directory is not the user home directory
 * 3. No additional slashes when adding remoteFilePath.
 * 4. Using InputStream instead of path to a local file
 * <p>
 * If you need extra functionality check out the url above
 */
public class SftpUtil {
    private final static Logger log = LoggerFactory.getLogger(SftpUtil.class);

    public static void upload(String hostName, String username,
                              String password, Map<String, InputStream> mapInputStream, String remoteFilePath, String targetFileNamePrefix) throws Exception {

        StandardFileSystemManager manager = new StandardFileSystemManager();
        FileObject localFile = null;
        FileObject remoteFile = null;

        try {
            manager.init();
            final String ROOTPATH = "ram://virtual";
            FileSystemOptions fileSystemOptions = createDefaultOptions();
            manager.createVirtualFileSystem(ROOTPATH);


            for (Map.Entry<String, InputStream> entry : mapInputStream.entrySet()) {
                localFile = manager.resolveFile(ROOTPATH + "/" + entry.getKey());
                localFile.createFile();
                OutputStream localOutputStream = localFile.getContent().getOutputStream();
                IOUtils.copy(entry.getValue(), localOutputStream);
                localOutputStream.flush();
                remoteFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath + targetFileNamePrefix + entry.getKey()), fileSystemOptions);
                remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
            }

            log.debug("File upload success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (remoteFile != null)
                remoteFile.close();
            if (localFile != null)
                localFile.close();
            manager.close();
        }
    }

    public static void upload(String hostName, String username,
                              String password, Map<String, InputStream> mapInputStream, String remoteFilePath, String backupFilePath, String targetFileNamePrefix) throws Exception {

        StandardFileSystemManager manager = new StandardFileSystemManager();
        FileObject localFile = null;
        FileObject remoteFile = null;
        FileObject backupFile = null;

        try {
            manager.init();
            final String ROOTPATH = "ram://virtual";
            FileSystemOptions fileSystemOptions = createDefaultOptions();
            manager.createVirtualFileSystem(ROOTPATH);


            for (Map.Entry<String, InputStream> entry : mapInputStream.entrySet()) {
                localFile = manager.resolveFile(ROOTPATH + "/" + entry.getKey());
                localFile.createFile();
                OutputStream localOutputStream = localFile.getContent().getOutputStream();
                IOUtils.copy(entry.getValue(), localOutputStream);
                localOutputStream.flush();
                remoteFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath + targetFileNamePrefix + entry.getKey()), fileSystemOptions);
                remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
                backupFile = manager.resolveFile(createConnectionString(hostName, username, password, backupFilePath + targetFileNamePrefix + entry.getKey()), fileSystemOptions);
                backupFile.copyFrom(localFile, Selectors.SELECT_SELF);
            }

            log.debug("File upload success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (backupFile != null)
                backupFile.close();
            if (remoteFile != null)
                remoteFile.close();
            if (localFile != null)
                localFile.close();
            manager.close();
        }
    }

    public static String createConnectionString(String hostName, String username, String password, String remoteFilePath) {
        return "sftp://" + username + ":" + password + "@" + hostName + remoteFilePath;
    }

    public static String createConnectionStringFTPS(String hostName, String username, String password, String remoteFilePath) {
        return "sftp://" + username + ":" + password + "@" + hostName + remoteFilePath;
    }

    public static FileSystemOptions createDefaultOptions() throws FileSystemException {
        FileSystemOptions opts = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);
        return opts;
    }

    public static FileObject read(String hostName, String username,
                            String password, String filePath){
        StandardFileSystemManager manager = new StandardFileSystemManager();
        FileObject remoteFile = null;

        try {
            manager.init();
            final String ROOTPATH = "ram://virtual";
            FileSystemOptions fileSystemOptions = createDefaultOptions();
            manager.createVirtualFileSystem(ROOTPATH);
            String uri = createConnectionString(hostName, username, password, filePath);
            remoteFile = manager.resolveFile(uri, fileSystemOptions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }

        return remoteFile;
    }

    public static boolean exist(String hostName, String username, String password, String remoteFilePath) {
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create remote object

            String uri = createConnectionStringFTPS(hostName, username, password, remoteFilePath);

            final String ROOTPATH = "ram://virtual";
            FileSystemOptions fileSystemOptions = createDefaultOptions();
            manager.createVirtualFileSystem(ROOTPATH);

            FileObject remoteFile = manager.resolveFile(UriParser.encode(uri), fileSystemOptions);

            System.out.println("File exist: " + remoteFile.exists());

            return remoteFile.exists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    public static void download(String hostName, String username, String password, String localFilePath, String remoteFilePath) {

        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create local file object. Change location if necessary for new downloadFilePath
            FileObject localFile = manager.resolveFile(localFilePath);

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions());

            // Copy local file to sftp server
            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);

            System.out.println("File download success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    public static FileObject getFile(String hostName, String username, String password, String remoteFilePath) {

        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create remote object

            String uri = createConnectionStringFTPS(hostName, username, password, remoteFilePath);
            System.out.println(UriParser.encode(uri));
            final String ROOTPATH = "ram://virtual";
            FileSystemOptions fileSystemOptions = createDefaultOptions();
            manager.createVirtualFileSystem(ROOTPATH);

            FileObject remoteFile = manager.resolveFile(UriParser.encode(uri), fileSystemOptions);

            System.out.println("File exist: " + remoteFile.exists());

            return remoteFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    public static void delete(String hostName, String username, String password, String remoteFilePath) {
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create remote object
            FileObject remoteFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions());

            if (remoteFile.exists()) {
                remoteFile.delete();
                System.out.println("Delete remote file success");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }
}
