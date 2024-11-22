package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.dto.PokemonDto;
import org.example.service.PokemonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class PokemonController {
  private final PokemonService pokemonService;

  @GetMapping("/pokemon/{name}")
  public ResponseEntity<PokemonDto> getPokemon(@PathVariable(value = "name") String name) {
    return ResponseEntity.ok(pokemonService.getPokemon(name));
  }
}
