#!/usr/bin/env python
# encoding: utf-8
import json
from flask import Flask, request
from base64 import decodebytes

app = Flask(__name__)


@app.route('/', methods=['POST'])
def index():
    image_base64 = request.form['param1']
    str_to_bytes = str.encode(image_base64)
    image = decodebytes(str_to_bytes)
    with open("foo.jpg", "wb") as f:
        f.write(image)
    return json.dumps({'name': 'alice',
                       'email': 'alice@outlook.com'})


app.run(host='0.0.0.0')
