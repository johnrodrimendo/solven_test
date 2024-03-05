package com.affirm.common.service;

import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.aws.RekognitionAdapter;
import com.affirm.bancoazteca.model.ReniecDataResponse;
import com.affirm.bancoazteca.service.BancoAztecaReniecServiceCall;
import com.affirm.common.EntityProductParamIdentityValidationParamsConfig;
import com.affirm.common.EntityProductParamUserFileUploadValidationConfig;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.util.Util;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.affirm.system.configuration.Configuration;
import com.amazonaws.services.rekognition.model.*;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.tuple.Tuple2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.model.DetectProtectiveEquipmentResponse;
import software.amazon.awssdk.services.rekognition.model.EquipmentDetection;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentBodyPart;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentPerson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jarmando on 15/03/17.
 */
@Service
public class RekognitionService {

    private static Logger logger = Logger.getLogger(RekognitionService.class);
    RekognitionAdapter rekognitionAdapter = new RekognitionAdapter();

    public static final String NO_FACE_DETECTED_ERROR = "NO_FACE_DETECTED";
    public static final String MULTIPLE_FACES_DETECTED_ERROR = "MULTIPLE_FACES_DETECTED";
    public static final String MINIMUM_CONFIDENCE_VALUE_ERROR = "CONFIDENCE_NOT_ALLOWED";
    public static final String MINIMUM_SHARPNESS_VALUE_ERROR = "SHARPNESS_INVALID";
    public static final String MINIMUM_FACE_SIZE_VALUE_ERROR = "FACE_SIZE_NOT_ALLOWED";
    public static final String NO_TEXT_DETECTED_ERROR = "NO_TEXT_DETECTED";
    public static final String IMAGE_WITH_INVALID_CONTENT_ERROR = "IMAGE_WITH_INVALID_CONTENT";
    public static final String NO_LABELS_DETECTED_ERROR = "NO_LABELS_DETECTED_ERROR";
    public static final String INVALID_LABEL_DETECTED_ERROR = "INVALID_LABEL_DETECTED_ERROR";
    public static final String NO_BODY_PARTS_DETECTED_ERROR = "NO_BODY_PARTS_DETECTED";
    public static final String INVALID_CONTENT_FOUND_DETECTED_ERROR = "INVALID_CONTENT_FOUND";
    public static final double MINIMUM_CONFIDENCE_FOR_TEXT = 85.0;
    public static final String DNI_FEC_CADUCIDAD_NO_CADUCA = "no caduca";
    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    FileService fileService;
    @Autowired
    UserService userService;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    LoanApplicationApprovalValidationService loanApplicationApprovalValidationService;
    @Autowired
    SecurityDAO securityDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private BancoAztecaReniecServiceCall bancoAztecaReniecServiceCall;
    @Autowired
    private SecurityDAO securityDao;
    @Autowired
    private AwsTextractService awsTextractService;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private LoanApplicationService loanApplicationService;

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage rotateImage(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage newImage = new BufferedImage(height, width, 5);
        //interchange all pixels
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                newImage.setRGB(height - 1 - j, i, img.getRGB(i, j));
        return newImage;
    }

