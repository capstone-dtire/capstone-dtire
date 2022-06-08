# ML Model

## datasets
Kaggle Dataset
author = {Siegel, Joshua}  
publisher = {Harvard Dataverse}  
title = {{Oxidized and non-oxidized tire sidewall and tread images}}  
year = {2021}  
version = {V1}  
doi = {10.7910/DVN/Z3ZYLI}  
dataset link = https://www.kaggle.com/datasets/jehanbhathena/tire-texture-image-recognition


## Prediction
According to this https://github.com/keras-team/keras/blob/master/keras/applications/imagenet_utils.py#L72  
Restnet use 'caffe' style / zero-centering on preprocessing the input data, so when you want to predict using this model make sure to zero centering the image data so the model wont misclasify the result.
