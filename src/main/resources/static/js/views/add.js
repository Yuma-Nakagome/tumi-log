import { getActivities, createLog } from '../api.js';

export async function renderAdd(appRoot, selectedDate) {
    const allActivities = await getActivities();
    const activities = Array.isArray(allActivities) ? allActivities.filter(a => !a.archived) : [];
    const targetDate = selectedDate || new Date().toLocaleDateString('sv-SE');

    appRoot.innerHTML = `
        <div class="add-container">
            <h2>どの活動をしましたか？</h2>
            <p class="subtitle">日付: <strong>${targetDate}</strong> の記録を追加します</p>
            
            <div id="activity-selection" class="activity-grid">
                <!-- 動的に生成 -->
            </div>
            
            ${activities.length === 0 ? `
                <div class="empty-state">
                    <p>まだ活動の種類が登録されていません。</p>
                    <a href="#log" class="btn-outline">活動を登録しに行く</a>
                </div>
            ` : ''}
        </div>
    `;

    const grid = document.getElementById('activity-selection');

    activities.forEach(activity => {
        const btn = document.createElement('button');
        btn.className = 'activity-card';
        btn.innerHTML = `
            <div class="icon">${activity.displayStyle || '📝'}</div>
            <div class="name">${activity.title}</div>
        `;

        btn.onclick = async () => {
            btn.disabled = true;
            btn.style.opacity = '0.5';

            try {
                await createLog({
                    activityId: activity.id,
                    logDate: targetDate,
                    memo: ""
                });
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
