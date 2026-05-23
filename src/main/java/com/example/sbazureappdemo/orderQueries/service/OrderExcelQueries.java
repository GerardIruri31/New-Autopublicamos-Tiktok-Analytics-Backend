package com.example.sbazureappdemo.orderQueries.service;
import com.example.sbazureappdemo.excelService.ExcelService;
import com.example.sbazureappdemo.exceptions.ExcelGenerationException;
import com.example.sbazureappdemo.orderQueries.dto.QueryResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


@Service
public class OrderExcelQueries {
    Logger logger = LoggerFactory.getLogger(OrderExcelQueries.class);
    public byte[] downloadExcel(List<QueryResponse> data) {
        // Usa Apache POI para crear y llenar un archivo Excel
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                XSSFSheet sheet = workbook.createSheet("Orden Queries");

                List<String> columns = Arrays.asList(
                        "codordentrabajo",
                        "codcabeceraordentrabajo",
                        "codautora",
                        "codlibro",
                        "tippublicacion",
                        "nTippublicacion",
                        "codescena",
                        "codposteador",
                        "codtelefono",
                        "codcuentatiktok",
                        "codsonido",
                        "desscenahook",
                        "descaption",
                        "destropo",
                        "desslide1keywordshide",
                        "desslide2keywordshide",
                        "deshashtag",
                        "despalote",
                        "codimagenprincipal",
                        "codimagenscreenshot",
                        "codimagendialogo",
                        "codvideo",
                        "desinstrucciones",
                        "fecplanposteo",
                        "codestadoorden",
                        "tipregistroorden",
                        "flgordencompleta",
                        "ctddatoobligincompleto",
                        "desdatoobligincompleto",
                        "deslogerrororden",
                        "codusuarioauditoriacreareg",
                        "codusuarioauditoriaactualizareg",
                        "fecreacionregistro",
                        "horacreacionregistro",
                        "fecactualizacionregistro",
                        "horaactualizacionregistro"
                );

                // Estilo de cabecera
                CellStyle cabeceraStyle = workbook.createCellStyle();
                cabeceraStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                cabeceraStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                Font cabeceraFont = workbook.createFont();
                cabeceraFont.setBold(true);
                cabeceraStyle.setFont(cabeceraFont);

                cabeceraStyle.setAlignment(HorizontalAlignment.CENTER);
                cabeceraStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                cabeceraStyle.setWrapText(true);
                cabeceraStyle.setBorderTop(BorderStyle.THICK);
                cabeceraStyle.setBorderBottom(BorderStyle.THICK);
                cabeceraStyle.setBorderLeft(BorderStyle.THICK);
                cabeceraStyle.setBorderRight(BorderStyle.THICK);

                // Estilo contenido
                CellStyle contenidoStyle = workbook.createCellStyle();
                contenidoStyle.setAlignment(HorizontalAlignment.CENTER);
                contenidoStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                contenidoStyle.setWrapText(true);

                // Estilo numérico
                CellStyle numericStyle = workbook.createCellStyle();
                DataFormat format = workbook.createDataFormat();
                numericStyle.setDataFormat(format.getFormat("0"));
                numericStyle.setAlignment(HorizontalAlignment.CENTER);
                numericStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Estilo fecha
                CellStyle dateStyle = workbook.createCellStyle();
                dateStyle.setAlignment(HorizontalAlignment.CENTER);
                dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                CreationHelper createHelper = workbook.getCreationHelper();
                dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

                // Cabeceras
                Row headerRow = sheet.createRow(0);
                for (int col = 0; col < columns.size(); col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(columns.get(col));
                    cell.setCellStyle(cabeceraStyle);
                }

                // Datos
                int rowIndex = 1;

                if (data != null) {
                    for (QueryResponse rowData : data) {
                        Row row = sheet.createRow(rowIndex++);

                        for (int col = 0; col < columns.size(); col++) {
                            Cell cell = row.createCell(col);
                            String columnName = columns.get(col);

                            Object value = null;

                            if (rowData != null) {
                                value = getFieldValue(rowData, columnName);
                            }

                            if (value == null) {
                                cell.setCellValue("null"); // IMPORTANTE: no dejar vacío
                                cell.setCellStyle(contenidoStyle);
                            } else if (value instanceof Integer) {
                                cell.setCellValue(((Integer) value).doubleValue());
                                cell.setCellStyle(numericStyle);
                            } else if (value instanceof Long) {
                                cell.setCellValue(((Long) value).doubleValue());
                                cell.setCellStyle(numericStyle);
                            } else if (value instanceof Double) {
                                cell.setCellValue((Double) value);
                                cell.setCellStyle(numericStyle);
                            } else if (value instanceof LocalDate) {
                                cell.setCellValue(java.sql.Date.valueOf((LocalDate) value));
                                cell.setCellStyle(dateStyle);
                            } else if (value instanceof LocalTime) {
                                cell.setCellValue(value.toString());
                                cell.setCellStyle(contenidoStyle);
                            } else {
                                cell.setCellValue(value.toString());
                                cell.setCellStyle(contenidoStyle);
                            }
                        }
                    }
                }

                // Ajustar ancho columnas
                final int MAX_COLUMN_WIDTH = 65280;
                for (int col = 0; col < columns.size(); col++) {
                    sheet.autoSizeColumn(col);

                    int currentWidth = sheet.getColumnWidth(col);
                    int extraWidth = 2000;
                    int newWidth = currentWidth + extraWidth;

                    if (newWidth > MAX_COLUMN_WIDTH) {
                        newWidth = MAX_COLUMN_WIDTH;
                    }

                    sheet.setColumnWidth(col, newWidth);
                }

                // Ajustar alto filas
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        row.setHeightInPoints(45);
                    }
                }
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());
                String fileName = "orden_queries_" + timestamp + ".xlsx";
                workbook.write(outputStream);
                logger.info("Service - Excel generado Orden Queries: " + fileName);
                return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Service - Error al generar el archivo Excel Orden Queries", e);
            throw new ExcelGenerationException("Error al generar el archivo Excel Orden Queries", e);
        }

    }



    private Object getFieldValue(QueryResponse rowData, String fieldName) {
        try {
            Field field = QueryResponse.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(rowData);
        } catch (Exception e) {
            return null;
        }
    }
}
