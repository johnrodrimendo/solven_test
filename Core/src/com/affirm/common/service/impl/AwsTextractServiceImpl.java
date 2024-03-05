package com.affirm.common.service.impl;

import com.affirm.common.service.AwsTextractService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AwsTextractServiceImpl implements AwsTextractService {

    private static Logger logger = Logger.getLogger(AwsSesEmailServiceImpl.class);
    private static final String AWS_ACCESSKEY = System.getenv("AWS_ACCESS_KEY_ID");
    private static final String AWS_SECRETKEY = System.getenv("AWS_SECRET_ACCESS_KEY");
    private static final Regions AWS_SES_REGION = Regions.US_EAST_1;

    private AmazonTextract amazonTextract;

    @PostConstruct
    public void init() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESSKEY, AWS_SECRETKEY);
        AWSStaticCredentialsProvider initialCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonTextract = AmazonTextractClientBuilder.standard()
                .withCredentials(initialCredentialsProvider)
                .withRegion(AWS_SES_REGION)
                .build();
    }

    String getBucket() {
        return System.getenv("AWS_S3_BUCKET");
    }

    public DetectDocumentTextResult getDetectDocumentTextResult(String bucket, String name) {
        S3Object s3Object = new S3Object().withBucket(bucket).withName(name);
        Document document = new Document().withS3Object(s3Object);
        DetectDocumentTextRequest request = new DetectDocumentTextRequest().withDocument(document);
        return this.amazonTextract.detectDocumentText(request);
    }

    public List<String> getDetectedLineTexts(String bucket, String name) {
        List<String> detectedLineTexts = new ArrayList<>();
        DetectDocumentTextResult result = this.getDetectDocumentTextResult(bucket, name);
        if (result != null && result.getBlocks() != null) {
            result.getBlocks()
                    .stream()
                    .filter(e -> BlockType.LINE.toString().equals(e.getBlockType()))
                    .forEach(e -> detectedLineTexts.add(e.getText()));
        }
        return detectedLineTexts;
    }

    public JSONObject analyzeDoc(String documentPath, String bucket) {
        if(bucket == null || bucket.isEmpty()) bucket = getBucket();
        try {
            List<FeatureType> featureTypes = new ArrayList<FeatureType>();
            featureTypes.add(FeatureType.FORMS);

            AnalyzeDocumentRequest request = new AnalyzeDocumentRequest()
                    .withFeatureTypes("FORMS")
                    .withDocument(new Document().
                            withS3Object(new S3Object().withName(documentPath).withBucket(bucket)));

            AnalyzeDocumentResult analyzeDocument = amazonTextract.analyzeDocument(request);

            JSONObject map = new JSONObject();

            List<Block> blocksKeySet = analyzeDocument.getBlocks().stream().filter( e -> e.getBlockType().equalsIgnoreCase("KEY_VALUE_SET") && e.getEntityTypes().contains("KEY")).collect(Collectors.toList());

            for (Block block : blocksKeySet) {

                Relationship valueRelationShip = block.getRelationships().stream().filter( e -> e.getType().equalsIgnoreCase("VALUE")).findFirst().orElse(null);
                Relationship childRelationShip = block.getRelationships().stream().filter( e -> e.getType().equalsIgnoreCase("CHILD")).findFirst().orElse(null);

                //LOOKING FOR CHILD VALUES TO GET THE NAME OF THE KEY
                String keyValue = childRelationShip != null && !childRelationShip.getIds().isEmpty() ? getValueOfChild(childRelationShip.getIds(), analyzeDocument.getBlocks()) : null;
                String mapValue = null;

                if(valueRelationShip != null){
                    Block blockValueSet = analyzeDocument.getBlocks().stream().filter( e -> e.getBlockType().equalsIgnoreCase("KEY_VALUE_SET") && e.getId().equalsIgnoreCase(valueRelationShip.getIds().get(0))).findFirst().orElse(null);
                    if(blockValueSet != null && blockValueSet.getRelationships() != null && !blockValueSet.getRelationships().isEmpty()){
                        Relationship childValueRelationShip = blockValueSet.getRelationships().stream().filter(e ->  e.getType() != null && e.getType().equalsIgnoreCase("CHILD")).findFirst().orElse(null);
                        if(childValueRelationShip != null) mapValue = getValueOfChild(childValueRelationShip.getIds(), analyzeDocument.getBlocks());
                    }

                }

                map.put(keyValue, mapValue);

            }

            return map;

        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    private String getValueOfChild( List<String> ids, List<Block> blocksKeySet){
        String valueToReturn = null;
        for (String id : ids) {
            String value = blocksKeySet.stream().filter( e -> e.getId().equalsIgnoreCase(id)).findFirst().orElse(null).getText();
            if(valueToReturn == null) valueToReturn = ""+value;
            else valueToReturn = valueToReturn+" "+value;
        }
        return valueToReturn;
    }

}
