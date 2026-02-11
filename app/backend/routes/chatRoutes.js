import express from 'express';
import { sendMessage, getMessages } from '../controllers/chatController.js';

const router = express.Router();

// Send a new chat message
router.post('/send', sendMessage);

// Change this line:
router.get('/messages/:user1/:user2', getMessages);

// Get chat messages between two users
router.get('/:user1/:user2', getMessages);

export default router;
