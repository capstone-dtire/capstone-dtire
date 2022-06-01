# cloud-function

## What is this?
This is a cloud function that is triggered by a HTTP request. We're using it because the endpoint for Vertex AI is not public.

## Okay, how'd it work?
The cloud function is triggered by a HTTP request. The cloud function will generate a token which then will be used to call the ML endpoint. Make sure to use a POST request with a JSON body. The example JSON body contains the following fields:

    {
        "instances": [
            [
            [
                [138, 30, 66],
                [130, 20, 56]
            ],
            [
                [126, 38, 61],
                [122, 24, 57]
            ]
            ]
        ]
    }

The above JSON field is representing images, which can be represented different ways. In that encoding scheme the first two dimensions represent the rows and columns of the image, and the third dimension contains lists (vectors) of the R, G, and B values for each pixel.

After sending a request to the cloud function, it will return a JSON response with the following fields:

    {
        "predictions": [
            [
                0.384460628
            ]
        ],
        "deployedModelId": "3992995223524343808",
        "model": "projects/912000053681/locations/us-central1/models/2325462694699728896",
        "modelDisplayName": "dtire-model"
    }

## Guide to test on local machine
1. ```pip install -r requirements.txt```
2. Make sure you have a service account key file in the same directory as this file.
3. ```set GOOGLE_APPLICATION_CREDENTIALS=<path to service account key file>```
4. ```functions-framework-python --target reqPred --debug```

## Todo
- NOTHING! :D

## Author
Allief Nuriman