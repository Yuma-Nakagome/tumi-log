import { fetchLogsByDate, getActivities } from '../api.js';

export async function renderDetails(appRoot, selectedDate) {
    // 1. 活動定義を取得してマップを作成（IDから名前を引くため）
    const activities = await getActivities();
    const activityMap = new Map();
    if (Array.isArray(activities)) {
        activities.forEach(a => activityMap.set(a.id, a));
    }

    // 2. 画面の外枠を描画
    appRoot.innerHTML = `
        <div class="details-container">
            <header class="details-header">
                <h2>${selectedDate} の記録</h2>
            </header>
            
            <ul id="day-log-list" class="details-log-list">
                <li class="loading">読み込み中...</li>
            </ul>

            <div class="details-actions">
                <button id="to-add-btn" class="btn-primary">記録を追加する</button>
                <button id="to-home-btn" class="btn-secondary">カレンダーに戻る</button>
            </div>
        </div>
    `;

    const listEl = document.getElementById('day-log-list');

    // ボタンのイベント設定
    document.getElementById('to-add-btn').onclick = () => {
        location.hash = `#add?date=${selectedDate}`;
    };
    document.getElementById('to-home-btn').onclick = () => {
        location.hash = '#home';
    };

    // 3. その日のログを取得して表示
    try {
        const logs = await fetchLogsByDate(selectedDate);

        if (!logs || logs.length === 0) {
            listEl.innerHTML = '<li class="empty-msg">この日の記録はありません。</li>';
            return;
        }

        // 配列をループ（map）で回してHTMLを生成
        listEl.innerHTML = logs.map(log => {
            const activity = activityMap.get(log.activityId);
            return `
                <li class="details-log-item">
                    <span class="activity-icon">${activity ? activity.displayStyle || '📝' : '📝'}</span>
                    <div class="activity-content">
                        <span class="activity-title">${activity ? activity.title : '不明な活動'}</span>
                        ${log.memo ? `<p class="activity-memo">${log.memo}</p>` : ''}
                    </div>
                </li>
            `;
        }).join('');

    } catch (error) {
        console.error('詳細表示エラー:', error);
        listEl.innerHTML = '<li class="error-msg">記録の取得に失敗しました。</li>';
    }
}