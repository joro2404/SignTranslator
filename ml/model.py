import torch
import i3d
import cv2
import pickle
import numpy as np

frames = []
vidcap = cv2.VideoCapture('./source/book.mp4')
def getFrame(sec):
    vidcap.set(cv2.CAP_PROP_POS_MSEC,sec*1000)
    hasFrames,image = vidcap.read()
    if hasFrames:
        image = cv2.resize(image, (224, 224))
        image = cv2.dnn.blobFromImage(image)
        frames.append(image)
    return hasFrames

sec = 0
frameRate = 0.04 #//it will capture image in each 0.04 second
count=1
success = getFrame(sec)

while success:
    count = count + 1
    sec = sec + frameRate
    sec = round(sec, 2)
    success = getFrame(sec)


while len(frames) > 50:
    frames.pop()

frames = np.array(frames)

frames = np.array([f for f in frames])
print(frames.shape)
frames = np.moveaxis(frames, 0, 2)
frames = torch.from_numpy(frames)
print('frames:', frames.shape)

model = i3d.InceptionI3d(2000, num_in_frames=50)

with open('./lables.pkl', 'rb') as f:
    labels = pickle.load(f)

checkpoint = torch.load('./wlasl16.pth.tar', map_location='cpu')
state_dict = {k[7:]: v for k, v in checkpoint['state_dict'].items()}
model.load_state_dict(state_dict)
model.eval()
torch.no_grad()

data = cv2.imread("./source/nine.jpg")
# print(data.shape)
data = cv2.resize(data , (224, 224))
# print(data.shape)
# print(data)
data = cv2.dnn.blobFromImage(data) #this is (1, 3, 224, 224) shaped image
# print(data.shape)
data = np.repeat(data, 50, axis=1).reshape((-1, 1, 3, 224, 224))
data = np.moveaxis(data, 0, 2)
data = torch.from_numpy(data)
print('data:', data.shape)


a = [x for x in labels['words'] if list(x)[0] == 't']
print(labels['words_to_id']['three'])
print(labels['words'][1800:1840])
print(a)

# a = [x for x in labels['words'] if list(x)[0] == 't']

a = model(data)
print(a)
