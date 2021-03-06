from urllib.request import urlretrieve
import PIL
from PIL import Image
import json
import PIL.Image
import numpy as np
import google.auth
import google.auth.transport.requests
import requests

def process(request):
    """Responds to any HTTP request.
    Args:
        request (flask.Request): HTTP request object.
    Returns:
        The response text or any set of values that can be turned into a
        Response object using
        `make_response <http://flask.pocoo.org/docs/1.0/api/#flask.Flask.make_response>`.
    """
    # request_json = request.get_json()
    # if request.args and 'message' in request.args:
    #     return request.args.get('message')
    # elif request_json and 'message' in request_json:
    #     return request_json['message']
    # else:
    #     return f'Hello World!'

    # Fetch the image from the URL: https://storage.googleapis.com/dtire-images/Cracked-1.jpg
    url = request.args.get('url')
    # Download the image
    urlretrieve(url, '/tmp/Cracked-1.jpg')

    image_path = '/tmp/Cracked-1.jpg'

    # use pil
    img_p = PIL.Image.open(image_path)
    resample = PIL.Image.NEAREST
    img_p = img_p.resize((150, 150), resample)
    img_p_arr = np.asarray(img_p)
    img_p_arr = np.expand_dims(img_p_arr, axis=0)

    img_arr_p = np.copy(img_p_arr).astype('float32')
    #change to rgb to bgr
    img_arr_p = img_arr_p[..., ::-1]
    #mean from ImageNet dataset. (used in preprocess_input() restnet50)
    mean = [103.939, 116.779, 123.68]

    img_arr_p[..., 0] -= mean[0]
    img_arr_p[..., 1] -= mean[1]
    img_arr_p[..., 2] -= mean[2]
    img_arr_p_json = json.dumps(img_arr_p.tolist())
    json_str = '{"instances":' + img_arr_p_json + '}'

    creds, project = google.auth.default()

    # creds.valid is False, and creds.token is None
    # Need to refresh credentials to populate those

    auth_req = google.auth.transport.requests.Request()
    creds.refresh(auth_req)

    headers = {
        'Authorization': "Bearer " + creds.token
        # Already added when you pass json= but not when you pass data=
        # 'Content-Type': 'application/json',
    }

    response = requests.post(
        'https://us-central1-aiplatform.googleapis.com/v1/projects/evident-plane-343600/locations/us-central1/endpoints/4805507928171741184:predict', headers=headers, json=json.loads(json_str))

    return response.json()
