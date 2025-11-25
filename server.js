import express from "express";
import dotenv from "dotenv";
import cors from "cors";
import authRoutes from "./routes/auth.js";

dotenv.config();
const app = express();

// Middlewares
app.use(cors());
app.use(express.json());

// Root route (fixes "Cannot GET /")
app.get("/", (req, res) => {
    res.json({ message: "API is running correctly 🎉" });
});

// Auth API
app.use("/api/auth", authRoutes);

// Start server
const PORT = process.env.PORT || 10000;
app.listen(PORT, () => {
    console.log("Server running on port " + PORT);
});
