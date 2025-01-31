import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
    // Créer la carte
    const map = L.map('map', {
      center: [51.505, -0.09],  // Coordonnées initiales de la carte
      zoom: 3  // Niveau de zoom initial pour inclure plusieurs pays
    });

    // Ajouter la couche OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);

    // URL du service WFS pour les villes (GeoServer)
    const wfsCitiesUrl = 'http://localhost:8080/geoserver/cartowiki/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=cartowiki:cities&outputFormat=application/json';

    // URL du service WFS pour les pays (GeoServer)
    const wfsCountriesUrl = 'http://localhost:8080/geoserver/cartowiki/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=cartowiki:countries&outputFormat=application/json';

    // Charger les données des pays en GeoJSON et les ajouter à la carte
  //   fetch(wfsCountriesUrl)
  //     .then(response => response.json())
  //     .then(data => {
  //       // Ajouter les pays (multipolygones) sur la carte
  //       L.geoJSON(data, {
  //         style: {
  //           color: '#0000FF',  // Couleur des frontières des pays
  //           weight: 2,
  //           opacity: 1,
  //           fillColor: '#0000FF',  // Couleur de remplissage des pays
  //           fillOpacity: 0.3
  //         }
  //       }).addTo(map);
  //     })
  //     .catch(error => console.error('Erreur lors du chargement des pays:', error));

  //   // Charger les données des villes en GeoJSON et les ajouter à la carte
  //   fetch(wfsCitiesUrl)
  //     .then(response => response.json())
  //     .then(data => {
  //       // Ajouter les points GeoJSON sur la carte pour les villes
  //       L.geoJSON(data, {
  //         pointToLayer: (feature, latlng) => {
  //           return L.circleMarker(latlng, {
  //             radius: 8,
  //             fillColor: '#FF0000',  // Couleur du point
  //             color: '#FFFFFF',      // Couleur du contour
  //             weight: 2,             // Largeur du contour
  //             opacity: 1,
  //             fillOpacity: 0.8
  //           });
  //         }
  //       }).addTo(map);
  //     })
  //     .catch(error => console.error('Erreur lors du chargement des villes:', error));
  }
}
