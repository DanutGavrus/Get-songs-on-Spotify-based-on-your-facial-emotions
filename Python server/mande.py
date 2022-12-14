########## Mande APP ##########
from api import api
from emotiondetector import emotiondetector
from moodpredictor import moodpredictor

# If the CVS with song emotions does not exist, label songs and create it
min_popularity = 0 # Variable in order to label less or more songs in tests
moodpredictor.df_songs_with_mood = moodpredictor.load_or_create_songs_with_mood_csv(min_popularity)

# Tie api with emotion detector #
def post_image_callback(img):
    return emotiondetector.get_emotions(img)
api.post_image_callback = post_image_callback

# Tie api with mood predictor #
def post_mood_callback(mood):
    return moodpredictor.get_song_for_mood(mood)
api.post_mood_callback = post_mood_callback

api.app.run(host='0.0.0.0', port=80, debug=True)
