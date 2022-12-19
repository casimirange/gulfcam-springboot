package com.gulfcam.fuelcoupon.store.helper;

import com.gulfcam.fuelcoupon.order.dto.ResponseOrderDTO;
import com.gulfcam.fuelcoupon.store.dto.ResponseCouponMailDTO;
import com.gulfcam.fuelcoupon.store.entity.Coupon;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


public class ExcelCouponHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Reference", "Numéro de série", "Modulo", "Type de Bon", "Date de création" , "Date de fabrication en usine"  , "Statut" };
    static String SHEET = "Coupons";


    public static ByteArrayInputStream couponsToExcel(List<ResponseCouponMailDTO> responseCouponMailList) {

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
            for (ResponseCouponMailDTO coupon : responseCouponMailList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(coupon.getInternalReference());
                row.createCell(1).setCellValue(coupon.getSerialNumber());
                row.createCell(2).setCellValue(coupon.getModulo());
                row.createCell(3).setCellValue((coupon.getIdTypeVoucher() != null)? coupon.getIdTypeVoucher().getAmount(): 0);
                row.createCell(4).setCellValue(dateFor.format(Date.from(coupon.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
                row.createCell(5).setCellValue(coupon.getProductionDate());
                row.createCell(6).setCellValue(coupon.getStatus().getName().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}