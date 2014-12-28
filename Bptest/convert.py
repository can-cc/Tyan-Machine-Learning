from PIL import Image
import numpy as np
import neurolab as nl

WHITE = 255
BLACK = 0

def convert_to_bw(im):
    im = im.convert("L")
    im.save("sample_L.bmp")
    im = im.point(lambda x: WHITE if x > 196 else BLACK)
    im = im.convert('1')
    im.size=(260,197)
    im.save("sample_1.bmp")
    return im

# if __name__ == "__main__":
#     im = Image.open('simple.gif')
#     convert_to_bw(im)

def split(im):
    assert im.mode == '1'
    result = []
    w, h = im.size
    data = im.load()
    xs = [0, 23, 57, 77, 106, 135, 159, 179, 205, 228, w-5]
    ys = [0, 22, 60, 97, 150, h-5]
    for i, x in enumerate(xs):
        if i + 1 >= len(xs):
            break
        for j, y in enumerate(ys):
            if j + 1 >= len(ys):
                break
            box = (x, y, xs[i+1], ys[j+1])
            t = im.crop(box).copy()
            box = box + ((i + 1) % 10, )
            save_32_32(t, 'num_%d_%d_%d_%d_%d'%box)
            result.append((normalize_32_32(t, 'num_%d_%d_%d_%d_%d'%box), (i + 1) % 10))
    return result

def save_32_32(t, name):
    t.save(name+'.bmp')

def normalize_32_32(t, name):
    t = t.resize((32,32))
    return t




if __name__ == "__main__":
    im = Image.open('sample_1.bmp')
    result = split(im)
    f = open('train.data', 'wt')
    print >>f, len(result), 256, 10
    for input, output in result:
        print >>f, input
        print >>f, output

