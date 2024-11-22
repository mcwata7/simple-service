package org.example.service;

import org.example.dto.PokemonDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PokemonService {
  private final RestClient restClient;
  @Value("${spring.main.urls.poke-url}")
  private String baseUrl;

  public PokemonService() {
    this.restClient = RestClient.builder().baseUrl(this.baseUrl).build();
  }

  public PokemonDto getPokemon(String name) {
    return restClient
            .get()
            .uri(this.baseUrl + "/{name}", name)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(PokemonDto.class);
  }
}
