import { getActivities, createActivity, updateActivity, deleteActivity } from '../api.js';

export async function renderLog(appRoot) {
    appRoot.innerHTML = `
        <div class="activity-manager">
            <h2>つみあげの管理</h2>
            <p>日々の記録をつけるための「つみあげの種類」をここで登録します。</p>
            
            <div class="activity-form-container">
                <input type="hidden" id="act-id">
                <input type="text" id="act-title" placeholder="つみあげ名 (例: 読書, ランニング)" required>
                <input type="text" id="act-style" placeholder="アイコン (例: 📖, 🏃)" style="max-width: 150px;">
                <button id="save-act-btn" class="btn-primary" style="width: auto; padding: 0 20px;">保存</button>
                <button id="cancel-act-btn" class="btn-danger" style="display:none; width: auto; padding: 0 20px; background-color:#6c757d;">キャンセル</button>
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

    async function refreshList() {
        try {
            const activities = await getActivities();
            listEl.innerHTML = '';

            if (!activities || activities.length === 0) {
                listEl.innerHTML = '<li class="activity-item" style="justify-content:center; color:#888;">まだ活動が登録されていません。上のフォームから追加してください。</li>';
                return;
            }

            activities.forEach(act => {
                const li = document.createElement('li');
                li.className = 'activity-item';
                li.innerHTML = `
                    <div class="activity-info">
                        <span class="activity-icon">${act.displayStyle || '📝'}</span>
                        <span class="activity-name">${act.title}</span>
                    </div>
                    <div class="activity-actions">
                        <button class="btn-edit" data-id="${act.id}" data-title="${act.title}" data-style="${act.displayStyle || ''}">編集</button>
                        <button class="btn-danger delete-act-btn" data-id="${act.id}">削除</button>
                    </div>
                `;
                listEl.appendChild(li);
            });

            // 編集ボタンのイベント
            document.querySelectorAll('.btn-edit').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    const { id, title, style } = e.target.dataset;
                    idInput.value = id;
                    titleInput.value = title;
                    styleInput.value = style;

                    saveBtn.innerText = '更新';
                    cancelBtn.style.display = 'inline-block';

                    // フォームへスクロール
                    titleInput.focus();
                });
            });

            // 削除ボタンのイベント
            document.querySelectorAll('.delete-act-btn').forEach(btn => {
                btn.addEventListener('click', async (e) => {
                    if (confirm('この活動を削除しますか？\n※これまでの記録ログの表示に影響が出る可能性があります。')) {
                        try {
                            await deleteActivity(e.target.dataset.id);
                            refreshList();
                        } catch (err) {
                            console.error(err);
                            alert('削除に失敗しました。');
                        }
                    }
                });
            });

        } catch (error) {
            console.error('読み込みエラー:', error);
            listEl.innerHTML = '<li class="activity-item">データの取得に失敗しました。</li>';
        }
    }

    saveBtn.onclick = async () => {
        const title = titleInput.value;
        const style = styleInput.value;
        const id = idInput.value;

        if (!title) {
            alert('活動名は必須です');
            return;
        }

        const data = { title: title, displayStyle: style };

        try {
            if (id) {
                await updateActivity(id, data);
            } else {
                await createActivity(data);
            }
            // フォームリセット
            resetForm();
            refreshList();
        } catch (e) {
            console.error(e);
            alert('保存に失敗しました。');
        }
    };

    cancelBtn.onclick = () => {
        resetForm();
    };

    function resetForm() {
        idInput.value = '';
        titleInput.value = '';
        styleInput.value = '';
        saveBtn.innerText = '保存';
        cancelBtn.style.display = 'none';
    }

    // 初回読み込み
    await refreshList();
}
