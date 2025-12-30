const week = ["日", "月", "火", "水", "木", "金", "土"];

const today = new Date(); // 今日の日付と時刻すべてを取得する。
const todayYear = today.getFullYear();
const todayMonth = today.getMonth() + 1;
const todayDate = today.getDate();
// ※ getDate()で「日」を取得できる。

// 【変数】カレンダーで「今、表示している年と月」を管理するための変数（State）。
// 「日」は「1日」で固定することで、月の増減（次月/前月ボタン）処理での
// 月末日付の（31日がない月や2月）バグを防ぎ、カレンダーの開始曜日特定を容易にしている。
let showdate = new Date(today.getFullYear(), today.getMonth(), 1);

// この処理により、ブラウザが起動した直後に、設定された当月のカレンダーが画面に描画される。
window.onload = function () {
    showCalendar(showdate);
    // TODO: ボタン関数をここに定義する（未定義だとクリック時にエラーになる）
}

function showCalendar(date) {
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // (※getMonth()は0-11なので+1が必要)

    // IDが「year_month_label」の要素（HTMLのpタグ）を探し、
    // その中身（innerHTML）を「〇〇年 〇〇月」という文字列に書き換える
    // ${}はテンプレートリテラルといい、動的に値を埋め込む
    document.querySelector('#year_month_label').innerHTML = `${year}年 ${month}月`;
    // IDが「calendar_body」の要素（HTMLのdivタグ）を探し、
    // その中身（innerHTML）を、createCalendarTable関数が返すHTML（カレンダーの<table>）に書き換える
    document.querySelector('#calendar_body').innerHTML = createCalendarTable(year, month);
}

// カレンダーテーブルの作成
function createCalendarTable(year, month) {
    let html = '<table class="calendar_tbl"><thead><tr>';

    // 曜日のセルにクラスを付与
    for (let i = 0; i < week.length; i++) {
        const className = (i === 0) ? 'sun' : (i === 6) ? 'sat' : '';
        html += `<th class="${className}">${week[i]}</th>`;
    }
    html += '</tr></thead><tbody>';
    // 1日の曜日番号（0:日〜6:土）を取得。
    // ※ new Date().getDay()は「曜日」を返す。
    const startDay = new Date(year, month - 1, 1).getDay();
    // ※ getDate()で「日」を、getDay()で「曜日」を取得できる。

    // その月の最終日を取得（例: 31）。
    // ※ new Date().getDate()は「日付」を返す。
    const endDate = new Date(year, month, 0).getDate();
    let count = 0;

    for (let i = 0; i < 6; i++) {
        html += '<tr>';
        for (let j = 0; j < 7; j++) {
            let cellContent = '';
            let cellClass = 'no_date';

            if (i === 0 && j < startDay) {
                // 前月の日付
            } else if (count >= endDate) {
                // 次月の日付
            } else {
                count++;
                cellContent = count;
                cellClass = 'with_date';

                // ★★★ 今日の日付ならハイライト用のクラスを追加 ★★★
                if (year === todayYear && month === todayMonth && count === todayDate) {
                    cellClass += ' today';
                }

                // 土日にもクラスを追加
                if (j === 0) { cellClass += ' sun'; }
                if (j === 6) { cellClass += ' sat'; }

                // ★★★ data-date属性に完全な日付情報を格納 ★★★
                html += `<td class="${cellClass}" data-date="${year}-${month}-${count}">${cellContent}</td>`;
                continue; // 既に<td>を出力したので次のループへ
            }

            html += `<td class="${cellClass}">${cellContent}</td>`;
        }
        html += '</tr>';

        // 最終週が空の場合にループを抜ける（カレンダーの見た目を整える）
        if (count >= endDate) break;
    }
    html += '</tbody></table>';
    return html;
}

// 注意: 上記のコードでは、まだprev_month()などのボタン関数は未定義です。
// 定義しないと、HTMLのボタンをクリックした際にエラーが発生します。