import torch
import i3d
import cv2
import pickle
import numpy as np
import matplotlib.pyplot as plt
import time

frames = []

cap = cv2.VideoCapture('./source/book.mp4')

# get total number of frames
totalFrames = cap.get(cv2.CAP_PROP_FRAME_COUNT)
myFrameNumber = 0
# check for valid frame number

for i in range(50):
    if myFrameNumber >= 0 & myFrameNumber <= totalFrames:
    # set frame position
        cap.set(cv2.CAP_PROP_POS_FRAMES,myFrameNumber)

    ret, frame = cap.read()
    
    frame = cv2.resize(frame, (224, 224)) # (224, 224, 3)
    frame = np.expand_dims(frame, axis=0)
    frame = np.swapaxes(frame, 1, 3)
    frame = np.swapaxes(frame, 2, 3)
    # print(frame.shape)
    # frame = cv2.dnn.blobFromImage(frame) #(1, 3, 224, 224)
    # plt.imshow(frame[0])
    frames.append(frame)
    myFrameNumber += 1

cv2.destroyAllWindows()

frames = np.array(frames)
frames = np.swapaxes(frames, 0, 1)
frames = np.swapaxes(frames, 1, 2)
# print(frames[0])
frames = torch.from_numpy(frames).type(torch.FloatTensor)
print('frames:', frames.shape)

model = i3d.InceptionI3d(2000, num_in_frames=50).type(torch.FloatTensor)

with open('./lables.pkl', 'rb') as f:
    labels = pickle.load(f)

checkpoint = torch.load('./source/wlasl16.pth.tar')
state_dict = {k[7:]: v for k, v in checkpoint['state_dict'].items()}
model.load_state_dict(state_dict)
model.eval()
torch.no_grad()

data = cv2.imread("./source/tree.jpg")
data = cv2.resize(data, (224, 224))
data = cv2.dnn.blobFromImage(data)  # this is (1, 3, 224, 224) shaped image

data = np.repeat(data, 50, axis=1).reshape((-1, 1, 3, 224, 224))
data = np.moveaxis(data, 0, 2)
data = torch.from_numpy(data)

# print('data:', data.shape)

# a = [x for x in labels['words'] if list(x)[0] == 't']

a = model(frames)
# print(a)
word_index = np.argmax(a['logits'].detach().numpy()[0])
print(word_index)
print(labels['words'][word_index])
