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
app.use('/uploads', express.static('uploads'));

// Test route (Check this in browser to see if server is alive)
app.get('/', (req, res) => {
    res.send('Backend is running...');
});

// Routes - This combines with the paths inside the route files
app.use('/api/auth', authRoutes);
app.use('/api/chat', chatRoutes);

// Add this temporarily to index.js
// Replace your current /debug route with this in index.js
app.get('/debug-routes', (req, res) => {
    const routes = [];
    app._router.stack.forEach((middleware) => {
        if (middleware.route) {
            routes.push(`${Object.keys(middleware.route.methods)} ${middleware.route.path}`);
        } else if (middleware.name === 'router') {
            // This part now captures the prefix (like /api/auth)
            const prefix = middleware.regexp.toString()
                .replace('/^\\', '')
                .replace('\\/?(?=\\/|$)/i', '')
                .replace(/\\/g, '');

            middleware.handle.stack.forEach((handler) => {
                if (handler.route) {
                    const path = handler.route.path;
                    routes.push(`${Object.keys(handler.route.methods)} ${prefix}${path}`);
                }
            });
        }
    });
    res.json({ routes });
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

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on port ${PORT}`);
});