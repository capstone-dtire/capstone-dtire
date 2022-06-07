const db = require('../config/database');
const { v4: uuidv4 } = require('uuid');

async function addDetectionHistory(req, res) {
    const user_id = req.body.user_id;

    // Check if the user_id is existent
    await db.any('SELECT * FROM public.user WHERE user_id = $1', [user_id])
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
    await db.none('INSERT INTO public.detection_history(detection_id, user_id, condition_title, recommendation, date_of_check) VALUES($1, $2, $3, $4, $5)', [detection_id, user_id, condition_title, recommendation, timestamp])
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

module.exports = addDetectionHistory;