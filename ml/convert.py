import torch
import i3d
import numpy as np

model = i3d.InceptionI3d(num_in_frames=50)
checkpoint = torch.load('./wlasl16.pth.tar', map_location='cpu')
model.load_state_dict(checkpoint['state_dict'], strict=False)
model.eval()
data = torch.zeros(10, 3, 250, 250, 250)
a = model(data)
print(model.Conv3d_1a_7x7)
