import { getActivities, fetchLogs } from '../api.js';

export async function renderHome(appRoot) {
    // A. 実行した瞬間の「今日」の年月をセット
    const now = new Date();
    let currentYear = now.getFullYear();
    let currentMonth = now.getMonth(); // 0-11

    // 活動定義（マスタデータ）を取得
    const activityDefinitions = await getActivities();
    const activityMap = new Map();
    if (Array.isArray(activityDefinitions)) {
        activityDefinitions.forEach(a => activityMap.set(a.id, a));
    }

    // B. まず「外枠」だけを表示
    appRoot.innerHTML = `
        <div class="calendar-container">
            <div class="calendar-header">
                <button id="prev-btn">＜ 前月</button>
                <h2 id="month-label"></h2>
                <button id="next-btn">次月 ＞</button>
            </div>
            <div id="calendar-grid" class="calendar-grid"></div>
        </div>
    `;

    // C. 中身を埋める関数を定義
    async function update() {
        const grid = document.getElementById('calendar-grid');
        const label = document.getElementById('month-label');

        if (!grid || !label) return;

        // ヘッダー表示を更新
        label.innerText = `${currentYear}年 ${currentMonth + 1}月`;

        // その月のログデータを取得
        // monthは1始まりで渡す
        const logs = await fetchLogs(currentYear, currentMonth + 1);

        // --- カレンダーの計算ロジック ---
        const firstDay = new Date(currentYear, currentMonth, 1).getDay(); // 1日の曜日
        const lastDate = new Date(currentYear, currentMonth + 1, 0).getDate(); // 月の末日

        let html = '';
        // 曜日の見出し
        ['日', '月', '火', '水', '木', '金', '土'].forEach(d => html += `<div class="weekday">${d}</div>`);

        // 1日より前の空白
        for (let i = 0; i < firstDay; i++) html += `<div class="day empty"></div>`;

        // 日付の生成
        for (let date = 1; date <= lastDate; date++) {
            // Javaのデータと照合するための日付文字列 (例: 2026-02-01)
            const dateStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(date).padStart(2, '0')}`;

            // その日のログを抽出
            const todaysLogs = Array.isArray(logs) ? logs.filter(l => l.logDate === dateStr) : [];

            // ラベル生成
            const labels = todaysLogs.map(log => {
                const activity = activityMap.get(log.activityId);
                const title = activity ? activity.title : '不明';
                // 表示スタイル（アイコンなど）があれば使うことも可能
                // 今回はシンプルにタイトル先頭5文字
                return `<div class="log-label" title="${title}">${title.substring(0, 5)}</div>`;
            }).join('');

            html += `<div class="day" onclick="location.hash='#add?date=${dateStr}'" style="cursor: pointer;">
                        <span>${date}</span>
                        <div class="log-container">${labels}</div>
                     </div>`;

        }
        grid.innerHTML = html;
    }

    // D. 【onclickの設定】 ボタンを動くようにする
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');

    if (prevBtn) prevBtn.onclick = () => {
        currentMonth--;
        if (currentMonth < 0) { currentMonth = 11; currentYear--; }
        update(); // 月を更新して再描画
    };

    if (nextBtn) nextBtn.onclick = () => {
        currentMonth++;
        if (currentMonth > 11) { currentMonth = 0; currentYear++; }
        update(); // 月を更新して再描画
    };

    // E. 最初に1回実行して初期画面を出す
    await update();
}
