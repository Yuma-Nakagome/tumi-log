// api.jsから「登録用」の通信関数をインポート
import { register } from '../api.js';

/**
 * 新規ユーザー登録画面のHTML構造を生成する関数
 */
export async function renderRegister() {
    return `
        <div class="auth-box">
            <h2>新規ユーザー登録</h2>
            <form id="register-form">
                <div class="form-group">
                    <label>ユーザー名</label>
                    <input type="text" id="reg-username" name="username" placeholder="使いたいユーザー名" required>
                </div>
                <div class="form-group">
                    <label>パスワード</label>
                    <input type="password" id="reg-password" name="password" placeholder="パスワード（8文字以上）" required>
                </div>
                <div class="form-group">
                    <label>パスワード（確認）</label>
                    <input type="password" id="reg-confirm-password" name="confirmPassword" placeholder="もう一度入力してください" required>
                </div>
                <button type="submit" class="btn-main">アカウントを作成</button>
            </form>
            <p class="auth-nav">
                すでにアカウントをお持ちですか？<br>
                <a href="#login">ログイン画面へ戻る</a>
            </p>
        </div>
    `;
}

/**
 * 登録画面の「送信処理」を定義する関数
 */
export function initRegister() {
    // 登録フォームを取得
    const registerForm = document.getElementById('register-form');
    // フォームがない場合は何もしない
    if (!registerForm) return;

    // フォームの送信ボタンが押された時の動作
    registerForm.addEventListener('submit', async (event) => {
        // ページのリロードを防ぐ
        event.preventDefault();

        const username = document.getElementById('reg-username').value;
        const password = document.getElementById('reg-password').value;
        const confirmPassword = document.getElementById('reg-confirm-password').value;

        // クライアントサイドでの簡易バリデーション
        if (password !== confirmPassword) {
            alert('パスワードが一致しません。');
            return;
        }

        // 入力されたユーザー名とパスワードを取得
        const userData = {
            userName: username,
            password: password,
            confirmPassword: confirmPassword
        };

        try {
            // api.jsのregister関数を使って、サーバーへデータを送信
            await register(userData);

            // 登録成功時のメッセージ
            alert('登録が完了しました！作成したアカウントでログインしてください。');

            // ログインを促すため、ログイン画面に自動で移動させる
            window.location.hash = '#login';
        } catch (error) {
            // ユーザー名が既に使われている場合などのエラー表示
            console.error('登録エラー:', error);
            alert(error.message || '登録に失敗しました。そのユーザー名は既に使用されている可能性があります。');
        }
    });
}
