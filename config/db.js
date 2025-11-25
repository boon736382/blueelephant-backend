// db.js
import pkg from 'pg';
const { Pool } = pkg;

const connectionString = process.env.DATABASE_URL;

if (!connectionString) {
  console.error('Error: DATABASE_URL is not defined in environment.');
  // Don't exit; keep running but queries will fail with clearer errors later.
}

const pool = new Pool({
  connectionString,
  ssl: connectionString ? { rejectUnauthorized: false } : false,
});

pool.on('connect', () => {
  console.log('Postgres pool connected');
});

pool.on('error', (err) => {
  console.error('Postgres pool error', err);
});

/**
 * Initialize DB: create users table if not exists.
 * Call this once at server startup.
 */
export async function initDb() {
  const createUsersTable = `
    CREATE TABLE IF NOT EXISTS users (
      id SERIAL PRIMARY KEY,
      email VARCHAR(255) UNIQUE NOT NULL,
      password VARCHAR(255) NOT NULL
    );
  `;
  try {
    await pool.query(createUsersTable);
    console.log('Ensured users table exists');
  } catch (err) {
    console.error('Error creating users table:', err);
    throw err;
  }
}

export default pool;
