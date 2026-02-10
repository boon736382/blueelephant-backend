import express from 'express';
import { body } from 'express-validator';
import multer from 'multer';
import { registerUser, loginUser, getAllUsers, updateProfile } from '../controllers/authController.js';

const upload = multer({ dest: 'uploads/' });
const router = express.Router();

/**
 * REGISTER ROUTE
 * Relaxed validation so the initial account creation (Email/Password)
 * saves to pgAdmin even before onboarding details are provided.
 */
router.post('/register', upload.single('profile_image'), [
    body('email').isEmail().withMessage('Enter a valid email'),
    body('password').isLength({ min: 6 }).withMessage('Password must be at least 6 characters')
    // Note: 'name', 'age', and 'gender' checks are removed here because
    // we handle them in 'update-profile' or as optional fields.
], registerUser);

/**
 * UPDATE PROFILE ROUTE
 * Used by OnboardingActivity to fill in name, age, gender, and image.
 */
router.post('/update-profile', upload.single('profile_image'), updateProfile);

/**
 * LOGIN ROUTE
 */
router.post('/login', loginUser);

/**
 * GET USERS ROUTE
 */
router.get('/users', getAllUsers);

export default router;