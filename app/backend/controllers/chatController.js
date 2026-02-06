import pool from '../config/db.js'; // PostgreSQL pool connection

// Send a chat message
export const sendMessage = async (req, res) => {
const { sender, receiver, message } = req.body;

if (!sender || !receiver || !message) {
return res.status(400).json({ message: 'sender, receiver, and message are required' });
}

try {
const result = await pool.query(
'INSERT INTO messages(sender, receiver, message, created_at) VALUES($1, $2, $3, NOW()) RETURNING *',
[sender, receiver, message]
);
res.status(201).json(result.rows[0]);
} catch (err) {
console.error('Error sending message:', err);
res.status(500).json({ message: 'Failed to send message', error: err.message });
}
};

// Get chat messages between two users
export const getMessages = async (req, res) => {
const { user1, user2 } = req.params;

try {
const result = await pool.query(
'SELECT * FROM messages WHERE (sender=$1 AND receiver=$2) OR (sender=$2 AND receiver=$1) ORDER BY created_at ASC',
[user1, user2]
);
res.status(200).json(result.rows);
} catch (err) {
console.error('Error fetching messages:', err);
res.status(500).json({ message: 'Failed to fetch messages', error: err.message });
}
};
