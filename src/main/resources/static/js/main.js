import { handleRoute } from './router.js';


const appRoot = document.getElementById('app-root');

// イベントリスナーの登録
window.addEventListener('hashchange', () => handleRoute(appRoot)); // ハッシュが変わった時
window.addEventListener('load', () => handleRoute(appRoot));      // ページを読み込んだ時

