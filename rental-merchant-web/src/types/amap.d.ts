// 高德地图API类型声明
declare global {
  interface Window {
    AMap: typeof AMap;
  }
}

declare namespace AMap {
  class Map {
    constructor(container: string | HTMLElement, options?: MapOptions);
    on(event: string, handler: Function): void;
    add(overlay: any): void;
    remove(overlay: any): void;
    setFitView(overlays?: any[]): void;
  }

  class Polygon {
    constructor(options: PolygonOptions);
  }

  interface MapOptions {
    zoom?: number;
    center?: [number, number];
  }

  interface PolygonOptions {
    path: [number, number][];
    strokeColor?: string;
    strokeWeight?: number;
    strokeOpacity?: number;
    fillOpacity?: number;
    fillColor?: string;
    zIndex?: number;
  }
}

export {};