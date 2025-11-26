import 'dotenv/config'; // ES module style


// auth.js
import express from "express";
import pool from "../config/db.js"; // Ensure this exports a connected pg Pool
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";

const router = express.Router();

// --- REGISTER ---
router.post("/register", async (req, res) => {
  const { name, email, password } = req.body;

  // Validate all fields
  if (!name || !email || !password) {
    return res
      .status(400)
      .json({ success: false, message: "Name, email & password required" });
  }

  try {
    // Check if email already exists
    const existing = await pool.query(
      "SELECT * FROM users WHERE email = $1",
      [email]
    );

    if (existing.rows.length > 0) {
      return res
        .status(400)
        .json({ success: false, message: "Email already exists" });
    }

    // Hash the password
    const hashedPassword = await bcrypt.hash(password, 10);

    // Insert new user including name
    await pool.query(
      "INSERT INTO users(name, email, password) VALUES($1, $2, $3)",
      [name, email, hashedPassword]
    );

    return res
      .status(201)
      .json({ success: true, message: "Registered successfully" });
  } catch (err) {
    console.error("Register error:", err);
    return res
      .status(500)
      .json({ success: false, message: "Server error" });
  }
});

// --- LOGIN ---
router.post("/login", async (req, res) => {
  const { email, password } = req.body;

  // Validate input
  if (!email || !password) {
    return res
      .status(400)
      .json({ success: false, message: "Email & password required" });
  }

  try {
    // Get user by email
    const result = await pool.query(
      "SELECT * FROM users WHERE email = $1",
      [email]
    );

    if (result.rows.length === 0) {
      return res
        .status(401)
        .json({ success: false, message: "Invalid email or password" });
    }

    const user = result.rows[0];

    // Compare passwords
    const match = await bcrypt.compare(password, user.password);
    if (!match) {
      return res
        .status(401)
        .json({ success: false, message: "Invalid email or password" });
    }

    // Sign JWT token
    const token = jwt.sign(
      { id: user.id, email: user.email, name: user.name },
      process.env.JWT_SECRET,
      { expiresIn: "7d" }
    );

    return res
      .status(200)
      .json({ success: true, message: "Login successful", token });
  } catch (err) {
    console.error("Login error:", err);
    return res
      .status(500)
      .json({ success: false, message: "Server error" });
  }
});

export default router;
