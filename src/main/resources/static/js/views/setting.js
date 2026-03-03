import { logout, checkAuth } from '../api.js';

export async function renderSettings(appRoot) {
    const user = await checkAuth();
    appRoot.innerHTML = `
        <div class="settings-container">
            <h2 style="font-size: 24px; font-weight: 800; margin-bottom: 24px;">設定</h2>
            
            <div style="background: var(--card-bg); padding: 20px; border-radius: var(--radius); box-shadow: var(--shadow-sm); margin-bottom: 24px;">
                <p style="margin: 0; color: var(--text-secondary); font-size: 13px; text-transform: uppercase; font-weight: 600; letter-spacing: 0.05em; margin-bottom: 8px;">ログイン中のユーザー</p>
                <p style="margin: 0; font-size: 17px; font-weight: 600; color: var(--text-primary);">${user ? user.username : '読み込み中...'}</p>
            </div>

            <div style="display: flex; flex-direction: column; gap: 12px;">
                <button id="logout-btn" class="btn-danger" style="width: 100%;">ログアウト</button>
            </div>
        </div>
    `;

    // イベントリスナーの登録
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.onclick = async () => {
            if (confirm('ログアウトしますか？')) {
                try {
                    await logout();
                    window.location.hash = '#login';
                } catch (error) {
                    console.error('ログアウトエラー:', error);
                    alert('ログアウトに失敗しました');
                }
            }
        };
    }
}

// 互換性のための空関数
export function initSettings() {}