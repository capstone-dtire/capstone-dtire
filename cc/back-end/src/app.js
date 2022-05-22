const express = require('express');
var cors = require('cors');
const app = express();
const routes = require('./router');

app.use(cors());
app.use(routes);

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});