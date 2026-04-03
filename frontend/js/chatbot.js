function toggleChatbot() {
    const panel = document.getElementById('chatbot-panel');
    panel.style.display = panel.style.display === 'none' ? 'flex' : 'none';
}

async function sendChat() {
    const input = document.getElementById('chatbot-input');
    const message = input.value.trim();
    if (!message) return;

    appendChatMessage(message, 'chat-user');
    input.value = '';

    appendChatMessage('Thinking...', 'chat-bot', 'bot-thinking');

    try {
        const result = await apiPost('/chat', { message });
        const thinking = document.getElementById('bot-thinking');
        if (thinking) thinking.remove();
        appendChatMessage(result.response || 'No response.', 'chat-bot');
    } catch (e) {
        const thinking = document.getElementById('bot-thinking');
        if (thinking) thinking.remove();
        appendChatMessage('Sorry, I could not connect to the assistant.', 'chat-bot');
    }
}

function appendChatMessage(text, cls, id) {
    const container = document.getElementById('chatbot-messages');
    const div = document.createElement('div');
    div.className = 'chat-msg ' + cls;
    if (id) div.id = id;
    div.style.whiteSpace = 'pre-wrap';
    div.textContent = text;
    container.appendChild(div);
    container.scrollTop = container.scrollHeight;
}
