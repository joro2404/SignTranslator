import os
import cv2
import numpy as np

dir_arr = []
d = "source"
for path in os.listdir(d):
    full_path = os.path.join(d, path)
    if os.path.isfile(full_path):
        #print (full_path)
        dir_arr.append(full_path)

model = cv2.dnn.readNetFromCaffe(dir_arr[0], dir_arr[2])
img = cv2.imread(dir_arr[1])
img = cv2.resize(img , (224, 224)) #this is (224, 224, 3) shaped image
img_blob = cv2.dnn.blobFromImage(img ) #this is (1, 3, 224, 224) shaped image

print(type(img_blob))

# np.repeat(img_blob, 300)
# print(model.dump())

model.setInput(img_blob)
output = model.forward()
print(output)

'''
name: "CaffeNet"
state {
  phase: TEST
  level: 0
}
layer {
  name: "input"
  type: "Input"
  top: "data"
  top: "seq_ind"
  top: "bw_seq_ind"
  input_param {
    shape {
      dim: 300
      dim: 3
      dim: 224
      dim: 224
    }
    shape {
      dim: 300
      dim: 1
    }
    shape {
      dim: 300
      dim: 1
    }
  }
}
layer {
  name: "conv1"
  type: "Convolution"
  bottom: "data"
  top: "conv1"
  param {
    lr_mult: 1
    decay_mult: 1
  }
  param {
    lr_mult: 2
    decay_mult: 0
  }
  convolution_param {
    num_output: 96
    kernel_size: 11
    stride: 4
    weight_filler {
      type: "gaussian"
      std: 0.01
    }
    bias_filler {
      type: "constant"
      value: 0
    }
  }
}
layer {
  name: "relu1"
  type: "ReLU"
  bottom: "conv1"
  top: "conv1"
}
layer {
  name: "pool1"
  type: "Pooling"
  bottom: "conv1"
  top: "pool1"
  pooling_param {
    pool: MAX
    kernel_size: 3
    stride: 2
  }
}
layer {
  name: "norm1"
  type: "LRN"
  bottom: "pool1"
  top: "norm1"
  lrn_param {
    local_size: 5
    alpha: 0.0001
    beta: 0.75
  }
}
layer {
  name: "conv2"
  type: "Convolution"
  bottom: "norm1"
  top: "conv2"
  param {
    lr_mult: 1
    decay_mult: 1
  }
  param {
    lr_mult: 2
    decay_mult: 0
  }
  convolution_param {
    num_output: 256
    pad: 2
    kernel_size: 5
    group: 2
    weight_filler {
      type: "gaussian"
      std: 0.01
    }
    bias_filler {
      type: "constant"
      value: 1
    }
  }
}
layer {
  name: "relu2"
  type: "ReLU"
  bottom: "conv2"
  top: "conv2"
}
layer {
  name: "pool2"
  type: "Pooling"
  bottom: "conv2"
  top: "pool2"
  pooling_param {
    pool: MAX
    kernel_size: 3
    stride: 2
  }
}
layer {
  name: "norm2"
  type: "LRN"
  bottom: "pool2"
  top: "norm2"
  lrn_param {
    local_size: 5
    alpha: 0.0001
    beta: 0.75
  }
}
layer {
  name: "conv3"
  type: "Convolution"
  bottom: "norm2"
  top: "conv3"
  param {
    lr_mult: 1
    decay_mult: 1
  }
  param {
    lr_mult: 2
    decay_mult: 0
  }
  convolution_param {
    num_output: 384
    pad: 1
    kernel_size: 3
    weight_filler {
      type: "gaussian"
      std: 0.01
    }
    bias_filler {
      type: "constant"
      value: 0
    }
  }
}
layer {
  name: "relu3"
  type: "ReLU"
  bottom: "conv3"
  top: "conv3"
}
layer {
  name: "conv4"
  type: "Convolution"
  bottom: "conv3"
  top: "conv4"
  param {
    lr_mult: 1
    decay_mult: 1
  }
  param {
    lr_mult: 2
    decay_mult: 0
  }
  convolution_param {
    num_output: 384
    pad: 1
    kernel_size: 3
    group: 2
    weight_filler {
      type: "gaussian"
      std: 0.01
    }
    bias_filler {
      type: "constant"
      value: 1
    }
  }
}
layer {
  name: "relu4"
  type: "ReLU"
  bottom: "conv4"
  top: "conv4"
}
layer {
  name: "conv5"
  type: "Convolution"
  bottom: "conv4"
  top: "conv5"
  param {
    lr_mult: 1
    decay_mult: 1
  }
  param {
    lr_mult: 2
    decay_mult: 0
  }
  convolution_param {
    num_output: 256
    pad: 1
    kernel_size: 3
    group: 2
    weight_filler {
      type: "gaussian"
      std: 0.01
    }
    bias_filler {
      type: "constant"
      value: 1
    }
  }
}
layer {
  name: "relu5"
  type: "ReLU"
  bottom: "conv5"
  top: "conv5"
}
layer {
  name: "pool5"
  type: "Pooling"
  bottom: "conv5"
  top: "pool5"
  pooling_param {
    pool: MAX
    kernel_size: 3
    stride: 2
  }
}
layer {
  name: "fc6"
  type: "InnerProduct"
  bottom: "pool5"
  top: "fc6"
  param {
    lr_mult: 1
    decay_mult: 1
  }
  param {
    lr_mult: 2
    decay_mult: 0
  }
  inner_product_param {
    num_output: 4096
    weight_filler {
      type: "gaussian"
      std: 0.005
    }
    bias_filler {
      type: "constant"
      value: 1
    }
  }
}
layer {
  name: "relu6"
  type: "ReLU"
  bottom: "fc6"
  top: "fc6"
}
layer {
  name: "drop6"
  type: "Dropout"
  bottom: "fc6"
  top: "fc6"
  dropout_param {
    dropout_ratio: 0.5
  }
}
layer {
  name: "fc7"
  type: "InnerProduct"
  bottom: "fc6"
  top: "fc7"
  param {
    lr_mult: 1
    decay_mult: 1
  }
  param {
    lr_mult: 2
    decay_mult: 0
  }
  inner_product_param {
    num_output: 4096
    weight_filler {
      type: "gaussian"
      std: 0.005
    }
    bias_filler {
      type: "constant"
      value: 1
    }
  }
}
layer {
  name: "relu7"
  type: "ReLU"
  bottom: "fc7"
  top: "fc7"
}
layer {
  name: "drop7"
  type: "Dropout"
  bottom: "fc7"
  top: "fc7"
  dropout_param {
    dropout_ratio: 0.5
  }
}
layer {
  name: "reshape_first"
  type: "Reshape"
  bottom: "fc7"
  top: "first_reshaped_data"
  reshape_param {
    shape {
      dim: 0
      dim: 1
      dim: -1
    }
  }
}
layer {
  name: "slicer_blob"
  type: "Slice"
  bottom: "first_reshaped_data"
  top: "blob0"
  top: "blob1"
  top: "blob2"
  slice_param {
    slice_point: 300
    slice_point: 600
    axis: 0
  }
}
layer {
  name: "concat_blob"
  type: "Concat"
  bottom: "blob0"
  bottom: "blob1"
  bottom: "blob2"
  top: "lstm_data"
  concat_param {
    axis: 1
  }
}
layer {
  name: "lstm_forward"
  type: "LSTM"
  bottom: "lstm_data"
  bottom: "seq_ind"
  top: "lstm_forward"
  recurrent_param {
    num_output: 1024
    weight_filler {
      type: "xavier"
    }
    bias_filler {
      type: "constant"
    }
  }
}
layer {
  name: "lstm_forward_drop"
  type: "Dropout"
  bottom: "lstm_forward"
  top: "lstm_forward"
  dropout_param {
    dropout_ratio: 0.5
  }
}
layer {
  name: "reverse_data"
  type: "Reverse"
  bottom: "lstm_data"
  top: "rev_lstm_data"
}
layer {
  name: "lstm_backward"
  type: "LSTM"
  bottom: "rev_lstm_data"
  bottom: "bw_seq_ind"
  top: "lstm_backward"
  recurrent_param {
    num_output: 1024
    weight_filler {
      type: "xavier"
    }
    bias_filler {
      type: "constant"
    }
  }
}
layer {
  name: "lstm_backward_drop"
  type: "Dropout"
  bottom: "lstm_backward"
  top: "lstm_backward"
  dropout_param {
    dropout_ratio: 0.5
  }
}
layer {
  name: "reverse_lstm_output"
  type: "Reverse"
  bottom: "lstm_backward"
  top: "rev_lstm_backward"
}
layer {
  name: "combined_lstm_data"
  type: "Concat"
  bottom: "rev_lstm_backward"
  bottom: "lstm_forward"
  top: "combined_lstm"
  concat_param {
    axis: 2
  }
}
layer {
  name: "ip_layer"
  type: "InnerProduct"
  bottom: "combined_lstm"
  top: "ip_layer"
  inner_product_param {
    num_output: 1232
    weight_filler {
      type: "xavier"
    }
    axis: 2
  }
}

'''

