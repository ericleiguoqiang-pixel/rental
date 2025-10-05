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
    off(event: string, handler: Function): void;
    add(overlay: any): void;
    remove(overlay: any): void;
    setFitView(overlays?: any[]): void;
    destroy(): void;
    setCenter(center: [number, number]): void;
  }

  class Marker {
    constructor(options?: MarkerOptions);
    on(event: string, handler: Function): void;
    setPosition(position: [number, number]): void;
    getPosition(): any;
    setDraggable(draggable: boolean): void;
    destroy(): void;
  }

  interface MapOptions {
    zoom?: number;
    center?: [number, number];
  }

  interface MarkerOptions {
    position?: [number, number];
    draggable?: boolean;
    cursor?: string;
  }
}

export {};