    private byte[] joinByteBuffers(byte[] b0, byte[] b1) {
        byte[] joinedImageBytes = null;
        try {
            BufferedImage joinedDni = Util.joinByteArray(b0, b1);
            BufferedImage openJoinedImage = new BufferedImage(joinedDni.getWidth(), joinedDni.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            openJoinedImage.getGraphics().drawImage(joinedDni, 0, 0, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(openJoinedImage, "jpg", baos);
            baos.flush();
            joinedImageBytes = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return joinedImageBytes;
    }

    private byte[] generateAllImagesMerged(byte[] originalImageBytes) {
        byte allImages[];
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(originalImageBytes);
            BufferedImage originalImage = ImageIO.read(bais);
            BufferedImage imageAux = originalImage;
            allImages = originalImageBytes;
            byte[] bytesAux = null;
            for (int i = 0; i < 3; i++) {
                //rotate the image
                imageAux = rotateImage(imageAux);
                //get image bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(imageAux, "jpg", baos);
                baos.flush();
                bytesAux = baos.toByteArray();
                baos.close();
                allImages = joinByteBuffers(allImages, bytesAux);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return allImages;

    }


    /* return number of comparisons made */
    public void updateCompareOneDniVsSelfieNotYetCompared(int loanApplicationId, int personId, int userId, Locale locale, int fileType) {
        Configuration.rekognitionExecutor.execute(
                Unchecked.runnable(() -> {
                    Tuple2<ByteBuffer, ByteBuffer> byteBufferParams;
                    List<UserFile> dnisUserFilesToProcess = new ArrayList<>();
                    List<UserFile> selfiesToProcess = new ArrayList<>();
                    Integer mergedUserFileId = null;
                    try {

                        // get selfies
                        List<LoanApplicationUserFiles> loanApplicationFiles = personDAO.getLoanApplicationFiles(loanApplicationId, personId, locale);
                        if (loanApplicationFiles == null) return;

                        List<UserFile> allLoanAppFiles = loanApplicationFiles
                                .stream()
                                .flatMap(x -> x.getUserFileList().stream())
                                .collect(Collectors.toList());

                        List<UserFile> allSelfies = userService.getUserFileByType(allLoanAppFiles, UserFileType.SELFIE);
                        if (allSelfies == null) return;

                        List<UserFile> allDnis =
                                userService.getUserFileByType(allLoanAppFiles, UserFileType.DNI_FRONTAL, UserFileType.DNI_ANVERSO, UserFileType.CEDULA_CIUDADANIA_FRONTAL, UserFileType.CEDULA_CIUDADANIA_ANVERSO);
                        //evaluate the new image

                        ByteBuffer bbDni1 = fileService.userFileToByteBuffer(allDnis.get(0));
                        byte[] b0 = new byte[bbDni1.remaining()];
                        bbDni1.get(b0);

                        final int id2;
                        if (allDnis.size() > 1) {
                            id2 = 1;
                        } else {
                            id2 = 0;
                        }
                        ByteBuffer bbDni2 = fileService.userFileToByteBuffer(allDnis.get(id2));
                        byte[] b1 = new byte[bbDni2.remaining()];
                        bbDni2.get(b1);

                        BufferedImage joinedDni = Util.joinByteArray(b0, b1);
                        BufferedImage openJoinedImage = new BufferedImage(joinedDni.getWidth(), joinedDni.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                        openJoinedImage.getGraphics().drawImage(joinedDni, 0, 0, null);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(openJoinedImage, "jpg", baos);
                        baos.flush();
                        byte[] joinedImageBytes = baos.toByteArray();
                        baos.close();

                        //selfie to process
                        ByteBuffer selfieToProcess = fileService.userFileToByteBuffer(allSelfies.get(0));
                        byte[] selfie = new byte[selfieToProcess.remaining()];
                        selfieToProcess.get(selfie);
                        //selfie= generateAllImagesMerged(selfie);
                        selfieToProcess = ByteBuffer.wrap(selfie);

                        //take selfie foto for param1
                        ByteBuffer bytes1 = selfieToProcess;
                        ByteBuffer bytes2 = ByteBuffer.wrap(joinedImageBytes);

                        // Save the file in s3
                        String fileName = fileService.writeUserFile(joinedImageBytes, userId, "merged_dni.jpg");
                        // Register the file
                        mergedUserFileId = userDAO.registerUserFile(userId, loanApplicationId, UserFileType.DNI_MERGED, fileName);

                        final Integer mergedUserFileIduserFinal = mergedUserFileId;


                        final List<UserFile> dnisUserFilesToProcessFinal = dnisUserFilesToProcess;
                        final List<UserFile> selfiesToProcessFinal = selfiesToProcess;

                        System.out.println("init update comparison");

                        BufferedImage aux = createImageFromBytes(selfie);
                        ArrayList<Tuple2<Double, JSONObject>> results = new ArrayList<>();
                        for (int i = 0; i < 4; i++) {

                            byteBufferParams = new Tuple2<>(bytes1, bytes2);
                            if (allDnis.size() > 0) {
                                rekognitionAdapter.compareFaces(
                                        byteBufferParams.v1(),
                                        byteBufferParams.v2(),
                                        (highestSimilarity, result) -> {
                                            results.add(new Tuple2<Double, JSONObject>(highestSimilarity, result));
                                            //System.out.println("highest_similarity: " + highestSimilarity + "%");
                                            //register success recognition
                                            System.out.println("register success recognition: " + result);
                                            loanApplicationDAO.registerAmazonRekognition(loanApplicationId, highestSimilarity, result.toString() == null ? "{}" : result.toString(), allDnis.get(0).getId(), allDnis.get(id2).getId(), mergedUserFileIduserFinal, allSelfies.get(0).getId());
                                        }, e -> {
                                            //register error recognition (json null)
                                            System.out.println("register error recognition (json null)");
                                            loanApplicationDAO.registerAmazonRekognition(loanApplicationId, 0.0, "{}", allDnis.get(0).getId(), allDnis.get(id2).getId(), mergedUserFileIduserFinal, allSelfies.get(0).getId());
                                            e.printStackTrace();
                                        }
                                );
                            }


                            loanApplicationDAO.registerAmazonRekognitionFacesLabels(loanApplicationId, rekognitionAdapter.detectFaces(bytes1), rekognitionAdapter.detectLabels(bytes1));
                            //System.out.println("Terminado de registrar el resultado del rekognition N"+(i+1));

                            BufferedImage rotatedSelfie = rotateImage(aux);
                            baos = new ByteArrayOutputStream();
                            ImageIO.write(rotatedSelfie, "jpg", baos);
                            baos.flush();
                            byte[] imageInByte = baos.toByteArray();
                            baos.close();

                            bytes1 = ByteBuffer.wrap(imageInByte);
                        }

                        double highestSimilarity = results.get(0).v1;
                        JSONObject result = results.get(0).v2;
                        ;

                        for (Tuple2<Double, JSONObject> t : results) {
                            if (highestSimilarity < t.v1) {
                                highestSimilarity = t.v1;
                                result = t.v2;
                            }
                        }

                        loanApplicationDAO.registerAmazonRekognition(loanApplicationId, highestSimilarity, result.toString() == null ? "{}" : result.toString(), allDnis.get(0).getId(), allDnis.get(id2).getId(), mergedUserFileIduserFinal, allSelfies.get(0).getId());

                    } catch (Exception e) {
                        //register error recognition (json null)
                        final int id2;
                        if (dnisUserFilesToProcess.size() > 1) {
                            id2 = 1;
                        } else {
                            id2 = 0;
                        }
                        if (dnisUserFilesToProcess.size() > 0 && selfiesToProcess.size() > 0) {
                            System.out.println("register error recognition (json null)");
                            loanApplicationDAO.registerAmazonRekognition(
                                    loanApplicationId,
                                    0.0,
                                    "{}",
                                    Optional.ofNullable(dnisUserFilesToProcess.get(0).getId()).orElse(null),
                                    Optional.ofNullable(dnisUserFilesToProcess.get(id2).getId()).orElse(null),
                                    mergedUserFileId,
                                    Optional.ofNullable(selfiesToProcess.get(0).getId()).orElse(null));
                            loanApplicationDAO.registerAmazonRekognitionFacesLabels(loanApplicationId, "{}", "{}");
                        }
                        e.printStackTrace();
                    }
                })
        );
    }

    /* return number of comparisons made */
    public void compareOneDniVsSelfieNotYetCompared(int loanApplicationId, int personId, int userId, Locale locale, boolean runApprovalValidation) {
        Configuration.rekognitionExecutor.execute(
                Unchecked.runnable(() -> {
                    Tuple2<ByteBuffer, ByteBuffer> byteBufferParams;
                    boolean debug = false;

                    if (!debug) {
                        List<UserFile> dnisUserFilesToProcess = new ArrayList<>();
                        List<UserFile> selfiesToProcess = new ArrayList<>();
                        Integer mergedUserFileId = null;
                        try {

                            // get all loan files
                            List<LoanApplicationUserFiles> loanApplicationFiles = personDAO.getLoanApplicationFiles(loanApplicationId, personId, locale);
                            if (loanApplicationFiles == null) return;
                            List<UserFile> allLoanAppFiles = loanApplicationFiles
                                    .stream()
                                    .flatMap(x -> x.getUserFileList().stream())
                                    .collect(Collectors.toList());

                            //rekognition results to know what userfiles have been processed
                            List<RecognitionResultsPainter> rekognitionPreviousResults = loanApplicationDAO.getRecognitionResults(personId, locale);
                            if (rekognitionPreviousResults == null)
                                rekognitionPreviousResults = new ArrayList<>();

                            //mix two dni fotos for param2
                            List<UserFile> allDnis = userService.getUserFileByType(allLoanAppFiles, UserFileType.DNI_FRONTAL, UserFileType.DNI_ANVERSO, UserFileType.CEDULA_CIUDADANIA_FRONTAL, UserFileType.CEDULA_CIUDADANIA_ANVERSO);
                            List<Integer> allDniIdsWithResults = rekognitionPreviousResults
                                    .stream()
                                    .filter(x -> x.getLoanApplicationId() == loanApplicationId)
                                    .flatMap(x -> x.getResults().stream())
                                    .flatMap(x -> Stream.of(x.getUserFilesIdDniA(), x.getUserFilesIdDniB(), x.getUserFilesIdDniMerged()))
                                    .collect(Collectors.toList());

                            dnisUserFilesToProcess = allDnis.stream()//all dnis
                                    .filter(s -> !allDniIdsWithResults.contains(s.getId()))// that are not dnis with results
                                    .collect(Collectors.toList());

                            if(dnisUserFilesToProcess.size() > 0){
                                ByteBuffer bbDni1 = fileService.userFileToByteBuffer(dnisUserFilesToProcess.get(0));
                                byte[] b0 = new byte[bbDni1.remaining()];
                                bbDni1.get(b0);
                                BufferedImage joinedDni = Util.toBufferedImage(b0);

                                if (dnisUserFilesToProcess.size() > 1) {
                                    ByteBuffer bbDni2 = fileService.userFileToByteBuffer(dnisUserFilesToProcess.get(1));
                                    byte[] b1 = new byte[bbDni2.remaining()];
                                    bbDni2.get(b1);
                                    joinedDni = Util.joinByteArray(b0, b1);
                                }

                                BufferedImage openJoinedImage = new BufferedImage(joinedDni.getWidth(), joinedDni.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                                openJoinedImage.getGraphics().drawImage(joinedDni, 0, 0, null);

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ImageIO.write(openJoinedImage, "jpg", baos);
                                baos.flush();
                                byte[] joinedImageBytes = baos.toByteArray();
                                baos.close();

                                ByteBuffer bytes2 = ByteBuffer.wrap(joinedImageBytes);

                                // Save the file in s3 and register ir
                                String fileName = fileService.writeUserFile(joinedImageBytes, userId, "merged_dni.jpg", false);
                                mergedUserFileId = userDAO.registerUserFile(userId, loanApplicationId, UserFileType.DNI_MERGED, fileName);


                                //selfies of this loan app
                                List<UserFile> allSelfies = userService.getUserFileByType(allLoanAppFiles, UserFileType.SELFIE);
                                if (allSelfies == null) return;
                                //with results
                                List<Integer> allSelfiesIdsWithResults = rekognitionPreviousResults
                                        .stream()
                                        .filter(x -> x.getLoanApplicationId() == loanApplicationId)
                                        .flatMap(x -> x.getResults().stream())
                                        .map(x -> x.getSelfieUserFileId())
                                        .collect(Collectors.toList());
                                //without results
                                selfiesToProcess = allSelfies.stream()//all selfies
                                        .filter(s -> !allSelfiesIdsWithResults.contains(s.getId()))// that are not selfies with results
                                        .collect(Collectors.toList());

                                System.out.println("size to process: " + selfiesToProcess.size());

                                if (selfiesToProcess.size() <= 0) {
                                    return;
                                }
                                //selfie to process
                                ByteBuffer selfieToProcess = fileService.userFileToByteBuffer(selfiesToProcess.get(0));

                                //take selfie foto for param1
                                ByteBuffer bytes1 = selfieToProcess;


                                // Run rekognition
                                byteBufferParams = new Tuple2<>(bytes1, bytes2);

                                final List<UserFile> dnisUserFilesToProcessFinal = dnisUserFilesToProcess;
                                final Integer mergedUserFileIduserFinal = mergedUserFileId;
                                final List<UserFile> selfiesToProcessFinal = selfiesToProcess;

                                final int id2;
                                if (dnisUserFilesToProcessFinal.size() > 1) {
                                    id2 = 1;
                                } else {
                                    id2 = 0;
                                }

                                System.out.println("init compare ");
                                rekognitionAdapter.compareFaces(
                                        byteBufferParams.v1(),
                                        byteBufferParams.v2(),
                                        (highestSimilarity, result) -> {
                                            try{
                                                System.out.println("highest_similarity: " + highestSimilarity + "%");
                                                //register success recognition
                                                System.out.println("register success recognition: " + result);
                                                loanApplicationDAO.registerAmazonRekognition(loanApplicationId, highestSimilarity, result.toString() == null ? "{}" : result.toString(), dnisUserFilesToProcessFinal.get(0).getId(), dnisUserFilesToProcessFinal.get(id2).getId(), mergedUserFileIduserFinal, selfiesToProcessFinal.get(0).getId());
                                                if(runApprovalValidation)
                                                    loanApplicationApprovalValidationService.validateAndUpdate(loanApplicationId, ApprovalValidation.IDENTIDAD);
                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                            }
                                        }, e -> {
                                            try{
                                                //register error recognition (json null)
                                                System.out.println("register error recognition (json null)");
                                                loanApplicationDAO.registerAmazonRekognition(loanApplicationId, 0.0, "{}", dnisUserFilesToProcessFinal.get(0).getId(), dnisUserFilesToProcessFinal.get(id2).getId(), mergedUserFileIduserFinal, selfiesToProcessFinal.get(0).getId());
                                                e.printStackTrace();
                                                if(runApprovalValidation)
                                                    loanApplicationApprovalValidationService.validateAndUpdate(loanApplicationId, ApprovalValidation.IDENTIDAD);
                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                            }
                                        }
                                );

                                loanApplicationDAO.registerAmazonRekognitionFacesLabels(loanApplicationId, rekognitionAdapter.detectFaces(bytes1), rekognitionAdapter.detectLabels(bytes1));
                            }

                        } catch (Exception e) {
                            //register error recognition (json null)
                            System.out.println("register error recognition (json null)");
                            final int id2;
                            if (dnisUserFilesToProcess.size() > 1) {
                                id2 = 1;
                            } else {
                                id2 = 0;
                            }
                            loanApplicationDAO.registerAmazonRekognition(
                                    loanApplicationId,
                                    0.0,
                                    "{}",
                                    dnisUserFilesToProcess.size() > 0 ? Optional.ofNullable(dnisUserFilesToProcess.get(0).getId()).orElse(null) : null,
                                    dnisUserFilesToProcess.size() > 0 ? Optional.ofNullable(dnisUserFilesToProcess.get(id2).getId()).orElse(null) : null,
                                    mergedUserFileId,
                                    Optional.ofNullable(selfiesToProcess.get(0).getId()).orElse(null));

                            loanApplicationDAO.registerAmazonRekognitionFacesLabels(loanApplicationId, "{}", "{}");
                            if(runApprovalValidation)
                                loanApplicationApprovalValidationService.validateAndUpdate(loanApplicationId, ApprovalValidation.IDENTIDAD);
                            e.printStackTrace();
                        }
                    } else {
                        byteBufferParams = getTestParams();
                        System.out.println("init compare ");
                        rekognitionAdapter.compareFaces(
                                byteBufferParams.v1(),
                                byteBufferParams.v2(),
                                (highestSimilarity, result) -> {
                                    System.out.println("highest_similarity: " + highestSimilarity + "%");
                                    //register success recognition
                                    System.out.println("register success recognition: " + result);
                                }, e -> {
                                    //register error recognition (json null)
                                    System.out.println("register error recognition (json null)");
                                    e.printStackTrace();
                                }
                        );
                    }
                })
        );
    }

    public double getPercentageSimilarityFaces(LoanApplication loanApplication, Tuple2<Integer, Integer> fileTypes) {

        List<LoanApplicationUserFiles> loanApplicationFiles =
                personDAO.getLoanApplicationFiles(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale());

        if (loanApplicationFiles == null)
            return 0;

        List<UserFile> allLoanAppFiles = loanApplicationFiles
                .stream()
                .flatMap(x -> x.getUserFileList().stream())
                .collect(Collectors.toList());

        List<UserFile> allFiles = userService.getUserFileByTypes(allLoanAppFiles, fileTypes.v1(), fileTypes.v2());

        ByteBuffer byteBuffer1 = fileService.userFileToByteBuffer(allFiles.get(0));
        ByteBuffer byteBuffer2 = fileService.userFileToByteBuffer(allFiles.get(1));

        Tuple2<ByteBuffer, ByteBuffer> byteBufferParams = new Tuple2<>(byteBuffer1, byteBuffer2);

        CompareFacesResult result = rekognitionAdapter.compareFaces(byteBufferParams);

        Double highestSimilarity = result.getFaceMatches().stream().mapToDouble(x -> x.getSimilarity()).max().orElse(0);

        return highestSimilarity;
    }

    public List<String> getTextsInImage(LoanApplication loanApplication, Integer fileTypeId) {

        List<LoanApplicationUserFiles> loanApplicationFiles =
                personDAO.getLoanApplicationFiles(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale());

        if (loanApplicationFiles == null)
            return Collections.emptyList();

        List<UserFile> allLoanAppFiles = loanApplicationFiles
                .stream()
                .flatMap(x -> x.getUserFileList().stream())
                .collect(Collectors.toList());

        List<UserFile> allFiles = new ArrayList<>();

        try {
            allFiles = userService.getUserFileByType(allLoanAppFiles, fileTypeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allFiles.isEmpty())
            return  Collections.emptyList();

        ByteBuffer byteBufferImage = fileService.userFileToByteBuffer(allFiles.get(0));

        DetectTextResult result = rekognitionAdapter.detectTexts(byteBufferImage);
        List<TextDetection> textDetections = result.getTextDetections();

        List<String> texts = textDetections
                .stream()
                .filter(e -> TextTypes.LINE.toString().equals(e.getType()))
                .map(TextDetection::getDetectedText)
                .collect(Collectors.toList());

        return texts;
    }

    private Tuple2<ByteBuffer, ByteBuffer> getTestParams() throws Exception {
        URL url11 = new URL("http://i.imgur.com/ouEHKDh.jpg");
        ByteArrayOutputStream baos11 = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(url11), "jpg", baos11);
        baos11.flush();
        byte[] imageInByte11 = baos11.toByteArray();
        baos11.close();

        URL url12 = new URL("http://uscdn02.mundotkm.com/2015/08/cara-31.jpg");
        ByteArrayOutputStream baos12 = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(url12), "jpg", baos12);
        baos12.flush();
        byte[] imageInByte12 = baos12.toByteArray();
        baos12.close();

        BufferedImage joinedImage = Util.joinByteArray(imageInByte11, imageInByte12);

        BufferedImage openJoinedImage = new BufferedImage(joinedImage.getWidth(), joinedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        openJoinedImage.getGraphics().drawImage(joinedImage, 0, 0, null);

        File outputfile = new File("image.jpg");
        ImageIO.write(openJoinedImage, "jpg", outputfile);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(openJoinedImage, "jpg", baos);
        baos.flush();
        byte[] joinedImageBytes = baos.toByteArray();
        baos.close();

        URL url2 = new URL("http://www.thetimes.co.uk/tto/multimedia/archive/00955/TMM15CARADELIVIGNE1_955640b.jpg");
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(url2), "jpg", baos2);
        baos2.flush();
        byte[] imageInByte2 = baos2.toByteArray();
        baos2.close();

        return new Tuple2<>(ByteBuffer.wrap(imageInByte2), ByteBuffer.wrap(joinedImageBytes));
    }

    public static void main(String[] vargs) throws Exception {
        //RekognitionService fc = new RekognitionService();
        //fc.compareOneDniVsSelfieNotYetCompared(0,0, null);
        new RekognitionService();
    }

    public RekognitionProProcess getFacialAnalysis(LoanApplication loanApplication, Integer fileTypeId, Double faceRatio) throws Exception {
        return getFacialAnalysis(loanApplication, fileTypeId, faceRatio, null, null);
    }

    public RekognitionProProcess getFacialAnalysis(LoanApplication loanApplication, Integer fileTypeId, Double faceRatio,  List<LoanApplicationUserFiles> loanApplicationFiles, EntityProductParamUserFileUploadValidationConfig.UploadValidations config) throws Exception {
        if(loanApplicationFiles == null) loanApplicationFiles =
                personDAO.getLoanApplicationFiles(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale());

        if (loanApplicationFiles == null)
            return null;

        List<UserFile> allLoanAppFiles = loanApplicationFiles
                .stream()
                .flatMap(x -> x.getUserFileList().stream())
                .collect(Collectors.toList());

        List<UserFile> allFiles = new ArrayList<>();

        try {
            allFiles = userService.getUserFileByType(allLoanAppFiles, fileTypeId);
        } catch (Exception e) {
            errorService.onError(e);
        }

        if (allFiles.isEmpty()) return null;

        RekognitionProProcess rekognitionProProcess =  securityDAO.getRekognitionProProcessByLoanIdUserFileIdAndTypeAndStatus(loanApplication.getId(), RekognitionProProcess.FACIAL_ANALYSIS_TYPE, allFiles.get(0).getId(), RekognitionProProcess.SUCCESS_STATUS);
        if(rekognitionProProcess != null) return rekognitionProProcess;

        rekognitionProProcess = securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.FACIAL_ANALYSIS_TYPE, fileTypeId,allFiles.get(0).getId(), RekognitionProProcess.RUNNING_STATUS, null);

        try{
            ByteBuffer byteBufferImage = fileService.userFileToByteBuffer(allFiles.get(0));

            String result = rekognitionAdapter.detectFaces(byteBufferImage);

            JSONObject resultJson = new JSONObject(result);

            rekognitionProProcess.setResponse(resultJson);

            JSONArray faceDetails = resultJson.getJSONArray("faceDetails");

            if(faceDetails.length() == 0) throw new Exception(NO_FACE_DETECTED_ERROR);
            if(faceDetails.length() != 1) throw new Exception(MULTIPLE_FACES_DETECTED_ERROR);

            JSONObject faceDetail = faceDetails.getJSONObject(0);

            Double minConfidence = 90.5;
            Double qualityMinPercentage = 50.0;
            if(config != null && config.getConfig() != null){
                if(config.getConfig().getConfidenceMinPercentage() != null) minConfidence = config.getConfig().getConfidenceMinPercentage();
                if(config.getConfig().getQualityMinPercentage() != null) qualityMinPercentage = config.getConfig().getQualityMinPercentage();
            }

            if(faceDetail.getDouble("confidence") < minConfidence) throw new Exception(MINIMUM_CONFIDENCE_VALUE_ERROR);
            if(faceDetail.getJSONObject("quality").getDouble("sharpness") < qualityMinPercentage || faceDetail.getJSONObject("quality").getDouble("brightness") < qualityMinPercentage) throw new Exception(MINIMUM_SHARPNESS_VALUE_ERROR);

            if(faceRatio != null){
                //if() throw new Exception(MINIMUM_FACE_SIZE_VALUE_ERROR);
            }

            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.SUCCESS_STATUS, null);
        }
        catch (Exception e){
            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.FAILED_STATUS, e.getMessage());
            throw e;
        }

        return rekognitionProProcess;

    }

    public RekognitionProProcess getLabelsDetections(LoanApplication loanApplication, Integer fileTypeId) throws Exception {
        return getLabelsDetections(loanApplication, fileTypeId, null, null);
    }

    public RekognitionProProcess getLabelsDetections(LoanApplication loanApplication, Integer fileTypeId, List<LoanApplicationUserFiles> loanApplicationFiles, EntityProductParamUserFileUploadValidationConfig.UploadValidations config) throws Exception {
        if(loanApplicationFiles == null) loanApplicationFiles =
                personDAO.getLoanApplicationFiles(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale());

        if (loanApplicationFiles == null)
            return null;

        List<UserFile> allLoanAppFiles = loanApplicationFiles
                .stream()
                .flatMap(x -> x.getUserFileList().stream())
                .collect(Collectors.toList());

        List<UserFile> allFiles = new ArrayList<>();

        try {
            allFiles = userService.getUserFileByType(allLoanAppFiles, fileTypeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allFiles.isEmpty()) return null;

        RekognitionProProcess rekognitionProProcess =  securityDAO.getRekognitionProProcessByLoanIdUserFileIdAndTypeAndStatus(loanApplication.getId(), RekognitionProProcess.LABEL_DETECTION_TYPE, allFiles.get(0).getId(), RekognitionProProcess.SUCCESS_STATUS);
        if(rekognitionProProcess != null) return rekognitionProProcess;

        rekognitionProProcess = securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.LABEL_DETECTION_TYPE, fileTypeId,allFiles.get(0).getId(), RekognitionProProcess.RUNNING_STATUS, null);

        try{
            ByteBuffer byteBufferImage = fileService.userFileToByteBuffer(allFiles.get(0));

            String result = rekognitionAdapter.detectLabels(byteBufferImage);

            JSONObject resultJson = new JSONObject(result);

            rekognitionProProcess.setResponse(resultJson);

            DetectLabelsResult labelsResult = new Gson().fromJson(result, DetectLabelsResult.class);

            if(labelsResult.getLabels() == null || labelsResult.getLabels().size() == 0) throw new Exception(NO_LABELS_DETECTED_ERROR);

            if(config != null && config.getConfig() != null){
                if(config.getConfig().getIncludeLabels() != null){
                    for (EntityProductParamUserFileUploadValidationConfig.UploadValidationsConfigLabel includeLabel : config.getConfig().getIncludeLabels()) {
                        Label label = labelsResult.getLabels().stream().filter(e -> e.getName().equalsIgnoreCase(includeLabel.getLabel())).findFirst().orElse(null);
                        if(label == null || (includeLabel.getMinPercentage() != null &&  label.getConfidence() < includeLabel.getMinPercentage()) || (includeLabel.getMaxPercentage() != null &&  label.getConfidence() > includeLabel.getMaxPercentage())){
                            throw new Exception(INVALID_LABEL_DETECTED_ERROR);
                        }
                    }
                }
                if(config.getConfig().getExcludeLabels() != null){
                    for (EntityProductParamUserFileUploadValidationConfig.UploadValidationsConfigLabel includeLabel : config.getConfig().getExcludeLabels()) {
                        Label label = labelsResult.getLabels().stream().filter(e -> e.getName().equalsIgnoreCase(includeLabel.getLabel())).findFirst().orElse(null);
                        if(label != null){
                            if(includeLabel.getMaxPercentage() == null || (includeLabel.getMaxPercentage() != null && label.getConfidence() > includeLabel.getMaxPercentage())) throw new Exception(INVALID_LABEL_DETECTED_ERROR);
                        }
                    }
                }
            }

            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.SUCCESS_STATUS, null);
        }
        catch (Exception e){
            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.FAILED_STATUS, e.getMessage());
            throw e;
        }

        return rekognitionProProcess;

    }


    public RekognitionProProcess getTextInImage(LoanApplication loanApplication, Person person, Integer fileTypeId) throws Exception{
        return getTextInImage(loanApplication, person, fileTypeId, null, null);
    }

