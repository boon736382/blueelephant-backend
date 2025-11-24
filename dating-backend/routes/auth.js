import express from "express";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import pool from "../db.js"; // PostgreSQL connection

const router = express.Router();
const JWT_SECRET = process.env.JWT_SECRET || "your_secret_key";

// Register user
router.post("/register", async (req, res) => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ success: false, message: "Email and password are required" });
    }

    try {
        // Check if email already exists
        const userExists = await pool.query("SELECT * FROM users WHERE email=$1", [email]);
        if (userExists.rows.length > 0) {
            return res.status(400).json({ success: false, message: "Email already exists" });
        }

        // Hash password
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        // Insert user into DB
        const newUser = await pool.query(
            "INSERT INTO users (email, password) VALUES ($1, $2) RETURNING id, email",
            [email, hashedPassword]
        );

        res.status(201).json({ success: true, message: "User registered successfully" });
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, message: "Server error" });
    }
});

export default router;
