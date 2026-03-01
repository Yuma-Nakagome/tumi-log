import { getActivities, createLog } from '../api.js';

export async function renderAdd(appRoot, selectedDate) {
    // 1. ユーザーが登録した活動一覧を取得
    const activities = await getActivities();

    // 日付を確定させる（渡された日付があればそれ、なければ今日）
    const targetDate = selectedDate || new Date().toLocaleDateString('sv-SE');

    // 2. HTML構造をセット
    appRoot.innerHTML = `
        <div class="add-container">
            <h2>どの活動をしましたか？</h2>
            <p class="subtitle">日付: <strong>${targetDate}</strong> の記録を追加します</p>
            
            <div id="activity-selection" class="activity-grid">
                <!-- ここに活動ボタンが動的に追加されます -->
            </div>
            
            ${activities.length === 0 ? `
                <div class="empty-state">
                    <p>まだ活動の種類が登録されていません。</p>
                    <a href="#log" class="btn-link">活動を登録しに行く</a>
                </div>
            ` : ''}
        </div>
    `;

    const grid = document.getElementById('activity-selection');

    // 3. 各活動のボタン（カード）を生成
    activities.forEach(activity => {
        const btn = document.createElement('button');
        btn.className = 'activity-card';
        btn.innerHTML = `
            <div class="icon">${activity.displayStyle || '📝'}</div>
            <div class="name">${activity.title}</div>
        `;

        // 4. ボタンクリック時の保存処理
        btn.onclick = async () => {
            // 二重送信防止
            btn.disabled = true;
            btn.style.opacity = '0.5';

            try {
                await createLog({
                    activityId: activity.id,
                    logDate: targetDate, // 確定させた日付を使用
                    memo: ""
                });

                // 成功時はホーム（カレンダー）へ
                window.location.hash = '#home';
            } catch (error) {
                console.error(error);
                alert('記録の保存に失敗しました。');
                btn.disabled = false;
                btn.style.opacity = '1';
            }
        };

        grid.appendChild(btn);
    });
}