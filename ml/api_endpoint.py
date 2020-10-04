#!/usr/bin/env python
# encoding: utf-8
import json
from flask import Flask, request
from base64 import decodebytes
from model import Translator

app = Flask(__name__)
t = Translator()


def base_to_jpg(key):
    form_data = request.form
    image_base64 = form_data[key]
    str_to_bytes = str.encode(image_base64)
    return decodebytes(str_to_bytes)


@app.route('/', methods=['POST'])
def index():
    vod = [base_to_jpg('param' + str(i)) for i in range(16)]
    word = t.predict_from_img(vod[0])
    return json.dumps({'word': word})


app.run(host='0.0.0.0')
