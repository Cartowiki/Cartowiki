package com.cartowiki.webapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/api")
public class GeoController {
    private final WebClient webClient;

    @Value("${geoserver.url}")
    private String geoserverUrl;

    // Constructeur pour injecter WebClient
    public GeoController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/hello")
    public String helloWorld() {
        System.out.println("Hello World!");
        return "Hello World!";
    }

    @GetMapping(value = "/geojson", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<String>> getGeoJson(@RequestParam("year") String year) {
        String url = geoserverUrl + "/cartowiki/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cartowiki:cities&outputFormat=application/json&viewparams=year:" + year;

        // Effectuer la requête GET et récupérer la réponse en Flux<DataBuffer>
        Flux<String> geoJsonResponse = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(DataBuffer.class) // Flux de DataBuffer
                .map(dataBuffer -> {
                    // Convertir chaque DataBuffer en String (en utilisant un TextDecoder ou en convertissant directement)
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    return new String(bytes, StandardCharsets.UTF_8); // Convertir en String UTF-8
                });

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(geoJsonResponse.map(s -> {
                    byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                    return s;
                }));
    }
}
