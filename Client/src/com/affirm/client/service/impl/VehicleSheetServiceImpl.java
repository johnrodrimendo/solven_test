package com.affirm.client.service.impl;

import com.affirm.client.dao.GuaranteedVehicleDAO;
import com.affirm.client.service.VehicleSheetService;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.GuaranteedVehicle;
import com.affirm.common.model.catalog.MaintainedCarBrand;
import com.affirm.common.service.CatalogService;
import jxl.WorkbookSettings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.shiro.SecurityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service("VehicleSheetService")
public class VehicleSheetServiceImpl implements VehicleSheetService {

    @Autowired
    CatalogService catalogService;
    @Autowired
    GuaranteedVehicleDAO guaranteedVehicleDAO;

    public JSONObject saveVehicles(String jsonVehiclesArray)throws Exception{
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("toAdd", 0);
        jsonResponse.put("toUpdate", 0);

        List <GuaranteedVehicle> guaranteedVehicles=new ArrayList<>();
        JSONArray vehiclesArray = new JSONArray(jsonVehiclesArray);
        if(jsonVehiclesArray!=null){
            guaranteedVehicleDAO.saveGuaranteedVehicles(jsonVehiclesArray);
        }
        jsonResponse.put("toAdd", vehiclesArray.length());

        return jsonResponse;
    }

    public JSONObject getJsonVehicles( MultipartFile[] file)throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(file[0].getBytes());
        jxl.Workbook workbook = null;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("ISO-8859-1");

        ArrayList <GuaranteedVehicle> guaranteedVehicles=new ArrayList<>();
        ArrayList <GuaranteedVehicle> guaranteedVehiclesFinal=new ArrayList<>();
        int vehiclesWithEmptyfields=0;
        int vehiclesWithUnkwonBrand=0;
        int vehiclesRepeated=0;
        int vehiclesToAdd=0;
        HashMap <String, Integer> brandbraModelmap=new HashMap<String, Integer>();

        try {
            workbook = jxl.Workbook.getWorkbook(bis, ws);
            jxl.Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();

            for (int i = 2; i < rows; i++) {

                if (sheet.getRow(i).length == 0) {
                    continue;
                }
                boolean someEmpty = false;
                ArrayList <String> al=new ArrayList<>();
                if(sheet.getCell(3, i).getContents().trim().toLowerCase().equals("si")){
                    for (int j = 4; j <= 37; j++) {
                        String cellValue=sheet.getCell(j, i).getContents().trim();
                        if((j+1)%5!=0){
                            al.add(cellValue);
                            if (cellValue.isEmpty()) {
                                someEmpty = true;
                                break;
                            }
                        }
                    }
                }

                if (someEmpty) {
                    vehiclesWithEmptyfields++;
                }else{
                    GuaranteedVehicle guaranteedVehicle;
                    String brandName,model,year, price,accepted;
                    //Brand
                    brandName=sheet.getCell(0, i).getContents().trim()+"";
                    //Model
                    model=sheet.getCell(1, i).getContents().trim();
                    //accepted
                    accepted=sheet.getCell(2, i).getContents().trim();

                    if(brandbraModelmap.containsKey(brandName+model)){
                        brandbraModelmap.put(brandName+model,brandbraModelmap.get(brandName+model)+1);
                    }else{
                        brandbraModelmap.put(brandName+model,1);
                    }

                    Integer maintianedCarBrandId=catalogService.getBrandId(brandName);

                    if(maintianedCarBrandId!=-1){
                        //for each year
                        for(int j=0;j<7;j++){
                            //Year
                            year=sheet.getCell(3+j*5, 1).getContents().trim();
                            // for each miles
                            for(int k=0;k<4;k++){
                                //Value
                                int indexK=4+j*5+k;
                                price=sheet.getCell(indexK, i).getContents().trim();

                                if(sheet.getCell(3, i).getContents().trim().toLowerCase().equals("no")){
                                    price="0";
                                }
                                if (!brandName.isEmpty() && !model.isEmpty() && !price.isEmpty() && !year.isEmpty()) {
                                    guaranteedVehicle = new GuaranteedVehicle();
                                    guaranteedVehicle.setMaintianedCarBrandId(maintianedCarBrandId);
                                    guaranteedVehicle.setModel("" + model);
                                    guaranteedVehicle.setYear(Integer.parseInt("" + year));
                                    guaranteedVehicle.setPrice(Double.parseDouble("" + price.replace(",", ".")));
                                    guaranteedVehicle.setMileageId(k + 1);
                                    guaranteedVehicle.setIsAccepted(accepted);
                                    guaranteedVehicles.add(guaranteedVehicle);
                                }
                            }
                        }
                    }else{
                        vehiclesWithUnkwonBrand++;
                    }
                }
            }
            HashSet <String> isVehicleRepeated=new HashSet<>();
            HashSet <String> isVehicleAdded=new HashSet<>();
            for(GuaranteedVehicle gv:guaranteedVehicles){
                MaintainedCarBrand maintainedCarBrand=catalogService.getMaintainedCarBrand(gv.getMaintianedCarBrandId());
                String key=maintainedCarBrand.getBrand()+gv.getModel();
                int times=brandbraModelmap.get(key);
                if(times>1){
                    if(!isVehicleRepeated.contains(key)){
                        isVehicleRepeated.add(key);
                        vehiclesRepeated++;
                    }

                }else{
                    if(!isVehicleAdded.contains(key)){
                        isVehicleAdded.add(key);
                        vehiclesToAdd++;
                    }
                    guaranteedVehiclesFinal.add(gv);
                }
            }
            SecurityUtils.getSubject().getSession().setAttribute("vehiclesToAdd", guaranteedVehicles.size());
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (workbook != null)
                workbook.close();
        }

