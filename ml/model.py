import torch
import i3d
import cv2
import pickle
import numpy as np
import matplotlib.pyplot as plt
import time


class Translator:
    def __init__(self):
        self.model = i3d.InceptionI3d(2000, num_in_frames=16)
        self._load_weights()
        with open('./lables.pkl', 'rb') as f:
            self.labels = pickle.load(f)

    def _load_weights(self):
        checkpoint = torch.load('./source/wlasl16.pth.tar', map_location='cpu')
        state_dict = {k[7:]: v for k, v in checkpoint['state_dict'].items()}
        self.model.load_state_dict(state_dict)
        self.model.eval()
        torch.no_grad()

    @staticmethod
    def softmax(outputs):
        e_x = np.exp(outputs - np.max(outputs))
        return e_x / e_x.sum(axis=0)

    def predict_from_img(self, img):
        data = self._prepare_image(img)
        out = self.model(data)

        norm_out = self.softmax(out['logits'].detach().numpy()[0])

        word_index = np.argmax(norm_out)
        label = self.labels['words'][word_index]
        print(norm_out[word_index])
        print(label)
        return label

    @staticmethod
    def _prepare_image(images):
        frames = []

        for i in range(16):
            with open('./source/foo.png', 'wb') as f:
                f.write(images[i])
            frame = cv2.imread("./source/foo.png")
            frame = cv2.resize(frame, (224, 224))  # (224, 224, 3)
            frame = np.expand_dims(frame, axis=0)
            frame = np.swapaxes(frame, 1, 3)
            frame = np.swapaxes(frame, 2, 3)

            frames.append(frame)

        frames = np.array(frames)
        frames = np.swapaxes(frames, 0, 1)
        frames = np.swapaxes(frames, 1, 2)
        print('frames:', frames.shape)
        return torch.from_numpy(frames).type(torch.FloatTensor)


if __name__ == '__main__':
    t = Translator()
