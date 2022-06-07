const db = require('../config/database');

async function getUser(req, res) {
    const user_id = req.params.user_id;

    await db.any('SELECT * FROM public.user WHERE user_id = $1', [user_id])
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

module.exports = getUser;