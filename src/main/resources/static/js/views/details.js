import { fetchLogsByDate, getActivities, deleteLog } from '../api.js';

export async function renderDetails(appRoot, selectedDate) {
    const activities = await getActivities();
    const activityMap = new Map();
    if (Array.isArray(activities)) {
        activities.forEach(a => activityMap.set(a.id, a));
    }

    appRoot.innerHTML = `
        <div class="details-container">
            <header class="details-header">
                <h2 style="font-size: 17px; font-weight: 800; margin-bottom: 4px;">${selectedDate} の記録</h2>
            </header>
            
            <ul id="day-log-list" class="details-log-list">
                <li class="loading">読み込み中...</li>
            </ul>

            <div class="details-actions" style="display: flex; flex-direction: column; gap: 12px;">
                <button id="to-add-btn" class="btn-main">記録を追加する</button>
                <button id="to-home-btn" class="btn-sub">カレンダーに戻る</button>
            </div>
        </div>
    `;

    const listEl = document.getElementById('day-log-list');

    document.getElementById('to-add-btn').onclick = () => {
        location.hash = `#add?date=${selectedDate}`;
    };
    document.getElementById('to-home-btn').onclick = () => {
        location.hash = '#home';
    };

    try {
        const logs = await fetchLogsByDate(selectedDate);

        if (!logs || logs.length === 0) {
            listEl.innerHTML = '<li style="text-align:center; padding: 40px 0; color:#8E8E93; font-size:15px; list-style:none;">この日の記録はありません。</li>';
            return;
        }

        listEl.innerHTML = logs.map(log => {
            const activity = activityMap.get(log.activityId);
            return `
                <li class="details-log-item">
                    <span class="activity-icon">${activity ? activity.displayStyle || '📝' : '📝'}</span>
                    <div class="activity-content">
                        <span class="activity-title">${activity ? activity.title : '不明な活動'}</span>
                        ${log.memo ? `<p class="activity-memo">${log.memo}</p>` : ''}
                    </div>
                    <button class="delete-btn" data-id="${log.id}">削除</button>
                </li>
            `;
        }).join('');

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const logId = e.currentTarget.getAttribute('data-id');
                if (confirm('この記録を削除してもよろしいですか？')) {
                    try {
                        await deleteLog(logId);
                        renderDetails(appRoot, selectedDate);
                    } catch (error) {
                        alert('削除に失敗しました。');
                    }
                }
            });
        });

    } catch (error) {
        console.error('詳細表示エラー:', error);
        listEl.innerHTML = '<li class="error-msg">記録の取得に失敗しました。</li>';
    }
}