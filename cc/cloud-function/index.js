/**
 * Responds to any HTTP request.
 *
 * @param {!express:Request} req HTTP request context.
 * @param {!express:Response} res HTTP response context.
 */
exports.reqPred = (req, res) => {
    // let message = req.query.message || req.body.message || 'Hello World!';
    // res.status(200).send(message);

    const body = req.body;
    const instances = body.instances;
    const endpointId = "4805507928171741184";
    const project = 'evident-plane-343600';
    const location = 'us-central1';
    const util = require('util');
    // const {readFile} = require('fs');
    // const readFileAsync = util.promisify(readFile);

    // Imports the Google Cloud Prediction Service Client library
    const { PredictionServiceClient } = require('@google-cloud/aiplatform');

    // Specifies the location of the api endpoint
    const clientOptions = {
        apiEndpoint: 'us-central1-aiplatform.googleapis.com',
    };

    // Instantiates a client
    const client = new PredictionServiceClient(clientOptions);

    const endpoint = `projects/${project}/locations/${location}/endpoints/${endpointId}`;

    console.log(instances);

    const request = {
        endpoint,
        instances
    }

    const response = client.predict(request);
    res.status(200).send(request);
}