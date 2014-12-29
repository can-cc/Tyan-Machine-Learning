__author__ = 'tyan'
from C45 import C4_5
from C45 import readData






format = []
for i in range(28):
    format.append("nominal")
for i in [0,17,19,21,23,25]:
    format[i] = "numeric"

inputfile = "allbp"
trainSet = readData(inputfile)
classifier = C4_5(trainSet, format, [])
classifier.startTrain()
classifier.tree.toString(classifier.tree,0)
#classifier.tree.printPic("show.png")
print '________________________________-'
#classifier.rule_generator(classifier.tree, [])
#print classifier.rule