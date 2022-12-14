########## Emotion detection ##########
from fer import FER
import base64
from PIL import Image
import io
import numpy as np
import tensorflow as tf

# FIX: Get the sessions and graph from the main thread in order to run the detection
# of the emotion here without reinitializing it. (from ~1,5s to ~100ms for each call)
sess = tf.compat.v1.keras.backend.get_session()
graph = tf.compat.v1.get_default_graph()
detector = FER(mtcnn=True)

def get_emotions(img):
    global sess, graph, detector
    # Convert base64 string to jpg file then to numpy array #
    imagedecoded = base64.b64decode(str(img))
    imagefile = Image.open(io.BytesIO(imagedecoded))
    imagenp = np.array(imagefile)
    with sess.as_default():
        with graph.as_default():
            emotions = detector.detect_emotions(imagenp)
    return emotions
