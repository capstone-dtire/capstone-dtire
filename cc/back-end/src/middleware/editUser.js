const db = require('../config/database');

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
    let url_picture = req.body.url_picture || null;

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
    db.one('SELECT name, email, address, phone, url_picture FROM public.user WHERE user_id = $1', [user_id])
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
            if (url_picture === null) {
                url_picture = user.url_picture;
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
            db.none('UPDATE public.user SET name = $1, email = $2, address = $3, phone = $4, url_picture = $6 WHERE user_id = $5', [name, email, address, phone, user_id, url_picture])
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

module.exports = editUser;