const express = require('express');
const router = express.Router();
const bodyParser = require('body-parser');
const { index, register, login, getUser, getUserDetails, editUserDetails, editUserNameOrEmail, addDetectionHistory, getDetectionHistory } = require('./middleware');

router.use(bodyParser.json());

router.get('/', index);
router.post('/api/register', register);
router.post('/api/login', login);
router.get('/api/user/:user_id', getUser);
router.get('/api/user_details/:user_id', getUserDetails);
router.get('/api/detection_history/:user_id', getDetectionHistory);
router.put('/api/user_details/:user_id', editUserDetails);
router.put('/api/user/:user_id', editUserNameOrEmail);
router.post('/api/detection_history', addDetectionHistory);

module.exports = router;