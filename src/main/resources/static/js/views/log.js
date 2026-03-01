import { getActivities, createActivity, updateActivity, toggleActivityArchive } from '../api.js';

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
            <div class="toggle-wrapper">
    <span>非表示の活動も表示</span>
    <label class="switch">
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

    // ★ ここがポイント：Javaから取ってきた全データをここに一時保存する
    let allActivities = [];

    // --- 【表示だけを担当する関数】 ---
    function renderUI(activitiesToShow) {
        listEl.innerHTML = '';
        if (!activitiesToShow || activitiesToShow.length === 0) {
            listEl.innerHTML = '<li class="activity-item">活動がありません。</li>';
            return;
        }

        activitiesToShow.forEach(act => {
            const li = document.createElement('li');
            li.className = `activity-item ${act.archive ? 'is-archived' : ''}`; // 非表示ならクラス付与
            li.innerHTML = `
                <div class="activity-info">
                    <span class="activity-icon">${act.displayStyle || '📝'}</span>
                    <span class="activity-name">${act.title}</span>
                </div>
                <div class="activity-actions">
                    <button class="btn-edit" data-id="${act.id}" ...>編集</button>
                    <button class="btn-danger toggle-act-btn" data-id="${act.id}" data-status="${act.archive}">
                        ${act.archive ? '表示に戻す' : '非表示にする'}
                    </button>
                </div>
            `;
            listEl.appendChild(li);
        });

        // ボタンイベントの設定 (renderUIの中で毎回行う)
        attachEvents();
    }

    // --- 【B. フィルターしてUIに渡す関数】 ---
    function applyFilter() {
        const isShowAll = archiveToggle.checked;
        const filtered = isShowAll ? allActivities : allActivities.filter(a => !a.archive);
        renderUI(filtered);
    }

    async function refreshList() {
        try {
            const activities = await getActivities(true); // includeArchived=true で全てのアクティビティを取得
            allActivities = activities; // ★ 変数に保存！
            // 今のトグルの状態を見て表示を反映
            applyFilter();

        } catch (error) {
            console.error('読み込みエラー:', error);
            listEl.innerHTML = '<li class="activity-item">データの取得に失敗しました。</li>';
        }
    }

    // トグルが変更された時
    archiveToggle.onchange = () => {
        applyFilter(); // APIは叩かず、手元のデータをフィルターするだけ
    };


    saveBtn.onclick = async () => {
        const data = { title: titleInput.value, displayStyle: styleInput.value }; // 入力値を取得
        const id = idInput.value; // IDがあれば「更新」、なければ「新規」
        if (!data.title) return alert('活動名は必須です'); // 空入力チェック

        try {
            if (id) await updateActivity(id, data); // IDがあれば更新API
            else await createActivity(data);         // なければ新規作成API

            resetForm();         // フォームを綺麗にする
            await refreshList(); // 保存が終わったのでJavaと同期してリストを新しくする
        } catch (e) {
            alert('保存に失敗しました');
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

    // 編集・トグルボタンのイベント登録 (再描画のたびに呼ぶ)
    function attachEvents() {
        // 編集ボタン
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.onclick = (e) => {
                const { id, title, style } = e.target.dataset;
                idInput.value = id; titleInput.value = title; styleInput.value = style;
                saveBtn.innerText = '更新'; cancelBtn.style.display = 'inline-block';
                titleInput.focus();
            };
        });

        // 非表示ボタン
        document.querySelectorAll('.toggle-act-btn').forEach(btn => {
            btn.onclick = async (e) => {
                const currentStatus = e.target.dataset.status === 'true';
                const nextStatus = !currentStatus;
                const actionText = nextStatus ? '非表示に' : '表示に';
                if (confirm(`この活動を${actionText}しますか？`)) {
                    try {
                        await toggleActivityArchive(e.target.dataset.id, nextStatus);
                        await refreshList(); // 状態変更後はJavaと同期
                    } catch (err) { alert('失敗しました'); }
                }
            };
        });
    }


    // 初回読み込み
    await refreshList();
}

