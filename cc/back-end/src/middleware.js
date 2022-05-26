const bcrypt = require('bcryptjs');
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
                        message: 'Internal server error while registering user details',
                        error: error
                    });
                    return;
                });
        })
        .catch(function (error) {
            res.status(500).json({
                status: 500,
                message: 'Internal server error while registering user',
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

function getUser(req, res) {
    const user_id = req.params.user_id;

    db.one('SELECT user_id, name FROM public.user WHERE user_id = $1', [user_id])
        .then(function (user) {
            res.json({
                status: 200,
                user: user
            });
            return;
        })
        .catch(function (error) {
            res.json({
                status: 500,
                message: 'Internal server error while getting user',
                error: error
            });
            return;
        });
}

function getUserDetails(req, res) {
    const user_id = req.params.user_id;

    // get all from user_details where user_id = user_id
    db.one('SELECT * FROM public.user_details WHERE user_id = $1', [user_id])
        .then(function (user_details) {
            res.json({
                status: 200,
                user_details: user_details
            });
            return;
        })
        .catch(function (error) {
            res.json({
                status: 500,
                message: 'Internal server error while getting user details',
                error: error
            });
            return;
        });
}

function editUserDetails(req, res) {
    const user_id = req.params.user_id;
    let address = req.body.address || null;
    let phone = req.body.phone || null;

    // query if address or phone is null
    db.one('SELECT address, phone FROM public.user_details WHERE user_id = $1', [user_id])
        .then(function (user_details) {
            if (address === null) {
                address = user_details.address;
            }
            if (phone === null) {
                phone = user_details.phone;
            }
            // update user_details
            db.none('UPDATE public.user_details SET address = $1, phone = $2 WHERE user_id = $3', [address, phone, user_id])
                .then(function () {
                    res.json({
                        status: 200,
                        message: 'User details updated'
                    });
                    return;
                })
                .catch(function (error) {
                    res.json({
                        status: 500,
                        message: 'Internal server error when updating user details',
                        error: error
                    });
                    return;
                });
        })
        .catch(function (error) {
            res.json({
                status: 500,
                message: 'Internal server error when getting user details',
                error: error
            });
            return;
        });
}

function editUserNameOrEmail(req, res) {
    const user_id = req.params.user_id;
    let name = req.body.name || null;

    // Validate email
    if (!/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(req.body.email)) {
        res.status(400).json({
            status: 400,
            message: 'Invalid email address'
        });
        return;
    }

    let email = req.body.email || null;

    // query if name or email is null
    db.one('SELECT name, email FROM public.user WHERE user_id = $1', [user_id])
        .then(function (user) {
            if (name === null) {
                name = user.name;
            }
            if (email === null) {
                email = user.email;
            }
            else {
                // check if email is already in use
                db.one('SELECT email FROM public.user WHERE email = $1 and user_id != $2', [email, user_id])
                    .then(function (user) {
                        // Check if the result is empty
                        if (user.email === null) {
                            res.status(400).json({
                                status: 400,
                                message: 'Email already in use'
                            });
                            return;
                        }
                    });
            }
            // update user
            db.none('UPDATE public.user SET name = $1, email = $2 WHERE user_id = $3', [name, email, user_id])
                .then(function () {
                    res.json({
                        status: 200,
                        message: 'User updated'
                    });
                    return;
                })
                .catch(function (error) {
                    res.json({
                        status: 500,
                        message: 'Internal server error when updating user',
                        error: error
                    });
                    return;
                });
        })
        .catch(function (error) {
            res.json({
                status: 500,
                message: 'Internal server error when getting user',
                error: error
            });
            return;
        });
}

function addDetectionHistory(req, res) {
    const user_id = req.body.user_id;
    
    // Generate a random detection_id
    const detection_id = uuidv4();

    // Get current epoch time with timezone Asia/Jakarta without moment.js
    const timestamp = new Date().toLocaleString('en-US', { timeZone: 'Asia/Jakarta' });

    const condition_title = req.body.condition_title;
    const recommendation = req.body.recommendation;

    // check if condition_title or recommendation is null
    // reject if any of them is null
    if (condition_title === null || recommendation === null || user_id === null) {
        res.json({
            status: 400,
            message: 'Invalid request'
        });
        return;
    }

    // insert into detection_history
    db.none('INSERT INTO public.detection_history(detection_id, user_id, condition_title, recommendation, date_of_check) VALUES($1, $2, $3, $4, $5)', [detection_id, user_id, condition_title, recommendation, timestamp])
        .then(function () {
            res.json({
                status: 200,
                message: 'Detection history added'
            });
            return;
        })
        .catch(function (error) {
            res.json({
                status: 500,
                message: 'Internal server error when adding detection history',
                error: error
            });
            return;
        });
}

module.exports = {
    index: index,
    register: register,
    login: login,
    getUser: getUser,
    getUserDetails: getUserDetails,
    editUserDetails: editUserDetails,
    editUserNameOrEmail: editUserNameOrEmail,
    addDetectionHistory: addDetectionHistory 
};