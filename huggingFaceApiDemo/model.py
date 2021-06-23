from sklearn import svm
from sklearn import datasets
from joblib import dump, load
from sklearn.model_selection import  train_test_split

clf = svm.SVC()
X, y= datasets.load_iris(return_X_y=True)
X_train, X_test, y_train, y_test = train_test_split(X,y, random_state=0)
clf.fit(X_train, y_train)
score = clf.score(X_test,y_test)
print(score)

model_file = 'iris.pkl'
s = dump(clf, model_file)
clf2 = load(model_file)
sample = X[0:1]
print(sample)
clf2.predict(sample)