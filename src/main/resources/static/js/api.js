// サーバーから「自分自身の情報」を取得して、ログイン状態を確認する関数
export async function checkAuth() {
    // APIを叩いてユーザー情報を取得（GETリクエスト）
    const response = await fetch('/api/auth/me', {
        method: 'POST'
    });
    // 応答が正常（200 OK）ならログイン済み、そうでなければ未ログインとしてnullを返す
    return response.ok ? await response.json() : null;
}

// ログインを行う関数。引数credentialsには{username, password}が入る
export async function login(credentials) {
    // フォームデータとして送信するための準備
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

// ユーザー登録を行う関数。引数userDataには{userName, password}が入る
export async function register(userData) {
    // APIへPOSTリクエストを送信
    const response = await fetch('/api/auth/register', {
        // 送信形式をJSONに指定
        method: 'POST',
        // ヘッダーでJSON送信であることを伝える
        headers: { 'Content-Type': 'application/json' },
        // JavaScriptのオブジェクトを文字列に変換してボディにセット
        body: JSON.stringify(userData)
    });
    // 成功（201 Createdなど）以外の場合はエラーを投げる
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || errorData.error || '登録に失敗しました');
    }
    // レスポンスをJSONとして解析して返す
    return await response.json();
}

// 記録（ログ）の一覧を取得する関数
export async function fetchLogs(year, month) {
    // 引数がなければ現在の日時を使用
    if (!year || !month) {
        const date = new Date();
        year = date.getFullYear();
        month = date.getMonth() + 1;
    }
    // サーバーのAPI（年と月を指定）からデータを取得
    const response = await fetch(`/api/logs/month?year=${year}&month=${month}`);
    // 成功ならリストを返し、失敗なら安全のために空の配列を返す
    return response.ok ? await response.json() : [];
}

// ログインが必要なAPIを叩く際など、他の関数からも利用できるようにエクスポート

export async function getActivities() {

    // 認証済みのユーザー情報を取得

    const user = await checkAuth();

    // ユーザーがいない（未ログイン）なら空のリストを返す

    if (!user) return [];

    // ユーザーIDを使って活動内容を取得

    const response = await fetch(`/api/activities/user/${user.id}`);

    // 成功ならデータを返し、失敗なら空の配列を返す

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
    // ユーザーIDをセット（サーバー側でセットする場合も多いですが、DTOの定義に合わせます）
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







    // ユーザーIDをセット



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







// アクティビティを削除する関数



export async function deleteActivity(id) {



    const response = await fetch(`/api/activities/${id}`, {



        method: 'DELETE'



    });



    if (!response.ok) throw new Error('アクティビティの削除に失敗しました');



    return true;



}