import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';


platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));

fetch('/assets/env.js')
  .then(response => response.text())
  .then(jsCode => {
    eval(jsCode);
    platformBrowserDynamic().bootstrapModule(AppModule)
      .catch(err => console.error(err));
  });