const express = require('express');
const router = express.Router();
const bodyParser = require('body-parser');
const { index, register, login } = require('./middleware');

router.use(bodyParser.json());

router.get('/', index);
router.post('/api/register', register);
router.post('/api/login', login);

module.exports = router;