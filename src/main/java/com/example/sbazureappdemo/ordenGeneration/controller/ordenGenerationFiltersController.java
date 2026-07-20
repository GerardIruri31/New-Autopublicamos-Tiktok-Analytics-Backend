package com.example.sbazureappdemo.ordenGeneration.controller;

import com.example.sbazureappdemo.ordenGeneration.dto.*;
import com.example.sbazureappdemo.ordenGeneration.service.OrdenGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/orden/filter")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ordenGenerationFiltersController {
    Logger logger = LoggerFactory.getLogger(ordenGenerationFiltersController.class);
    private final OrdenGenerationService ordenGenerationService;

    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @GetMapping("/pa")
    public ResponseEntity<List<PaResponseDTO>> selectPA() {
        logger.info("Iniciando obtención datos PA disponibles ... ");
        return ResponseEntity.ok(ordenGenerationService.selectPA());
    }

    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @GetMapping("/telephone")
    public ResponseEntity<List<TelephoneResponseDTO>> selectTelephone(@RequestParam String PaCode) {
        logger.info("Iniciando obtención datos Telephone disponibles ... ");
        return ResponseEntity.ok(ordenGenerationService.selectTelephone(PaCode));
    }

    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @PostMapping("/author")
    public ResponseEntity<List<AuthorResponseDTO>> selectAuthor(@RequestBody AuthorRequestDTO requestDTO) {
        logger.info("Iniciando obtención datos Author disponibles ... ");
        return ResponseEntity.ok(ordenGenerationService.selectAuthor(requestDTO));
    }

    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @GetMapping("/postType")
    public ResponseEntity<List<PostTypeResponseDTO>> selectPostType(@RequestParam String codlibro) {
        logger.info("Iniciando obtención datos PostType disponibles ... ");
        return ResponseEntity.ok(ordenGenerationService.selectPostType(codlibro));
    }

    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @GetMapping("/scene")
    public ResponseEntity<List<SceneResponseDTO>> selectScene(@ModelAttribute SceneRequestDTO requestDTO) {
        logger.info("Iniciando obtención datos Scene disponibles ... ");
        return ResponseEntity.ok(ordenGenerationService.selectScene(requestDTO));
    }


    @PreAuthorize("hasAnyRole('SUP','ADMIN')")
    @PatchMapping("/complete_flag")
    public ResponseEntity<String> editCompleteOrderFlag(@RequestParam Integer id, @RequestParam Integer flag) {
        logger.info("Iniciando edit complete_order_flag ... ");
        ordenGenerationService.editCompleteOrderFlag(id, flag);
        return ResponseEntity.ok("complete_order_flag edited successfully");
    }


    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @GetMapping("/required_images")
    public ResponseEntity<List<ImagesVideosPerTipPublicacionDTO>> requiredImagesPerTipPublicacion() {
        logger.info("Iniciando validación tipos imagen-videos por tippublicacion ... ");
        return ResponseEntity.ok(ordenGenerationService.requiredImagesPerTipPublicacion());
    }


    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponseDTO>> selectAccounts(@RequestParam String tiptelefono, @RequestParam String codtelefono) {
        logger.info("Iniciando select de accounts ... ");
        return ResponseEntity.ok(ordenGenerationService.selectAccounts(tiptelefono,codtelefono));
    }


}

