import pkg from 'pg';
import dotenv from 'dotenv';
dotenv.config();

const { Pool } = pkg;

// Create a pool using DATABASE_URL for cloud deployment
const pool = new Pool({
connectionString: process.env.DATABASE_URL,
ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false,
});

// Test connection
pool.connect()
.then(() => console.log('Connected to PostgreSQL database'))
.catch(err => console.error('PostgreSQL connection error:', err));

export default pool;
