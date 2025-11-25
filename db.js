import pg from "pg";
import dotenv from "dotenv";

dotenv.config();
const { Pool } = pg;

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: { rejectUnauthorized: false } // Required for Render Postgres
});

pool.connect()
  .then(() => console.log("Database connected"))
  .catch(err => console.error("Database connection error:", err));

export default pool;
