window.addEventListener('load', () => {

    const connectForm = document.querySelector('#connect-form');
    const sendMessageForm = document.querySelector('#send-message-form');
    const disconnectButton = document.querySelector('#disconnect-button');

    let webSocket = null;
    disconnectButton.style.display = 'none';

    // Connecting
    connectForm.addEventListener('submit', event => {
        if (!webSocket) {
            event.preventDefault();
            const username = document.querySelector('#username').value;
            console.log(username);

            webSocket = new WebSocket(`ws://localhost:10000/chat/${username}`);
            webSocket.onmessage = onReceiveMessage;
            connectForm.style.display = 'none';
            disconnectButton.style.display = null;
        }
    });

    // Disconnecting
    disconnectButton.addEventListener('click', () => {
        if (webSocket) {
            webSocket.close();
            webSocket = null;
            disconnectButton.style.display = 'none';
            connectForm.style.display = null;
        }
    });

    // Sending a message
    sendMessageForm.addEventListener('submit', event => {
        event.preventDefault();

        if (!webSocket) {
            alert('You need to be connected first!');
            return;
        }

        const content = document.querySelector('#message').value;
        const message = {
            content,
            timestamp: dayjs().format('YYYY-MM-DDTHH:mm:ss')
        };

        if (webSocket) {
            webSocket.send(JSON.stringify(message));
        }
    });

    // Receiving a message
    function onReceiveMessage(event) {
        const message = JSON.parse(event.data);
        const chatHistory = document.querySelector('#chat-history');

        const timestamp = dayjs(message.timestamp).format('YYYY-MM-DDTHH:mm:ss');
        const { content, username } = message;

        const text = `[${timestamp}] ${username} says: '${content}'`;
        chatHistory.innerHTML = `<p>${text}</p>` + chatHistory.innerHTML;
    }
});