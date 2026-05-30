package com.example.sbazureappdemo.dataMaintenance.service;

import com.example.sbazureappdemo.dataMaintenance.repository.DataMaintenanceRepository;
import com.example.sbazureappdemo.exceptions.ExcelGenerationException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ExcelUploadService {
    Logger logger = LoggerFactory.getLogger(ExcelUploadService.class);
    private final UploadTempTableService uploadTempTableService;
    private final DataMaintenanceRepository dataMaintenanceRepository;


    public Map<String, Object> processExcelFile(MultipartFile file, String userId) {
        if (userId == null || userId.isBlank()) {
            userId = "sistema";
        }
        Map<String, Object> response = new LinkedHashMap<>();
        List<Map<String, Object>> records = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("No se encontraron hojas en el archivo Excel");
            }
            Iterator<Row> rowIterator = sheet.iterator();
            Map<String, Integer> headerIndex = new LinkedHashMap<>();

            
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                int lastCol = headerRow.getLastCellNum();
                for (int i = 0; i < lastCol; i++) {
                    Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell == null) {
                        continue;
                    }
                    String header = getCellValueAsString(cell).trim();
                    if (header.isEmpty()) {
                        continue;
                    }
                    headerIndex.put(header, i);
                }
                if (headerIndex.isEmpty()) {
                    throw new IllegalArgumentException("No se encontraron encabezados válidos en la primera fila del Excel.");
                }
            }


            String tableName ="";
            List<String> conflicts = new ArrayList<>();
            if (headerIndex.containsKey("codescena") && headerIndex.containsKey("descaption")) {
                tableName = "m_escenalibro";
                conflicts.add("codescena");
            }

            else if (headerIndex.containsKey("codsonido") && headerIndex.containsKey("codgenero") && headerIndex.containsKey("desgenero")) {
                tableName = "m_relacionsonidogenero";
                conflicts.add("codsonido");
                conflicts.add("codgenero");
            }

            else if (headerIndex.containsKey("codsonido") && headerIndex.containsKey("urlsonido") && headerIndex.containsKey("codvibe") && headerIndex.containsKey("desvibeemoji") && headerIndex.containsKey("dessonido") && headerIndex.containsKey("descomentario") && headerIndex.containsKey("codestadosonido"))  {
                tableName = "m_sonido";
                conflicts.add("codsonido");
            }

            else if (headerIndex.containsKey("codcuenta") && headerIndex.containsKey("codtipocuenta") && headerIndex.containsKey("codestadocuenta") && headerIndex.containsKey("codtelefono"))  {
                tableName = "m_cuenta";
                conflicts.add("codcuenta");
            }

            else if (headerIndex.containsKey("codusuario") && headerIndex.containsKey("nbusuario") && headerIndex.containsKey("tiprol"))  {
                tableName = "m_usuariorol";
                conflicts.add("codusuario");
            }

            else if (headerIndex.containsKey("codlibro") && headerIndex.containsKey("codtelefono") && headerIndex.containsKey("codcuenta"))  {
                tableName = "m_librotelefonocuenta";
                conflicts.add("codlibro");
                conflicts.add("codtelefono");
                conflicts.add("codcuenta");
            }

            else if (headerIndex.containsKey("codlibro") && headerIndex.containsKey("codhashtag") && headerIndex.containsKey("deshashtag"))  {
                tableName = "m_librohashtag";
                conflicts.add("codlibro");
                conflicts.add("codhashtag");
            }

            else if (headerIndex.containsKey("tiprol") && headerIndex.containsKey("codmoduloapp"))  {
                tableName = "m_relacionrolmoduloapp";
                conflicts.add("tiprol");
                conflicts.add("codmoduloapp");
            }

            else if (headerIndex.containsKey("codescenasinversion") && headerIndex.containsKey("numversion") && headerIndex.containsKey("numslide") && headerIndex.containsKey("codtexto") && headerIndex.containsKey("destexto")) {
                tableName = "m_escenaslidetexto";
                conflicts.add("codescenasinversion");
                conflicts.add("numversion");
                conflicts.add("numslide");
                conflicts.add("codtexto");
            }

            else if (headerIndex.containsKey("codmes") && headerIndex.containsKey("codautora")) {
                tableName = "m_metapostautora";
                conflicts.add("codautora");
                conflicts.add("codmes");
            }

            else if (headerIndex.containsKey("codmes") && headerIndex.containsKey("codlibro")) {
                tableName = "m_metapostlibro";
                conflicts.add("codlibro");
                conflicts.add("codmes");
            }

            else if (headerIndex.containsKey("codautora") && !headerIndex.containsKey("codlibro") ) {
                tableName = "m_autora";
                conflicts.add("codautora");
            }

            else if (headerIndex.containsKey("codlibro") && headerIndex.containsKey("deslibro") && headerIndex.containsKey("codautora") && headerIndex.containsKey("destropo") && headerIndex.containsKey("desslide1keywordshide") && headerIndex.containsKey("desslide2keywordshide") ) {
                tableName = "m_libro";
                conflicts.add("codlibro");
            }

            else if (headerIndex.containsKey("codparametro") && headerIndex.containsKey("desparametro") && headerIndex.containsKey("numvalorparametro") && headerIndex.containsKey("textvalorparametro")) {
                tableName = "m_parametroordentrabajo";
                conflicts.add("codparametro");
            }

            else if (headerIndex.containsKey("tippublicacion") && headerIndex.containsKey("tipimagenvideo") && headerIndex.containsKey("tipnivelasignacion") ) {
                tableName = "m_elementostipoposteo";
                conflicts.add("tippublicacion");
                conflicts.add("tipimagenvideo");
                conflicts.add("tipnivelasignacion");
            }

            else if (headerIndex.containsKey("codescenasinversion") && headerIndex.containsKey("numversion") && headerIndex.containsKey("tipimagenvideo") && headerIndex.containsKey("urlimagenvideo")) {
                tableName = "m_escenaimagenvideo";
                conflicts.add("codescenasinversion");
                conflicts.add("numversion");
                conflicts.add("tipimagenvideo");
            }

            else if (headerIndex.containsKey("fecinicioperiodometa") && headerIndex.containsKey("fecfinperiodometa")) {
                tableName = "m_metaposteadorasistente";
                conflicts.add("codposteador");
                conflicts.add("fecinicioperiodometa");
                conflicts.add("fecfinperiodometa");
            }

            else if (headerIndex.containsKey("codposteador") && headerIndex.containsKey("dniposteador")) {
                tableName = "m_posteadorasistente";
                conflicts.add("codposteador");
            }

            else if (headerIndex.containsKey("codposteador") && headerIndex.containsKey("codtelefono")) {
                tableName = "m_posteadortelefono";
                conflicts.add("codposteador");
                conflicts.add("codtelefono");
            }

            else if (headerIndex.containsKey("tippublicacion") && headerIndex.containsKey("despost")) {
                tableName = "m_tipopost";
                conflicts.add("tippublicacion");
            }

            else if (headerIndex.containsKey("codlibro") && headerIndex.containsKey("tipimagenvideo") && headerIndex.containsKey("tippublicacion")  && headerIndex.containsKey("codimagenvideo") && headerIndex.containsKey("urlimagenvideo") && headerIndex.containsKey("flgprioridad")) {
                tableName = "m_librotipopostimagenvideo";
                conflicts.add("codlibro");
                conflicts.add("tippublicacion");
                conflicts.add("tipimagenvideo");
            }

            else if (headerIndex.containsKey("codtelefono") && headerIndex.containsKey("tiptelefono")) {
                tableName = "m_telefono";
                conflicts.add("codtelefono");
            }

            else {
                throw new IllegalArgumentException("No valid headers found in the Excel file.");
            }

            Set<String> columnasIgnoradas = new HashSet<>(Arrays.asList("fecreacionregistro", "horacreacionregistro", "fecactualizacionregistro", "horaactualizacionregistro","AUTORA","LIBRO"));
            if ("m_relacionsonidogenero".equalsIgnoreCase(tableName)) {
                columnasIgnoradas.add("urlsonido");
            }
            List<String> errores = new ArrayList<>();
            Set<String> columnasNotNull;
            if ("m_escenaimagenvideo".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codescenasinversion",
                        "numversion",
                        "tipimagenvideo",
                        "urlimagenvideo",
                        "flvigente"
                ));
            } else if ("m_librotipopostimagenvideo".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codlibro",
                        "tippublicacion",
                        "tipimagenvideo",
                        "urlimagenvideo",
                        "flgprioridad",
                        "flvigente"
                ));
            } else if ("m_telefono".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codtelefono", "tiptelefono",
                        "flvigente"
                ));
            } else if ("m_parametroordentrabajo".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codparametro", "desparametro", "flvigente"
                ));
            } else if ("m_elementostipoposteo".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "tippublicacion","tipimagenvideo","tipnivelasignacion","flvigente"
                ));
            }
            else if ("m_libro".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codlibro","deslibro","codautora","flvigente"
                ));
            } else if ("m_librohashtag".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codlibro","codhashtag","deshashtag","flvigente"
                ));
            } else if ("m_sonido".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codsonido","urlsonido","codvibe","desvibeemoji","dessonido","descomentario","codestadosonido"));
            } else if ("m_relacionsonidogenero".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codsonido","codgenero","desgenero","flvigente"));
            } else if ("m_posteadortelefono".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codposteador","codtelefono","flvigente"));
            } else if ("m_librotelefonocuenta".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codlibro","codtelefono","codcuenta","flvigente"));
            } else if ("m_escenalibro".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codescena","codestadoescena"));
            } else if ("m_posteadorasistente".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codposteador","dniposteador","nbposteador","flvigente"));
            } else if ("m_escenaslidetexto".equalsIgnoreCase(tableName)) {
                columnasNotNull = new HashSet<>(Arrays.asList(
                        "codescenasinversion","numversion","numslide","codtexto","destexto","flvigente"));
            }
            else {
                columnasNotNull = Collections.emptySet();
            }


            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (esFilaVacia(row, headerIndex, columnasIgnoradas)) {
                    continue;
                }
                Map<String, Object> record = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> e : headerIndex.entrySet()) {
                    String header = e.getKey();

                    if (columnasIgnoradas.contains(header)) {
                        continue;
                    }

                    int colIndex = e.getValue();
                    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                        Date fecha = cell.getDateCellValue();
                        LocalDate fechaParseada = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        record.put(header, fechaParseada);
                    }
                    else if ("numpostemeta".equalsIgnoreCase(header) || "numposteometa".equalsIgnoreCase(header) || "numversion".equalsIgnoreCase(header) || "codvibe".equalsIgnoreCase(header) || "codgenero".equalsIgnoreCase(header) || "codestadoescena".equalsIgnoreCase(header) || "numslide".equalsIgnoreCase(header) || "codtexto".equalsIgnoreCase(header) || "codsonido".equalsIgnoreCase(header) || "codestadosonido".equalsIgnoreCase(header) || "codtipocuenta".equalsIgnoreCase(header) || "codestadocuenta".equalsIgnoreCase(header)  || "tipimagenvideo".equalsIgnoreCase(header) || "tipnivelasignacion".equalsIgnoreCase(header) || "codhashtag".equalsIgnoreCase(header) || "codparametro".equalsIgnoreCase(header) || "numvalorparametro".equalsIgnoreCase(header) || "codimagenvideo".equalsIgnoreCase(header) || "id".equalsIgnoreCase(header)) {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            record.put(header, (int) cell.getNumericCellValue());
                        } else if (cell.getCellType() == CellType.STRING) {
                            String raw = cell.getStringCellValue().trim();
                            if (raw.isEmpty()) {
                                record.put(header, null); // <-- en vez de 0
                            } else {
                            try {
                                record.put(header, Integer.parseInt(raw));
                            } catch (NumberFormatException ex) {
                                logger.error("Fila " + (row.getRowNum() + 1) + ": '" + header + "' debe ser número entero, vino: '" + raw + "'");
                                errores.add("Fila " + (row.getRowNum() + 1) + ": '" + header + "' debe ser número entero, vino: '" + raw + "'");
                                record.put(header, null);
                                }
                            }
                        } else {
                            record.put(header, null);
                        }
                    } else {
                        String cellValue = getCellValueAsString(cell).trim();
                        record.put(header, cellValue.isEmpty() ? null : cellValue);
                    }
                }

                int filaExcel = row.getRowNum() + 1; // Excel 1-based para el usuario
                List<String> erroresFila = new ArrayList<>();
                for (String col : columnasNotNull) {
                    Object v = record.get(col);
                    boolean vacio = (v == null) || (v instanceof String && ((String) v).trim().isEmpty());
                    if (vacio) {
                        erroresFila.add("Row " + filaExcel + ": Missing value in '" + col + "'. ");
                    }
                }

                if (!erroresFila.isEmpty()) {
                    errores.addAll(erroresFila);
                } else {
                    records.add(record);
                }

            }

            if (!errores.isEmpty()) {
                throw new IllegalArgumentException(String.join("\n", errores));
            }

            List<String> headersFinales = headerIndex.keySet().stream().filter(h -> !columnasIgnoradas.contains(h)).toList();
            response.put("table", tableName);
            response.put("headers", headersFinales);
            response.put("conflict",conflicts);
            response.put("records", records);
            response.put("processedRecords", records.size());


            if (tableName.isEmpty()) {
                throw new IllegalArgumentException("Error: No se detectó una tabla válida.");
            } else if (tableName.equalsIgnoreCase("m_escenaimagenvideo") || tableName.equalsIgnoreCase("m_librotipopostimagenvideo") || tableName.equalsIgnoreCase("m_librotelefonocuenta")) {
                return uploadTempTableService.uploadStaging(response,userId);
            }
            else {
                return dataMaintenanceRepository.uploadRecordsExcelFileConnection(response,userId);
            }
        } catch (Exception e) {
            logger.error("Error procesando el archivo Excel",e);
            throw new ExcelGenerationException("Error procesando el archivo Excel", e);
        }
    }

    // Metodo auxiliar para obtener valores de celdas en formato String
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue().trim();
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(cell.getDateCellValue()); // Devuelve la fecha en formato "yyyy-MM-dd"
            }
            return String.valueOf((int) cell.getNumericCellValue()); // Convierte a entero si es número
        case BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());
        case FORMULA:
            return cell.getCellFormula();
        default:
            return "";
    }
    }


    private boolean esFilaVacia(Row row, Map<String, Integer> headerIndex, Set<String> columnasIgnoradas) {
        if (row == null) {
            return true;
        }

        for (Map.Entry<String, Integer> e : headerIndex.entrySet()) {
            String header = e.getKey();

            if (columnasIgnoradas.contains(header)) {
                continue;
            }

            int colIndex = e.getValue();
            Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

            if (cell == null) {
                continue;
            }

            if (cell.getCellType() == CellType.STRING) {
                if (!cell.getStringCellValue().trim().isEmpty()) {
                    return false;
                }
            } else if (cell.getCellType() == CellType.NUMERIC) {
                return false;
            } else if (cell.getCellType() == CellType.BOOLEAN) {
                return false;
            } else if (cell.getCellType() == CellType.FORMULA) {
                String valor = getCellValueAsString(cell).trim();
                if (!valor.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

}
