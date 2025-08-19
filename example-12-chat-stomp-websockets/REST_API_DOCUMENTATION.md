# Chat Application REST API

## Overview
The chat application now supports both WebSocket messaging and REST API endpoints for broadcasting messages.

## Endpoints

### 1. Get All Messages
**GET** `/messages`

Retrieves all chat messages from the database ordered by timestamp (newest first).

**Response:**
```json
[
  {
    "id": 1,
    "sender": "John",
    "content": "Hello everyone!",
    "sentAt": "2025-08-19T10:30:00"
  },
  {
    "id": 2,
    "sender": "Jane",
    "content": "Hi John!",
    "sentAt": "2025-08-19T10:31:00"
  }
]
```

### 2. Broadcast Message (NEW)
**POST** `/messages/broadcast`

Broadcasts a message to all connected WebSocket clients and saves it to the database.

**Request Body:**
```json
{
  "sender": "API User",
  "content": "This message was sent via REST API!"
}
```

**Response:**
```json
{
  "id": 3,
  "sender": "API User",
  "content": "This message was sent via REST API!",
  "sentAt": "2025-08-19T10:32:00"
}
```

**Status Codes:**
- `201 Created`: Message successfully created and broadcast
- `400 Bad Request`: Invalid request body (missing sender or content)

## Usage Examples

### Using curl
```bash
# Get all messages
curl -X GET http://localhost:8080/messages

# Broadcast a message
curl -X POST http://localhost:8080/messages/broadcast \
  -H "Content-Type: application/json" \
  -d '{
    "sender": "System Bot",
    "content": "Server maintenance in 5 minutes"
  }'
```

### Using JavaScript fetch
```javascript
// Broadcast a message
fetch('/messages/broadcast', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    sender: 'JavaScript Client',
    content: 'Hello from the web!'
  })
})
.then(response => response.json())
.then(data => console.log('Message sent:', data));
```

### Using Postman
1. Create a new POST request to `http://localhost:8080/messages/broadcast`
2. Set Content-Type header to `application/json`
3. Add request body:
   ```json
   {
     "sender": "Postman User",
     "content": "Testing the REST API"
   }
   ```

## Integration with WebSocket
- Messages sent via REST API will appear in real-time to all connected WebSocket clients
- The REST endpoint uses the same validation and storage logic as WebSocket messages
- Both methods result in identical message format and behavior

## Use Cases
- **System notifications**: Send automated messages from server processes
- **External integrations**: Allow other services to send messages to the chat
- **Batch operations**: Send multiple messages programmatically
- **Testing**: Easy way to test the chat functionality without WebSocket setup
