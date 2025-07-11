package com.romantrippel.gpt_service.dto;

import java.util.List;

public record TranslationResponse(String translation, List<Example> examples) {
  public record Example(String en, String ru) {}
}
