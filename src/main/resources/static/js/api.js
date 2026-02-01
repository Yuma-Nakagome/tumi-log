
export async function fetchLogs() {
    const res = await fetch('/api/logs');
    return await res.json();
}

export async function getActivities() {
    const res = await fetch('/api/activities');
    return await res.json();
}