    public RekognitionProProcess getTextInImage(LoanApplication loanApplication, Person person, Integer fileTypeId,  List<LoanApplicationUserFiles> loanApplicationFiles, EntityProductParamUserFileUploadValidationConfig.UploadValidations config) throws Exception {
        if(loanApplicationFiles == null) loanApplicationFiles =
                personDAO.getLoanApplicationFiles(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale());

        if (loanApplicationFiles == null)
            return null;

        List<UserFile> allLoanAppFiles = loanApplicationFiles
                .stream()
                .flatMap(x -> x.getUserFileList().stream())
                .collect(Collectors.toList());

        List<UserFile> allFiles = new ArrayList<>();

        try {
            allFiles = userService.getUserFileByType(allLoanAppFiles, fileTypeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allFiles.isEmpty()) return null;

        RekognitionProProcess rekognitionProProcess =  securityDAO.getRekognitionProProcessByLoanIdUserFileIdAndTypeAndStatus(loanApplication.getId(), RekognitionProProcess.TEXT_IN_IMAGE_TYPE, allFiles.get(0).getId(), RekognitionProProcess.SUCCESS_STATUS);
        if(rekognitionProProcess != null) return rekognitionProProcess;

        rekognitionProProcess = securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.TEXT_IN_IMAGE_TYPE, fileTypeId,allFiles.get(0).getId(), RekognitionProProcess.RUNNING_STATUS, null);

        try{
            ByteBuffer byteBufferImage = fileService.userFileToByteBuffer(allFiles.get(0));

            DetectTextResult result = rekognitionAdapter.detectTexts(byteBufferImage);

            if(result != null && result.getTextDetections() != null) this.sortTextDetection(result.getTextDetections());

            rekognitionProProcess.setResponse(new JSONObject(new Gson().toJson(result)));

            if(result.getTextDetections() == null || result.getTextDetections().isEmpty()) throw new Exception(NO_TEXT_DETECTED_ERROR);

            List<String> stringToCompare = getValuesToSearchInResult(loanApplication.getCountryId(),person.getDocumentType().getId(), fileTypeId);

            Integer minSuccessValidation = 3;

            if(config != null && config.getConfig() != null && config.getConfig().getMinCoincidences() != null) minSuccessValidation = config.getConfig().getMinCoincidences();

            boolean searchDocumentNumber = config != null && config.getConfig() != null && config.getConfig().getSearchDocumentNumber() != null && config.getConfig().getSearchDocumentNumber();

            if(!stringToCompare.isEmpty()){
                int successValidation = 0;
                for (String s : stringToCompare) {
                    if(result.getTextDetections().stream().anyMatch(e -> e.getDetectedText().toUpperCase().contains(s))) successValidation += 1;
                }
                if(successValidation < minSuccessValidation) throw new Exception(IMAGE_WITH_INVALID_CONTENT_ERROR);
                else if(searchDocumentNumber && fileTypeId == UserFileType.DNI_FRONTAL && !result.getTextDetections().stream().anyMatch(e -> e.getDetectedText().contains(person.getDocumentNumber()))) throw new Exception(IMAGE_WITH_INVALID_CONTENT_ERROR);
                else if(searchDocumentNumber && fileTypeId == UserFileType.DNI_ANVERSO && result.getTextDetections().stream().anyMatch(e -> e.getDetectedText().contains("<")) && !result.getTextDetections().stream().anyMatch(e -> e.getDetectedText().contains(person.getDocumentNumber()))) throw new Exception(IMAGE_WITH_INVALID_CONTENT_ERROR);
            }

            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.SUCCESS_STATUS, null);
        }
        catch (Exception e){
            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.FAILED_STATUS, e.getMessage());
            throw e;
        }

        return rekognitionProProcess;

    }

    private List<String> getValuesToSearchInResult(Integer countryId, Integer documentTypeId, Integer userFileTypeId){
        switch (countryId){
            case CountryParam.COUNTRY_PERU:
                switch (documentTypeId){
                    case IdentityDocumentType.DNI:{
                        switch (userFileTypeId){
                            case UserFileType.DNI_FRONTAL:
                                return Arrays.asList("REGISTRO","NACIONAL","DOCUMENTO","UBIGEO","IDENTI","DNI");
                            case UserFileType.DNI_ANVERSO:
                                return Arrays.asList("DEPARTAMENTO","DEPARTAMENT","PROVINCIA","DISTRITO","PER","UBIGEO","SUFRAGIO","DIRECCI","GRUPO","VOTA","OBSERVA");
                        }
                        break;
                    }
                    case IdentityDocumentType.CE:{
                        switch (userFileTypeId){
                            case UserFileType.DNI_FRONTAL:
                                return Arrays.asList("CARNÉ","EXTRANJERÍA","REPÚBLICA","NACIONAL","MIGRACIONES","APELLIDO","NOMBRE","NACIONALIDAD","SUPERINTENDENCIA");
                            case UserFileType.DNI_ANVERSO:
                                return Arrays.asList("DEPARTAMENTO","PROVINCIA","DISTRITO","PASAPORTE","DIRECCI","CADUCIDAD","RESIDENCIA","EXPEDICI","EMISI","INSCRIPCI","MIGRACIONES","ENTRAJER","PRORROGA","SUPERINTENDENCIA");
                        }
                        break;
                    }
            }
        }
        return new ArrayList<>();
    }

    public void faceComparison(LoanApplication loanApplication, Person person, Locale locale ) throws Exception {
        Configuration.rekognitionExecutor.execute(
                Unchecked.runnable(() -> {
                    Tuple2<ByteBuffer, ByteBuffer> byteBufferParams;
                    boolean debug = false;

                    if (!debug) {
                        List<UserFile> dnisUserFilesToProcess = new ArrayList<>();
                        List<UserFile> selfiesToProcess = new ArrayList<>();
                        Integer mergedUserFileId = null;
                        try {

                            // get all loan files
                            List<LoanApplicationUserFiles> loanApplicationFiles = personDAO.getLoanApplicationFiles(loanApplication.getId(), person.getId(), locale);
                            if (loanApplicationFiles == null) return;
                            List<UserFile> allLoanAppFiles = loanApplicationFiles
                                    .stream()
                                    .flatMap(x -> x.getUserFileList().stream())
                                    .collect(Collectors.toList());

                            //rekognition results to know what userfiles have been processed
                            List<RecognitionResultsPainter> rekognitionPreviousResults = loanApplicationDAO.getRecognitionResults(person.getId(), locale);
                            if (rekognitionPreviousResults == null)
                                rekognitionPreviousResults = new ArrayList<>();

                            //mix two dni fotos for param2
                            List<UserFile> allDnis = userService.getUserFileByType(allLoanAppFiles, UserFileType.DNI_FRONTAL, UserFileType.DNI_ANVERSO, UserFileType.CEDULA_CIUDADANIA_FRONTAL, UserFileType.CEDULA_CIUDADANIA_ANVERSO);
                            List<Integer> allDniIdsWithResults = rekognitionPreviousResults
                                    .stream()
                                    .filter(x -> x.getLoanApplicationId() == loanApplication.getId())
                                    .flatMap(x -> x.getResults().stream())
                                    .flatMap(x -> Stream.of(x.getUserFilesIdDniA(), x.getUserFilesIdDniB(), x.getUserFilesIdDniMerged()))
                                    .collect(Collectors.toList());

                            dnisUserFilesToProcess = allDnis.stream()//all dnis
                                    .filter(s -> !allDniIdsWithResults.contains(s.getId()))// that are not dnis with results
                                    .collect(Collectors.toList());

                            if(dnisUserFilesToProcess.size() > 0){
                                ByteBuffer bbDni1 = fileService.userFileToByteBuffer(dnisUserFilesToProcess.get(0));
                                byte[] b0 = new byte[bbDni1.remaining()];
                                bbDni1.get(b0);
                                BufferedImage joinedDni = Util.toBufferedImage(b0);

                                if (dnisUserFilesToProcess.size() > 1) {
                                    ByteBuffer bbDni2 = fileService.userFileToByteBuffer(dnisUserFilesToProcess.get(1));
                                    byte[] b1 = new byte[bbDni2.remaining()];
                                    bbDni2.get(b1);
                                    joinedDni = Util.joinByteArray(b0, b1);
                                }

                                BufferedImage openJoinedImage = new BufferedImage(joinedDni.getWidth(), joinedDni.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                                openJoinedImage.getGraphics().drawImage(joinedDni, 0, 0, null);

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ImageIO.write(openJoinedImage, "jpg", baos);
                                baos.flush();
                                byte[] joinedImageBytes = baos.toByteArray();
                                baos.close();

                                ByteBuffer bytes2 = ByteBuffer.wrap(joinedImageBytes);

                                // Save the file in s3 and register ir
                                String fileName = fileService.writeUserFile(joinedImageBytes, loanApplication.getUserId(), "merged_dni.jpg", false);
                                mergedUserFileId = userDAO.registerUserFile(loanApplication.getUserId(), loanApplication.getId(), UserFileType.DNI_MERGED, fileName);


                                //selfies of this loan app
                                List<UserFile> allSelfies = userService.getUserFileByType(allLoanAppFiles, UserFileType.SELFIE);
                                if (allSelfies == null) return;
                                //with results
                                List<Integer> allSelfiesIdsWithResults = rekognitionPreviousResults
                                        .stream()
                                        .filter(x -> x.getLoanApplicationId() == loanApplication.getId())
                                        .flatMap(x -> x.getResults().stream())
                                        .map(x -> x.getSelfieUserFileId())
                                        .collect(Collectors.toList());
                                //without results
                                selfiesToProcess = allSelfies.stream()//all selfies
                                        .filter(s -> !allSelfiesIdsWithResults.contains(s.getId()))// that are not selfies with results
                                        .collect(Collectors.toList());

                                System.out.println("size to process: " + selfiesToProcess.size());

                                if (selfiesToProcess.size() <= 0) {
                                    return;
                                }
                                //selfie to process
                                ByteBuffer selfieToProcess = fileService.userFileToByteBuffer(selfiesToProcess.get(0));

                                //take selfie foto for param1
                                ByteBuffer bytes1 = selfieToProcess;


                                // Run rekognition
                                byteBufferParams = new Tuple2<>(bytes1, bytes2);

                                final List<UserFile> dnisUserFilesToProcessFinal = dnisUserFilesToProcess;
                                final Integer mergedUserFileIduserFinal = mergedUserFileId;
                                final List<UserFile> selfiesToProcessFinal = selfiesToProcess;

                                final int id2;
                                if (dnisUserFilesToProcessFinal.size() > 1) {
                                    id2 = 1;
                                } else {
                                    id2 = 0;
                                }

                                System.out.println("init compare ");
                                RekognitionProProcess rekognitionProProcess = securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.FACE_COMPARISON_TYPE, UserFileType.DNI_MERGED,mergedUserFileId, RekognitionProProcess.RUNNING_STATUS, null);
                                rekognitionAdapter.compareFaces(
                                        byteBufferParams.v1(),
                                        byteBufferParams.v2(),
                                        (highestSimilarity, result) -> {
                                            try{
                                                System.out.println("highest_similarity: " + highestSimilarity + "%");
                                                //register success recognition
                                                System.out.println("register success recognition: " + result);
                                                rekognitionProProcess.setResponse(result);
                                                securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.SUCCESS_STATUS,null);
                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                                throw ex;
                                            }
                                        }, e -> {
                                            try{
                                                //register error recognition (json null)
                                                System.out.println("register error recognition (json null)");
                                                securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.FAILED_STATUS, e.getMessage());
                                                e.printStackTrace();
                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                            }

                                        }
                                );

                                loanApplicationDAO.registerAmazonRekognitionFacesLabels(loanApplication.getId(), rekognitionAdapter.detectFaces(bytes1), rekognitionAdapter.detectLabels(bytes1));
                            }

                        } catch (Exception e) {
                            //register error recognition (json null)
                            System.out.println("register error recognition (json null)");
                            final int id2;
                            if (dnisUserFilesToProcess.size() > 1) {
                                id2 = 1;
                            } else {
                                id2 = 0;
                            }
                            loanApplicationDAO.registerAmazonRekognition(
                                    loanApplication.getId(),
                                    0.0,
                                    "{}",
                                    dnisUserFilesToProcess.size() > 0 ? Optional.ofNullable(dnisUserFilesToProcess.get(0).getId()).orElse(null) : null,
                                    dnisUserFilesToProcess.size() > 0 ? Optional.ofNullable(dnisUserFilesToProcess.get(id2).getId()).orElse(null) : null,
                                    mergedUserFileId,
                                    Optional.ofNullable(selfiesToProcess.get(0).getId()).orElse(null));

                            loanApplicationDAO.registerAmazonRekognitionFacesLabels(loanApplication.getId(), "{}", "{}");

                            e.printStackTrace();
                        }
                    } else {
                        byteBufferParams = getTestParams();
                        System.out.println("init compare ");
                        rekognitionAdapter.compareFaces(
                                byteBufferParams.v1(),
                                byteBufferParams.v2(),
                                (highestSimilarity, result) -> {
                                    System.out.println("highest_similarity: " + highestSimilarity + "%");
                                    //register success recognition
                                    System.out.println("register success recognition: " + result);
                                }, e -> {
                                    //register error recognition (json null)
                                    System.out.println("register error recognition (json null)");
                                    e.printStackTrace();
                                }
                        );
                    }

                    generateRekognitionProResult(loanApplication.getId(), true);
                })
        );
    }

    public void generateRekognitionProResult(int loanApplicationId, boolean executeApprovalValidations) throws Exception {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId,Configuration.getDefaultLocale());
        Person person = personDAO.getPerson(loanApplication.getPersonId(),false,Configuration.getDefaultLocale());
        RekognitionProResult result = new RekognitionProResult();
        RekognitionProData data = new RekognitionProData();
        List<UserFile> userFiles = loanApplicationDAO.getLoanApplicationUserFiles(loanApplication.getId());
        try{
            RekognitionProProcess selfieFacialAnalysis = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.FACIAL_ANALYSIS_TYPE, UserFileType.SELFIE);
            RekognitionProProcess dniFrontalTextImages = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.TEXT_IN_IMAGE_TYPE, UserFileType.DNI_FRONTAL);
            RekognitionProProcess dniBackTextImages = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.TEXT_IN_IMAGE_TYPE, UserFileType.DNI_ANVERSO);
            RekognitionProProcess similarity = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.FACE_COMPARISON_TYPE, UserFileType.DNI_MERGED);
            data.setSelfieDocumentId(selfieFacialAnalysis.getUserFileId());
            data.setBackDocumentId(dniBackTextImages.getUserFileId());
            data.setFrontDocumentId(dniFrontalTextImages.getUserFileId());
            data.setUserFilesIdDniMerged(userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.DNI_MERGED).findFirst().map(u -> u.getId()).orElse(null));
            if(Arrays.asList(RekognitionProProcess.FAILED_STATUS,RekognitionProProcess.RUNNING_STATUS).contains(selfieFacialAnalysis.getStatus()) ||
                Arrays.asList(RekognitionProProcess.FAILED_STATUS,RekognitionProProcess.RUNNING_STATUS).contains(dniFrontalTextImages.getStatus()) ||
                Arrays.asList(RekognitionProProcess.FAILED_STATUS,RekognitionProProcess.RUNNING_STATUS).contains(dniBackTextImages.getStatus())
            ){
                data.setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
            }
            //OBTAIN SIMILARITY DATA
            if(similarity != null && similarity.getResponse() != null){
                CompareFacesResult compareFacesResult = new Gson().fromJson(similarity.getResponse().toString(), CompareFacesResult.class);
                Double highestSimilarity = compareFacesResult.getFaceMatches().stream().mapToDouble(x -> x.getSimilarity()).max().orElseGet(() -> 0);
                data.setFaceMatching(highestSimilarity);
            }
            if(person.getDocumentType().getId() == IdentityDocumentType.DNI){
                DetectTextResult detectTextResultDniFrontal = null;
                if(dniFrontalTextImages != null && Arrays.asList(RekognitionProProcess.SUCCESS_STATUS).contains(dniFrontalTextImages.getStatus())){
                    detectTextResultDniFrontal = new Gson().fromJson(dniFrontalTextImages.getResponse().toString(), DetectTextResult.class);
                }
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT, getMatchingValueFromResult(detectTextResultDniFrontal,person.getDocumentNumber()) != null ? person.getDocumentNumber() : null , "Número de documento no encontrado");
                String firstSurname = getMatchingValueFromResult(detectTextResultDniFrontal,person.getFirstSurname());
                if(firstSurname != null && firstSurname.contains(" ")){
                    firstSurname = firstSurname.split(" ")[0];
                }
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME, firstSurname, "Apellido paterno no encontrado");
                String lastSurname = getMatchingValueFromResult(detectTextResultDniFrontal,person.getLastSurname());
                if(lastSurname != null && lastSurname.contains(" ")){
                    lastSurname = lastSurname.substring(lastSurname.indexOf(" "));
                }
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME, lastSurname, "Apellido materno no encontrado");
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME, getMatchingValueFromResult(detectTextResultDniFrontal,person.getFirstName()), "Nombre no encontrado");
                String expirationDocumentDate = getFechaCaducidad(detectTextResultDniFrontal,  getClosestIndexFromWord(detectTextResultDniFrontal, "Fecha Caducidad","Caducidad"), 3," ", "^([0-2][0-9]|(3)[0-1])(\\ )(((0)[0-9])|((1)[0-2]))(\\ )\\d{4}$");
                if(expirationDocumentDate != null){
                    expirationDocumentDate = expirationDocumentDate.substring(0, expirationDocumentDate.length() > 12 ? 12 : expirationDocumentDate.length()).trim();
                }
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_EXPIRATION_DOCUMENT_DATE, expirationDocumentDate , "Fecha de expiración no encontrada");
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, getMatchingValue(detectTextResultDniFrontal,  getClosestIndexFromWord(detectTextResultDniFrontal, "Nacimiento: Fecha","Nacimiento"), 3," ", "^([0-2][0-9]|(3)[0-1])(\\ )([0-9]{2})(\\ )(\\d{4})$"), "Fecha de nacimiento no encontrada");
                DetectTextResult detectTextResultDniBack = null;
                String location = null;
                if(dniBackTextImages != null && Arrays.asList(RekognitionProProcess.SUCCESS_STATUS).contains(dniBackTextImages.getStatus())){
                    detectTextResultDniBack = new Gson().fromJson(dniBackTextImages.getResponse().toString(), DetectTextResult.class);
                    addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK, getMatchingValueFromResult(detectTextResultDniBack,person.getDocumentNumber()) != null ? person.getDocumentNumber() : null, "Número de documento no coincide");
                    location = getMatchingValue(detectTextResultDniBack,  getClosestIndexFromWord(detectTextResultDniBack, "Distr","vincia"), 3,"/", "^([a-zA-Z\\\\u00C0-\\\\u00FF-_.'\\/ -]*)$");
                    if(location == null){
                        Integer locationIndex =  getClosestIndexFromWord(detectTextResultDniBack, "Distr","vincia");
                        if(locationIndex != null){
                            locationIndex = getNextIndexThatNotIncludes(locationIndex, detectTextResultDniBack, "Distr","vincia");
                            location = getStringMatchingMultipleRegex(detectTextResultDniBack,locationIndex,6, "^([a-zA-Z]{2,})", "^([a-zA-Z]{2,})","^([a-zA-Z]{2,})");
                        }
                    }
                }
                //addDataLabel(data,"personLocation", location, "Localidad no encontrada");
            }
            else if(person.getDocumentType().getId() == IdentityDocumentType.CE){
                DetectTextResult detectTextResultDniFrontal = null;
                if(dniFrontalTextImages != null && Arrays.asList(RekognitionProProcess.SUCCESS_STATUS).contains(dniFrontalTextImages.getStatus())){
                    detectTextResultDniFrontal = new Gson().fromJson(dniFrontalTextImages.getResponse().toString(), DetectTextResult.class);
                }
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT, getMatchingValueFromResult(detectTextResultDniFrontal,person.getDocumentNumber()) != null ? person.getDocumentNumber() : null, "Número de documento no encontrado");
                String firstSurname = getMatchingValueFromResult(detectTextResultDniFrontal,person.getFirstSurname());
                if(firstSurname != null && firstSurname.contains(" ")){
                    firstSurname = firstSurname.split(" ")[0];
                }
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME, firstSurname, "Apellido paterno no encontrado");
                String lastSurname = getMatchingValueFromResult(detectTextResultDniFrontal,person.getLastSurname());
                if(lastSurname != null && lastSurname.contains(" ")){
                    lastSurname = lastSurname.substring(lastSurname.indexOf(" "));
                }
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME, lastSurname, "Apellido materno no encontrado");
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME, getMatchingValueFromResult(detectTextResultDniFrontal,person.getFirstName()), "Nombre no encontrado");
                addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, getMatchingValue(detectTextResultDniFrontal,  getClosestIndexFromWord(detectTextResultDniFrontal, "Nacimiento","Nacim"), 3," ", "^([0-2][0-9]|(3)[0-1])(\\ )([a-zA-Z]{3})(\\ )(\\d{4})$"), "Fecha de nacimiento no encontrada");
                DetectTextResult detectTextResultDniBack = null;
                String location = null;
                if(dniBackTextImages != null && Arrays.asList(RekognitionProProcess.SUCCESS_STATUS).contains(dniBackTextImages.getStatus())){
                    detectTextResultDniBack = new Gson().fromJson(dniBackTextImages.getResponse().toString(), DetectTextResult.class);
                    addDataLabel(data, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK, getMatchingValueFromResult(detectTextResultDniBack,this.getDocumentNumberToSearchWithPrefix(person)) != null ? person.getDocumentNumber() : null, "Número de documento no coincide");
                    String expirationDocumentDate = getMatchingValue(detectTextResultDniBack,  getClosestIndexFromWord(detectTextResultDniBack, "Caducidad","DUCIDAD"), 2," ", "^([0-2][0-9]|(3)[0-1])(\\ )([a-zA-Z]{3})(\\ )(\\d{4})$");
                    if(expirationDocumentDate == null){
                        Integer expirationDocumentIndex = getClosestIndexFromWord(detectTextResultDniBack, "Caducidad","DUCIDAD");
                        if(expirationDocumentIndex != null){
                            expirationDocumentDate = String.format("%s %s %s", detectTextResultDniBack.getTextDetections().get(expirationDocumentIndex+1).getDetectedText(),detectTextResultDniBack.getTextDetections().get(expirationDocumentIndex+2).getDetectedText(),detectTextResultDniBack.getTextDetections().get(expirationDocumentIndex+3).getDetectedText());
                            if(!isValidText(Arrays.asList("C.E","Lug.", "Expedici", "Lug" , "MI"), expirationDocumentDate)) expirationDocumentDate = getStringMatchingMultipleRegex(detectTextResultDniBack, expirationDocumentIndex + 1, 5,"^([0-2][0-9]|(3)[0-1])$","^([a-zA-Z]{3})$","^(\\d{4})$");
                        }
                    }
                    if(expirationDocumentDate != null){
                        expirationDocumentDate = expirationDocumentDate.substring(0, expirationDocumentDate.length() > 12 ? 12 : expirationDocumentDate.length()).trim();
                    }
                    addDataLabel(data,"expirationDocumentDate", expirationDocumentDate, "Fecha de expiración no encontrada");

                    String generatedDocumentDate = getMatchingValue(detectTextResultDniBack,  getClosestIndexFromWord(detectTextResultDniBack, "Emisi","Emisión"), 2," ", "^([0-2][0-9]|(3)[0-1])(\\ )([a-zA-Z]{3})(\\ )(\\d{4})$");
                    if(generatedDocumentDate == null){
                        Integer generatedDocumentIndex = getClosestIndexFromWord(detectTextResultDniBack, "Emisi","Emisión");
                        if(generatedDocumentIndex != null){
                            generatedDocumentDate = String.format("%s %s %s", detectTextResultDniBack.getTextDetections().get(generatedDocumentIndex+1).getDetectedText(),detectTextResultDniBack.getTextDetections().get(generatedDocumentIndex+2).getDetectedText(),detectTextResultDniBack.getTextDetections().get(generatedDocumentIndex+3).getDetectedText());
                            if(!isValidText(Arrays.asList("GRACI","CADUC", "CAD", "MIG"), generatedDocumentDate)) generatedDocumentDate = getStringMatchingMultipleRegex(detectTextResultDniBack, generatedDocumentIndex-1, 4,"^([0-2][0-9]|(3)[0-1])$","^([a-zA-Z]{3})$","^(\\d{4})$");
                        }
                    }
                    if(generatedDocumentDate != null){
                        generatedDocumentDate = generatedDocumentDate.substring(0, generatedDocumentDate.length() > 12 ? 12 : generatedDocumentDate.length()).trim();
                    }
                    addDataLabel(data,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_GENERATED_DOCUMENT_DATA, generatedDocumentDate, "Fecha de emisión no encontrada");
                }
                //addDataLabel(data,"personLocation", location, "Localidad no encontrada");
            }
            result.setResponse(data);
            setRekognitionProResultStatus(result, loanApplication, person);
                securityDAO.saveRekognitionProResult(loanApplicationId, RekognitionProResult.SUCCESS_STATUS, result.getResponse());
        }
        catch (Exception e){
            errorService.onError(e);
            result.setResponse(data);
            result = securityDAO.saveRekognitionProResult(loanApplicationId, RekognitionProResult.FAILED_STATUS, result.getResponse());
            errorService.sendErrorCriticSlack(String.format("ERROR REKOGNITION PRO: \n Loan: %s\n ProcessId: %s \n Exception: %s", loanApplication.getId(), result != null ? result.getId() : "-", e.getMessage()));
        }

        if(executeApprovalValidations) loanApplicationApprovalValidationService.validateAndUpdate(loanApplicationId, ApprovalValidation.IDENTIDAD);
    }

    public String getMatchingValue(DetectTextResult detectTextResult,Integer fromIndex, Integer maxIndexFromIndex, String containString, String matchingRegex){
        return getMatchingValue(detectTextResult,fromIndex, maxIndexFromIndex, containString, matchingRegex, false);
    }

    public String getMatchingValue(DetectTextResult detectTextResult,Integer fromIndex, Integer maxIndexFromIndex, String containString, String matchingRegex, boolean returnLastMatchingResult){
        if(fromIndex == null) fromIndex = 0;
        else fromIndex = fromIndex + 1;
        if(maxIndexFromIndex == null) maxIndexFromIndex = 5;
        Integer maxIndex = fromIndex + maxIndexFromIndex;
        String lastResult = null;
        for (int i = 0; i < detectTextResult.getTextDetections().size(); i++) {
            TextDetection textDetection = detectTextResult.getTextDetections().get(i);
            if(i >= fromIndex && i <= maxIndex){
                String compareText = textDetection.getDetectedText().replaceAll("\\s+", " ").trim();
                if(containString != null && !compareText.contains(containString)) continue;
                if(textDetection.getDetectedText().matches(matchingRegex)){
                    if(!returnLastMatchingResult) return textDetection.getDetectedText();
                    else lastResult = textDetection.getDetectedText();
                }
            }
        }
        return lastResult;
    }

    public String getFechaCaducidad(DetectTextResult detectTextResult,Integer fromIndex, Integer maxIndexFromIndex, String containString, String matchingRegex) {
        if(fromIndex == null) fromIndex = 0;
        else fromIndex = fromIndex + 1;
        if(maxIndexFromIndex == null) maxIndexFromIndex = 5;
        Integer maxIndex = fromIndex + maxIndexFromIndex;
        List<String> results = new ArrayList<>();
        for (int i = 0; i < detectTextResult.getTextDetections().size(); i++) {
            TextDetection textDetection = detectTextResult.getTextDetections().get(i);
            if(i >= fromIndex && i <= maxIndex){
                String compareText = textDetection.getDetectedText().replaceAll("\\s+", " ").trim();
                if(containString != null && !compareText.contains(containString)) continue;
                if(textDetection.getDetectedText().matches(matchingRegex) || textDetection.getDetectedText().equalsIgnoreCase(DNI_FEC_CADUCIDAD_NO_CADUCA)){
                    results.add(textDetection.getDetectedText());
                }
            }
        }
        if(!results.isEmpty()){
            if(results.stream().anyMatch(s -> s.equalsIgnoreCase(DNI_FEC_CADUCIDAD_NO_CADUCA)))
                return results.stream().filter(s -> s.equalsIgnoreCase(DNI_FEC_CADUCIDAD_NO_CADUCA)).findFirst().orElse(null);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy");
            Date maxDate = null;
            for(String s : results){
                try{
                    Date d = sdf.parse(s);
                    if(maxDate == null) maxDate = d;
                    else if (maxDate.before(d)) maxDate = d;
                }catch(Exception ex){
                    logger.error(ex);
                }
            }
            return sdf.format(maxDate);
        }
        return null;
    }


    public Integer getClosestIndexFromWord(DetectTextResult detectTextResult, String word){
        return getClosestIndexFromWord(detectTextResult,word,null);
    }

    public Integer getClosestIndexFromWord(DetectTextResult detectTextResult, String word, String word2){
        for (int i = 0; i < detectTextResult.getTextDetections().size(); i++) {
            TextDetection textDetection = detectTextResult.getTextDetections().get(i);
            if(textDetection.getDetectedText().toUpperCase().contains(word.toUpperCase())) return i;
            if(word2 != null && textDetection.getDetectedText().toUpperCase().contains(word2.toUpperCase())) return i;
        }
        return null;
    }

    public Integer getNextIndexThatNotIncludes(Integer index, DetectTextResult detectTextResult, String word, String word2){
        for (int i = index; i < detectTextResult.getTextDetections().size(); i++) {
            TextDetection textDetection = detectTextResult.getTextDetections().get(i);
            if(word != null && word2 != null){
                if(!textDetection.getDetectedText().toUpperCase().contains(word.toUpperCase()) && !textDetection.getDetectedText().toUpperCase().contains(word2.toUpperCase())) return i;
            }
            else if(word != null && !textDetection.getDetectedText().toUpperCase().contains(word.toUpperCase())) return i;
        }
        return null;
    }

    private void addDataLabel(RekognitionProData data, String type, String value, String reason){
        DataLabels dataLabel = new DataLabels(type, value, value != null ? null : reason);
        if(data.getData() == null) data.setData(new ArrayList<>());
        data.getData().add(dataLabel);
    }

    private void addDataLabel(RekognitionReniecData data, Character processType, String type, String value, String reason){
        addDataLabel(data,processType,type,value,reason, false);
    }

    private void addDataLabel(RekognitionReniecData data, Character processType, String type, String value, String reason, Boolean notFound){
        addDataLabel(data,processType,type,value,reason, false, null);
    }
    private void addDataLabel(RekognitionReniecData data, Character processType, String type, String value, String reason, Boolean notFound, String additionalValue){
        DataLabels dataLabel = new DataLabels(type, value, value != null ? null : reason, notFound);
        if(additionalValue != null && value == null){
            dataLabel.setData(new DataLabelsData(additionalValue, null));
        }
        switch (processType){
            case RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE:{
                if(data.getData() == null) data.setData(new ArrayList<>());
                data.getData().add(dataLabel);
                break;
            }
            case RekognitionReniecData.SELFIE_WITH_RENIEC_PROCESS_TYPE:{
                if(data.getSelfieReniecData() == null) data.setSelfieReniecData(new ArrayList<>());
                data.getSelfieReniecData().add(dataLabel);
                break;
            }
            case RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE:{
                if(data.getDocReniecData() == null) data.setDocReniecData(new ArrayList<>());
                data.getDocReniecData().add(dataLabel);
                break;
            }
        }
    }

    private String getDataLabel(RekognitionReniecData data, Character processType, String type){
        String valueFromData = null;
        DataLabels dataLabels = null;
        switch (processType){
            case RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE:{
                if(data.getData() != null) dataLabels = data.getData().stream().filter(e -> e.getType().equalsIgnoreCase(type)).findFirst().orElse(null);
                break;
            }
            case RekognitionReniecData.SELFIE_WITH_RENIEC_PROCESS_TYPE:{
                if(data.getSelfieReniecData() != null) dataLabels = data.getSelfieReniecData().stream().filter(e -> e.getType().equalsIgnoreCase(type)).findFirst().orElse(null);
                break;
            }
            case RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE:{
                if(data.getDocReniecData() != null) dataLabels = data.getDocReniecData().stream().filter(e -> e.getType().equalsIgnoreCase(type)).findFirst().orElse(null);
                break;
            }
        }
        if(dataLabels != null && dataLabels.getData() != null) valueFromData = dataLabels.getData().getValue();
        return valueFromData;
    }

    private String getMatchingValueFromResult(DetectTextResult detectTextResult, String word){
        return getMatchingValueFromResult(detectTextResult, word, null, null);
    }

    private String getMatchingValueFromResult(DetectTextResult detectTextResult, String word, String replaceBase, String replacement){
        if(detectTextResult == null) return null;
        for (int i = 0; i < detectTextResult.getTextDetections().size(); i++) {
            TextDetection textDetection = detectTextResult.getTextDetections().get(i);
            if(textDetection != null && textDetection.getDetectedText() != null && word != null){
                String textBase = Normalizer.normalize(textDetection.getDetectedText(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "").trim();
                String textToValidate= Normalizer.normalize(word, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "").trim();
                if(textBase.toUpperCase().contains(textToValidate.toUpperCase()) && textDetection.getConfidence() > MINIMUM_CONFIDENCE_FOR_TEXT) return textDetection.getDetectedText();
            }
        }
        return null;
    }

    private String getStringMatchingMultipleRegex(DetectTextResult detectTextResult, int startFromIndex, int lengthSearch, String regex1, String regex2, String regex3){
        return getStringMatchingMultipleRegex(detectTextResult, startFromIndex, lengthSearch, regex1, regex2, regex3, null);
    }
    private String getStringMatchingMultipleRegex(DetectTextResult detectTextResult, int startFromIndex, int lengthSearch, String regex1, String regex2, String regex3, String separator){
        List<String> textStrings = new ArrayList<>();
        if(regex1 == null || regex2 == null) return null;
        int finalIndexToCompare = (startFromIndex + lengthSearch) > detectTextResult.getTextDetections().size() ? detectTextResult.getTextDetections().size() :(startFromIndex + lengthSearch);
        for (int i = startFromIndex; i < finalIndexToCompare; i++) {
            if(detectTextResult.getTextDetections().get(i) != null && detectTextResult.getTextDetections().get(i).getConfidence() >= MINIMUM_CONFIDENCE_FOR_TEXT && detectTextResult.getTextDetections().get(i).getDetectedText() != null) textStrings.addAll(Stream.of(detectTextResult.getTextDetections().get(i).getDetectedText().split(" "))
                    .map(String::trim)
                    .collect(Collectors.toList()));
        }
        if(separator == null) separator = " ";
        for (int i = 0; i < textStrings.size(); i++) {
            if(textStrings.get(i).matches(regex1)){
                if((i+1) <= (textStrings.size() - 1)){
                    if(textStrings.get(i+1).matches(regex2)){
                        if(regex3 == null) return String.format("%s"+separator+"%s", textStrings.get(i), textStrings.get(i+1));
                        else if((i+2) <= (textStrings.size() - 1)){
                            if(textStrings.get(i+2).matches(regex3)) return String.format("%s"+separator+"%s"+separator+"%s", textStrings.get(i), textStrings.get(i+1), textStrings.get(i+2));
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isValidText(List<String> words, String text){
        if(text == null) return false;
        boolean isValid = true;
        for (String excludeWord : words) {
            if(text.toLowerCase().contains(excludeWord.toLowerCase())){
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    public void setRekognitionProResultStatus(RekognitionProResult result, LoanApplication loanApplication, Person person) throws Exception {
        if(result.getResponse() == null || result.getResponse().getData() == null || result.getResponse().getData().isEmpty()){
            result.setStatus(RekognitionProResult.FAILED_STATUS);
            if(result.getResponse() == null) result.setResponse(new RekognitionProData());
            result.getResponse().setStatus(RekognitionProData.REJECTED_STATUS);
            result.getResponse().getAdditionalErrors().add("Comparación de rostros inválida");
            return;
        }
        SimpleDateFormat simpleDateFormatDNI = new SimpleDateFormat("dd MM yyyy",  new Locale ( "es" , "ES" ));
        SimpleDateFormat simpleDateFormatCE = new SimpleDateFormat("dd MMM yyyy",  new Locale ( "es" , "ES" ));
        result.getResponse().setProcessDate(new Date());
        if(
                result.getResponse().getFaceMatching() == null ||
                (result.getResponse().getFaceMatching() != null && result.getResponse().getFaceMatching() >= 100.0) ||
                (result.getResponse().getFaceMatching() != null && result.getResponse().getFaceMatching() < RekognitionProData.MINIMUM_FACE_MATCHING_VALUE)
        ){
            result.getResponse().getAdditionalErrors().add("Comparación de rostros inválida");
            if(result.getResponse().getFaceMatching() == null) result.getResponse().setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
            else if(result.getResponse().getFaceMatching() != null && result.getResponse().getFaceMatching() >= 100.0) result.getResponse().setStatus(RekognitionProData.REJECTED_STATUS);
        }

        for (DataLabels datum : result.getResponse().getData()) {
            switch (datum.getType()){
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT:
                    if(datum.getError() != null) result.getResponse().setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME:
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME:
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME:
                    switch (person.getDocumentType().getId()){
                        case IdentityDocumentType.DNI:
                            if(datum.getError() != null) result.getResponse().setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
                            break;
                    }
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_EXPIRATION_DOCUMENT_DATE:
                    Date expirationDate = null;
                    if(datum.getData() != null && datum.getData().getValue() != null) {
                        switch (person.getDocumentType().getId()){
                            case IdentityDocumentType.DNI:
                                try{
                                    if(datum.getData().getValue().equalsIgnoreCase(DNI_FEC_CADUCIDAD_NO_CADUCA)){
                                        Calendar c = Calendar.getInstance();
                                        c.add(Calendar.DATE, 1);
                                        expirationDate = c.getTime();
                                    } else
                                        expirationDate = simpleDateFormatDNI.parse(datum.getData().getValue());
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case IdentityDocumentType.CE:
                                try{
                                    expirationDate = simpleDateFormatCE.parse(datum.getData().getValue());
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                    if(expirationDate == null || expirationDate.before(new Date()) || datum.getError() != null) result.getResponse().setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
                    if(expirationDate != null && expirationDate.before(new Date())){
                        datum.setError(new DataLabelsData(datum.getData().getValue(), "Documento se encuentra expirado"));
                    }
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_GENERATED_DOCUMENT_DATA:
                    Date generatedDocumentDate = null;
                    boolean isInvalid = false;
                    switch (person.getDocumentType().getId()){
                        case IdentityDocumentType.CE:
                            if(datum.getData() != null && datum.getData().getValue() != null) {
                                try{
                                    generatedDocumentDate = simpleDateFormatCE.parse(datum.getData().getValue());
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            if(generatedDocumentDate != null){
                                LocalDate start = generatedDocumentDate.toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                LocalDate end = new Date().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                start = start.withDayOfMonth(1);
                                end = end.withDayOfMonth(1);
                                Period period = Period.between(start, end);
                                if(period.getMonths() < 6) isInvalid = true;
                            }
                            if(isInvalid || datum.getError() != null) result.getResponse().setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);

                            break;
                    }
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH:
                    Date birthdate = null;
                    boolean isInvalidBirthdate = false;
                    switch (person.getDocumentType().getId()){
                        case IdentityDocumentType.CE:
                            if(datum.getData() != null && datum.getData().getValue() != null) {
                                try{
                                    birthdate = simpleDateFormatCE.parse(datum.getData().getValue());
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                    isInvalidBirthdate = true;
                                }
                            }
                            if(birthdate != null && person.getBirthday() != null){
                                if(!simpleDateFormatCE.format(person.getBirthday()).equalsIgnoreCase(datum.getData().getValue())) isInvalid = true;
                            }
                            if(isInvalidBirthdate ||  datum.getError() != null) result.getResponse().setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
                            break;
                    }
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK:
                    switch (person.getDocumentType().getId()){
                        case IdentityDocumentType.DNI:
                        case IdentityDocumentType.CE:
                            if(datum.getError() != null) result.getResponse().setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
                            break;
                    }
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION:
/*                    switch (person.getDocumentType().getId()){
                        case IdentityDocumentType.DNI:
                            if(datum.getError() != null) result.getResponse().setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
                            else{
                                Direccion disagregatedAddress = personDAO.getDisggregatedAddress(loanApplication.getPersonId(), "H");
                                if (disagregatedAddress != null) {
                                    Ubigeo ubigeo = catalogService.getUbigeo(disagregatedAddress.getUbigeo());
                                    if(ubigeo != null){
                                        String locationToCompare = Normalizer.normalize(String.format("%s/%s/%s", ubigeo.getDepartment().getName(), ubigeo.getProvince().getName(), ubigeo.getDistrict().getName()), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "").trim();
                                        String locationFromDocument = Normalizer.normalize(datum.getData().getValue(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "").trim();
                                        if(!locationToCompare.contains(locationFromDocument)) {
                                            datum.setError(new RekognitionProData.DataLabelsData(null, "Localidad no coincide con la declarada"));
                                            result.getResponse().setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
                                        }
                                    }
                                }
                            }
                            break;
                    }*/
                    break;
            }
        }
        if(result.getResponse().getStatus() == null) result.getResponse().setStatus(RekognitionProData.APPROVED_STATUS);
    }

    public RekognitionValidationError  executeUploadValidationFileProProcess(LoanApplication loanApplication, Person person, List<UserFile> userFiles, EntityProductParams entityProductParam, Locale locale){
        List<RekognitionValidationError.RekognitionValidationErrorData> userFileErrors = new ArrayList<>();
        List<LoanApplicationUserFiles> loanApplicationFiles = personDAO.getLoanApplicationFiles(loanApplication.getId(), loanApplication.getPersonId(), locale);

        Integer selfieUserFileId = userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.SELFIE).findFirst().map(u -> u.getId()).orElse(null);
        Integer dniFrontUserFileId = userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.DNI_FRONTAL).findFirst().map(u -> u.getId()).orElse(null);
        Integer dniBackUserFileId = userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.DNI_ANVERSO).findFirst().map(u -> u.getId()).orElse(null);

        boolean runFacialAnalysis = true;
        boolean runLabelDetections = false;

        EntityProductParamUserFileUploadValidationConfig.UploadValidations facialAnalysisApprovalValidations = null;
        EntityProductParamUserFileUploadValidationConfig.UploadValidations labelDetectionsApprovalValidations = null;
        EntityProductParamUserFileUploadValidationConfig.UploadValidations ocrDetectionApprovalValidations = null;

        if(entityProductParam.getEntityProductParamIdentityValidationConfig().getRunRekognitionReniec() != null && entityProductParam.getEntityProductParamIdentityValidationConfig().getRunRekognitionReniec()){
            if(entityProductParam.getEntityProductParamIdentityValidationConfig().getUserFileUploadValidation() != null && entityProductParam.getEntityProductParamIdentityValidationConfig().getUserFileUploadValidation().getUploadValidations() != null && !entityProductParam.getEntityProductParamIdentityValidationConfig().getUserFileUploadValidation().getUploadValidations().isEmpty()){
                facialAnalysisApprovalValidations = entityProductParam.getEntityProductParamIdentityValidationConfig().getUserFileUploadValidation().getUploadValidations().stream().filter(e -> e.getType().equalsIgnoreCase(EntityProductParamUserFileUploadValidationConfig.UPLOAD_VALIDATION_SELFIE_FACIAL_ANALYSIS_TYPE)).findFirst().orElse(null);
                runFacialAnalysis = facialAnalysisApprovalValidations != null;

                labelDetectionsApprovalValidations = entityProductParam.getEntityProductParamIdentityValidationConfig().getUserFileUploadValidation().getUploadValidations().stream().filter(e -> e.getType().equalsIgnoreCase(EntityProductParamUserFileUploadValidationConfig.UPLOAD_VALIDATION_SELFIE_LABEL_DETECTION_TYPE)).findFirst().orElse(null);
                runLabelDetections = labelDetectionsApprovalValidations != null;

                ocrDetectionApprovalValidations = entityProductParam.getEntityProductParamIdentityValidationConfig().getUserFileUploadValidation().getUploadValidations().stream().filter(e -> e.getType().equalsIgnoreCase(EntityProductParamUserFileUploadValidationConfig.UPLOAD_VALIDATION_DOCUMENT_OCR_TYPE)).findFirst().orElse(null);

            }
        }

        try{
            if(runFacialAnalysis) getFacialAnalysis(loanApplication, UserFileType.SELFIE, null, loanApplicationFiles, facialAnalysisApprovalValidations);
        }
        catch (Exception e){
            userFileErrors.add(new RekognitionValidationError.RekognitionValidationErrorData(selfieUserFileId, e.getMessage()));
        }

        try{
            if(runLabelDetections) getLabelsDetections(loanApplication, UserFileType.SELFIE, loanApplicationFiles, labelDetectionsApprovalValidations);
        }
        catch (Exception e){
            userFileErrors.add(new RekognitionValidationError.RekognitionValidationErrorData(selfieUserFileId, e.getMessage()));
        }

        try{
            getEquipmentDetection(loanApplication, UserFileType.SELFIE, loanApplicationFiles, labelDetectionsApprovalValidations);
        }
        catch (Exception e){
            userFileErrors.add(new RekognitionValidationError.RekognitionValidationErrorData(selfieUserFileId, e.getMessage()));
        }

        RekognitionProProcess dniFrontal = null;
        RekognitionProProcess dniDorso = null;

        try{
          dniFrontal = getTextInImage(loanApplication, person, UserFileType.DNI_FRONTAL, loanApplicationFiles, ocrDetectionApprovalValidations);
        }
        catch (Exception e){
            userFileErrors.add(new RekognitionValidationError.RekognitionValidationErrorData(dniFrontUserFileId, e.getMessage()));
        }

        try{
            dniDorso =  getTextInImage(loanApplication, person, UserFileType.DNI_ANVERSO, loanApplicationFiles, ocrDetectionApprovalValidations);
        }
        catch (Exception e){
            userFileErrors.add(new RekognitionValidationError.RekognitionValidationErrorData(dniBackUserFileId, e.getMessage()));
        }

        if(entityProductParam.getEntityProductParamIdentityValidationConfig().getRunISOValidation() != null && entityProductParam.getEntityProductParamIdentityValidationConfig().getRunISOValidation()){
            boolean isValid = false;
            boolean dniFrontalContainISO = false;
            boolean dniBackContainISO = false;
            ISO7501ValidationData validationData = null;
            Integer fileTypeId = null;
            Integer userFileId = null;
            if(dniFrontal != null && dniFrontal.getResponse() != null){
                DetectTextResult detectTextResultDniFront = new Gson().fromJson(dniFrontal.getResponse().toString(), DetectTextResult.class);
                dniFrontalContainISO = this.containData(detectTextResultDniFront.getTextDetections(), '<', 3);
                if(dniFrontalContainISO){
                    fileTypeId = dniFrontal.getUserFileTypeId();
                    userFileId = dniFrontal.getUserFileId();
                    String isoCode = this.getIsoCodeFromTextDetection(person.getCountry().getId(), person.getDocumentType().getId(), detectTextResultDniFront.getTextDetections());
                    validationData = this.isValidIsoCode(isoCode, person);
                    isValid = validationData.getValid();
                    if(!validationData.getValid()){
                        userFileErrors.add(new RekognitionValidationError.RekognitionValidationErrorData(dniFrontUserFileId, "INVALID_ISO"));
                    }
                }
            }
            if(dniDorso != null && dniDorso.getResponse() != null){
                DetectTextResult detectTextResultDniBack = new Gson().fromJson(dniDorso.getResponse().toString(), DetectTextResult.class);
                dniBackContainISO = this.containData(detectTextResultDniBack.getTextDetections(), '<', 3);
                if(dniBackContainISO){
                    fileTypeId = dniDorso.getUserFileTypeId();
                    userFileId = dniDorso.getUserFileId();
                    String isoCode = this.getIsoCodeFromTextDetection(person.getCountry().getId(), person.getDocumentType().getId(), detectTextResultDniBack.getTextDetections());
                    validationData = this.isValidIsoCode(isoCode, person);
                    isValid = validationData.getValid();
                    if(!validationData.getValid()){
                        userFileErrors.add(new RekognitionValidationError.RekognitionValidationErrorData(dniBackUserFileId, "INVALID_ISO"));
                    }
                }
            }
            if(!isValid && !dniFrontalContainISO && !dniBackContainISO){
                userFileErrors.add(new RekognitionValidationError.RekognitionValidationErrorData(dniFrontUserFileId, "INVALID_ISO"));
                userFileErrors.add(new RekognitionValidationError.RekognitionValidationErrorData(dniBackUserFileId, "INVALID_ISO"));
            }
            if(isValid){
                RekognitionProProcess rekognitionProProcessISO7501 = securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.ISO_CODE_7501_VALIDATION, fileTypeId, userFileId, RekognitionProProcess.SUCCESS_STATUS, new JSONObject(new Gson().toJson(validationData)));
            }
            else{
                securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.ISO_CODE_7501_VALIDATION, fileTypeId != null ? fileTypeId : (dniFrontal != null ? dniFrontal.getUserFileTypeId() : null), userFileId != null ? userFileId : (dniFrontal != null ? dniFrontal.getUserFileId() : null) , RekognitionProProcess.FAILED_STATUS, validationData != null ? new JSONObject(new Gson().toJson(validationData)) : null);
            }
        }

        return new RekognitionValidationError(userFileErrors);
    }

    public void executeRekognitionReniecProcess(LoanApplication loanApplication, Person person, Locale locale ) throws Exception {
        Configuration.rekognitionExecutor.execute(
                Unchecked.runnable(() -> {
                    Tuple2<ByteBuffer, ByteBuffer> byteBufferParams;
                    Tuple2<ByteBuffer, ByteBuffer> byteBufferParamsSelfieReniec;
                    Tuple2<ByteBuffer, ByteBuffer> byteBufferParamsDocumentReniec;
                    List<UserFile> dnisUserFilesToProcess = new ArrayList<>();
                    List<UserFile> selfiesToProcess = new ArrayList<>();
                    Integer mergedUserFileId = null;
                    try {
                        ReniecDataResponse reniecDataResponse = null;

                        if(person.getDocumentType().getId().intValue() == IdentityDocumentType.DNI){
                            reniecDataResponse = bancoAztecaReniecServiceCall.getPersonData(loanApplication, person, null);
                        }
                        // get all loan files
                        List<LoanApplicationUserFiles> loanApplicationFiles = personDAO.getLoanApplicationFiles(loanApplication.getId(), person.getId(), locale);
                        if (loanApplicationFiles == null) return;
                        List<UserFile> allLoanAppFiles = loanApplicationFiles
                                .stream()
                                .flatMap(x -> x.getUserFileList().stream())
                                .collect(Collectors.toList());

                        List<UserFile> allDnis = userService.getUserFileByType(allLoanAppFiles, UserFileType.DNI_FRONTAL, UserFileType.CEDULA_CIUDADANIA_FRONTAL);
                        List<UserFile> allDnisBack = userService.getUserFileByType(allLoanAppFiles, UserFileType.DNI_ANVERSO, UserFileType.CEDULA_CIUDADANIA_ANVERSO);
                        List<UserFile> selfieUserFile = userService.getUserFileByType(allLoanAppFiles, UserFileType.SELFIE);

                        if(allDnis.size() > 0 && selfieUserFile.size() > 0){
                            ByteBuffer dniToProcess = fileService.userFileToByteBuffer(allDnis.get(0));
                            ByteBuffer selfieToProcess = fileService.userFileToByteBuffer(selfieUserFile.get(0));

                            byteBufferParams = new Tuple2<>(selfieToProcess, dniToProcess);
                            byteBufferParamsDocumentReniec = new Tuple2<>(dniToProcess, dniToProcess);

                            //SELFIE VS DNI
                            RekognitionProProcess rekognitionProProcess = securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.FACE_COMPARISON_TYPE, UserFileType.SELFIE,selfieUserFile.get(0).getId(), RekognitionProProcess.RUNNING_STATUS, null);
                            rekognitionAdapter.compareFaces(
                                    byteBufferParams.v1(),
                                    byteBufferParams.v2(),
                                    (highestSimilarity, result) -> {
                                        try{
                                            System.out.println("highest_similarity: " + highestSimilarity + "%");
                                            System.out.println("register success recognition: " + result);
                                            rekognitionProProcess.setResponse(result);
                                            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.SUCCESS_STATUS, null);
                                        }catch(Exception ex){
                                            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.FAILED_STATUS, ex.getMessage());
                                            ex.printStackTrace();
                                            throw ex;
                                        }
                                    }, e -> {
                                        try{
                                            //register error recognition (json null)
                                            System.out.println("register error recognition (json null)");
                                            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.FAILED_STATUS, e.getMessage());
                                            e.printStackTrace();
                                        }catch(Exception ex){
                                            ex.printStackTrace();
                                        }

                                    }
                            );

                            if(person.getDocumentType().getId().intValue() == IdentityDocumentType.DNI){
                                ByteBuffer reniecPhotoToProcess = fileService.webServiceFileToByteBuffer(loanApplication.getId(), reniecDataResponse.getData().getFoto());
                                byteBufferParamsSelfieReniec = new Tuple2<>(selfieToProcess, reniecPhotoToProcess);

                                //SELFIE VS RENIEC
                                RekognitionProProcess rekognitionProProcessSelfieReniec = securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.FACE_COMPARISON_RENIEC_TYPE, UserFileType.SELFIE, selfieUserFile.get(0).getId(), RekognitionProProcess.RUNNING_STATUS, null);
                                rekognitionAdapter.compareFaces(
                                        byteBufferParamsSelfieReniec.v1(),
                                        byteBufferParamsSelfieReniec.v2(),
                                        (highestSimilarity, result) -> {
                                            try{
                                                System.out.println("highest_similarity: " + highestSimilarity + "%");
                                                System.out.println("register success recognition: " + result);
                                                rekognitionProProcessSelfieReniec.setResponse(result);
                                                securityDAO.updateRekognitionProcess(rekognitionProProcessSelfieReniec.getId(), rekognitionProProcessSelfieReniec.getResponse(), RekognitionProProcess.SUCCESS_STATUS, null);
                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                                securityDAO.updateRekognitionProcess(rekognitionProProcessSelfieReniec.getId(), rekognitionProProcessSelfieReniec.getResponse(), RekognitionProProcess.SUCCESS_STATUS, ex.getMessage());
                                                throw ex;
                                            }
                                        }, e -> {
                                            try{
                                                //register error recognition (json null)
                                                System.out.println("register error recognition (json null)");
                                                securityDAO.updateRekognitionProcess(rekognitionProProcessSelfieReniec.getId(), rekognitionProProcessSelfieReniec.getResponse(), RekognitionProProcess.FAILED_STATUS, e.getMessage());
                                                e.printStackTrace();
                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                            }

                                        }
                                );

                                //RENIEC VS DNI
                                RekognitionProProcess rekognitionProProcessSelfieDocument = securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.FACE_COMPARISON_RENIEC_WITH_DOCUMENT_TYPE, UserFileType.SELFIE,selfieUserFile.get(0).getId(), RekognitionProProcess.RUNNING_STATUS, null);
                                rekognitionAdapter.compareFaces(
                                        byteBufferParamsDocumentReniec.v1(),
                                        byteBufferParamsDocumentReniec.v2(),
                                        (highestSimilarity, result) -> {
                                            try{
                                                System.out.println("highest_similarity: " + highestSimilarity + "%");
                                                System.out.println("register success recognition: " + result);
                                                rekognitionProProcessSelfieDocument.setResponse(result);
                                                securityDAO.updateRekognitionProcess(rekognitionProProcessSelfieDocument.getId(), rekognitionProProcessSelfieDocument.getResponse(), RekognitionProProcess.SUCCESS_STATUS, null);
                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                                securityDAO.updateRekognitionProcess(rekognitionProProcessSelfieDocument.getId(), rekognitionProProcessSelfieDocument.getResponse(), RekognitionProProcess.FAILED_STATUS, ex.getMessage());
                                                throw ex;
                                            }
                                        }, e -> {
                                            try{
                                                //register error recognition (json null)
                                                System.out.println("register error recognition (json null)");
                                                securityDAO.updateRekognitionProcess(rekognitionProProcessSelfieDocument.getId(), rekognitionProProcessSelfieDocument.getResponse(), RekognitionProProcess.FAILED_STATUS, e.getMessage());
                                                e.printStackTrace();
                                            }catch(Exception ex){
                                                ex.printStackTrace();
                                            }

                                        }
                                );
                            }
                        }


                    } catch (Exception e) {
                        errorService.onError(e);
                    }

                    generateRekognitionReniecResult(loanApplication.getId(), true);
                    if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getSelectedEntityProductParameterId() != null && Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(loanApplication.getSelectedEntityProductParameterId())){
                        LoanApplication loanApplicationReload = loanApplicationDAO.getLoanApplication(loanApplication.getId(),Configuration.getDefaultLocale());
                        EntityProductParams entityProductParam = this.loanApplicationService.getEntityProductParamFromLoanApplication(loanApplicationReload);
                        if(loanApplicationApprovalValidationService.loanHasAllValidations(loanApplicationReload,entityProductParam)) this.loanApplicationService.sendLoanApplicationApprovalMail(loanApplication.getId(), loanApplication.getPersonId(), locale);
                    }
                })
        );
    }

    public void generateRekognitionReniecResult(int loanApplicationId, boolean executeApprovalValidations) throws Exception {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId,Configuration.getDefaultLocale());
        Person person = personDAO.getPerson(loanApplication.getPersonId(),false,Configuration.getDefaultLocale());
        RekognitionReniecResult result = new RekognitionReniecResult();
        RekognitionReniecData data = new RekognitionReniecData();
        List<UserFile> userFiles = loanApplicationDAO.getLoanApplicationUserFiles(loanApplication.getId());
        try{
            RekognitionProProcess selfieFacialAnalysis = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.FACIAL_ANALYSIS_TYPE, UserFileType.SELFIE);
            RekognitionProProcess dniFrontalTextImages = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.TEXT_IN_IMAGE_TYPE, UserFileType.DNI_FRONTAL);
            RekognitionProProcess dniBackTextImages = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.TEXT_IN_IMAGE_TYPE, UserFileType.DNI_ANVERSO);
            RekognitionProProcess similarity = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.FACE_COMPARISON_TYPE, UserFileType.SELFIE);
            RekognitionProProcess faceComparissonSelfieVsReniec = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.FACE_COMPARISON_RENIEC_TYPE, UserFileType.SELFIE);
            RekognitionProProcess faceComparissonDocumentVsReniec = securityDAO.getRekognitionProProcess(loanApplicationId,RekognitionProProcess.FACE_COMPARISON_RENIEC_WITH_DOCUMENT_TYPE, UserFileType.SELFIE);
            RekognitionProProcess isoCodeValidationRekognitionProcess = securityDAO.getRekognitionProProcess(loanApplicationId, RekognitionProProcess.ISO_CODE_7501_VALIDATION, null);
            ISO7501ValidationData iso7501ValidationData = null;
            if(isoCodeValidationRekognitionProcess != null && isoCodeValidationRekognitionProcess.getResponse() != null){
                iso7501ValidationData = new Gson().fromJson(isoCodeValidationRekognitionProcess.getResponse().toString(), ISO7501ValidationData.class);
            }
            data.setSelfieDocumentId(selfieFacialAnalysis.getUserFileId());
            data.setBackDocumentId(dniBackTextImages.getUserFileId());
            data.setFrontDocumentId(dniFrontalTextImages.getUserFileId());
            data.setUserFilesIdDniMerged(userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.DNI_MERGED).findFirst().map(u -> u.getId()).orElse(null));
            if(
                    Arrays.asList(RekognitionProProcess.FAILED_STATUS,RekognitionProProcess.RUNNING_STATUS).contains(selfieFacialAnalysis.getStatus()) ||
                    Arrays.asList(RekognitionProProcess.FAILED_STATUS,RekognitionProProcess.RUNNING_STATUS).contains(dniFrontalTextImages.getStatus()) ||
                    Arrays.asList(RekognitionProProcess.FAILED_STATUS,RekognitionProProcess.RUNNING_STATUS).contains(dniBackTextImages.getStatus()) ||
                    (isoCodeValidationRekognitionProcess != null && Arrays.asList(RekognitionProProcess.FAILED_STATUS,RekognitionProProcess.RUNNING_STATUS).contains(isoCodeValidationRekognitionProcess.getStatus()))
            ){
                data.setStatus(RekognitionReniecData.REVIEW_NEEDED_STATUS);
            }

            if(person.getDocumentType().getId().intValue() == IdentityDocumentType.DNI){
                if(Arrays.asList(RekognitionProProcess.FAILED_STATUS,RekognitionProProcess.RUNNING_STATUS).contains(faceComparissonSelfieVsReniec.getStatus()) ||
                        Arrays.asList(RekognitionProProcess.FAILED_STATUS,RekognitionProProcess.RUNNING_STATUS).contains(faceComparissonDocumentVsReniec.getStatus())) data.setStatus(RekognitionProData.REVIEW_NEEDED_STATUS);
            }
            //OBTAIN SIMILARITY DATA
            if(similarity != null && similarity.getResponse() != null){
                CompareFacesResult compareFacesResult = new Gson().fromJson(similarity.getResponse().toString(), CompareFacesResult.class);
                Double highestSimilarity = compareFacesResult.getFaceMatches().stream().mapToDouble(x -> x.getSimilarity()).max().orElseGet(() -> 0);
                data.setFaceMatching(highestSimilarity);
            }
            if(faceComparissonSelfieVsReniec != null && faceComparissonSelfieVsReniec.getResponse() != null){
                CompareFacesResult compareFacesResult = new Gson().fromJson(faceComparissonSelfieVsReniec.getResponse().toString(), CompareFacesResult.class);
                Double highestSimilarity = compareFacesResult.getFaceMatches().stream().mapToDouble(x -> x.getSimilarity()).max().orElseGet(() -> 0);
                data.setSelfieReniecFaceMatching(highestSimilarity);
            }
            if(faceComparissonDocumentVsReniec != null && faceComparissonDocumentVsReniec.getResponse() != null){
                CompareFacesResult compareFacesResult = new Gson().fromJson(faceComparissonDocumentVsReniec.getResponse().toString(), CompareFacesResult.class);
                Double highestSimilarity = compareFacesResult.getFaceMatches().stream().mapToDouble(x -> x.getSimilarity()).max().orElseGet(() -> 0);
                data.setDocReniecFaceMatching(highestSimilarity);
            }
            if(person.getDocumentType().getId() == IdentityDocumentType.DNI){

                EntityWsResult entityWsResultDetalle = securityDAO.getEntityResultWS(loanApplication.getId(), EntityWebService.BANCO_AZTECA_RENIEC_SERVICE_OBTENER_DATA);
                ReniecDataResponse reniecDataResponse = null;
                if (entityWsResultDetalle != null) {
                    reniecDataResponse = new Gson().fromJson(entityWsResultDetalle.getResult().toString(), ReniecDataResponse.class);
                }
                SimpleDateFormat sdfReniec = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat sdfTextImages = new SimpleDateFormat("dd MM yyyy");
                //VALIDATIONS SELFIE - DOC
                DetectTextResult detectTextResultDniFrontal = null;
                if(dniFrontalTextImages != null && Arrays.asList(RekognitionProProcess.SUCCESS_STATUS).contains(dniFrontalTextImages.getStatus())){
                    detectTextResultDniFrontal = new Gson().fromJson(dniFrontalTextImages.getResponse().toString(), DetectTextResult.class);
                }
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT, iso7501ValidationData != null && iso7501ValidationData.getDocumentNumber() != null ? iso7501ValidationData.getDocumentNumber() : (getMatchingValueFromResult(detectTextResultDniFrontal,person.getDocumentNumber()) != null ? person.getDocumentNumber() : null), "Número de documento no encontrado", true);
                String firstSurname = getMatchingValueFromResult(detectTextResultDniFrontal,person.getFirstSurname());
                if(firstSurname != null && firstSurname.contains(" ")){
                    firstSurname = firstSurname.split(" ")[0];
                }
                if(iso7501ValidationData != null && iso7501ValidationData.getLastName() != null) firstSurname = iso7501ValidationData.getLastName();
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME, firstSurname, "Apellido paterno no encontrado", true);
                String lastSurname = getMatchingValueFromResult(detectTextResultDniFrontal,person.getLastSurname());
                if(lastSurname != null && lastSurname.contains(" ")){
                    lastSurname = lastSurname.substring(lastSurname.indexOf(" "));
                }
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME, lastSurname, "Apellido materno no encontrado", true);
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME, getMatchingValueFromResult(detectTextResultDniFrontal,person.getFirstName()), "Nombre no encontrado", true);
                String expirationDocumentDate = getFechaCaducidad(detectTextResultDniFrontal,  getClosestIndexFromWord(detectTextResultDniFrontal, "Fecha Caducidad","Caducidad"), 3," ", "^([0-2][0-9]|(3)[0-1])(\\ )(((0)[0-9])|((1)[0-2]))(\\ )\\d{4}$");
                if(expirationDocumentDate != null){
                    expirationDocumentDate = expirationDocumentDate.substring(0, expirationDocumentDate.length() > 12 ? 12 : expirationDocumentDate.length()).trim();
                }
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_EXPIRATION_DOCUMENT_DATE, iso7501ValidationData != null && iso7501ValidationData.getExpirationDate() != null ? sdfTextImages.format(iso7501ValidationData.getExpirationDate()) : expirationDocumentDate , "Fecha de expiración no encontrada", true);
                String extractBirthdateFromResult =  getMatchingValue(detectTextResultDniFrontal,getClosestIndexFromWord(detectTextResultDniFrontal, "Nacimiento: Fecha","Nacimiento"), 3," ", "^([0-2][0-9]|(3)[0-1])(\\ )([0-9]{2})(\\ )(\\d{4})$");
                if(iso7501ValidationData != null && iso7501ValidationData.getBirthdate() != null) extractBirthdateFromResult = sdfTextImages.format(iso7501ValidationData.getBirthdate());
                if(extractBirthdateFromResult != null && reniecDataResponse != null && reniecDataResponse.getData().getFechaNacimientoAsDate() != null) {
                    addDataLabel(data, RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, extractBirthdateFromResult.equalsIgnoreCase(sdfTextImages.format(reniecDataResponse.getData().getFechaNacimientoAsDate())) ? extractBirthdateFromResult : null, "Fecha de nacimiento no coincide", false, extractBirthdateFromResult);
                }
                else addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, extractBirthdateFromResult , "Fecha de nacimiento no encontrada", true);
                DetectTextResult detectTextResultDniBack = null;
                String location = null;
                if(dniBackTextImages != null && Arrays.asList(RekognitionProProcess.SUCCESS_STATUS).contains(dniBackTextImages.getStatus())){
                    detectTextResultDniBack = new Gson().fromJson(dniBackTextImages.getResponse().toString(), DetectTextResult.class);
                    if(detectTextResultDniBack.getTextDetections().stream().anyMatch(e -> e.getDetectedText().contains("<"))) addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK, getMatchingValueFromResult(detectTextResultDniBack,this.getDocumentNumberToSearchWithPrefix(person)) != null ? person.getDocumentNumber() : null, "Número de documento no coincide");
                    location = getMatchingValue(detectTextResultDniBack,  getClosestIndexFromWord(detectTextResultDniBack, "Distr","vincia"), 3,"/", "^([a-zA-Z\\\\u00C0-\\\\u00FF-_.'\\/ -]*)$");
                    if(location == null){
                        Integer locationIndex = getClosestIndexFromWord(detectTextResultDniBack, "Distr","vincia");
                        if(locationIndex != null){
                            locationIndex = getNextIndexThatNotIncludes(locationIndex, detectTextResultDniBack, "distr","prov");
                            location = getStringMatchingMultipleRegex(detectTextResultDniBack,locationIndex,6, "^([a-zA-Z]{2,})", "^([a-zA-Z]{2,})","^([a-zA-Z]{2,})", "/");
                        }
                    }
                }
                if(location != null && reniecDataResponse != null && !reniecDataResponse.getData().getLocalidad().contains(location.toUpperCase())) addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION, null, "Localidad no coincide", false, location);
                else addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION, location, "Localidad no encontrada", true);
                //VALIDATE RENIEC DOC
                if (reniecDataResponse != null) {
                    DataLabels dateOfBirthDL = data.getData().stream().filter(e-> e.getType().equalsIgnoreCase(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH)).findFirst().orElse(null);

                    Date birthdate = reniecDataResponse.getData().getFechaNacimientoAsDate();
                    if(birthdate == null) addDataLabel(data, RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, null, "Fecha de nacimiento no encontrada", true);
                    else if(dateOfBirthDL == null || dateOfBirthDL.getData() == null || dateOfBirthDL.getData().getValue() == null || !dateOfBirthDL.getData().getValue().equalsIgnoreCase(sdfTextImages.format(birthdate))) addDataLabel(data, RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, null, "Fecha de nacimiento no coincide", false);
                    else addDataLabel(data, RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, sdfTextImages.format(birthdate), null);

                    //PERSON_DATA
                    //NAMES
                    ReniecDataResponse finalReniecDataResponse = reniecDataResponse;
                    String documentNumberFrontDNI = getDataLabel(data, RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT);
                    String firstSurnameDNI = getDataLabel(data, RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME);
                    String otherSurnamesDNI = getDataLabel(data, RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME);
                    String personNameDNI = getDataLabel(data, RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME);

                    addDataLabel(data,RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT, detectTextResultDniFrontal.getTextDetections().stream().anyMatch(e -> e.getDetectedText().toUpperCase().contains(person.getDocumentNumber())) ? person.getDocumentNumber() : null , "Número de documento no encontrado", true);
                    addDataLabel(data,RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME, Arrays.asList(firstSurname).stream().anyMatch(e -> e != null && e.toUpperCase().contains(finalReniecDataResponse.getData().getApellidoPaterno().toUpperCase())) ? finalReniecDataResponse.getData().getApellidoPaterno() : null, "Apellido paterno no encontrado", true);
                    addDataLabel(data,RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME, detectTextResultDniFrontal.getTextDetections().stream().anyMatch(e -> e.getDetectedText().toUpperCase().contains(finalReniecDataResponse.getData().getApellidoMaterno().toUpperCase())) ? finalReniecDataResponse.getData().getApellidoMaterno() : null, "Apellido materno no encontrado", true);
                    String documentNumberBackDNI = getDataLabel(data, RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK);
                    if(documentNumberBackDNI != null) addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK, documentNumberBackDNI != null && documentNumberBackDNI.equalsIgnoreCase(person.getDocumentNumber()) ? person.getDocumentNumber() : null, "Número de documento no coincide", false);
                    String personName = reniecDataResponse.getData().getNombres().split(" ")[0];
                    addDataLabel(data,RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME, personNameDNI != null && personName.toUpperCase().contains(personNameDNI.split(" ")[0]) ? personName : null, "Nombre no encontrado",true);
                    //MARITAL STATUS
                    addDataLabel(data,RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS, reniecDataResponse.getData().getEstadoCivil(), null);
                    //LOCATION
                    String locationDNI = getDataLabel(data, RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION);
                    if(locationDNI == null) addDataLabel(data,RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION, locationDNI != null && reniecDataResponse.getData().getLocalidad().contains(locationDNI) ? reniecDataResponse.getData().getLocalidad() : null, "Localidad no encontrada", true);
                    else addDataLabel(data,RekognitionReniecData.DOCUMENT_WITH_RENIEC_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION, reniecDataResponse.getData().getLocalidad().contains(locationDNI) ? reniecDataResponse.getData().getLocalidad() : null, "Localidad no coincide", false, reniecDataResponse.getData().getLocalidad());
                }
                else data.setDocReniecStatus(RekognitionReniecData.REVIEW_NEEDED_STATUS);
            }
            else if(person.getDocumentType().getId() == IdentityDocumentType.CE){
                DetectTextResult detectTextResultDniFrontal = null;
                if(dniFrontalTextImages != null && Arrays.asList(RekognitionProProcess.SUCCESS_STATUS).contains(dniFrontalTextImages.getStatus())){
                    detectTextResultDniFrontal = new Gson().fromJson(dniFrontalTextImages.getResponse().toString(), DetectTextResult.class);
                }
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT, getMatchingValueFromResult(detectTextResultDniFrontal,person.getDocumentNumber()) != null ? person.getDocumentNumber() : null, "Número de documento no encontrado");
                String firstSurname = getMatchingValueFromResult(detectTextResultDniFrontal,person.getFirstSurname());
                if(firstSurname != null && firstSurname.contains(" ")){
                    firstSurname = firstSurname.split(" ")[0];
                }
                addDataLabel(data, RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME, firstSurname, "Apellido paterno no encontrado", true);
                String lastSurname = getMatchingValueFromResult(detectTextResultDniFrontal,person.getLastSurname());
                if(lastSurname != null && lastSurname.contains(" ")){
                    lastSurname = lastSurname.substring(lastSurname.indexOf(" "));
                }
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME, lastSurname, "Apellido materno no encontrado", true);
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME, getMatchingValueFromResult(detectTextResultDniFrontal,person.getFirstName()), "Nombre no encontrado");
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, getMatchingValue(detectTextResultDniFrontal,  getClosestIndexFromWord(detectTextResultDniFrontal, "Nacimiento","Nacim"), 3," ", "^([0-2][0-9]|(3)[0-1])(\\ )([a-zA-Z]{3})(\\ )(\\d{4})$"), "Fecha de nacimiento no encontrada");
                DetectTextResult detectTextResultDniBack = null;
                String location = null;
                if(dniBackTextImages != null && Arrays.asList(RekognitionProProcess.SUCCESS_STATUS).contains(dniBackTextImages.getStatus())){
                    detectTextResultDniBack = new Gson().fromJson(dniBackTextImages.getResponse().toString(), DetectTextResult.class);
                    addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK, getMatchingValueFromResult(detectTextResultDniBack,this.getDocumentNumberToSearchWithPrefix(person)) != null ? person.getDocumentNumber() : null, "Número de documento no coincide");
                    String expirationDocumentDate = getMatchingValue(detectTextResultDniBack,  getClosestIndexFromWord(detectTextResultDniBack, "Caducidad","DUCIDAD"), 2," ", "^([0-2][0-9]|(3)[0-1])(\\ )([a-zA-Z]{3})(\\ )(\\d{4})$");
                    if(expirationDocumentDate == null){
                        Integer expirationDocumentIndex = getClosestIndexFromWord(detectTextResultDniBack, "Caducidad","DUCIDAD");
                        if(expirationDocumentIndex != null){
                            expirationDocumentDate = String.format("%s %s %s", detectTextResultDniBack.getTextDetections().get(expirationDocumentIndex+1).getDetectedText(),detectTextResultDniBack.getTextDetections().get(expirationDocumentIndex+2).getDetectedText(),detectTextResultDniBack.getTextDetections().get(expirationDocumentIndex+3).getDetectedText());
                            if(!isValidText(Arrays.asList("C.E","Lug.", "Expedici", "Lug" , "MI"), expirationDocumentDate)) expirationDocumentDate = getStringMatchingMultipleRegex(detectTextResultDniBack, expirationDocumentIndex + 1, 5,"^([0-2][0-9]|(3)[0-1])$","^([a-zA-Z]{3})$","^(\\d{4})$");
                        }
                    }
                    if(expirationDocumentDate != null){
                        expirationDocumentDate = expirationDocumentDate.substring(0, expirationDocumentDate.length() > 12 ? 12 : expirationDocumentDate.length()).trim();
                    }
                    addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,"expirationDocumentDate", expirationDocumentDate, "Fecha de expiración no encontrada", true);

                    String generatedDocumentDate = getMatchingValue(detectTextResultDniBack,  getClosestIndexFromWord(detectTextResultDniBack, "Emisi","Emisión"), 2," ", "^([0-2][0-9]|(3)[0-1])(\\ )([a-zA-Z]{3})(\\ )(\\d{4})$");
                    if(generatedDocumentDate == null){
                        Integer generatedDocumentIndex = getClosestIndexFromWord(detectTextResultDniBack, "Emisi","Emisión");
                        if(generatedDocumentIndex != null){
                            generatedDocumentDate = String.format("%s %s %s", detectTextResultDniBack.getTextDetections().get(generatedDocumentIndex+1).getDetectedText(),detectTextResultDniBack.getTextDetections().get(generatedDocumentIndex+2).getDetectedText(),detectTextResultDniBack.getTextDetections().get(generatedDocumentIndex+3).getDetectedText());
                            if(!isValidText(Arrays.asList("GRACI","CADUC", "CAD", "MIG"), generatedDocumentDate)) generatedDocumentDate = getStringMatchingMultipleRegex(detectTextResultDniBack, generatedDocumentIndex-1, 4,"^([0-2][0-9]|(3)[0-1])$","^([a-zA-Z]{3})$","^(\\d{4})$");
                        }
                    }
                    if(generatedDocumentDate != null){
                        generatedDocumentDate = generatedDocumentDate.substring(0, generatedDocumentDate.length() > 12 ? 12 : generatedDocumentDate.length()).trim();
                    }
                    addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_GENERATED_DOCUMENT_DATA, generatedDocumentDate, "Fecha de emisión no encontrada", true);
                }
                addDataLabel(data,RekognitionReniecData.SELFIE_WITH_DOCUMENT_PROCESS_TYPE,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION, location, "Localidad no encontrada", true);
            }
            result.setResponse(data);
            setRekognitionReniecResultStatus(result, loanApplication, person);
            result = securityDAO.saveRekognitionReniecResult(loanApplicationId, RekognitionReniecResult.SUCCESS_STATUS, result.getResponse());
            if(executeApprovalValidations) loanApplicationApprovalValidationService.validateAndUpdate(loanApplicationId, ApprovalValidation.IDENTIDAD);
        }
        catch (Exception e){
            errorService.onError(e);
            if(result.getId() == null){
                result.setResponse(data);
                result = securityDAO.saveRekognitionReniecResult(loanApplicationId, RekognitionReniecResult.FAILED_STATUS, result.getResponse());
            }
            errorService.sendErrorCriticSlack(String.format("ERROR REKOGNITION RENIEC: \n Loan: %s\n ProcessId: %s \n Exception: %s", loanApplication.getId(), result != null ? result.getId() : "-", e.getMessage()));
        }
    }

    public void setRekognitionReniecResultStatus(RekognitionReniecResult result, LoanApplication loanApplication, Person person) throws Exception {
        if(result.getResponse() == null || result.getResponse().getData() == null || result.getResponse().getData().isEmpty()){
            result.setStatus(RekognitionProResult.FAILED_STATUS);
            if(result.getResponse() == null) result.setResponse(new RekognitionReniecData());
            result.getResponse().setStatus(RekognitionProData.REJECTED_STATUS);
            result.getResponse().getAdditionalErrors().add("Comparación de rostros inválida");
            return;
        }
        result.getResponse().setProcessDate(new Date());

        //SELFIE VS DNI
        validateDataRekognitionReniec(result, result.getResponse().getData(), loanApplication, person, result.getResponse().getSelfieDocDataValidations(), RekognitionValidationData.REKOGNITION_RENIEC_PROCESS_TYPE, RekognitionValidationData.SELFIE_DOCUMENTATION_TYPE);
        result.getResponse().getSelfieDocDataValidations().add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING, !(result.getResponse().getFaceMatching() == null || result.getResponse().getFaceMatching() < RekognitionReniecData.MINIMUM_FACE_MATCHING_VALUE || result.getResponse().getFaceMatching() >= 100)));

        if(person.getDocumentType().getId() == IdentityDocumentType.DNI){
            //DOC VS RENIEC
            result.getResponse().getDocReniecDataValidations().add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING, !(result.getResponse().getDocReniecFaceMatching() == null || result.getResponse().getDocReniecFaceMatching() < RekognitionReniecData.MINIMUM_FACE_MATCHING_VALUE)));
            validateDataRekognitionReniec(result, result.getResponse().getDocReniecData(), loanApplication, person, result.getResponse().getDocReniecDataValidations(), RekognitionValidationData.REKOGNITION_RENIEC_PROCESS_TYPE, RekognitionValidationData.DOCUMENTATION_RENIEC_TYPE);

            //SELFIE VS RENIEC
            result.getResponse().getSelfieReniecDataValidations().add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING, !(result.getResponse().getDocReniecFaceMatching() == null || result.getResponse().getSelfieReniecFaceMatching() < RekognitionReniecData.MINIMUM_VALUE_FOR_SELFIE_RENIEC || result.getResponse().getSelfieReniecFaceMatching() >= 100.0)));
            validateDataRekognitionReniec(result, result.getResponse().getSelfieReniecData(), loanApplication, person, result.getResponse().getSelfieReniecDataValidations(), RekognitionValidationData.REKOGNITION_RENIEC_PROCESS_TYPE, RekognitionValidationData.SELFIE_RENIEC_TYPE);
        }

        EntityProductParams entityProductParams = loanApplication.getSelectedEntityProductParameterId() != null ? catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId()) : null;
        List<String> requiredValidations = Arrays.asList(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK,EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT);

        result.getResponse().setStatus(null);

        if(
                (result.getResponse().getFaceMatching() != null && result.getResponse().getFaceMatching() <= RekognitionReniecData.LIMIT_FACE_MATCHING_FOR_REJECT_VALUE) ||
                (result.getResponse().getSelfieReniecFaceMatching() != null && (result.getResponse().getSelfieReniecFaceMatching() < RekognitionReniecData.MINIMUM_VALUE_FOR_SELFIE_RENIEC || result.getResponse().getSelfieReniecFaceMatching() >= 100.0)) ||
                (result.getResponse().getDocReniecFaceMatching() != null && result.getResponse().getDocReniecFaceMatching() <= RekognitionReniecData.LIMIT_FACE_MATCHING_FOR_REJECT_VALUE) ||
                hasDisapprovedAnyValidation(Arrays.asList(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS), result.getResponse().getDocReniecDataValidations())
        )
            result.getResponse().setStatus(RekognitionReniecData.REJECTED_STATUS);
        else if(hasDisapprovedAnyValidation(requiredValidations, result.getResponse().getDocReniecDataValidations()) ||
                hasDisapprovedAnyValidation(requiredValidations, result.getResponse().getSelfieReniecDataValidations()) ||
                hasDisapprovedAnyValidation(requiredValidations, result.getResponse().getSelfieDocDataValidations())
        ){
            result.getResponse().setStatus(RekognitionReniecData.REVIEW_NEEDED_STATUS);
        }

        if(result.getResponse().getStatus() == null){
            if(entityProductParams != null && entityProductParams.getEntityProductParamIdentityValidationConfig() != null && entityProductParams.getEntityProductParamIdentityValidationConfig().getValidationParamsConfig() != null && !entityProductParams.getEntityProductParamIdentityValidationConfig().getValidationParamsConfig().isEmpty()){
                List<String> additionalValidationParams = entityProductParams.getEntityProductParamIdentityValidationConfig().getValidationParamsConfig().stream().filter(e -> e.getExclude() == null || !e.getExclude()).map(EntityProductParamIdentityValidationParamsConfig::getType).collect(Collectors.toList());
                if(hasDisapprovedAnyValidation(additionalValidationParams, result.getResponse().getDocReniecDataValidations()) ||
                        hasDisapprovedAnyValidation(additionalValidationParams, result.getResponse().getSelfieReniecDataValidations()) ||
                        hasDisapprovedAnyValidation(additionalValidationParams, result.getResponse().getSelfieDocDataValidations())
                ){
                    result.getResponse().setStatus(RekognitionReniecData.REVIEW_NEEDED_STATUS);
                }
            }
        }

        switch (person.getDocumentType().getId()){
            case IdentityDocumentType.DNI:
                break;
            case IdentityDocumentType.CE:
                if(result.getResponse().getStatus() == null){
                    if(result.getResponse().getSelfieDocDataValidations().stream().anyMatch( e-> !e.getApproved())) result.getResponse().setStatus(RekognitionReniecData.REVIEW_NEEDED_STATUS);
                }
                break;
        }

        if(result.getResponse().getStatus() == null) result.getResponse().setStatus(RekognitionReniecData.APPROVED_STATUS);

        if(result.getStatus() == null) result.setStatus(RekognitionReniecResult.SUCCESS_STATUS);
    }

    private boolean hasRekognitionValidationData(List<RekognitionValidationData> data, String type){
        if(data == null) return false;
        return data.stream().anyMatch(e -> e.getType().equalsIgnoreCase(type));
    }

    private void validateDataRekognitionReniec(RekognitionReniecResult result, List<DataLabels> data, LoanApplication loanApplication, Person person, List<RekognitionValidationData> validations, String processType, String type){
        SimpleDateFormat simpleDateFormatDNI = new SimpleDateFormat("dd MM yyyy",  new Locale ( "es" , "ES" ));
        SimpleDateFormat simpleDateFormatCE = new SimpleDateFormat("dd MMM yyyy",  new Locale ( "es" , "ES" ));
        switch (person.getDocumentType().getId()){
            case IdentityDocumentType.DNI:
                if(data.stream().anyMatch( e -> Arrays.asList(
                        EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME,
                        EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME,
                        EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME
                ).contains(e.getType()))){
                    validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME,
                            !data.stream().filter( e -> Arrays.asList(
                                    EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME,
                                    EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME,
                                    EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME
                            ).contains(e.getType())).collect(Collectors.toList()).stream().anyMatch(e -> e.getError() != null),
                            data.stream().filter( e -> Arrays.asList(
                                    EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FIRST_SURNAME,
                                    EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LAST_SURNAME,
                                    EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_PERSON_NAME
                            ).contains(e.getType())).collect(Collectors.toList()).stream().allMatch(e -> e.getNotFound() != null && e.getNotFound())
                    ));
                }

                break;
        }
        for (DataLabels datum : data) {
            boolean notFound = false;
            switch (datum.getType()){
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT:
                    if(!hasRekognitionValidationData(validations, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT)) validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT, datum.getError() == null, datum.getNotFound(), processType, type, person.getDocumentType().getId()));
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_EXPIRATION_DOCUMENT_DATE:
                    Date expirationDate = null;
                    if(datum.getData() != null && datum.getData().getValue() != null) {
                        switch (person.getDocumentType().getId()){
                            case IdentityDocumentType.DNI:
                                try{
                                    if(datum.getData().getValue().equalsIgnoreCase(DNI_FEC_CADUCIDAD_NO_CADUCA)){
                                        Calendar c = Calendar.getInstance();
                                        c.add(Calendar.DATE, 1);
                                        expirationDate = c.getTime();
                                    } else
                                        expirationDate = simpleDateFormatDNI.parse(datum.getData().getValue());
                                }
                                catch (Exception e){
                                    notFound = true;
                                    e.printStackTrace();
                                }
                                break;
                            case IdentityDocumentType.CE:
                                try{
                                    expirationDate = simpleDateFormatCE.parse(datum.getData().getValue());
                                }
                                catch (Exception e){
                                    notFound = true;
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                    if(expirationDate == null || expirationDate.before(new Date()) || datum.getError() != null) result.getResponse().setStatus(RekognitionReniecData.REVIEW_NEEDED_STATUS);
                    if(expirationDate != null && expirationDate.before(new Date())){
                        datum.setError(new DataLabelsData(datum.getData().getValue(), "Documento se encuentra expirado"));
                    }
                    if(!hasRekognitionValidationData(validations, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_EXPIRATION_DOCUMENT_DATE)) validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_EXPIRATION_DOCUMENT_DATE, datum.getError() == null, notFound, processType, type, person.getDocumentType().getId()));
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_GENERATED_DOCUMENT_DATA:
                    Date generatedDocumentDate = null;
                    boolean isInvalid = false;
                    switch (person.getDocumentType().getId()){
                        case IdentityDocumentType.CE:
                            if(datum.getData() != null && datum.getData().getValue() != null) {
                                try{
                                    generatedDocumentDate = simpleDateFormatCE.parse(datum.getData().getValue());
                                }
                                catch (Exception e){
                                    notFound = true;
                                    e.printStackTrace();
                                }
                            }
                            if(generatedDocumentDate != null){
                                LocalDate start = generatedDocumentDate.toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                LocalDate end = new Date().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                start = start.withDayOfMonth(1);
                                end = end.withDayOfMonth(1);
                                Period period = Period.between(start, end);
                                if(period.getMonths() < 6) isInvalid = true;
                            }
                            if(isInvalid ||  datum.getError() != null) {
                                result.getResponse().setStatus(RekognitionReniecData.REVIEW_NEEDED_STATUS);
                            }
                            if(!hasRekognitionValidationData(validations, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_GENERATED_DOCUMENT_DATA)) validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_GENERATED_DOCUMENT_DATA, isInvalid ||  datum.getError() != null, notFound,processType, type, person.getDocumentType().getId()));
                            break;
                    }
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH:
                    Date birthdate = null;
                    boolean isInvalidBirthdate = false;
                    switch (person.getDocumentType().getId()){
                        case IdentityDocumentType.CE:
                            if(datum.getData() != null && datum.getData().getValue() != null) {
                                try{
                                    birthdate = simpleDateFormatCE.parse(datum.getData().getValue());
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                    isInvalidBirthdate = true;
                                }
                            }
                            if(birthdate != null && person.getBirthday() != null){
                                if(!simpleDateFormatCE.format(person.getBirthday()).equalsIgnoreCase(datum.getData().getValue())) isInvalidBirthdate = true;
                            }
                            if(isInvalidBirthdate ||  datum.getError() != null) result.getResponse().setStatus(RekognitionReniecData.REVIEW_NEEDED_STATUS);
                            if(!hasRekognitionValidationData(validations, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH)) validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, !(isInvalidBirthdate ||  datum.getError() != null), datum.getNotFound() != null && datum.getNotFound(), processType, type, person.getDocumentType().getId()));
                            break;
                        case IdentityDocumentType.DNI:
                            if(!hasRekognitionValidationData(validations, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH)) validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DATE_OF_BIRTH, datum.getError() == null, datum.getNotFound() != null && datum.getNotFound(), processType, type, person.getDocumentType().getId()));
                            break;
                    }
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK:
                    switch (person.getDocumentType().getId()){
                        case IdentityDocumentType.DNI:
                        case IdentityDocumentType.CE:
                            if(!hasRekognitionValidationData(validations, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_FRONT)) validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_DOCUMENT_NUMBER_BACK, datum.getError() == null, datum.getNotFound(), processType, type, person.getDocumentType().getId()));
                            break;
                    }
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION:
                    if(!hasRekognitionValidationData(validations, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION)) validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_LOCATION, datum.getError() == null,datum.getNotFound() != null && datum.getNotFound(),processType, type, person.getDocumentType().getId()));
                    break;
                case EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS:
                    if(person != null && person.getMaritalStatus() != null && datum.getData() != null && datum.getData().getValue() != null){
                        String searchValue = null;
                        if(person.getMaritalStatus() != null){
                            switch (person.getMaritalStatus().getId()){
                                case MaritalStatus.SINGLE:
                                    searchValue = "SOLTER";
                                    break;
                                case MaritalStatus.DIVORCED:
                                    searchValue = "DIVORCIAD";
                                    break;
                                case MaritalStatus.MARRIED:
                                    searchValue = "CASAD";
                                    break;
                                case MaritalStatus.WIDOWED:
                                    searchValue = "VIUD";
                                    break;
                            }
                        }
                        if(searchValue != null) if(!hasRekognitionValidationData(validations, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS)) validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS, datum.getData().getValue().toUpperCase().contains(searchValue), datum.getNotFound(),processType, type, person.getDocumentType().getId()));
                    }
                    else if(!hasRekognitionValidationData(validations, EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS)) validations.add(new RekognitionValidationData(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_MARITAL_STATUS, false, false, processType, type, person.getDocumentType().getId()));

                    break;
            }
        }
    }

    private boolean hasDisapprovedAnyValidation(List<String> validationTypes, List<RekognitionValidationData> validations){
        return validations.stream().anyMatch(e -> validationTypes.contains(e.getType()) && !e.getApproved());
    }

    private String getDocumentNumberToSearchWithPrefix(Person person){
        switch (person.getDocumentType().getId()){
            case IdentityDocumentType.DNI:
                if(person.getCountry().getId() == CountryParam.COUNTRY_PERU) return "PER"+person.getDocumentNumber();
                break;
        }
        return person.getDocumentNumber();
    }

    public RekognitionProProcess getEquipmentDetection(LoanApplication loanApplication, Integer fileTypeId) throws Exception {
        return getEquipmentDetection(loanApplication, fileTypeId, null, null);
    }

    public RekognitionProProcess getEquipmentDetection(LoanApplication loanApplication, Integer fileTypeId,  List<LoanApplicationUserFiles> loanApplicationFiles, EntityProductParamUserFileUploadValidationConfig.UploadValidations config) throws Exception {
        if(loanApplicationFiles == null) loanApplicationFiles =
                personDAO.getLoanApplicationFiles(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale());

        if (loanApplicationFiles == null)
            return null;

        List<UserFile> allLoanAppFiles = loanApplicationFiles
                .stream()
                .flatMap(x -> x.getUserFileList().stream())
                .collect(Collectors.toList());

        List<UserFile> allFiles = new ArrayList<>();

        try {
            allFiles = userService.getUserFileByType(allLoanAppFiles, fileTypeId);
        } catch (Exception e) {
            errorService.onError(e);
        }

        if (allFiles.isEmpty()) return null;

        RekognitionProProcess rekognitionProProcess =  securityDAO.getRekognitionProProcessByLoanIdUserFileIdAndTypeAndStatus(loanApplication.getId(), RekognitionProProcess.EQUIPMENT_DETECTION_TYPE, allFiles.get(0).getId(), RekognitionProProcess.SUCCESS_STATUS);
        if(rekognitionProProcess != null) return rekognitionProProcess;

        rekognitionProProcess = securityDAO.saveRekognitionProcess(loanApplication.getId(), RekognitionProProcess.EQUIPMENT_DETECTION_TYPE, fileTypeId,allFiles.get(0).getId(), RekognitionProProcess.RUNNING_STATUS, null);

        try{
            ByteBuffer byteBufferImage = fileService.userFileToByteBuffer(allFiles.get(0));

            DetectProtectiveEquipmentResponse result = rekognitionAdapter.detectProtectiveEquipment(byteBufferImage);

            logger.debug("DetectPPE LoanId "+loanApplication.getId()+new Gson().toJson(result.persons()));

            if(result.persons().isEmpty() || result.persons().size() > 1) {
                JSONObject responseJSON = new JSONObject();
                responseJSON.put("multiplePeople", new Gson().toJson(result.persons()));
                rekognitionProProcess.setResponse(responseJSON);
                throw new RuntimeException(MULTIPLE_FACES_DETECTED_ERROR);
            }

            for (ProtectiveEquipmentPerson person: result.persons()) {

                JSONObject resultJson = new JSONObject(new Gson().toJson(person));
                rekognitionProProcess.setResponse(resultJson);

                List<ProtectiveEquipmentBodyPart> bodyParts=person.bodyParts();
                if (bodyParts.isEmpty()){
                    throw new RuntimeException(NO_BODY_PARTS_DETECTED_ERROR);
                } else
                    for (ProtectiveEquipmentBodyPart bodyPart: bodyParts) {
                        System.out.println("\t" + bodyPart.name() + ". Confidence: " + bodyPart.confidence().toString());
                        List<EquipmentDetection> equipmentDetections=bodyPart.equipmentDetections();
                        if(!equipmentDetections.isEmpty()){
                            for (EquipmentDetection item: equipmentDetections) {
                                if(item.type().name().equalsIgnoreCase("FACE_COVER") || item.type().name().equalsIgnoreCase("HEAD_COVER")){
                                    if(item.confidence() > 20.0) throw new RuntimeException(INVALID_CONTENT_FOUND_DETECTED_ERROR);
;                                }
                            }
                        }
                    }
            }


            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.SUCCESS_STATUS, null);
        }
        catch (Exception e){
            securityDAO.updateRekognitionProcess(rekognitionProProcess.getId(), rekognitionProProcess.getResponse(), RekognitionProProcess.FAILED_STATUS, e.getMessage());
            throw e;
        }

        return rekognitionProProcess;

    }

    public boolean containData(List<TextDetection> textDetections, Character search, int minOccurrences) {
        if (textDetections == null || textDetections.isEmpty()) return false;
        int occurrences = 0;
        for (TextDetection textDetection : textDetections) {
            if (textDetection != null && textDetection.getDetectedText() != null) {
                occurrences += textDetection.getDetectedText().chars().filter(e -> e == search.charValue()).count();
            }
        }
        return occurrences >= minOccurrences;
    }

    public String getImageLabels(byte[] imageByte){
        return rekognitionAdapter.detectLabels(ByteBuffer.wrap(imageByte));
    }

    public String getIsoCodeFromTextDetection(Integer countryId, Integer documentTypeId, List<TextDetection> textDetections){
        if(textDetections == null || textDetections.isEmpty()) return null;
        String firstLetter = null;
        String countryCode = null;
        switch (countryId){
            case CountryParam.COUNTRY_PERU:
                countryCode = "PER";
                switch (documentTypeId){
                    case IdentityDocumentType.DNI:{
                        firstLetter = "I";
                        break;
                    }
                    case IdentityDocumentType.CE:{
                        firstLetter = "C";
                        break;
                    }
                }
                break;
        }
        if(firstLetter == null) return null;
        String textDetectionResult = "";
        textDetections = this.sortTextDetection(textDetections);
        for (TextDetection textDetection : textDetections) {
            if(textDetection != null && textDetection.getDetectedText() != null && textDetection.getType().equalsIgnoreCase("LINE")){
                textDetectionResult = textDetectionResult.concat(textDetection.getDetectedText());
            }
        }
        int locationData = textDetectionResult.indexOf(firstLetter+"<");
        if(locationData == -1) return null;
        String textDetectionSubString = textDetectionResult.substring(locationData);
        if(textDetectionSubString.contains(firstLetter+"<"+countryCode)) return textDetectionSubString;
        return null;
    }

    public List<TextDetection> sortTextDetection( List<TextDetection> textDetections){
        if(textDetections == null) return null;
        Collections.sort(textDetections, new Comparator<TextDetection>(){
            @Override
            public int compare(final TextDetection o1, final TextDetection o2){
                // let your comparator look up your car's color in the custom order
                return o1.getGeometry().getBoundingBox().getTop().compareTo(o2.getGeometry().getBoundingBox().getTop());
            }
        });
        return textDetections;
    }

    public ISO7501ValidationData isValidIsoCode(String isoCode, Person person){
        ISO7501ValidationData validationData = new ISO7501ValidationData();
        if(isoCode == null || person == null){
            validationData.setValid(false);
            return validationData;
        }
        boolean isValid = true;
        //STAGE 1 - MINIMAL VALIDATION
        String firstLetter = null;
        String countryCode = null;
        switch (person.getCountry().getId()){
            case CountryParam.COUNTRY_PERU:
                countryCode = "PER";
                switch (person.getDocumentType().getId()){
                    case IdentityDocumentType.DNI:{
                        firstLetter = "I";
                        break;
                    }
                    case IdentityDocumentType.CE:{
                        firstLetter = "C";
                        break;
                    }
                }
                break;
        }
        if(!isoCode.startsWith(firstLetter+"<")) isValid = false;
        if(isValid && !isoCode.contains(countryCode+person.getDocumentNumber())) isValid = false;
        //STAGE 2 - VALIDATE DATA
        if(isValid){
            List<String> list = Arrays.asList(isoCode.split("<"));
            //DOCUMENT NUMBER INDEX
            int position = -1;
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).contains(person.getDocumentNumber())) {
                    position = i;
                    break;
                }
            }
            if(position == -1) isValid = false;
            else{
                //DOCUMENT NUMBER IS OK
                validationData.setDocumentNumber(person.getDocumentNumber());
                //SEARCH EXTRADATA
                List<String> additionalDataList = list.subList(position+1, list.size());
                String additionalData = additionalDataList.stream().filter(e -> e.length() >= 14).findFirst().orElse(null);
                if(additionalData == null) isValid = false;
                else{
                    //SPLIT FOR GET NUMERIC VALUES
                    List<String> numericValues = Arrays.asList(additionalData.split("\\D+"));
                    //SPLIT FOR GET ALPHABETICAL VALUES
                    List<String> alphabeticalValues = Arrays.asList(additionalData.split("\\d")).stream().filter(e -> !e.isEmpty()).collect(Collectors.toList());
                    if(numericValues.isEmpty() || numericValues.size() < 2 || alphabeticalValues.isEmpty() || alphabeticalValues.size() < 2) isValid = false;
                    else{
                        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
                        switch (person.getCountry().getId()){
                            case CountryParam.COUNTRY_PERU:
                                switch (person.getDocumentType().getId()){
                                    case IdentityDocumentType.DNI:{
                                        //VALIDATE GENDER
                                        if(!alphabeticalValues.get(0).equalsIgnoreCase("F") && !alphabeticalValues.get(0).equalsIgnoreCase("M")) isValid = false;
                                        else{
                                            validationData.setGender(alphabeticalValues.get(0));
                                            //VALIDATE DATES
                                            try{
                                                Date birthdate = sdf.parse(numericValues.get(0).substring(0, 6));
                                                validationData.setBirthdate(birthdate);
                                            }
                                            catch (Exception e){
                                                isValid = false;
                                            }

                                            try{
                                                Date expirationDate = sdf.parse(numericValues.get(1).substring(0, 6));
                                                validationData.setExpirationDate(expirationDate);
                                            }
                                            catch (Exception e){
                                                isValid = false;
                                            }

                                            //GET LAST NAME
                                            List<String> allAlphabeticalValues = Arrays.asList(isoCode.substring(isoCode.indexOf(additionalData) + additionalData.length()).split("\\d")).stream().filter(e -> !e.isEmpty()).collect(Collectors.toList());
                                            if(allAlphabeticalValues.size() == 1){
                                                int lastIndex = allAlphabeticalValues.get(0).lastIndexOf("<<");
                                                if(lastIndex != -1){
                                                    String lastName = allAlphabeticalValues.get(0).substring(0, lastIndex).replaceAll("<", " ").trim();
                                                    validationData.setLastName(lastName);
                                                }
                                            }
                                        }
                                        break;
                                    }
                                    case IdentityDocumentType.CE:{
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
        validationData.setValid(isValid);
        return validationData;
    }
}