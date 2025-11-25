import pool from "../config/db.js";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

// REGISTER
export const register = async (req, res) => {
    try {
        const { name, email, password } = req.body; // include name

        // Validate input
        if (!name || !email || !password) {
            return res.status(400).json({ success: false, message: "Name, email & password required" });
        }

        // Check if user already exists
        const found = await pool.query(
            "SELECT * FROM users WHERE email = $1",
            [email]
        );
        if (found.rows.length > 0) {
            return res.status(400).json({ success: false, message: "Email already exists" });
        }

        // Hash password
        const hash = await bcrypt.hash(password, 10);

        // Insert user with name, email, password
        await pool.query(
            "INSERT INTO users(name, email, password) VALUES($1, $2, $3)",
            [name, email, hash]
        );

        res.json({ success: true, message: "User registered!" });
    } catch (err) {
        console.error("REGISTER ERROR:", err);
        res.status(500).json({ success: false, message: "Server error" });
    }
};

// LOGIN
export const login = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ success: false, message: "Email & password required" });
        }

        const result = await pool.query(
            "SELECT * FROM users WHERE email = $1",
            [email]
        );

        if (result.rows.length === 0) {
            return res.status(400).json({ success: false, message: "Invalid email" });
        }

        const user = result.rows[0];

        const match = await bcrypt.compare(password, user.password);
        if (!match) {
            return res.status(400).json({ success: false, message: "Wrong password" });
        }

        // Sign JWT with user id and optionally name/email
        const token = jwt.sign(
            { id: user.id, name: user.name, email: user.email },
            process.env.JWT_SECRET,
            { expiresIn: "7d" }
        );

        res.json({ success: true, message: "Login success", token });
    } catch (err) {
        console.error("LOGIN ERROR:", err);
        res.status(500).json({ success: false, message: "Server error" });
    }
};
