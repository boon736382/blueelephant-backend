import { registerUser, loginUser, getAllUsers } from '../controllers/authController.js'; // 1. Add getAllUsers here
import { Router } from 'express';

const router = Router();

router.post('/register', registerUser);
router.post('/login', loginUser);


router.get('/users', getAllUsers);

export default router;