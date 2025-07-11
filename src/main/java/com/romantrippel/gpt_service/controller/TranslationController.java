package com.romantrippel.gpt_service.controller;

import com.romantrippel.gpt_service.dto.TranslationRequest;
import com.romantrippel.gpt_service.dto.TranslationResponse;
import com.romantrippel.gpt_service.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/translate")
@RequiredArgsConstructor
public class TranslationController {

  private final TranslationService translationService;

  @PostMapping
  public Mono<ResponseEntity<TranslationResponse>> translate(
      @RequestBody TranslationRequest request) {
    return translationService.translateWord(request.word()).map(ResponseEntity::ok);
  }
}
