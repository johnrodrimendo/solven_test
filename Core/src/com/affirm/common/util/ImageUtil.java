package com.affirm.common.util;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;



public class ImageUtil {

    private static final int IMG_WIDTH = 1200;
    private static final int IMG_HEIGHT = 1200;
    public static final int IMG_WIDTH_MIN_MATI = 610;
    public static final int IMG_HEIGHT_MIN_MATI = 610;


    public String processFile(String resultBase64Encoded,String extension, Boolean validateImageSizeForMati){
        String result=null;

        //check if is an image or a PDF
        switch (extension){
            //if is a image compress and save in jpeg format
            case "jpeg":
            case "jpg":
            case "png":
                byte[] imageByte = DatatypeConverter.parseBase64Binary(resultBase64Encoded);
                if(validateImageSizeForMati == null || !validateImageSizeForMati) imageByte = reduceImageSize(imageByte);
/*                else{
                    if(!this.isValidImageSize(imageByte, IMG_WIDTH_MIN_MATI, IMG_HEIGHT_MIN_MATI)){
                        return null;
                    }
                }*/
                String resultBase=DatatypeConverter.printBase64Binary(imageByte);
                result = resultBase;
                break;
            //if is PDF
            default:
                result = resultBase64Encoded;
                break;
        }
        return result;
    }


    public byte [] reduceImageSize(byte[] imageByte){

        byte [] result=null;
        BufferedImage originalImage = this.byteArrayToBufferedImage(imageByte);
        int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        if(originalImage.getWidth()>IMG_WIDTH){
            BufferedImage resizeImageJpg = this.resizeImage(originalImage, type);
            result = bufferedImageToByteArray(resizeImageJpg);
        }else{
            result = bufferedImageToByteArray(originalImage);
        }

        return result;
    }

    public boolean isValidImageSize(byte[] imageByte, int minWidth, int minHeight){
        BufferedImage originalImage = this.byteArrayToBufferedImage(imageByte);
        if(originalImage.getWidth() < minWidth || originalImage.getHeight() < minHeight){
            return false;
        }
        return true;
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int type){
        double aspectRatio = (double) originalImage.getHeight(null)/(double) originalImage.getWidth(null);
        int newWidth=IMG_WIDTH;
        int newHeigth= (int) (IMG_WIDTH*aspectRatio);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeigth, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeigth, null);
        g.dispose();
        return resizedImage;
    }

    public BufferedImage byteArrayToBufferedImage(byte[] imageInByte){
        BufferedImage bImageFromConvert=null;
        InputStream in = new ByteArrayInputStream(imageInByte);
        try {
            bImageFromConvert = ImageIO.read(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bImageFromConvert;
    }

    public byte [] bufferedImageToByteArray(BufferedImage originalImage){
        byte [] imageInByte=null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageInByte;

    }


}
