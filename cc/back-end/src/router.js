const express = require('express');
const router = express.Router();
const bodyParser = require('body-parser');
const index = require('./middleware/index');
const register = require('./middleware/register');
const login = require('./middleware/login');
const getUser = require('./middleware/getUser');
const editUser = require('./middleware/editUser');
const addDetectionHistory = require('./middleware/addDetectionHistory');
const getDetectionHistory = require('./middleware/getDetectionHistory');
const uploadTire = require('./middleware/uploadTire');

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