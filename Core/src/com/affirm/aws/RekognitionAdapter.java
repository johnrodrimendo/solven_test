package com.affirm.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.google.gson.Gson;
import org.jooq.lambda.function.Consumer2;
import org.jooq.lambda.tuple.Tuple2;
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectProtectiveEquipmentRequest;
import software.amazon.awssdk.services.rekognition.model.DetectProtectiveEquipmentResponse;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentSummarizationAttributes;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Created by jarmando on 16/03/17.
 */
public class RekognitionAdapter {

    Gson gson = new Gson();
    AWSCredentialsProvider acp = new AWSCredentialsProvider() {
        @Override
        public AWSCredentials getCredentials() {
            return new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY"));
        }

        @Override
        public void refresh() { }
    };

    AwsCredentialsProvider acpV2 = new AwsCredentialsProvider() {
        @Override
        public AwsCredentials resolveCredentials() {
            return new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return System.getenv("AWS_ACCESS_KEY_ID");
                }
                @Override
                public String secretAccessKey() {
                    return System.getenv("AWS_SECRET_ACCESS_KEY");
                }
            };
        }
    };
    AmazonRekognition rekognition = AmazonRekognitionClientBuilder
            .standard().withRegion(System.getenv("AWS_REGION"))
            .withCredentials(acp).build();

    Region region = Region.of(System.getenv("AWS_REGION"));

    RekognitionClient rekognitionV2 = RekognitionClient.builder()
            .region(region)
            .credentialsProvider(acpV2)
            .build();



    public void compareFaces(ByteBuffer b1, ByteBuffer b2, Consumer2<Double, JSONObject> responseHandler, Consumer<? super Throwable> expHandler) {
        Image image1 = new Image();
        Image image2 = new Image();
        image1.setBytes(b1);
        image2.setBytes(b2);

        CompareFacesRequest cfr = new CompareFacesRequest();
        cfr.setSourceImage(image1);
        cfr.setTargetImage(image2);
        cfr.setSimilarityThreshold(0f);

        try {
            CompareFacesResult result = rekognition.compareFaces(cfr);
            Double highestSimilarity = result.getFaceMatches().stream().mapToDouble(x -> x.getSimilarity()).max().orElseGet(() -> 0);
            JSONObject jsonResult = new JSONObject(gson.toJson(result));
            responseHandler.accept(highestSimilarity, jsonResult);
        }
        catch (Throwable e) {
            expHandler.accept(e);
        }
    }

    public String detectFaces(ByteBuffer image){
        Image image1 = new Image();
        image1.setBytes(image);
        DetectFacesRequest dfr = new DetectFacesRequest();
        dfr.setImage(image1);
        dfr.withAttributes(Attribute.ALL);
        DetectFacesResult result = rekognition.detectFaces(dfr);
        System.out.println(result.toString());
        JSONObject jsonResult = new JSONObject(gson.toJson(result));
        return jsonResult.toString();
    }

    public String detectLabels(ByteBuffer image){
        Image image1 = new Image();
        image1.setBytes(image);
        DetectLabelsRequest dlr = new DetectLabelsRequest();
        dlr.setImage(image1);
        dlr.setMaxLabels(20);
        dlr.setMinConfidence(25F);
        DetectLabelsResult result = rekognition.detectLabels(dlr);
        System.out.println(result.toString());
        JSONObject jsonResult = new JSONObject(gson.toJson(result));
        return jsonResult.toString();
    }

    public CompareFacesResult compareFaces(Tuple2<ByteBuffer, ByteBuffer> bytesBufferImages) {
        Image source = new Image().withBytes(bytesBufferImages.v1());
        Image target = new Image().withBytes(bytesBufferImages.v2());

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(source)
                .withTargetImage(target)
                .withSimilarityThreshold(0f);

        CompareFacesResult compareFacesResult = rekognition.compareFaces(request);

        return compareFacesResult;
    }

    public DetectTextResult detectTexts(ByteBuffer byteBufferImage) {
        Image image = new Image();
        image.setBytes(byteBufferImage);

        DetectTextRequest request = new DetectTextRequest()
                .withImage(image);

        DetectTextResult result = rekognition.detectText(request);

        return result;
    }


    public DetectProtectiveEquipmentResponse detectProtectiveEquipment(ByteBuffer byteBufferImage) {

        ProtectiveEquipmentSummarizationAttributes summarizationAttributes = ProtectiveEquipmentSummarizationAttributes.builder()
                .minConfidence(18F)
                .requiredEquipmentTypesWithStrings("FACE_COVER", "HEAD_COVER")
                .build();

        SdkBytes sourceBytes = SdkBytes.fromByteBuffer(byteBufferImage);

        software.amazon.awssdk.services.rekognition.model.Image souImage = software.amazon.awssdk.services.rekognition.model.Image.builder()
                .bytes(sourceBytes)
                .build();

        DetectProtectiveEquipmentRequest request = DetectProtectiveEquipmentRequest.builder()
                .image(souImage)
                .summarizationAttributes(summarizationAttributes)
                .build();

        DetectProtectiveEquipmentResponse result = rekognitionV2.detectProtectiveEquipment(request);

        return result;
    }

}
