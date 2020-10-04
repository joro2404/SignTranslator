import os
import cv2
import numpy as np
import torch
import torchvision

model = torch.load('source/wlasl16.pth')

shape_of_first_layer = list(model.parameters())[0].shape #shape_of_first_layer

N,C = shape_of_first_layer[:2]

print(N, C)
