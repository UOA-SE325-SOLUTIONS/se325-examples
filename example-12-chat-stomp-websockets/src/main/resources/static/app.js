/**
 * Modern Chat Application JavaScript
 * Handles WebSocket connections using STOMP.js for real-time messaging
 */

class ChatApp {
    constructor() {
        this.stompClient = null;
        this.isConnected = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.init();
    }

    /**
     * Initialize the chat application
     */
    init() {
        // Bind methods to maintain context
        this.connect = this.connect.bind(this);
        this.disconnect = this.disconnect.bind(this);
        this.sendMessage = this.sendMessage.bind(this);
        this.handleReconnect = this.handleReconnect.bind(this);
        
        // Add event listeners when DOM is ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => {
                this.setupEventListeners();
                this.setConnected(false);
            });
        } else {
            this.setupEventListeners();
            this.setConnected(false);
        }
    }

    /**
     * Set up event listeners for UI elements
     */
    setupEventListeners() {
        const connectBtn = document.getElementById('connect');
        const disconnectBtn = document.getElementById('disconnect');
        const sendBtn = document.getElementById('sendMessage');
        const textInput = document.getElementById('text');
        const nicknameInput = document.getElementById('from');

        if (!connectBtn || !disconnectBtn || !sendBtn || !textInput || !nicknameInput) {
            console.error('Required DOM elements not found');
            return;
        }

        connectBtn.addEventListener('click', this.connect);
        disconnectBtn.addEventListener('click', this.disconnect);
        sendBtn.addEventListener('click', this.sendMessage);
        
        // Allow sending message with Enter key
        textInput.addEventListener('keypress', (event) => {
            if (event.key === 'Enter' && this.isConnected) {
                event.preventDefault();
                this.sendMessage();
            }
        });

        // Connect with Enter on nickname input
        nicknameInput.addEventListener('keypress', (event) => {
            if (event.key === 'Enter' && !this.isConnected) {
                event.preventDefault();
                this.connect();
            }
        });

        // Auto-focus nickname input
        nicknameInput.focus();
    }

    /**
     * Update UI based on connection status
     * @param {boolean} connected - Connection status
     */
    setConnected(connected) {
        this.isConnected = connected;
        const connectBtn = document.getElementById('connect');
        const disconnectBtn = document.getElementById('disconnect');
        const conversationDiv = document.getElementById('conversationDiv');
        const statusDiv = document.getElementById('status');
        const textInput = document.getElementById('text');

        if (!connectBtn || !disconnectBtn || !conversationDiv || !statusDiv) {
            console.error('Required DOM elements not found');
            return;
        }

        connectBtn.disabled = connected;
        disconnectBtn.disabled = !connected;
        conversationDiv.style.display = connected ? 'block' : 'none';
        
        // Clear messages when disconnecting
        if (!connected) {
            const response = document.getElementById('response');
            if (response) {
                response.innerHTML = '';
            }
        } else {
            // Focus text input when connected
            if (textInput) {
                textInput.focus();
            }
        }
        
        // Update status
        statusDiv.textContent = connected ? 'Connected to chat' : 'Disconnected from chat';
        statusDiv.className = `status ${connected ? 'connected' : 'disconnected'}`;

        // Reset reconnect attempts on successful connection
        if (connected) {
            this.reconnectAttempts = 0;
        }
    }

    /**
     * Connect to the WebSocket server
     */
    async connect() {
        const nicknameInput = document.getElementById('from');
        const nickname = nicknameInput?.value?.trim();
        
        if (!nickname) {
            alert('Please choose a nickname');
            nicknameInput?.focus();
            return;
        }

        try {
            this.setLoadingState(true);
            const socket = new SockJS('/chat');
            
            // Use modern STOMP.js API
            this.stompClient = new StompJs.Client({
                webSocketFactory: () => socket,
                connectHeaders: {},
                debug: (str) => {
                    console.log('STOMP: ' + str);
                },
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
            });

            this.stompClient.onConnect = (frame) => {
                console.log('Connected: ' + frame);
                this.setConnected(true);
                this.setLoadingState(false);
                
                // Subscribe to message topic
                this.stompClient.subscribe('/topic/messages', (messageOutput) => {
                    try {
                        const message = JSON.parse(messageOutput.body);
                        this.showMessageOutput(message);
                    } catch (error) {
                        console.error('Error parsing message:', error);
                    }
                });

                // Send join notification
                this.sendSystemMessage(`${nickname} joined the chat`);
            };

            this.stompClient.onStompError = (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
                this.setConnected(false);
                this.setLoadingState(false);
                this.handleConnectionError('Server error occurred');
            };

            this.stompClient.onWebSocketError = (error) => {
                console.error('WebSocket error: ', error);
                this.setConnected(false);
                this.setLoadingState(false);
                this.handleConnectionError('Connection failed');
            };

            this.stompClient.onWebSocketClose = (event) => {
                console.log('WebSocket closed:', event);
                this.setConnected(false);
                this.setLoadingState(false);
                
                if (!event.wasClean && this.reconnectAttempts < this.maxReconnectAttempts) {
                    this.handleReconnect();
                }
            };

            this.stompClient.activate();
            
        } catch (error) {
            console.error('Connection error:', error);
            this.setConnected(false);
            this.setLoadingState(false);
            this.handleConnectionError('Failed to connect');
        }
    }

    /**
     * Disconnect from the WebSocket server
     */
    disconnect() {
        const nicknameInput = document.getElementById('from');
        const nickname = nicknameInput?.value?.trim();
        
        if (this.stompClient !== null && this.isConnected && nickname) {
            // Send leave notification
            this.sendSystemMessage(`${nickname} left the chat`);
            
            // Small delay to ensure message is sent before disconnecting
            setTimeout(() => {
                this.stompClient.deactivate();
                this.stompClient = null;
                this.setConnected(false);
                console.log("Disconnected");
            }, 100);
        } else {
            if (this.stompClient !== null) {
                this.stompClient.deactivate();
                this.stompClient = null;
            }
            this.setConnected(false);
            console.log("Disconnected");
        }
    }

    /**
     * Send a chat message
     */
    sendMessage() {
        const fromInput = document.getElementById('from');
        const textInput = document.getElementById('text');
        
        if (!fromInput || !textInput) {
            console.error('Input elements not found');
            return;
        }
        
        const from = fromInput.value.trim();
        const text = textInput.value.trim();
        
        if (!from) {
            alert('Please choose a nickname');
            fromInput.focus();
            return;
        }
        
        if (!text) {
            alert('Please enter a message');
            textInput.focus();
            return;
        }
        
        if (this.stompClient && this.isConnected) {
            try {
                this.stompClient.publish({
                    destination: "/app/chat",
                    body: JSON.stringify({
                        sender: from,
                        content: text
                    })
                });
                
                // Clear the text input after sending
                textInput.value = '';
                textInput.focus();
            } catch (error) {
                console.error('Error sending message:', error);
                alert('Failed to send message. Please try again.');
            }
        } else {
            alert('Not connected to chat. Please connect first.');
        }
    }

    /**
     * Send a system message (like join/leave notifications)
     * @param {string} message - System message content
     */
    sendSystemMessage(message) {
        if (this.stompClient && this.isConnected) {
            try {
                this.stompClient.publish({
                    destination: "/app/chat",
                    body: JSON.stringify({
                        sender: "System",
                        content: message
                    })
                });
            } catch (error) {
                console.error('Error sending system message:', error);
            }
        }
    }

    /**
     * Display a received message in the chat
     * @param {Object} messageOutput - Message object from server
     */
    showMessageOutput(messageOutput) {
        const response = document.getElementById('response');
        if (!response) {
            console.error('Response container not found');
            return;
        }
        
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message';
        
        const senderSpan = document.createElement('span');
        senderSpan.className = 'message-sender';
        senderSpan.textContent = messageOutput.sender;
        
        const contentDiv = document.createElement('div');
        contentDiv.className = 'message-content';
        contentDiv.textContent = messageOutput.content;
        
        const timeSpan = document.createElement('span');
        timeSpan.className = 'message-time';
        
        // Format timestamp
        const timestamp = messageOutput.sentAt || new Date().toLocaleString();
        timeSpan.textContent = timestamp;
        
        messageDiv.appendChild(senderSpan);
        messageDiv.appendChild(contentDiv);
        messageDiv.appendChild(timeSpan);
        
        response.appendChild(messageDiv);
        
        // Auto-scroll to bottom
        response.scrollTop = response.scrollHeight;
        
        // Limit number of messages to prevent memory issues
        const messages = response.querySelectorAll('.message');
        if (messages.length > 100) {
            messages[0].remove();
        }
    }

    /**
     * Handle connection errors
     * @param {string} message - Error message to display
     */
    handleConnectionError(message) {
        const statusDiv = document.getElementById('status');
        if (statusDiv) {
            statusDiv.textContent = message;
            statusDiv.className = 'status disconnected';
        }
        console.error(message);
    }

    /**
     * Handle automatic reconnection
     */
    handleReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            const delay = Math.pow(2, this.reconnectAttempts) * 1000; // Exponential backoff
            
            console.log(`Attempting to reconnect in ${delay}ms (attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
            
            const statusDiv = document.getElementById('status');
            if (statusDiv) {
                statusDiv.textContent = `Reconnecting in ${delay/1000}s... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`;
                statusDiv.className = 'status disconnected';
            }
            
            setTimeout(() => {
                if (!this.isConnected) {
                    this.connect();
                }
            }, delay);
        } else {
            console.log('Max reconnection attempts reached');
            this.handleConnectionError('Connection lost. Please refresh the page to reconnect.');
        }
    }

    /**
     * Set loading state for connection button
     * @param {boolean} loading - Loading state
     */
    setLoadingState(loading) {
        const connectBtn = document.getElementById('connect');
        if (connectBtn) {
            if (loading) {
                connectBtn.innerHTML = '<span class="loading"></span> Connecting...';
                connectBtn.disabled = true;
            } else {
                connectBtn.innerHTML = 'Connect';
                connectBtn.disabled = this.isConnected;
            }
        }
    }
}

// Initialize the chat app when script loads
let chatApp;

// Initialize when DOM is ready or immediately if already loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        chatApp = new ChatApp();
    });
} else {
    chatApp = new ChatApp();
}

// Export for potential use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ChatApp;
}
