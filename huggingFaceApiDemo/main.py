import joblib
from flask import Flask, render_template, request, jsonify, make_response
from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline
import numpy as np

app = Flask(__name__)
model_name = "distilbert-base-uncased-finetuned-sst-2-english"
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForSequenceClassification.from_pretrained(model_name)
classifier = pipeline('sentiment-analysis', model=model, tokenizer=tokenizer)
sklearn_clf = joblib.load('iris.pkl')


@app.route('/')
def home():
    return render_template('index.html')

# @app.before_first_request
# def before_first_request():
#     tokenizer = AutoTokenizer.from_pretrained(model_name)
#     model = AutoModelForSequenceClassification.from_pretrained(model_name)
#     classifier = pipeline('sentiment-analysis', model=model, tokenizer=tokenizer)
#
#prueba

@app.route('/predict')
def predict():
    review = request.args.get('review')
    predictions = classifier(review)
    return jsonify(predictions)


@app.post('/predict_iris')
def predict_iris():
    post_params = request.form
    sepal_length = float(post_params.get("sepal_length"))
    sepal_width = float(post_params.get("sepal_width"))
    petal_length = float(post_params.get("petal_length"))
    petal_width = float(post_params.get("petal_width"))
    features_array = np.array([[
        sepal_length, sepal_width, petal_length, petal_width
    ]])
    predictions = sklearn_clf.predict(features_array)
    # response = make_response("Hello world", 200)
    # response.mimetype = "text/plain"
    # return response
    return jsonify({'predictions': predictions})


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080, debug=True)