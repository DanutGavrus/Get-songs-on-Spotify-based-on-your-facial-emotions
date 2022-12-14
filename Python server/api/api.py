########## Rest API ##########
from flask import Flask, request, Response, jsonify

app = Flask(__name__)

### Test route ###
@app.route('/')
def hello():
    return 'Welcome'

### POST - image ###
# Receive an image from any user and return an array of emotions if face detected. #
post_image_callback = None
@app.route('/image', methods = ['POST'])
def image():
    if request.method == 'POST':
        content = request.get_json()
        img = content['img']
        payload = post_image_callback(img)
        return jsonify(payload=payload)

### POST - mood ###
# Receive a mood from any user and return a song recommendation. #
post_mood_callback = None
@app.route('/mood', methods = ['POST'])
def mood():
    if request.method == 'POST':
        content = request.get_json()
        mood = content['mood']
        payload = post_mood_callback(mood)
        return Response(payload, mimetype='application/json')
