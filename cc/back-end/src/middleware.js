const bcrypt = require('bcryptjs');
const pgp = require('pg-promise')();
const { v4: uuidv4 } = require('uuid');
require('dotenv').config();
const db = pgp(process.env.DATABASE_URL);
const processFile = require("./upload");
const { format } = require("util");
const { Storage } = require("@google-cloud/storage");

// Instantiate a storage client with credentials
const storage = new Storage({ keyFilename: "key.json" });
const bucket = storage.bucket("dtire-images");

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
    db.none('INSERT INTO public.user (user_id, email, password, name, address, phone) VALUES($1, $2, $3, $4, $5, $6)', [user_id, email, hashedPassword, name, null, null])
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

    db.any('SELECT * FROM public.user WHERE user_id = $1', [user_id])
        .then(function (user) {
            // Check if the result is null
            if (user.length === 0) {
                res.status(404).json({
                    status: 404,
                    message: 'User not found'
                });
                return;
            } else {
                res.json({
                    status: 200,
                    user: user
                });
                return;
            }
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

function editUser(req, res) {
    const user_id = req.params.user_id;

    // Check if the user_id is existent
    db.any('SELECT * FROM public.user WHERE user_id = $1', [user_id])
        .then(function (user) {
            if (user.length === 0) {
                res.status(404).json({
                    status: 404,
                    message: 'User not found'
                });
                return;
            }
        })
        .catch(function (error) {
            res.json({
                status: 500,
                message: 'Internal server error while checking user',
                error: error
            });
            return;
        });

    let name = req.body.name || null;
    let email = req.body.email || null;
    let address = req.body.address || null;
    let phone = req.body.phone || null;

    // Validate email
    if (!/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(req.body.email) && email !== null) {
        console.log(req.body.email);
        res.status(400).json({
            status: 400,
            message: 'Invalid email address'
        });
        return;
    }

    // query if name or email is null
    db.one('SELECT name, email, address, phone FROM public.user WHERE user_id = $1', [user_id])
        .then(function (user) {
            if (name === null) {
                name = user.name;
            }
            if (email === null) {
                email = user.email;
            }
            if (address === null) {
                address = user.address;
            }
            if (phone === null) {
                phone = user.phone;
            }
            else {
                // check if email is already in use
                db.one('SELECT email FROM public.user WHERE email = $1 and user_id != $2', [email, user_id])
                    .then(function (user) {
                        // Check if the result is empty
                        if (user.email !== null) {
                            res.status(400).json({
                                status: 400,
                                message: 'Email already in use'
                            });
                            return;
                        }
                    });
            }
            // update user
            db.none('UPDATE public.user SET name = $1, email = $2, address = $3, phone = $4 WHERE user_id = $5', [name, email, address, phone, user_id])
                .then(function () {
                    res.status(204).json({
                        status: 204,
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

    // Check if the user_id is existent
    db.any('SELECT * FROM public.user WHERE user_id = $1', [user_id])
        .then(function (user) {
            if (user.length === 0) {
                res.status(404).json({
                    status: 404,
                    message: 'User not found'
                });
                return;
            }
        })
        .catch(function (error) {
            res.json({
                status: 500,
                message: 'Internal server error while checking user',
                error: error
            });
            return;
        });
    
    // Generate a random detection_id
    const detection_id = uuidv4();

    // Get current epoch time with timezone Asia/Jakarta without moment.js
    let timestamp = new Date().toLocaleString('en-US', { timeZone: 'Asia/Jakarta' });

    // convert timestamp to epoch time
    timestamp = new Date(timestamp).getTime();

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
            res.status(201).json({
                status: 201,
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

function getDetectionHistory(req, res) {
    // Sanitize user_id with pg-escape
    const user_id = req.params.user_id;

    // Check if the user_id is existent
    db.any('SELECT * FROM public.user WHERE user_id = $1', [user_id])
        .then(function (user) {
            if (user.length === 0) {
                res.status(404).json({
                    status: 404,
                    message: 'User not found'
                });
                return;
            }
        })
        .catch(function (error) {
            res.json({
                status: 500,
                message: 'Internal server error while checking user',
                error: error
            });
            return;
        });

    // get all from detection_history where user_id = user_id
    db.any('SELECT * FROM public.detection_history WHERE user_id = $1', [user_id])
        .then(function (detection_history) {
            // Change all date_of_check to readable format (e.g. to 2019-01-01 12:00:00) without moment.js
            for (let i = 0; i < detection_history.length; i++) {
                let date_of_check = new Date(+detection_history[i].date_of_check);
                detection_history[i].date_of_check = date_of_check.toLocaleString();
            }
            res.json({
                status: 200,
                message: 'Detection history retrieved',
                detection_history: detection_history
            });
            return;
        })
        .catch(function (error) {
            res.json({
                status: 500,
                message: 'Internal server error when getting detection history',
                error: error
            });
            return;
        });
}

async function uploadTire(req, res) {
    console.log("Halo");
    try {
        await processFile(req, res);
        // print the file name
        console.log("Halo");
        console.log("File name: " + req.file.filename);
        console.log(req.file.filename);
        if (!req.file) {
            return res.status(400).send({ message: "Please upload a file!" });
        }
        // Create a new blob in the bucket and upload the file data.
        const blob = bucket.file(req.file.originalname);
        const blobStream = blob.createWriteStream({
            resumable: false,
        });
        blobStream.on("error", (err) => {
            res.status(500).send({ message: err.message });
        });
        blobStream.on("finish", async (data) => {
            // Create URL for directly file access via HTTP.
            const publicUrl = format(
                `https://storage.googleapis.com/${bucket.name}/${blob.name}`
            );
            try {
                // Make the file public
                await bucket.file(req.file.originalname).makePublic();
            } catch {
                return res.status(500).send({
                    message:
                        `Uploaded the file successfully: ${req.file.originalname}, but public access is denied!`,
                    url: publicUrl,
                });
            }
            res.status(200).send({
                message: "Uploaded the file successfully: " + req.file.originalname,
                url: publicUrl,
            });
        });
        blobStream.end(req.file.buffer);
    } catch (err) {
        res.status(500).send({
            message: `Could not upload the file: ${req.file.originalname}. ${err}`,
        });
    }
}

module.exports = {
    index: index,
    register: register,
    login: login,
    getUser: getUser,
    editUser: editUser,
    addDetectionHistory: addDetectionHistory,
    getDetectionHistory: getDetectionHistory,
    uploadTire: uploadTire,
    db: db
};