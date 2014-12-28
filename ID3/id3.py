__author__ = 'ub'
import operator
import math

def createDataSet():
    dataSet = [[1,1,'yes'],[1,1,'yes'],[1,0,'no'],[0,1,'no'],[0,1,'no']]
    features = ['no surfacing','flippers']
    return dataSet,features

def treeGrowth(dataSet,features):
    classList = [example[-1] for example in dataSet]
    if classList.count(classList[0])==len(classList):
        return classList[0]
    if len(dataSet[0])==1:# no more features
        return classify(classList)

    bestFeat = findBestSplit(dataSet)#bestFeat is the index of best feature
    bestFeatLabel = features[bestFeat]
    myTree = {bestFeatLabel:{}}
    featValues = [example[bestFeat] for example in dataSet]
    uniqueFeatValues = set(featValues)
    del (features[bestFeat])
    for values in uniqueFeatValues:
        subDataSet = splitDataSet(dataSet,bestFeat,values)
        myTree[bestFeatLabel][values] = treeGrowth(subDataSet,features)
    features.insert(bestFeat, bestFeatLabel)
    return myTree

def classify(classList):
    '''
    find the most in the set
    '''
    classCount = {}
    for vote in classList:
        if vote not in classCount.keys():
            classCount[vote] = 0
        classCount[vote] += 1
    sortedClassCount = sorted(classCount.iteritems(),key = operator.itemgetter(1),reverse = True)
    return sortedClassCount[0][0]


def findBestSplit(dataset):
    numFeatures = len(dataset[0])-1
    baseEntropy = calcShannonEnt(dataset)
    bestInfoGain = 0.0
    bestFeat = -1
    for i in range(numFeatures):
        featValues = [example[i] for example in dataset]
        uniqueFeatValues = set(featValues)
        newEntropy = 0.0
        for val in uniqueFeatValues:
            subDataSet = splitDataSet(dataset,i,val)
            prob = len(subDataSet)/float(len(dataset))
            newEntropy += prob*calcShannonEnt(subDataSet)
        if(baseEntropy - newEntropy)>bestInfoGain:
            bestInfoGain = baseEntropy - newEntropy
            bestFeat = i
    return bestFeat


def splitDataSet(dataset,feat,values):
    retDataSet = []
    for featVec in dataset:
        if featVec[feat] == values:
            reducedFeatVec = featVec[:feat]
            reducedFeatVec.extend(featVec[feat+1:])
            retDataSet.append(reducedFeatVec)
    return retDataSet

def calcShannonEnt(dataset):
    numEntries = len(dataset)
    labelCounts = {}
    for featVec in dataset:
        currentLabel = featVec[-1]
        if currentLabel not in labelCounts.keys():
            labelCounts[currentLabel] = 0
        labelCounts[currentLabel] += 1
    shannonEnt = 0.0

    for key in labelCounts:
        prob = float(labelCounts[key])/numEntries
        if prob != 0:
            shannonEnt -= prob*math.log(prob,2)
    return shannonEnt

def predict(tree,newObject):
    while isinstance(tree,dict):
        key = tree.keys()[0]
        tree = tree[key][newObject[key]]
    return tree

if __name__ == '__main__':
    dataset,features = createDataSet()
    tree = treeGrowth(dataset,features)
    print tree
    print predict(tree,{'no surfacing':1,'flippers':1})
    print predict(tree,{'no surfacing':1,'flippers':0})
    print predict(tree,{'no surfacing':0,'flippers':1})
    print predict(tree,{'no surfacing':0,'flippers':0})