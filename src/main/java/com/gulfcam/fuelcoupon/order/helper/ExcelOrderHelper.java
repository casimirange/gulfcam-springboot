package com.gulfcam.fuelcoupon.order.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelOrderHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Reférence Commande", "Reférence du client", "Nom du client", "Localisation du magasin" , "Nom du reponsable de la commande" , "Montant" , "Temps de livraison" , "Méthode de paiement" , "Reférence du paiement" , "Description" , "Date de création", "Statut Commande" };
    static String SHEET = "Commandes";


    public static ByteArrayInputStream ordersToExcel(List<ResponseOrderDTO> responseOrderList) {

        SimpleDateFormat dateFor = new SimpleDateFormat("dd/MM/yyyy");

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }

            int rowIdx = 1;
            for (ResponseOrderDTO order : responseOrderList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(order.getInternalReference());
                row.createCell(1).setCellValue(order.getClient().getInternalReference());
                row.createCell(2).setCellValue(order.getClient().getCompleteName());
                row.createCell(3).setCellValue(order.getLocalisation());
                row.createCell(4).setCellValue((order.getCommercialAttache()!= null)? order.getCommercialAttache().getFirstName()+" "+order.getCommercialAttache().getLastName(): " ");
//                row.createCell(5).setCellValue((order.getSpaceManager2() != null)? order.getSpaceManager2().getFirstName()+" "+order.getSpaceManager2().getLastName(): " ");
                row.createCell(5).setCellValue(order.getNetAggregateAmount());
//                row.createCell(7).setCellValue(order.getTTCAggregateAmount());
//                row.createCell(8).setCellValue(order.getTax());
                row.createCell(6).setCellValue(order.getDeliveryTime());
                row.createCell(7).setCellValue((order.getPaymentMethod() != null)? order.getPaymentMethod().getDesignation(): " ");
                row.createCell(8).setCellValue(order.getPaymentReference());
                row.createCell(9).setCellValue(order.getDescription());
                row.createCell(10).setCellValue(dateFor.format(Date.from(order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
                row.createCell(11).setCellValue(order.getStatus().getName().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}