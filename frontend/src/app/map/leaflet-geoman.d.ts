import * as L from 'leaflet';

declare module 'leaflet' {
  interface Map {
    pm: any;
  }
}
