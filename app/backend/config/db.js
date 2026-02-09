import pkg from 'pg';
import dotenv from 'dotenv';
dotenv.config();

const { Pool } = pkg;

const pool = new Pool({
    connectionString: process.env.DATABASE_URL,
    ssl: { rejectUnauthorized: false },
});

// This function creates the table if it doesn't exist
const initDb = async () => {
    const queryText = `
    CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        name VARCHAR(100),
        email VARCHAR(100) UNIQUE NOT NULL,
        password TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );`;

    try {
        await pool.query(queryText);
        console.log("✅ Users table is ready");
    } catch (err) {
        console.error("❌ Error initializing database:", err.message);
    }
};

// Run the initialization
initDb();

export default pool;