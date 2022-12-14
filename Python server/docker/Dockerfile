# syntax=docker/dockerfile:experimental

FROM python:3.8

WORKDIR /usr/src/app

RUN apt-get update
RUN apt-get install ffmpeg libsm6 libxext6  -y

COPY requirements.txt /usr/src/app/
RUN --mount=type=cache,target=/root/.cache/pip pip install -r requirements.txt
COPY . /usr/src/app

CMD [ "python", "./mande.py" ]
