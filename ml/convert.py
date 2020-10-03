import os
import caffe

def shai_net_to_py_readable(prototxt_filename, caffemodel_filename):
  net = caffe.Net(prototxt_filename, caffemodel_filename, caffe.TEST) # read the net + weights
  pynet_ = [] 
  for li in xrange(len(net.layers)):  # for each layer in the net
    layer = {}  # store layer's information
    layer['name'] = net._layer_names[li]
    # for each input to the layer (aka "bottom") store its name and shape
    layer['bottoms'] = [(net._blob_names[bi], net.blobs[net._blob_names[bi]].data.shape) 
                         for bi in list(net._bottom_ids(li))] 
    # for each output of the layer (aka "top") store its name and shape
    layer['tops'] = [(net._blob_names[bi], net.blobs[net._blob_names[bi]].data.shape) 
                      for bi in list(net._top_ids(li))]
    layer['type'] = net.layers[li].type  # type of the layer
    # the internal parameters of the layer. not all layers has weights.
    layer['weights'] = [net.layers[li].blobs[bi].data[...] 
                        for bi in xrange(len(net.layers[li].blobs))]
    pynet_.append(layer)
  return pynet_

dir_arr = []
d = "source"
for path in os.listdir(d):
    full_path = os.path.join(d, path)
    if os.path.isfile(full_path):
        print (full_path)
        dir_arr.append(full_path)


shai_net_to_py_readable(dir_arr[0], dir_arr[1])
