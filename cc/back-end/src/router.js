const express = require('express');
const router = express.Router();
const bodyParser = require('body-parser');
const { index, register, login, getUser, editUser, addDetectionHistory, getDetectionHistory, uploadTire } = require('./middleware');

router.use(bodyParser.json());

router.get('/', index);
router.post('/api/register', register);
router.post('/api/login', login);
router.get('/api/user/:user_id', getUser);
router.get('/api/detection_history/:user_id', getDetectionHistory);
router.put('/api/user/:user_id', editUser);
router.post('/api/detection_history', addDetectionHistory);
router.post('/api/upload-tire', uploadTire);

module.exports = router;