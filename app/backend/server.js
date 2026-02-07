import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';

import authRoutes from './routes/authRoutes.js';
import chatRoutes from './routes/chatRoutes.js';
import './config/db.js';

dotenv.config();

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Test route (Check this in browser to see if server is alive)
app.get('/', (req, res) => {
    res.send('Backend is running...');
});

// Routes - This combines with the paths inside the route files
app.use('/api/auth', authRoutes);
app.use('/api/chat', chatRoutes);

// Add this temporarily to index.js
app.get('/debug', (req, res) => {
    res.json({
        message: "Server is working",
        available_paths: ["/api/auth/users", "/api/auth/register"]
    });
});


// 404 handler
app.use((req, res) => {
    res.status(404).json({ message: 'Route not found' });
});

// Global error handler
app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).json({ message: 'Server error', error: err.message });
});

const PORT = process.env.PORT || 4000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});