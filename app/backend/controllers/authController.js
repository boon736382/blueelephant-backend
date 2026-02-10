import pool from '../config/db.js';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import { validationResult } from 'express-validator';

// --- UPDATE PROFILE (Used in OnboardingActivity) ---
export const updateProfile = async (req, res) => {
    const { email, name, age, gender } = req.body;
    const profile_image = req.file ? req.file.path : null;

    try {
        const updatedUser = await pool.query(
            `UPDATE users
             SET name = $1, age = $2, gender = $3,
                 profile_image = COALESCE($4, profile_image),
                 status = 'Online'
             WHERE email = $5
             RETURNING id, name, email, age, gender, profile_image, status`,
            [name, age, gender, profile_image, email]
        );

        if (updatedUser.rows.length === 0) {
            return res.status(404).json({ success: false, message: "User not found" });
        }

        res.json({
            success: true,
            message: "Profile updated successfully!",
            user: updatedUser.rows[0]
        });
    } catch (err) {
        console.error("Update Error:", err.message);
        res.status(500).json({ success: false, message: err.message });
    }
};

// --- REGISTER USER (Now allows partial data to ensure pgAdmin saves) ---
export const registerUser = async (req, res) => {
    const { name, email, password, age, gender } = req.body;
    const profile_image = req.file ? req.file.path : null;

    // Only Email and Password are strictly required for the first step
    if (!email || !password) {
        return res.status(400).json({
            success: false,
            message: "Email and Password are required"
        });
    }

    try {
        const userExist = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
        if (userExist.rows.length > 0) {
            return res.status(400).json({ success: false, message: "User already registered" });
        }

        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        // We use placeholders or nulls for name/age/gender if they aren't provided yet
        const newUser = await pool.query(
            `INSERT INTO users (name, email, password, age, gender, profile_image, status)
             VALUES ($1, $2, $3, $4, $5, $6, $7)
             RETURNING id, name, email, age, gender, profile_image, status`,
            [name || 'New User', email, hashedPassword, age || 0, gender || 'Not Specified', profile_image, 'Online']
        );

        res.status(201).json({
            success: true,
            message: "Registration successful!",
            user: newUser.rows[0]
        });

    } catch (err) {
        console.error("Registration Error:", err.message);
        res.status(500).json({ success: false, message: "Server Error", error: err.message });
    }
};

// --- LOGIN USER ---
export const loginUser = async (req, res) => {
    const { email, password } = req.body;

    try {
        const result = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
        if (result.rows.length === 0) {
            return res.status(400).json({ message: "Invalid Email or Password" });
        }

        const user = result.rows[0];
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ message: "Invalid Email or Password" });
        }

        await pool.query('UPDATE users SET status = $1 WHERE id = $2', ['Online', user.id]);

        const token = jwt.sign(
            { id: user.id, email: user.email },
            process.env.JWT_SECRET || 'fallback_secret',
            { expiresIn: '1d' }
        );

        res.json({
            message: "Login successful!",
            token,
            user: {
                id: user.id,
                name: user.name,
                email: user.email,
                age: user.age,
                gender: user.gender,
                profile_image: user.profile_image,
                status: 'Online'
            }
        });

    } catch (err) {
        console.error("Login Error:", err.message);
        res.status(500).send("Server Error");
    }
};

// --- GET ALL USERS ---
export const getAllUsers = async (req, res) => {
    try {
        const result = await pool.query(
            'SELECT id, name, email, profile_image, status, age, gender FROM users ORDER BY id DESC'
        );
        res.json(result.rows);
    } catch (error) {
        res.status(500).json({ message: "Error fetching users", error: error.message });
    }
};

// --- LOGOUT ---
export const logoutUser = async (req, res) => {
    const { userId } = req.body;
    try {
        await pool.query('UPDATE users SET status = $1 WHERE id = $2', ['Offline', userId]);
        res.status(200).json({ success: true, message: "Logged out" });
    } catch (err) {
        res.status(500).json({ success: false, message: "Logout failed" });
    }
};