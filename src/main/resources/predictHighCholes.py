import sys
import pymongo
import numpy as np
import pandas as pd
from sklearn.naive_bayes import GaussianNB
from sklearn.model_selection import train_test_split
from sklearn.impute import SimpleImputer
from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score

# connect to mongo database
myclient = pymongo.MongoClient()
db = myclient["FIT3077JD"]

# connect to observation collection
observation = db["Observation"]
obs = observation.find({}, {"subject.reference": 1, "code.text": 1, "valueQuantity.value": 1, "code.coding": 1, "component": 1, "_id": 0})

# create empty data frame
columns = ["Patient", "Type", "Value"]
df = pd.DataFrame(columns=columns)

# add values to data frame
j = 1
for entry in obs:
    code = entry["code"]["coding"][0]["code"]
    lines_to_add = []
    if code == "55284-4":
        for i in range(2):
            patient = entry["subject"]["reference"]
            measurement_type = entry["component"][i]["code"]["text"]
            value = entry["component"][i]["valueQuantity"]["value"]
            line = {"Patient": patient, "Type": measurement_type, "Value": value}
            lines_to_add.append(line)
    else:
        patient = entry["subject"]["reference"]
        measurement_type = entry["code"]["text"]
        value = entry["valueQuantity"]["value"]
        line = {"Patient": patient, "Type": measurement_type, "Value": value}
        lines_to_add.append(line)
    
    for line in lines_to_add:
        row_df = pd.DataFrame(line, index=[j])
        df = pd.concat([df, row_df])
        j += 1

# df

# group by patient and type
by_patient = df.groupby(['Patient', 'Type'], as_index=False)['Patient', 'Type','Value'].mean()

# tidy data
by_patient_complete = by_patient.pivot(index='Patient', columns='Type').reset_index(drop=True)
by_patient_complete.columns = by_patient_complete.columns.droplevel()
# print(by_patient_complete)

# drop HDL and LDL columns and also RBC because it has has too many NA values
by_patient_complete = by_patient_complete.drop(columns=['High Density Lipoprotein Cholesterol', 'Low Density Lipoprotein Cholesterol', 'RBC Auto (Bld) [#/Vol]'])
# print(by_patient_complete)
# by_patient_complete = by_patient_complete.drop(columns=['Body Height', 'Erythrocytes [#/volume] in Blood by Automated count', 'Glucose', 'Hemoglobin [Mass/volume] in Blood', 'Oral temperature', 'RBC Auto (Bld) [#/Vol]'])

# add column to classify values into high or low cholesterol
def put_class(row):
    if row['Total Cholesterol'] >= 200:
        val = 'high'
    else:
        val = 'low'

    return val

by_patient_complete['Class'] = by_patient_complete.apply(put_class, axis=1)

# classifier
df_info = by_patient_complete
# df_info['Class'].fillna('low', inplace=True)
y = df_info.iloc[:,-1]
df_info = df_info[df_info.columns[:-1]]

# create training and testing data
# set random_state to 
x_train, x_test, y_train, y_test = train_test_split(df_info, y, test_size=0.3, random_state=40)

# print(x_train.shape, y_train.shape)
# print(x_test.shape, y_test.shape)

# create our imputer to replace missing values with the mean
imp = SimpleImputer(missing_values=np.nan, strategy='mean')
imp = imp.fit(x_train)
x_train_imp = imp.transform(x_train)
x_test_imp = imp.transform(x_test)

# create a Gaussian classifier
model = GaussianNB()

# train the model using the training sets
model.fit(x_train_imp, y_train)

# predicted output
predicted = model.predict(x_test_imp)
model.score(x_test_imp, y_test)

# confusion matrix
matrix = confusion_matrix(y_test, predicted)
# print(matrix)

# calculate accuracy
accuracy = matrix[0][0]/sum(matrix[0])
print(accuracy)

y_test_modified = []
predicted_modified = []

# replace highs and lows with 1s and 0s respectively
for val in y_test:
    if val == "high":
        y_test_modified.append(1)
    else:
        y_test_modified.append(0)

for val in predicted:
    if val == "high":
        predicted_modified.append(1)
    else:
        predicted_modified.append(0)

# calculate f1_score
# print(f1_score(y_test_modified, predicted_modified))
sys.exit(0)

