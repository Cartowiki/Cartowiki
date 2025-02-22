import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import '@geoman-io/leaflet-geoman-free';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {
  private map!: L.Map;
  private year: number = 2023; // Année par défaut
  private cityLayer!: L.TileLayer;

  ngOnInit(): void {
    this.initMap();
    this.loadTiles(this.year);
    this.addGeomanControls();
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: [20, 0], // Centrage optimisé
      zoom: 3
    });

    // ✅ Fond de carte OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);
  }

  private loadTiles(year: number): void {
    // ✅ Suppression des anciennes couches
    if (this.cityLayer) this.map.removeLayer(this.cityLayer);

    // ✅ Utilisation de GeoWebCache (GWC) pour un chargement rapide
    this.cityLayer = L.tileLayer(`http://localhost:8081/api/geoserver/tiles/cities/{year}/{z}/{x}/{y}.png`, {
      tileSize: 256,
      attribution: "© CartoWiki",
      opacity: 0.7
    }).addTo(this.map);
  }

  private addGeomanControls(): void {
    // Add Geoman controls to the map
    this.map.pm.addControls({
      position: 'topleft',
      drawMarker: true,
      drawPolygon: true,
      editMode: true,
      dragMode: true,
      cutPolygon: true,
      removalMode: true
    });
  }

  // ✅ Sélection dynamique de l'année pour changer les tuiles
  public updateYear(event: Event): void {
    const selectedYear = (event.target as HTMLSelectElement).value;
    this.year = parseInt(selectedYear, 10);
    this.loadTiles(this.year);
  }
}
