package com.example.sbazureappdemo.excelService;

import com.example.sbazureappdemo.exceptions.ExcelGenerationException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ExcelColumnReaderService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelColumnReaderService.class);

    public List<String> leerColumna(MultipartFile archivoExcel, String sheetName, String headerName) {
        if (archivoExcel == null || archivoExcel.isEmpty()) {
            throw new IllegalArgumentException("El archivo Excel está vacío.");
        }
        if (headerName == null || headerName.trim().isEmpty()) {
            throw new IllegalArgumentException("headerName no puede estar vacío.");
        }

        try (InputStream is = archivoExcel.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = (sheetName != null && !sheetName.isBlank()) ? workbook.getSheet(sheetName) : workbook.getSheetAt(0);

            if (sheet == null) {
                throw new IllegalArgumentException("No se encontró el sheet: " + sheetName);
            }
            DataFormatter formatter = new DataFormatter(true);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            int firstRowNum = sheet.getFirstRowNum();
            Row headerRow = sheet.getRow(firstRowNum);
            if (headerRow == null) {
                throw new IllegalArgumentException("El sheet no tiene fila de encabezados.");
            }
            int colIndex = findColumnIndex(headerRow, headerName, formatter, evaluator);
            if (colIndex < 0) {
                throw new IllegalArgumentException("No se encontró la columna '" + headerName + "' en el header.");
            }
            List<String> valores = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();
            for (int r = firstRowNum + 1; r <= lastRow; r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String value = formatter.formatCellValue(cell, evaluator);
                if (value != null) {
                    value = value.trim();
                }
                if (value != null && !value.isEmpty()) {
                    valores.add(value.toLowerCase(Locale.ROOT));
                }
            }

            logger.info("ImportExcel ApifyCall: Leídos {} valores de la columna '{}' en sheet '{}'", valores.size(), headerName, sheet.getSheetName());
            return valores;
        } catch (Exception e) {
            logger.error("ImportExcel ApifyCall: Error leyendo columna '{}' del Excel", headerName, e);
            throw new ExcelGenerationException("Error procesando el Excel: " + e.getMessage(), e);
        }
    }

    private int findColumnIndex(Row headerRow, String headerName, DataFormatter formatter, FormulaEvaluator evaluator) {
        String target = headerName.trim().toLowerCase();
        int lastCell = headerRow.getLastCellNum();
        for (int c = 0; c < lastCell; c++) {
            Cell cell = headerRow.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String header = formatter.formatCellValue(cell, evaluator);
            if (header != null && header.trim().toLowerCase().equals(target)) {
                return c;
            }
        }
        return -1;
    }
}