        JSONObject resJson = new JSONObject();
        resJson.put("vehiclesArr",getVehiclesJsonArray(guaranteedVehiclesFinal));
        resJson.put("vehiclesToAdd", vehiclesToAdd);
        resJson.put("vehiclesWithEmptyfields",vehiclesWithEmptyfields );
        resJson.put("vehiclesWithUnkwonBrand", vehiclesWithUnkwonBrand );
        resJson.put("vehiclesRepeated", vehiclesRepeated );

        return resJson;
    }

    private static CellStyle createBorderedStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }

    public void createImportEmployeesExcelTemplate(OutputStream outputStream) throws Exception {

        //LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        //boolean customMaxAmountActivated = isCustomMaxAmountActivated();
        //boolean validateAgreementProduct = validateAgreementProduct(user.getActiveCompany().getId());

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Listado de vehiculos");

        // Cell Styles
        //CellStyle headStyle = workbook.createCellStyle();
        CellStyle headStyle = createBorderedStyle(workbook);
        headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 10);
        headStyleFont.setBold(true);
        headStyle.setFont(headStyleFont);


        CellStyle quantityStyle = workbook.createCellStyle();
        Font quantityStyleFont = workbook.createFont();
        quantityStyleFont.setFontHeightInPoints((short) 9);
        quantityStyle.setFont(quantityStyleFont);
        quantityStyle.setAlignment(HorizontalAlignment.RIGHT);
        quantityStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("@"));
        //quantityStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0"));

        CellStyle valueStyle = workbook.createCellStyle();
        Font valueStyleFont = workbook.createFont();
        valueStyleFont.setFontHeightInPoints((short) 9);
        valueStyle.setFont(valueStyleFont);
        valueStyle.setAlignment(HorizontalAlignment.LEFT);
        valueStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("@"));


        // Setting widths
        for (int i = 0; i < 41; i++) {
            sheet.setColumnWidth(i, 20 * 200);
        }

        // Paint head row
        Row headRow = sheet.createRow(sheet.getPhysicalNumberOfRows()+1);
        createCell(headRow, 0, headStyle).setCellValue("Marca");
        createCell(headRow, 1, headStyle).setCellValue("Modelo");
        createCell(headRow, 2, headStyle).setCellValue("Aceptado (si/no)");
        int yearIni=2011;
        int yearEnd=2018;

        for(int year=yearEnd;year>yearIni;year--){
            //Year
            int colunm=3+(6-(year-yearIni)+1)*5;
            createCell(headRow,colunm, headStyle).setCellValue(""+year);
            createCell(headRow,colunm+1, headStyle).setCellValue("De 1-10,000");
            createCell(headRow, colunm+2, headStyle).setCellValue("De 10,000-30,000");
            createCell(headRow, colunm+3, headStyle).setCellValue("De 30,000-60,000");
            createCell(headRow, colunm+4, headStyle).setCellValue("De 60,000-90,000");
        }

        // Print demo values
        for (int i = 0; i < 2; i++) {
            Row demoRow = sheet.createRow(sheet.getPhysicalNumberOfRows()+1);
            createCell(demoRow, demoRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "Toyota" : "Kia");
            createCell(demoRow, demoRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "Hilux" : "Cerato");
            createCell(demoRow, demoRow.getPhysicalNumberOfCells(), valueStyle, CellType.STRING).setCellValue(i == 0 ? "si" : "no");
            double value=i == 0 ?  110000: 90000;
            for(int year=yearEnd;year>yearIni;year--){
                //Year
                int colunm=3+(6-(year-yearIni)+1)*5;
                createCell(demoRow,colunm, quantityStyle).setCellValue(value);
                createCell(demoRow,colunm+1, quantityStyle).setCellValue(value-100);
                createCell(demoRow, colunm+2, quantityStyle).setCellValue(value-150);
                createCell(demoRow, colunm+3, quantityStyle).setCellValue(value-200);
                createCell(demoRow, colunm+4, quantityStyle).setCellValue(value-250);
                value-=300;
            }
        }

        workbook.write(outputStream);
        workbook.close();
    }

    private Cell createCellWithComment(Row row, int column, CellStyle style, String comment, Sheet sheet, Workbook workbook) {
        Cell cell = row.createCell(column);
        if (style != null)
            cell.setCellStyle(style);

        if (comment != null) {
            Drawing drawing = sheet.createDrawingPatriarch();
            CreationHelper factory = workbook.getCreationHelper();

            ClientAnchor anchor = factory.createClientAnchor();
            anchor.setCol1(cell.getColumnIndex());
            anchor.setCol2(cell.getColumnIndex());
            anchor.setRow1(row.getRowNum());
            anchor.setRow2(row.getRowNum());

            // Create the comment and set the text+author
            Comment commentElement = drawing.createCellComment(anchor);
            RichTextString str = factory.createRichTextString(comment);
            commentElement.setString(str);

            // Assign the comment to the cell
            cell.setCellComment(commentElement);
        }
        return cell;
    }

    private Cell createCell(Row row, int column, CellStyle style) {
        return createCell(row, column, style, null);
    }

    private Cell createCell(Row row, int column, CellStyle style, CellType cellType) {
        Cell cell = row.createCell(column);
        if (style != null)
            cell.setCellStyle(style);
        if (cellType != null)
            cell.setCellType(cellType);
        return cell;
    }

    public JSONArray getVehiclesJsonArray(ArrayList<GuaranteedVehicle> guaranteedVehicles) throws Exception{
        JSONArray vehiclesArr = new JSONArray();
        for (GuaranteedVehicle guaranteedVehicle : guaranteedVehicles) {
            JSONObject jsVehicle = new JSONObject();
            jsVehicle.put("maintainedCarBrandId", guaranteedVehicle.getMaintianedCarBrandId());
            jsVehicle.put("model", guaranteedVehicle.getModel());
            jsVehicle.put("mileageId", guaranteedVehicle.getMileageId());
            jsVehicle.put("year", guaranteedVehicle.getYear());
            jsVehicle.put("isAccepted", guaranteedVehicle.isAccepted());
            jsVehicle.put("currencyId",Currency.PEN);
            jsVehicle.put("price", guaranteedVehicle.getPrice());
            vehiclesArr.put(jsVehicle);
        }
        return vehiclesArr;
    }
}
