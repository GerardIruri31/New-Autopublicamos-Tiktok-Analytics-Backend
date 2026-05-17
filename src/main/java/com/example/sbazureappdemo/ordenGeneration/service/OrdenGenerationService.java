package com.example.sbazureappdemo.ordenGeneration.service;


import com.example.sbazureappdemo.ordenGeneration.dto.*;
import com.example.sbazureappdemo.ordenGeneration.repository.OrdenGenerationRepository;
import com.example.sbazureappdemo.orderQueries.dto.QueryResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenGenerationService {
    Logger logger = LoggerFactory.getLogger(OrdenGenerationService.class);
    private final OrdenGenerationRepository ordenGenerationRepository;

    public List<PaResponseDTO> selectPA() {
        return ordenGenerationRepository.selectPA();
    }

    public List<TelephoneResponseDTO> selectTelephone(String PaCode) {
        return ordenGenerationRepository.selectTelephone(PaCode);
    }

    public List<AuthorResponseDTO> selectAuthor(AuthorRequestDTO requestDTO) {
        String codposteador = requestDTO.getCodposteador();
        String codtelefono = requestDTO.getCodtelefono();
        String tiptelefono = requestDTO.getTiptelefono();
        boolean tieneTelefono = codtelefono != null && !codtelefono.isBlank();
        boolean esTelefonoSoporte = tiptelefono != null && (tiptelefono.equalsIgnoreCase("SOP") || tiptelefono.equalsIgnoreCase("SOP2"));

        if (tieneTelefono && esTelefonoSoporte) {
            return ordenGenerationRepository.selectAuthorBySupportPhone(codtelefono);
        }
        if (!tieneTelefono) {
            return ordenGenerationRepository.selectAuthorByPaWithoutPhone(codposteador);
        }
        return ordenGenerationRepository.selectAuthorByNormalPhone(codtelefono, codposteador);
    }

    public List<PostTypeResponseDTO> selectPostType(String codlibro) {
        return ordenGenerationRepository.selectPostType(codlibro);
    }

    public List<SceneResponseDTO> selectScene(SceneRequestDTO requestDTO) {
        return ordenGenerationRepository.selectScene(requestDTO);
    }

    public OrderGenerationDetailResponseDTO manualOrder(NewManualOrderRequestDTO requestDTO) {
        // proceso de imagenes
        imagesVideoDTO dto = ordenGenerationRepository.insertImagesVideo(requestDTO.getTippublicacion(), requestDTO.getCorreo(), requestDTO.getCodimagenprincipal(), requestDTO.getCodimagendialogo(),requestDTO.getCodimagenscreenshot(),requestDTO.getCodvideo(),requestDTO.getCodescena(), requestDTO.getCodlibro());
        if (dto.getCoderror() != null || dto.getDeserror() != null) {
            throw new DataRetrievalFailureException("Error al insertar imagenes-videos: " + "coderror: " + dto.getCoderror() + " | deserror: " + dto.getDeserror());
        }
        // insert manual de orden
        List<String> columnas = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        List<Object> valores = new ArrayList<>();

        addIfValue(columnas, placeholders, valores, "codposteadorauto", requestDTO.getCodposteador());
        addIfValue(columnas, placeholders, valores, "codposteador", requestDTO.getCodposteador());

        addIfValue(columnas, placeholders, valores, "codtelefono", requestDTO.getCodtelefono());
        addIfValue(columnas, placeholders, valores, "codautora", requestDTO.getCodautora());
        addIfValue(columnas, placeholders, valores, "codlibro", requestDTO.getCodlibro());
        //addIfValue(columnas, placeholders, valores, "tippublicacion", requestDTO.getTippublicacion());

        addIfValue(columnas, placeholders, valores, "codescenaauto", requestDTO.getCodescena());
        addIfValue(columnas, placeholders, valores, "codescena", requestDTO.getCodescena());

        addIfValue(columnas, placeholders, valores, "codcuentatiktokauto", requestDTO.getCodcuentatiktok());
        addIfValue(columnas, placeholders, valores, "codcuentatiktok", requestDTO.getCodcuentatiktok());


        addIfValue(columnas, placeholders, valores, "codsonidoauto", requestDTO.getCodsonido());
        addIfValue(columnas, placeholders, valores, "codsonido", requestDTO.getCodsonido());

        addIfValue(columnas, placeholders, valores, "desscenahookauto", requestDTO.getDesscenahook());
        addIfValue(columnas, placeholders, valores, "desscenahook", requestDTO.getDesscenahook());

        addIfValue(columnas, placeholders, valores, "descaptionauto", requestDTO.getDescaption());
        addIfValue(columnas, placeholders, valores, "descaption", requestDTO.getDescaption());

        addIfValue(columnas, placeholders, valores, "destropoauto", requestDTO.getDestropo());
        addIfValue(columnas, placeholders, valores, "destropo", requestDTO.getDestropo());

        addIfValue(columnas, placeholders, valores, "desslide1keywordshideauto", requestDTO.getDesslide1keywordshide());
        addIfValue(columnas, placeholders, valores, "desslide1keywordshide", requestDTO.getDesslide1keywordshide());

        addIfValue(columnas, placeholders, valores, "desslide2keywordshideauto", requestDTO.getDesslide2keywordshide());
        addIfValue(columnas, placeholders, valores, "desslide2keywordshide", requestDTO.getDesslide2keywordshide());

        addIfValue(columnas, placeholders, valores, "deshashtagauto", requestDTO.getDeshashtag());
        addIfValue(columnas, placeholders, valores, "deshashtag", requestDTO.getDeshashtag());

        addIfValue(columnas, placeholders, valores, "despaloteauto", requestDTO.getDespalote());
        addIfValue(columnas, placeholders, valores, "despalote", requestDTO.getDespalote());

        addIfValue(columnas, placeholders, valores, "codimagenprincipalauto", dto.getCodimagenprincipal());
        addIfValue(columnas, placeholders, valores, "codimagenprincipal", dto.getCodimagenprincipal());

        addIfValue(columnas, placeholders, valores, "codimagenscreenshotauto", dto.getCodimagenscreenshot());
        addIfValue(columnas, placeholders, valores, "codimagenscreenshot", dto.getCodimagenscreenshot());

        addIfValue(columnas, placeholders, valores, "codimagendialogoauto", dto.getCodimagendialogo());
        addIfValue(columnas, placeholders, valores, "codimagendialogo", dto.getCodimagendialogo());

        addIfValue(columnas, placeholders, valores, "codvideoauto", dto.getCodvideo());
        addIfValue(columnas, placeholders, valores, "codvideo", dto.getCodvideo());


        addIfValue(columnas, placeholders, valores, "desinstrucciones", requestDTO.getDesinstrucciones());

        addIfValue(columnas, placeholders, valores, "fecplanposteoauto", requestDTO.getFecplanposteo());
        addIfValue(columnas, placeholders, valores, "fecplanposteo", requestDTO.getFecplanposteo());

        addIfValue(columnas, placeholders, valores, "codestadoorden", 1);
        addIfValue(columnas, placeholders, valores, "tipregistroorden", "MANUAL");
        addIfValue(columnas, placeholders, valores, "flgordencompleta", "S");
        addIfValue(columnas, placeholders, valores, "desdatoobligincompleto", "no observations");
        addIfValue(columnas, placeholders, valores, "codusuarioauditoriacreareg", requestDTO.getCorreo());
        columnas.add("fecreacionregistro");
        placeholders.add("CURRENT_DATE");
        columnas.add("horacreacionregistro");
        placeholders.add("LOCALTIME");
        Long codordentrabajo = ordenGenerationRepository.manualOrder(columnas,placeholders,valores);
        List<OrderGenerationDetailResponseDTO> resultado = ordenGenerationRepository.selectAutoGeneration(null, codordentrabajo);
        if (resultado.isEmpty()) {
            throw new RuntimeException("No se encontró la orden con codordentrabajo: " + resultado.get(0).getCodordentrabajo());
        }
        return resultado.get(0);
    }

    public OrderGenerationDetailResponseDTO editOrder(NewManualOrderRequestDTO requestDTO, Long codordentrabajo) {
        // proceso de imagenes
        imagesVideoDTO dto = ordenGenerationRepository.insertImagesVideo(requestDTO.getTippublicacion(), requestDTO.getCorreo() ,requestDTO.getCodimagenprincipal(), requestDTO.getCodimagendialogo(),requestDTO.getCodimagenscreenshot(),requestDTO.getCodvideo(),requestDTO.getCodescena(), requestDTO.getCodlibro());
        System.out.println("insertImagesVideo result => " + dto);
        if (dto.getCoderror() != null || dto.getDeserror() != null) {
            throw new DataRetrievalFailureException("Error al insertar imagenes-videos en orden: " + codordentrabajo + " | coderror: " + dto.getCoderror() + " | deserror: " + dto.getDeserror());
        }
        // insert manual de orden
        List<String> columnas = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        List<Object> valores = new ArrayList<>();

        addManualOverride(columnas, placeholders, valores, "codposteador", "codposteadormanual", requestDTO.getCodposteador());
        addIfValue(columnas, placeholders, valores, "codtelefono", requestDTO.getCodtelefono());
        addIfValue(columnas, placeholders, valores, "codautora", requestDTO.getCodautora());
        addIfValue(columnas, placeholders, valores, "codlibro", requestDTO.getCodlibro());
        //addIfValue(columnas, placeholders, valores, "tippublicacion", requestDTO.getTippublicacion());
        addManualOverride(columnas, placeholders, valores, "codescena", "codescenamanual", requestDTO.getCodescena());
        addManualOverride(columnas, placeholders, valores, "codcuentatiktok", "codcuentatiktokmanual", requestDTO.getCodcuentatiktok());
        addManualOverride(columnas, placeholders, valores, "codsonido", "codsonidomanual", requestDTO.getCodsonido());
        addManualOverride(columnas, placeholders, valores, "desscenahook", "desscenahookmanual", requestDTO.getDesscenahook());
        addManualOverride(columnas, placeholders, valores, "descaption", "descaptionmanual", requestDTO.getDescaption());
        addManualOverride(columnas, placeholders, valores, "destropo", "destropomanual", requestDTO.getDestropo());
        addManualOverride(columnas, placeholders, valores, "desslide1keywordshide", "desslide1keywordshidemanual", requestDTO.getDesslide1keywordshide());
        addManualOverride(columnas, placeholders, valores, "desslide2keywordshide", "desslide2keywordshidemanual", requestDTO.getDesslide2keywordshide());
        addManualOverride(columnas, placeholders, valores, "deshashtag", "deshashtagmanual", requestDTO.getDeshashtag());
        addManualOverride(columnas, placeholders, valores, "despalote", "despalotemanual", requestDTO.getDespalote());
        addManualOverrideIfPositive(columnas, placeholders, valores, "codimagenprincipal", "codimagenprincipalmanual", dto.getCodimagenprincipal());
        addManualOverrideIfPositive(columnas, placeholders, valores, "codimagenscreenshot", "codimagenscreenshotmanual", dto.getCodimagenscreenshot());
        addManualOverrideIfPositive(columnas, placeholders, valores, "codimagendialogo", "codimagendialogomanual", dto.getCodimagendialogo());
        addManualOverrideIfPositive(columnas, placeholders, valores, "codvideo", "codvideomanual", dto.getCodvideo());
        addIfValue(columnas, placeholders, valores, "desinstrucciones", requestDTO.getDesinstrucciones());

        addManualOverride(
                columnas,
                placeholders,
                valores,
                "fecplanposteo",
                "fecplanposteomanual",
                requestDTO.getFecplanposteo() == null
                        ? null
                        : java.sql.Date.valueOf(requestDTO.getFecplanposteo())
        );
        addIfValue(columnas,placeholders,valores,"flgordencompleta","S");
        addIfValue(columnas,placeholders,valores,"ctddatoobligincompleto",0);
        addIfValue(columnas, placeholders, valores, "codestadoorden", requestDTO.getCodestadoorden());
        addIfValue(columnas, placeholders, valores, "desdatoobligincompleto", "no observations");
        addIfValue(columnas, placeholders, valores, "codusuarioauditoriaactualizareg", requestDTO.getCorreo());
        columnas.add("fecactualizacionregistro");
        placeholders.add("CURRENT_DATE");
        columnas.add("horaactualizacionregistro");
        placeholders.add("LOCALTIME");

        ordenGenerationRepository.editOrder(codordentrabajo, columnas,placeholders,valores);
        List<OrderGenerationDetailResponseDTO> resultado = ordenGenerationRepository.selectAutoGeneration(null, codordentrabajo);
        if (resultado.isEmpty()) {
            throw new RuntimeException("No se encontró la orden con codordentrabajo: " + codordentrabajo);
        }
        return resultado.get(0);
    }

    private void addManualOverrideIfPositive(
            List<String> columnas,
            List<String> placeholders,
            List<Object> valores,
            String columnaFinal,
            String columnaManual,
            Integer valor
    ) {
        if (valor == null || valor == 0) {
            return;
        }

        addManualOverride(columnas, placeholders, valores, columnaFinal, columnaManual, valor);
    }

    private void addManualOverride(List<String> columnas, List<String> placeholders, List<Object> valores, String campoFinal, String campoManual, Object valor) {
        if (valor != null) {
            columnas.add(campoFinal);
            placeholders.add("?");
            valores.add(valor);

            columnas.add(campoManual);
            placeholders.add("?");
            valores.add(valor);
        }
    }




    public void editCompleteOrderFlag(Integer id, Integer flag) {
        Integer flag_final = flag != null ? flag : 1;
        ordenGenerationRepository.editCompleteOrderFlag(id,flag_final);
    }

    private void addIfValue(List<String> columnas, List<String> placeholders, List<Object> valores, String columna, Object valor) {
        if (valor == null) {
            return;
        }
        if (valor instanceof String texto) {
            if (texto.trim().isEmpty()) {
                return;
            }
            columnas.add(columna);
            placeholders.add("?");
            valores.add(texto.trim());
            return;
        }

        columnas.add(columna);
        placeholders.add("?");
        valores.add(valor);
    }


    public AutoGenerationResultDTO autoGeneration(FiltersRequestDTO request) {
        logger.info("autoGeneration request recibido: correo={}, codposteador={}, codtelefono={}, codautora={}, codlibro={}, tippublicacion={}, codescena={}, fecinicioplanposteo={}, fecfinplanposteo={}, ctdordenesmetamanual={}",
                request.getCorreo(),
                request.getCodposteador(),
                request.getCodtelefono(),
                request.getCodautora(),
                request.getCodlibro(),
                request.getTippublicacion(),
                request.getCodescena(),
                request.getFecinicioplanposteo(),
                request.getFecfinplanposteo(),
                request.getCtdordenesmetamanual()
        );

        AutoGenerationResponseDTO dto = ordenGenerationRepository.autoGeneration(request);

        logger.info("Respuesta SP autoGeneration: codcabeceraordentrabajo={}, ctdordenes={}, ctdordenescompleta={}, ctdordenesincompleta={}, msj_error_log={}",
                dto != null ? dto.getCodcabeceraordentrabajo() : null,
                dto != null ? dto.getCtdordenes() : null,
                dto != null ? dto.getCtdordenescompleta() : null,
                dto != null ? dto.getCtdordenesincompleta() : null,
                dto != null ? dto.getMsj_error_log() : null
        );

        if (dto == null) {
            throw new IllegalStateException("El procedimiento sp_orquestar_generacion_ordenes_automaticas no devolvió respuesta.");
        }
        if (dto.getCodcabeceraordentrabajo() == null) {
            throw new IllegalStateException("El procedimiento no devolvió codcabeceraordentrabajo.");
        }

        List<OrderGenerationDetailResponseDTO> ordenes = ordenGenerationRepository.selectAutoGeneration(dto.getCodcabeceraordentrabajo(),null);
        logger.info("Resultado selectAutoGeneration para codcabeceraordentrabajo={}: total ordenes={}",
                dto.getCodcabeceraordentrabajo(),
                ordenes != null ? ordenes.size() : null
        );

        if (ordenes != null && !ordenes.isEmpty()) {
            OrderGenerationDetailResponseDTO primera = ordenes.get(0);
            logger.info("Primera orden encontrada: codordentrabajo={}, codcabeceraordentrabajo={}, codautora={}, codlibro={}, codescena={}, codposteador={}, codtelefono={}, fecplanposteo={}",
                    primera.getCodordentrabajo(),
                    primera.getCodcabeceraordentrabajo(),
                    primera.getCodautora(),
                    primera.getCodlibro(),
                    primera.getCodescena(),
                    primera.getCodposteador(),
                    primera.getCodtelefono(),
                    primera.getFecplanposteo()
            );
        } else {
            logger.warn("selectAutoGeneration devolvió lista vacía para codcabeceraordentrabajo={}", dto.getCodcabeceraordentrabajo());
        }

        AutoGenerationResultDTO result = new AutoGenerationResultDTO(
                dto.getCodcabeceraordentrabajo(),
                dto.getCtdordenes(),
                dto.getCtdordenescompleta(),
                dto.getCtdordenesincompleta(),
                dto.getMsj_error_log(),
                ordenes
        );

        logger.info("Respuesta final al frontend: codcabeceraordentrabajo={}, ctdordenes={}, ctdordenescompleta={}, ctdordenesincompleta={}, msj_error_log={}, totalOrdenesEnLista={}",
                result.getCodcabeceraordentrabajo(),
                result.getCtdordenes(),
                result.getCtdordenescompleta(),
                result.getCtdordenesincompleta(),
                result.getMsj_error_log(),
                result.getOrdenes() != null ? result.getOrdenes().size() : null
        );
        return result;
    }


    public List<ImagesVideosPerTipPublicacionDTO> requiredImagesPerTipPublicacion() {
        return ordenGenerationRepository.requiredImagesPerTipPublicacion();
    }


}
