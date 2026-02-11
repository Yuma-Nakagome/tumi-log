import { logout, checkAuth } from '../api.js';

export async function renderSettings() {
    const user = await checkAuth();
    return `
        <div class="settings-container">
            <h2>設定</h2>
            <div class="user-info">
                <p><strong>ユーザー名:</strong> ${user ? user.username : '読み込み中...'}</p>
            </div>
            <button id="logout-btn" class="btn-danger">ログアウト</button>
        </div>
    `;
}

export function initSettings() {
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            if (confirm('ログアウトしますか？')) {
                try {
                    await logout();
                    window.location.hash = '#login';
                } catch (error) {
                    console.error('ログアウトエラー:', error);
                    alert('ログアウトに失敗しました');
                }
            }
        });
    }
}
