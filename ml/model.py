import torch
import i3d
import cv2
import pickle
import numpy as np


model = i3d.InceptionI3d(num_in_frames=50)

with open('./lables.pkl', 'rb') as f:
    labels = pickle.load(f)

checkpoint = torch.load('./wlasl16.pth.tar', map_location='cpu')
model.load_state_dict(checkpoint['state_dict'], strict=False)
model.eval()

data = cv2.imread("./source/3.jpg")
data = cv2.resize(data , (224, 224))
# print(data)
data = cv2.dnn.blobFromImage(data) #this is (1, 3, 224, 224) shaped image
data = np.repeat(data, 50, axis=1).reshape(-1, 1, 3, 224, 224)
data = np.moveaxis(data, 0, 2)
data = torch.from_numpy(data)
# print(data.shape)
# prepare_input(data)
# print(data)


img = cv2.imread("./source/3.jpg")
img = cv2.resize(img , (224, 224))
# print(data)
img = cv2.dnn.blobFromImage(img) #this is (1, 3, 224, 224) shaped image
img = np.repeat(img, 50, axis=1).reshape((-1, 1, 3, 224, 224))
pixel = img[0, 0, 2, 112, 112]
img = np.moveaxis(img, 0, 2)
img = torch.from_numpy(img)

a = [x for x in labels['words'] if list(x)[0] == 't']
print(labels['words_to_id']['three'])
print(labels['words'][1800:1840])
print(a)

# a = model(data)
# print(np.argmax(a['logits'].detach().numpy()[0]))


