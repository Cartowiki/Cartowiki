declare var window: any;

export const environment = {
  production: false,
  API_URL: window.__env?.API_URL || '',
};
