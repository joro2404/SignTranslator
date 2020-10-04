import torch
import i3d
import numpy as np
import cv2



model = i3d.InceptionI3d(num_in_frames=50)
checkpoint = torch.load('./source/wlasl16.pth.tar', map_location='cpu')
model.load_state_dict(checkpoint['state_dict'], strict=False)
model.eval()

data = cv2.imread("./source/iloveyou.jpg")
data = cv2.resize(data , (224, 224))
# print(data)
data = cv2.dnn.blobFromImage(data) #this is (1, 3, 224, 224) shaped image
data = np.repeat(data, 50, axis=0).reshape(-1, 1, 3, 224, 224)
data = np.moveaxis(data, 0, 2)
data = torch.from_numpy(data)
print(data.shape)
# prepare_input(data)
# print(data)

a = model(data)
print(np.argmax(a['logits'].detach().numpy()[0]))

