import pool from '../config/db.js';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import { validationResult } from 'express-validator';

export const registerUser = async (req, res) => {
    // 1. Check for express-validator errors first
   const errors = validationResult(req);
       if (!errors.isEmpty()) {
           // This will send back exactly which field failed (e.g., "Age is required")
           return res.status(400).json({
               success: false,
               errors: errors.array(),
               receivedBody: req.body // This shows us what actually arrived
           });
       }

    // 2. Extract fields from req.body (Multer populated this)
    const { name, email, password, age, gender } = req.body;

    // 3. Get the image path from Multer (req.file)
    const profile_image = req.file ? req.file.path : null;

    // 4. Manual check to ensure no fields are empty
    if (!name || !email || !password || !age || !gender) {
        return res.status(400).json({
            success: false,
            message: "All fields required",
            debug: {
                name: !!name,
                email: !!email,
                password: !!password,
                age: !!age,
                gender: !!gender,
                file: !!req.file
            }
        });
    }

    try {
        // 5. Check if user already exists
        const userExist = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
        if (userExist.rows.length > 0) {
            return res.status(400).json({ success: false, message: "User already registered" });
        }

        // 6. Hash the password
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        // 7. Save to PostgreSQL (Make sure your table has these columns!)
        const newUser = await pool.query(
            'INSERT INTO users (name, email, password, age, gender, profile_image) VALUES ($1, $2, $3, $4, $5, $6) RETURNING id, name, email, age, gender, profile_image',
            [name, email, hashedPassword, age, gender, profile_image]
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

export const getAllUsers = async (req, res) => {
    try {
        // This line asks the REAL database for the REAL users
        const result = await pool.query('SELECT id, name, email FROM users ORDER BY id DESC');

        // If the table is empty, return an empty list []
        // If it has users, return the users
        res.json(result.rows);

    } catch (error) {
        console.error("Database Query Error:", error.message);
        res.status(500).json({ message: "Error fetching users", error: error.message });
    }
};

export const loginUser = async (req, res) => {
    const { email, password } = req.body;

    try {
        // 1. Check if the user exists
        const result = await pool.query('SELECT * FROM users WHERE email = $1', [email]);

        if (result.rows.length === 0) {
            return res.status(400).json({ message: "Invalid Email or Password" });
        }

        const user = result.rows[0];

        // 2. Compare the typed password with the hashed password in DB
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ message: "Invalid Email or Password" });
        }

        // 3. Create a JWT Token (Valid for 1 day)
        const token = jwt.sign(
            { id: user.id, email: user.email },
            process.env.JWT_SECRET,
            { expiresIn: '1d' }
        );

        // 4. Send the token back to the Android app
        res.json({
            message: "Login successful!",
            token,
            user: { id: user.id, name: user.name, email: user.email }
        });

    } catch (err) {
        console.error(err.message);
        res.status(500).send("Server Error");
    }
};

