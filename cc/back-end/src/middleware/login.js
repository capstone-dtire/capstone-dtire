const bcrypt = require('bcryptjs');
const db = require('../config/database');

async function login(req, res) {

    // Check if email is valid
    if (!/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(req.body.email)) {
        res.status(400).json({
            status: 400,
            message: 'Invalid email address'
        });
        return;
    }

    const email = req.body.email;
    const password = req.body.password;

    // get the user from the database
    await db.one('SELECT * FROM public.user WHERE email = $1', [email])
        .then(function (user) {
            // compare the password
            if (bcrypt.compareSync(password, user.password)) {
                // send the user object back to the client
                res.json({
                    status: 200,
                    user_id: user.user_id,
                    message: 'User logged in',
                });
                return;
            } else {
                res.json({
                    status: 401,
                    message: 'User not found or incorrect password'
                });
                return;
            }
        })
        .catch(function (error) {
            res.json({
                status: 401,
                message: 'User not found or incorrect password'
            });
            return;
        });
}

module.exports = login;