import express from 'express';
import { body } from 'express-validator';
// Add loginUser to the curly braces
import multer from 'multer'; // 👈 Add this
import { registerUser, getAllUsers, loginUser } from '../controllers/authController.js';


const upload = multer({ dest: 'uploads/' });
const router = express.Router();

router.post('/register', upload.single('profile_image'), [
    body('email').isEmail().withMessage('Enter a valid email'),
    body('password').isLength({ min: 6 }).withMessage('Password must be at least 6 characters'),
    body('name').notEmpty().withMessage('Name is required'),
    body('age').notEmpty().withMessage('Age is required'),    // 👈 Add this
    body('gender').notEmpty().withMessage('Gender is required') // 👈 Add this
], registerUser);

router.post('/update-profile', upload.single('profile_image'), updateProfile); // Add this!
router.get('/users', getAllUsers);
router.post('/login', loginUser);

export default router;