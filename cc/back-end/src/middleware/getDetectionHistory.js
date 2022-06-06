const db = require('../config/database');

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

module.exports = getDetectionHistory;