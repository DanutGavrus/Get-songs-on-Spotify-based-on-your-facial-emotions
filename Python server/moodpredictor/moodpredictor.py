########## Mood predictor ##########
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import LabelEncoder, MinMaxScaler
from keras.wrappers.scikit_learn import KerasClassifier
from keras.models import Sequential
from keras.layers import Dense
import pandas as pd
import numpy as np
import random

path = '/usr/src/app/dataset'
#path = '/home/danutgavrus/Documents/College/Licenta/Server/dataset'
df_songs_with_mood = None

def load_or_create_songs_with_mood_csv(min_popularity):
    global df_songs_with_mood
    try:
        df_songs_with_mood = pd.read_csv(
            path + '/600000_tracks_with_mood_added.csv',
            header=0
        )
        print('CSV songs with mood loaded')
        return df_songs_with_mood
    except:
        df_songs_without_mood = get_df_songs_without_mood(min_popularity)
        df_trained = pd.read_csv(path + '/../../Dataset/700_tracks_prelabeled_for_train.csv')
        moods = df_trained['mood']
        encoder = LabelEncoder()
        encoder.fit(moods)
        moods_encoded = encoder.transform(moods)
        target = pd.DataFrame({'mood':df_trained['mood'].tolist(), 'encode':moods_encoded}).drop_duplicates().sort_values(['encode'], ascending=True)
        pip = get_pipeline(df_trained, moods_encoded)

        mood_list = []
        i = 1
        for _, row in df_songs_without_mood.iterrows():
            print('#{0}/{1}'.format(i, len(df_songs_without_mood.index)))
            i += 1
            mood = predict_mood(pip, row, target)
            mood_list.append(mood)

        df_songs_with_mood = df_songs_without_mood
        df_songs_with_mood['mood'] = mood_list
        shrink_df_songs_with_mood()
        
        df_songs_with_mood.to_csv(path + '/600000_tracks_with_mood_added.csv', encoding='utf-8', index=False)
        print('CSV songs with mood saved')
        return df_songs_with_mood

def get_df_songs_without_mood(min_popularity):
    columns_to_keep = ['duration_ms', 'danceability', 'acousticness', 'energy', 'instrumentalness', 'liveness', 'valence', 'loudness', 'speechiness', 'tempo', 'id', 'popularity']
    df_songs_without_mood = pd.read_csv(
        path + '/../../Dataset/600000_tracks_with_all_info.csv',
        usecols=columns_to_keep,
        header=0
    )
    df_songs_without_mood = df_songs_without_mood[df_songs_without_mood['popularity'] >= min_popularity]
    return df_songs_without_mood

def get_pipeline(df_trained, moods_encoded):
    features = np.array(df_trained[df_trained.columns[6:-3]])
    pip = Pipeline(
        [
            ('minmaxscaler', MinMaxScaler()),
            ('keras', KerasClassifier(
                build_fn=base_model,
                epochs=300,
                batch_size=200,
                verbose=0)
            )
        ]
    )
    pip.fit(features, moods_encoded)
    return pip

def base_model():
    model = Sequential()
    model.add(Dense(8, input_dim=10, activation='relu'))
    model.add(Dense(4, activation='softmax'))
    model.compile(
        loss='categorical_crossentropy',
        optimizer='adam',
        metrics=['accuracy']
    )
    return model

def predict_mood(pip, row, target):
    preds = get_song_features(row)
    preds_features = np.array(preds).reshape(-1,1).T
    results = pip.predict(preds_features)
    moods = np.array(target['mood'][target['encode'] == int(results)])
    acousticness = preds[2]
    valence = preds[6]
    tempo = preds[9]
    if (moods[0] == 'Energetic'):
        if ((valence <= 0.3) & (tempo >= 120) & (acousticness <= 0.01)):
            moods[0] = 'Angry'
    return moods[0]

def get_song_features(row):
    duration_ms = row['duration_ms']
    danceability = row['danceability']
    acousticness= row['acousticness']
    energy = row['energy']
    instrumentalness = row['instrumentalness']
    liveness = row['liveness']
    valence = row['valence']
    loudness = row['loudness']
    speechiness = row['speechiness']
    tempo = row['tempo']
    return [duration_ms, danceability, acousticness, energy, instrumentalness, liveness, valence, loudness, speechiness, tempo]

def shrink_df_songs_with_mood():
    global df_songs_with_mood
    df_songs_with_mood.drop('duration_ms', inplace=True, axis=1)
    df_songs_with_mood.drop('danceability', inplace=True, axis=1)
    df_songs_with_mood.drop('acousticness', inplace=True, axis=1)
    df_songs_with_mood.drop('energy', inplace=True, axis=1)
    df_songs_with_mood.drop('instrumentalness', inplace=True, axis=1)
    df_songs_with_mood.drop('liveness', inplace=True, axis=1)
    df_songs_with_mood.drop('valence', inplace=True, axis=1)
    df_songs_with_mood.drop('loudness', inplace=True, axis=1)
    df_songs_with_mood.drop('speechiness', inplace=True, axis=1)
    df_songs_with_mood.drop('tempo', inplace=True, axis=1)
    df_songs_with_mood.drop('popularity', inplace=True, axis=1)

def get_song_for_mood(mood):
    if mood == 'Happy':
        moods = ['Happy', 'Energetic']
        mood = random.choice(moods)
    if mood == 'Neutral':
        moods = ['Happy', 'Energetic', 'Sad', 'Angry', 'Calm']
        mood = random.choice(moods)
    if mood == 'Others':
        moods = ['Happy', 'Energetic', 'Sad', 'Angry', 'Calm']
        mood = random.choice(moods)
    if mood == 'Error':
        return ''
    
    df_song_for_mood = df_songs_with_mood[df_songs_with_mood['mood'] == mood]
    df_song_for_mood.drop('mood', inplace=True, axis=1)
    song_for_mood = df_song_for_mood.sample()
    return '{"id": "' + song_for_mood['id'].values[0] + '"}'
