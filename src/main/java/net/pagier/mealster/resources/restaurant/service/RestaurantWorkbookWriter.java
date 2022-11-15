package net.pagier.mealster.resources.restaurant.service;

import lombok.extern.log4j.Log4j2;
import net.pagier.mealster.resources.restaurant.model.dto.RestaurantSearchResultDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Log4j2
@Component
public class RestaurantWorkbookWriter {

    public RestaurantWorkbookWriter() {
    }

    public void write(List<RestaurantSearchResultDto> results, OutputStream out) {
        try (Workbook report = new XSSFWorkbook()) {
            Sheet sheet = report.createSheet("restaurants");
            createHeaderRow(sheet);
            for (int i = 0, rowNum = 1; i < results.size(); i++, rowNum++) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(results.get(i).name());
                row.createCell(1).setCellValue(results.get(i).x());
                row.createCell(2).setCellValue(results.get(i).y());
                row.createCell(3).setCellValue(results.get(i).foodType());
                row.createCell(4).setCellValue(results.get(i).distance().toString());
            }
            report.write(out);
        } catch (IOException e) {
            LOG.error("Error while creating report", e);
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("name");
        headerRow.createCell(1).setCellValue("x");
        headerRow.createCell(2).setCellValue("y");
        headerRow.createCell(3).setCellValue("food type");
        headerRow.createCell(4).setCellValue("distance");
    }
}
