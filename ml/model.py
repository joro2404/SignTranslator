import torch
import i3d
import numpy as np
import cv2


frames = []
vidcap = cv2.VideoCapture('./source/book.mp4')
def getFrame(sec):
    vidcap.set(cv2.CAP_PROP_POS_MSEC,sec*1000)
    hasFrames,image = vidcap.read()
    if hasFrames:
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

# print(frames[0])

model = i3d.InceptionI3d(num_in_frames=50)
checkpoint = torch.load('./source/wlasl16.pth.tar', map_location='cpu')
model.load_state_dict(checkpoint['state_dict'], strict=False)
model.eval()

data = cv2.imread("./source/nine.jpg")
data = cv2.resize(data , (224, 224))
# print(data)
data = cv2.dnn.blobFromImage(data) #this is (1, 3, 224, 224) shaped image
data = np.repeat(data, 50, axis=1).reshape((-1, 1, 3, 224, 224))
data = np.moveaxis(data, 0, 2)
data = torch.from_numpy(data)
# print(data.shape)
# prepare_input(data)
# print(data)


img = cv2.imread("./source/nine.jpg")
img = cv2.resize(img , (224, 224))
# print(data)
img = cv2.dnn.blobFromImage(img) #this is (1, 3, 224, 224) shaped image
img = np.repeat(img, 50, axis=1).reshape((-1, 1, 3, 224, 224))
pixel = img[0, 0, 2, 112, 112]
img = np.moveaxis(img, 0, 2)
img = torch.from_numpy(img)
# print(img.shape)

print(np.array_equal(data, img))
# print(pixel, data[0, 0, 112, 112])
a = model.forward(data)
print(np.argmax(a['logits'].detach().numpy()[0]))

