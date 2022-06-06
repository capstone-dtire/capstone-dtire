import google.auth
import google.auth.transport.requests
import requests


def reqPred(request):
    """Responds to any HTTP request.
    Args:
        request (flask.Request): HTTP request object.
    Returns:
        The response text or any set of values that can be turned into a
        Response object using
        `make_response <http://flask.pocoo.org/docs/1.0/api/#flask.Flask.make_response>`.
    """

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

    json_data = request.get_json()

    response = requests.post(
        'https://us-central1-aiplatform.googleapis.com/v1/projects/evident-plane-343600/locations/us-central1/endpoints/4805507928171741184:predict', headers=headers, json=json_data)

    return response.json()