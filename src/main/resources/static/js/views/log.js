import { getActivities, createActivity, updateActivity, toggleActivityArchive } from '../api.js';

export async function renderLog(appRoot) {
    appRoot.innerHTML = `
        <div class="activity-manager" style="width: 100%; box-sizing: border-box;">
            <h2 style="font-size: 24px; font-weight: 800; margin-bottom: 4px;">つみあげの管理</h2>
            <p style="color: #8E8E93; font-size: 15px; margin-bottom: 20px;">積み上げる活動を定義しましょう。</p>
            
            <div class="activity-form-container">
                <input type="hidden" id="act-id">
                
                <!-- 縦並びレイアウト -->
                <div style="display: flex; flex-direction: column; gap: 12px;">
                    <!-- タイトル (旧アイコン欄) -->
                    <div style="width: 100%;">
                        <label style="display:block; font-size:12px; font-weight:600; color:#8E8E93; margin-bottom:4px; padding-left:4px;">タイトル</label>
                        <input type="text" id="act-title" placeholder="例: 読書, プログラミング" required style="height: 50px;">
                    </div>
                    <!-- メモ (旧活動名欄) -->
                    <div style="width: 100%;">
                        <label style="display:block; font-size:12px; font-weight:600; color:#8E8E93; margin-bottom:4px; padding-left:4px;">メモ</label>
                        <input type="text" id="act-style" placeholder="例: 📚, 💻" style="height: 50px;">
                    </div>
                    <!-- 保存ボタン (横幅いっぱい) -->
                    <div class="form-actions" style="display: flex; gap: 8px; margin-top: 4px;">
                        <button id="save-act-btn" class="btn-main" style="height: 50px; flex: 1;">保存</button>
                        <button id="cancel-act-btn" class="btn-sub" style="display:none; height: 50px; flex: 1;">取消</button>
                    </div>
                </div>
            </div>

            <div style="display: flex; justify-content: space-between; align-items: center; padding: 0 4px; margin-bottom: 12px;">
                <span style="font-weight: 600; font-size: 14px; color: #8E8E93;">アーカイブした活動を表示</span>
                <label class="toggle-switch">
                    <input type="checkbox" id="show-archived-toggle">
                    <span class="slider"></span>
                </label>
            </div>

            <ul id="activity-list" class="activity-list"></ul>
        </div>
    `;

    const listEl = document.getElementById('activity-list');
    const titleInput = document.getElementById('act-title');
    const styleInput = document.getElementById('act-style');
    const idInput = document.getElementById('act-id');
    const saveBtn = document.getElementById('save-act-btn');
    const cancelBtn = document.getElementById('cancel-act-btn');
    const archiveToggle = document.getElementById('show-archived-toggle');

    let allActivities = [];

    function renderUI(activitiesToShow) {
        listEl.innerHTML = '';
        if (!activitiesToShow || activitiesToShow.length === 0) {
            listEl.innerHTML = '<li style="text-align:center; padding: 40px 0; color:#8E8E93; font-size:15px; list-style:none;">活動がありません。</li>';
            return;
        }

        activitiesToShow.forEach(act => {
            const isArchived = act.archive || act.archived;
            const li = document.createElement('li');
            li.className = `activity-item ${isArchived ? 'is-archived' : ''}`;
            li.innerHTML = `
                <div class="activity-info">
                    <span class="activity-icon">${act.displayStyle || '📝'}</span>
                    <span class="activity-name">${act.title}</span>
                </div>
                <div class="activity-actions">
                    <button class="btn-edit" data-id="${act.id}" data-title="${act.title}" data-style="${act.displayStyle || ''}">編集</button>
                    <label class="toggle-switch">
                        <input type="checkbox" class="toggle-act-checkbox" data-id="${act.id}" ${!isArchived ? 'checked' : ''}>
                        <span class="slider"></span>
                    </label>
                </div>
            `;
            listEl.appendChild(li);
        });

        attachEvents();
    }

    function applyFilter() {
        const isShowAll = archiveToggle.checked;
        const filtered = isShowAll ? allActivities : allActivities.filter(a => !(a.archive || a.archived));
        renderUI(filtered);
    }

    async function refreshList() {
        try {
            const activities = await getActivities(); 
            allActivities = activities; 
            applyFilter();
        } catch (error) {
            listEl.innerHTML = '<li class="activity-item">データの取得に失敗しました。</li>';
        }
    }

    archiveToggle.onchange = () => applyFilter();

    saveBtn.onclick = async () => {
        // title欄 (act-title) と style欄 (act-style) をそのまま使用
        const data = { title: titleInput.value, displayStyle: styleInput.value };
        const id = idInput.value;
        if (!data.title) return alert('タイトルは必須です');
        try {
            if (id) await updateActivity(id, data);
            else await createActivity(data);
            resetForm();
            await refreshList();
        } catch (e) { alert('保存に失敗しました'); }
    };

    cancelBtn.onclick = () => resetForm();

    function resetForm() {
        idInput.value = ''; titleInput.value = ''; styleInput.value = '';
        saveBtn.innerText = '保存'; cancelBtn.style.display = 'none';
    }

    function attachEvents() {
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.onclick = (e) => {
                const { id, title, style } = e.currentTarget.dataset;
                idInput.value = id; titleInput.value = title; styleInput.value = style;
                saveBtn.innerText = '更新'; cancelBtn.style.display = 'inline-block';
                titleInput.focus();
            };
        });

        document.querySelectorAll('.toggle-act-checkbox').forEach(checkbox => {
            checkbox.onchange = async (e) => {
                const isVisible = e.target.checked;
                const nextArchiveStatus = !isVisible;
                try {
                    await toggleActivityArchive(e.target.dataset.id, nextArchiveStatus);
                    await refreshList();
                } catch (err) {
                    alert('失敗しました');
                    e.target.checked = !isVisible;
                }
            };
        });
    }

    await refreshList();
}
