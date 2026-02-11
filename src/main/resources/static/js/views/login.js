
// api.jsから「ログイン用」の通信関数をインポート
import { login } from '../api.js';

/**
 * ログイン画面のHTML構造を生成する関数
 */
export async function renderLogin() {
    return `
        <div class="auth-box">
            <h2>ログイン</h2>
            <form id="login-form">
                <div class="form-group">
                    <label>ユーザー名</label>
                    <input type="text" id="login-username" name="username" placeholder="ユーザー名" required>
                </div>
                <div class="form-group">
                    <label>パスワード</label>
                    <input type="password" id="login-password" name="password" placeholder="パスワード" required>
                </div>
                <button type="submit" class="btn-primary">ログイン</button>
            </form>
            <p class="auth-nav">
                アカウントをお持ちでないですか？ 
                <a href="#register">新規登録はこちら</a>
            </p>
        </div>
    `;
}

/**
 * ログイン画面の「送信処理」を定義する関数
 */
export function initLogin() {
    const loginForm = document.getElementById('login-form');
    if (!loginForm) {
        console.error('Login form not found!');
        return;
    }

    console.log('Login form initialized.');

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        console.log('Login form submitted.');

        const credentials = {
            username: document.getElementById('login-username').value,
            password: document.getElementById('login-password').value
        };

        try {
            await login(credentials);
            // ログイン成功時、ホームへリダイレクト
            window.location.hash = '#home';
        } catch (error) {
            console.error('ログインエラー:', error);
            alert('ログインに失敗しました。ユーザー名かパスワードが間違っています。');
        }
    });
}

