const bcrypt = require('bcryptjs');
const { response } = require('express');
const pgp = require('pg-promise')();
const { v4: uuidv4 } = require('uuid');
require('dotenv').config();
const db = pgp(process.env.DATABASE_URL);

function index(req, res) {
    res.send("<h1>Welcome to the API.</h1>");
}

function register(req, res) {

    // Test if name, email, and password are not null
    if (!req.body.name || !req.body.email || !req.body.password) {
        // Send 400 with JSON error message
        res.status(400).json({
            status: 400,
            message: 'You need to provide a name, email, and password'
        });
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
    db.none('INSERT INTO public.user (user_id, email, password, name) VALUES($1, $2, $3, $4)', [user_id, email, hashedPassword, name])
        .then(function () {
            // Insert the user data to user_details: user_id, address (if any), phone number (if any)
            // then continue to send 200 status code
            db.none('INSERT INTO public.user_details(user_id, address, phone) VALUES($1, $2, $3)', [user_id, null, null])
                .then(function () {
                    res.status(200).json({
                        status: 200,
                        message: 'User registered'
                    });
                    return;
                })
                .catch(function (error) {
                    res.status(500).json({
                        status: 500,
                        message: 'Internal server error',
                        error: error
                    });
                    return;
                });
        })
        .catch(function (error) {
            res.status(500).json({
                status: 500,
                message: 'Internal server error',
                error: error
            });
            return;
        });

    return;

}

function login(req, res) {

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
    db.one('SELECT * FROM public.user WHERE email = $1', [email])
        .then(function (user) {
            // compare the password
            if (bcrypt.compareSync(password, user.password)) {
                // send the user object back to the client
                res.json({
                    status: 200,
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

module.exports = {
    index: index,
    register: register,
    login: login
};