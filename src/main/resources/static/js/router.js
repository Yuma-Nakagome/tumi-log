import { renderHome } from './views/home.js';
import { renderLog } from './views/log.js';
import { renderAdd } from './views/add.js';
import { renderStats } from './views/stats.js';
import { renderSettings } from './views/setting.js';

export async function handleRoute(appRoot) {
    const hash = window.location.hash || '#home';
    if (hash === '#home') {
        appRoot.innerHTML = await renderHome();
    } else if (hash === '#log') {
        appRoot.innerHTML = await renderLog();
    } else if (hash === '#add') {
        appRoot.innerHTML = await renderAdd();
    } else if (hash === '#stats') {
        appRoot.innerHTML = await renderStats();
    } else if (hash === '#settings') {
        appRoot.innerHTML = await renderSettings();
    }
}
