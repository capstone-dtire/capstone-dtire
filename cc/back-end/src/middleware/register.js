const bcrypt = require('bcryptjs');
const db = require('../config/database');
const { v4: uuidv4 } = require('uuid');

async function register(req, res) {

    // Test if name, email, and password are not null
    if (!req.body.name || !req.body.email || !req.body.password) {
        // Send 400 with JSON error message
        res.status(400).json({
            status: 400,
            message: 'You need to provide a name, email, and password'
        });
        return;
    }

    // Test if req.body.email is valid
    if (!/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(req.body.email)) {
        res.status(400).json({
            status: 400,
            message: 'Invalid email address'
        });
        return;
    }

    // Test if password is 8 characters long
    if (req.body.password.length < 8) {
        res.status(400).json({
            status: 400,
            message: 'Password must be at least 8 characters long'
        });
        return;
    }

    const name = req.body.name;
    const email = req.body.email;
    const password = req.body.password;

    // hash the password
    let hashedPassword = bcrypt.hashSync(password, 8);

    // generate random id with uuid
    const user_id = uuidv4();

    // try to insert the user into the database
    await db.none('INSERT INTO public.user (user_id, email, password, name, address, phone) VALUES($1, $2, $3, $4, $5, $6)', [user_id, email, hashedPassword, name, null, null])
        .then(function () {
            res.status(201).json({
                status: 201,
                message: 'User registered'
            });
            return;
        })
        .catch(function (error) {
            res.status(500).json({
                status: 500,
                message: 'Internal server error when registering user',
                error: error
            });
            return;
        });

    return;

}

module.exports = register;