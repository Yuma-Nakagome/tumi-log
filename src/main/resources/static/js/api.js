// サーバーから「自分自身の情報」を取得して、ログイン状態を確認する関数
export async function checkAuth() {
    const response = await fetch('/api/auth/me', {
        method: 'POST'
    });
    return response.ok ? await response.json() : null;
}

// ログインを行う関数
export async function login(credentials) {
    const params = new URLSearchParams();
    params.append('username', credentials.username);
    params.append('password', credentials.password);

    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'ログインに失敗しました');
    }
    return await response.json();
}

// ログアウトを行う関数
export async function logout() {
    const response = await fetch('/api/auth/logout', {
        method: 'POST'
    });
    if (!response.ok) throw new Error('ログアウトに失敗しました');
    return await response.json();
}

// ユーザー登録を行う関数
export async function register(userData) {
    const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData)
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || errorData.error || '登録に失敗しました');
    }
    return await response.json();
}

// 記録（ログ）の一覧を取得する関数 (月単位)
export async function fetchLogs(year, month) {
    if (!year || !month) {
        const date = new Date();
        year = date.getFullYear();
        month = date.getMonth() + 1;
    }
    const response = await fetch(`/api/logs/month?year=${year}&month=${month}`);
    return response.ok ? await response.json() : [];
}

// 特定の日付のログを取得する関数
export async function fetchLogsByDate(dateStr) {
    const response = await fetch(`/api/logs/date?date=${dateStr}`);
    return response.ok ? await response.json() : [];
}

// アクティビティ定義の一覧を取得する関数
export async function getActivities(includeArchived = false) {
    const user = await checkAuth();
    if (!user) return [];
    const response = await fetch(`/api/activities?includeArchived=${includeArchived}`);
    return response.ok ? await response.json() : [];
}


// ログを削除する関数
export async function deleteLog(logId) {
    const response = await fetch(`/api/logs/${logId}`, {
        method: 'DELETE'
    });
    if (!response.ok) throw new Error('削除に失敗しました');
    return true;
}

// ログを新規登録する関数
export async function createLog(logData) {
    const user = await checkAuth();
    if (!user) throw new Error('ログインが必要です');
    logData.userId = user.id;

    const response = await fetch('/api/logs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(logData)
    });

    if (!response.ok) throw new Error('ログの登録に失敗しました');
    return await response.json();
}

// アクティビティを作成する関数
export async function createActivity(activityData) {
    const user = await checkAuth();
    if (!user) throw new Error('ログインが必要です');
    activityData.userId = user.id;

    const response = await fetch('/api/activities', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(activityData)
    });
    if (!response.ok) throw new Error('アクティビティの作成に失敗しました');
    return await response.json();
}

// アクティビティを更新する関数
export async function updateActivity(id, activityData) {
    const user = await checkAuth();
    if (!user) throw new Error('ログインが必要です');
    activityData.userId = user.id;

    const response = await fetch(`/api/activities/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(activityData)
    });
    if (!response.ok) throw new Error('アクティビティの更新に失敗しました');
    return await response.json();
}
// アクティビティを表示/非表示を切り替える関数
export async function toggleActivityArchive(id, status) {
    const response = await fetch(`/api/activities/${id}/archive?status=${status}`, {
        method: 'PATCH' // ここもPATCHに合わせる
    });

    if (!response.ok) throw new Error('更新失敗');

    // Javaから返ってきた最新のDTOをそのまま返す
    return await response.json();
}
