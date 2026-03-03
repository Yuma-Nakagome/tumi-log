import { getActivities, fetchLogs } from '../api.js';

export async function renderHome(appRoot) {
    const now = new Date();
    let currentYear = now.getFullYear();
    let currentMonth = now.getMonth();

    const activityDefinitions = await getActivities();
    const activityMap = new Map();
    if (Array.isArray(activityDefinitions)) {
        activityDefinitions.forEach(a => activityMap.set(a.id, a));
    }

    appRoot.innerHTML = `
        <div class="calendar-container">
            <div class="calendar-header">
                <button id="prev-btn">前月</button>
                <h2 id="month-label"></h2>
                <button id="next-btn">次月</button>
            </div>
            <div id="calendar-grid" class="calendar-grid"></div>
        </div>
    `;

    async function update() {
        const grid = document.getElementById('calendar-grid');
        const label = document.getElementById('month-label');

        if (!grid || !label) return;

        label.innerText = `${currentYear}年 ${currentMonth + 1}月`;
        const logs = await fetchLogs(currentYear, currentMonth + 1);

        const firstDay = new Date(currentYear, currentMonth, 1).getDay();
        const lastDate = new Date(currentYear, currentMonth + 1, 0).getDate();

        let html = '';
        // 曜日の見出し
        ['日', '月', '火', '水', '木', '金', '土'].forEach(d => {
            html += `<div class="weekday">${d}</div>`;
        });

        // 1日より前の空白
        for (let i = 0; i < firstDay; i++) {
            html += `<div class="day empty"></div>`;
        }

        const today = new Date();
        const isThisMonth = today.getFullYear() === currentYear && today.getMonth() === currentMonth;

        for (let date = 1; date <= lastDate; date++) {
            const dateStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(date).padStart(2, '0')}`;
            const isToday = isThisMonth && today.getDate() === date;
            const todaysLogs = Array.isArray(logs) ? logs.filter(l => l.logDate === dateStr) : [];

            const labels = todaysLogs.map(log => {
                const activity = activityMap.get(log.activityId);
                const title = activity ? activity.title : '不明';
                return `<div class="log-label" title="${title}">${title.substring(0, 5)}</div>`;
            }).join('');

            html += `
                <div class="day ${isToday ? 'is-today' : ''}" onclick="location.hash='#details?date=${dateStr}'">
                    <span>${date}</span>
                    <div class="log-container">${labels}</div>
                </div>
            `;
        }
        grid.innerHTML = html;
    }

    document.getElementById('prev-btn').onclick = () => {
        currentMonth--;
        if (currentMonth < 0) { currentMonth = 11; currentYear--; }
        update();
    };

    document.getElementById('next-btn').onclick = () => {
        currentMonth++;
        if (currentMonth > 11) { currentMonth = 0; currentYear++; }
        update();
    };

    await update();
}