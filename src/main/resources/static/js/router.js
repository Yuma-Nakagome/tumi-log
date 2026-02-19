// 各画面を表示するための「レンダリング関数」をインポート
import { renderHome } from './views/home.js';
// 記録一覧画面を表示する関数をインポート
import { renderLog } from './views/log.js';
// 記録追加画面を表示する関数をインポート
import { renderAdd } from './views/add.js';
// ログイン画面の表示と初期化（イベント設定）をインポート
import { renderLogin, initLogin } from './views/login.js';
// 登録画面の表示と初期化をインポート
import { renderRegister, initRegister } from './views/register.js';
// 設定画面の表示と初期化をインポート
import { renderSettings, initSettings } from './views/setting.js';
// 現在のログイン状態を確認する共通関数をインポート
import { checkAuth } from './api.js';
import { renderDetails } from './views/details.js';

// URLのハッシュ（#以降）の変化に合わせて、appRootの中身を書き換えるメイン関数
export async function handleRoute(appRoot) {
    // 現在のハッシュを取得し、何もなければ「#home」をデフォルトとする
    const fullHash = window.location.hash || '#home';
    // "?" で分割して、[0]にハッシュ名、[1]にパラメータを入れる
    const [hash, queryString] = fullHash.split('?');

    // パラメータを使いやすいように解析する
    const params = new URLSearchParams(queryString);
    const selectedDate = params.get('date');

    // ログインしていないと閲覧できない「保護されたページ」のリスト
    const isPrivate = ['#home', '#log', '#add', '#stats', '#settings'].includes(hash);

    // サーバーに問い合わせて、現在のログインユーザー情報を取得
    const user = await checkAuth();

    // 「ログイン必須のページ」なのに「ログインしていない（userがnull）」場合
    if (isPrivate && !user) {
        // 強制的にURLをログイン画面（#login）に書き換える
        window.location.hash = '#login';
        // URL変更によって再度このhandleRouteが呼ばれるため、ここで処理を終了
        return;
    }

    // すでにログインしているのに、ログイン画面や登録画面に行こうとした場合
    if ((hash === '#login' || hash === '#register') && user) {
        // 強制的にホーム画面へ戻す
        window.location.hash = '#home';
        return;
    }

    // app-rootの中身を一旦空にして、新しい画面を表示する準備をする
    appRoot.innerHTML = '';

    // ハッシュの値（現在のページ）に応じた条件分岐
    if (hash === '#login') {
        // ログイン画面のHTMLを生成してセット
        appRoot.innerHTML = await renderLogin();
        // ログインボタンを押した時の処理（fetchなど）を有効にする
        initLogin();
    } else if (hash === '#register') {
        // 新規登録画面のHTMLを生成してセット
        appRoot.innerHTML = await renderRegister();
        // 登録ボタンを押した時の処理を有効にする
        initRegister();
    } else if (hash === '#home') {
        // ホーム画面（カレンダーなど）を表示
        await renderHome(appRoot);
    } else if (hash === '#log') {
        // 過去のログ一覧画面を表示
        await renderLog(appRoot);
    } else if (hash === '#add') {
        // 記録追加画面を表示
        await renderAdd(appRoot, selectedDate);
    } else if (hash === '#settings') {
        // 設定画面を表示
        appRoot.innerHTML = await renderSettings();
        initSettings();
    } else if (hash === '#details') {
        await renderDetails(appRoot, selectedDate);
    } else {
        // 該当するページがない（404）場合に、とりあえずホームへ飛ばす
        window.location.hash = '#home';
    }

    const navBar = document.querySelector('.bottom-nav');
    if (!navBar) return;

    const isAuthpage = (hash === '#login' || hash === '#register');
    navBar.style.display = isAuthpage ? 'none' : 'flex';
}