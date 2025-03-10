package com.cartowiki.webapp;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api")
public class GeoController {
    // private static Logger logger = LoggerFactory.getLogger(GeoController.class);
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

    @GetMapping("/hello2")
    public String helloWorld2() {
        // logger.info("Hello World!");
        return "Hello World!";
    }

    // Endpoint pour récupérer le GeoJSON depuis le serveur externe
    @GetMapping("/geojson")
    public ResponseEntity<String> getGeoJson(@RequestParam("year") String year) {
        // Construire l'URL avec l'année spécifiée
        String url = geoserverUrl + "/cartowiki/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cartowiki:cities&outputFormat=application/json&viewparams=year:" + year;
        // System.out.println(url);

        // Effectuer la requête GET avec WebClient
        String geoJsonResponse = webClient.get()
                                          .uri(url) // Injecter dynamiquement l'URL
                                          .retrieve()
                                          .bodyToMono(String.class)
                                          .block(); // Bloquer ici pour attendre la réponse
        
        // Retourner la réponse GeoJSON comme ResponseEntity
        // System.out.println(geoJsonResponse);
        return ResponseEntity.ok(geoJsonResponse);
    }

    @GetMapping("/geojson2")
    public ResponseEntity<String> getGeoJson2(@RequestParam("year") String year) {
        // Construire l'URL avec l'année spécifiée
        String url = "http://localhost:8080/geoserver/cartowiki/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cartowiki:cities&outputFormat=application/json&viewparams=year:" + year;
        logger.info(url);

        // Effectuer la requête GET avec WebClient
        String geoJsonResponse = webClient.get()
                                          .uri(url) // Injecter dynamiquement l'URL
                                          .retrieve()
                                          .bodyToMono(String.class)
                                          .block(); // Bloquer ici pour attendre la réponse
        
        // Retourner la réponse GeoJSON comme ResponseEntity
        logger.info(geoJsonResponse);
        return ResponseEntity.ok(geoJsonResponse);
    }
}
