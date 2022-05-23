const express = require('express');
var cors = require('cors');
const app = express();
const routes = require('./router');
require('dotenv').config();

app.use(cors({
    // allow CORS from the env variable
    origin: process.env.CORS_ORIGIN
}));
app.use(routes);

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});