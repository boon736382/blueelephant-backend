import pool from '../config/db.js';

// Send a chat message
export const sendMessage = async (req, res) => {
    // Note: Matches your Android MessageRequest fields
    const { senderEmail, receiverEmail, content } = req.body;

    if (!senderEmail || !receiverEmail || !content) {
        return res.status(400).json({ message: 'All fields are required' });
    }

    try {
        const result = await pool.query(
            `INSERT INTO messages(sender_email, receiver_email, content, created_at)
             VALUES($1, $2, $3, NOW())
             RETURNING *`,
            [senderEmail, receiverEmail, content]
        );
        res.status(201).json(result.rows[0]);
    } catch (err) {
        console.error('Error sending message:', err);
        res.status(500).json({ message: 'Failed to send message', error: err.message });
    }
};

// Get chat messages (Filters out messages older than 24 hours)
export const getMessages = async (req, res) => {
    const { user1, user2 } = req.params;

    try {
        // This query fetches messages between users but ONLY if they are less than 24 hours old
        const result = await pool.query(
            `SELECT * FROM messages
             WHERE ((sender_email=$1 AND receiver_email=$2) OR (sender_email=$2 AND receiver_email=$1))
             AND created_at > NOW() - INTERVAL '24 hours'
             ORDER BY created_at ASC`,
            [user1, user2]
        );
        res.status(200).json(result.rows);
    } catch (err) {
        console.error('Error fetching messages:', err);
        res.status(500).json({ message: 'Failed to fetch messages' });
    }
};