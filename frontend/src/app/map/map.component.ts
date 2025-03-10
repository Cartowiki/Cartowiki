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
  private countryLayer!: L.GeoJSON;

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
    // MouseEvent.mozPressure est obsolète. Veuillez utiliser plutôt PointerEvent.pressure. leaflet-src.js:28:5
    // MouseEvent.mozInputSource est obsolète. Veuillez utiliser plutôt PointerEvent.pointerType.

    // ✅ Fond de carte OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);
  }

  private loadTiles(year: number): void {
    // ✅ Suppression des anciennes couches
    if (this.cityLayer) this.map.removeLayer(this.cityLayer);
    if (this.countryLayer) this.map.removeLayer(this.countryLayer);

    console.log(`Chargement des tuiles pour l'année ${year}`);

    // ✅ Utilisation de GeoWebCache (GWC) pour un chargement rapide
    // this.cityLayer = L.tileLayer(`http://localhost:8081/api/geoserver/tiles/cities/{year}/{z}/{x}/{y}.png`, {
    //   tileSize: 256,
    //   attribution: "© CartoWiki",
    //   opacity: 0.7
    // }).addTo(this.map);
    // URL de la requête WFS avec le paramètre year
    const wfsUrl = `http://localhost:8081/api/geojson?year=${year}`;

    // Fetch GeoJSON data from the backend
    fetch(wfsUrl)
    .then(response => {
      if (!response.ok) {
        throw new Error('Failed to fetch GeoJSON data');
      }

      const reader = response.body?.getReader(); // Get the reader for streaming
      const decoder = new TextDecoder(); // Decode chunks of data as text
      let jsonData = ''; // Buffer for the GeoJSON data

      // Read and process chunks of data
      const processStream = async () => {
        while (true) {
          const { done, value } = await reader?.read()!;
          if (done) break;

          // Append chunk to the full GeoJSON string
          jsonData += decoder.decode(value, { stream: true });

          // Check if we have a complete GeoJSON object and parse it
          try {
            const data = JSON.parse(jsonData);

            // Add the parsed GeoJSON to the map
            this.countryLayer = L.geoJSON(data, {
              style: {
                color: 'blue',
                weight: 2,
                opacity: 0.7
              }
            }).addTo(this.map);
            console.log(`Tiles loaded for the year ${year}`);
          } catch (e) {
            // If not a complete JSON, continue reading
            continue;
          }
        }
      };

      // Process the stream asynchronously
      processStream().catch(error => {
        console.error('Error processing the stream:', error);
      });
    })
    .catch(error => {
      console.error('Error during the fetch:', error);
    });
  }

  private addGeomanControls(): void {
    console.log('Ajout des contrôles Geoman');
    // Add Geoman controls to the map
    this.map.pm.addControls({
      position: 'topleft',
      drawCircle: false,
      drawCircleMarker: false,
      drawPolyline: false,
      drawRectangle: false,
      drawMarker: false,
      drawPolygon: true,
      editMode: true,
      dragMode: true,
      cutPolygon: true,
      removalMode: true,
      rotateMode: false,
      drawText: false
    });

  }

  // ✅ Sélection dynamique de l'année pour changer les tuiles
  public updateYear(event: Event): void {
    const selectedYear = (event.target as HTMLSelectElement).value;
    this.year = parseInt(selectedYear, 10);
    this.loadTiles(this.year);
  }
}
