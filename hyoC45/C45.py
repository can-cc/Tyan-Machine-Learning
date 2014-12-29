__author__ = 'tyan'
#coding=utf-8
import os
import math
import copy
import operator
from collections import Counter

class Node:
    def __init__(self, val, child=[], condition=None):
        self.val = val
        self.child = child
        self.condition = condition

class C4_5(object):

    #初始化
    def __init__(self, trainSet, format, rule):
        self.tree = Node(None, [])
        trainSet = list(trainSet)
        self.attributes = trainSet[0][:-1]
        self.format = format
        self.trainSet = trainSet[1:]
        self.dataLen = len(self.trainSet)
        self.rule = rule

    def startTrain(self):
        self.train(self.trainSet, self.tree, self.attributes, 0)

    #处理缺失值，我这是图方便，实际测试predict时，不建议用测试样本中的数据来生成缺失数据，应该用训练数据来生成
    def rep_miss(self, dataSet):
        exp = copy.deepcopy(dataSet)
        for attr in self.attributes:
            idx = self.attributes.index(attr)
            if self.format[idx] == 'nominal':
                #expN 用频率最大的填补缺失
                expN = getDefault([item[idx] for item in exp])
                for item in exp:
                    if item[idx] == '?':
                        item[idx] = expN
                else:
                    num_lst  = [float(item[idx]) for item in exp if item[idx] != '?']
                    mean = sum(num_lst) / len(num_lst)
                    for item in exp:
                        if item[idx] == '?':
                            item[idx] = mean
        return exp

    #寻找合适的分割点
    def split(self, lst, idx):
        split_candidate = []
        for x, y in zip (lst, lst[1:]):
            if (x[-1] != y[-1]) and (x[idx] != y[idx]):
                split_candidate.append( (x[idx] + y[idx]) / 2 )
        return split_candidate

    def validate(self, validFile):
        validSet =  list( readData(validFile) )
        validData = validSet[1:]
        #acc：正确数
        acc = 0.0
        exp = self.rep_miss(validData)
        for item in exp:
            rlt = self.test(item[:-1])
            if rlt == item[-1]:
                acc += 1.0
        return acc/len(validData)

    def preProcess(self, validFile):
        validSet = list( readData(validFile) )
        validData = validSet[1:]
        exp = self.rep_miss(validData)
        return exp

    def prune(self, validFile):
        exp = self.preProcess(validFile)
        for singeRule in self.rule:
            before = self.validPrune(exp)
            oldRule = copy.deepcopy(self.Rule)
            self.rule.remove(singeRule)
            after = self.validPrune(exp)
            print before
            print after

            if after < before:
                self.rule = oldRule

    def test(self, dataSet):
        for singeRule in self.rule:
            for stump in singeRule:
                if stump[0] == 'class':
                    return stump[2]
                idx = self.attributes.index(stump[0])
                attr = dataSet[idx]
                if self.format[idx] == 'nomianal':
                    if attr == stump[2]:
                        pass
                    else:
                        break
                else:
                    if stump[1] == '>':
                        if float(attr) > float(stump[2]):
                            pass
                        else:
                            break
                    else:
                        if float(attr) <= float(stump[2]):
                            pass
                        else:
                            break
        return '0'

    def rule_generator(self, tree, single_rule):
        #if flag:
        #	self.rule = []
        #print tree
        if tree.child:
            if isinstance(tree.val, list):
                single_rule.append(tree.val)
            if tree.child[0] == 'negative':
                single_rule.append(['class', '=', 'negative'])
                self.rule.append(single_rule)
            elif tree.child[0] == 'increased binding proteinval':
                single_rule.append(['class', '=', 'increased binding proteinval'])
                self.rule.append(single_rule)
            elif tree.child[0] == 'decreased binding protein':
                single_rule.append(['class', '=', 'decreased binding protein'])
                self.rule.append(single_rule)
            else:
                for item in tree.child:
                    self.rule_generator(item, list(single_rule))

    def train(self, dataSet, tree, attributes, default):
        if len(dataSet) == 0:
            return Node(default)
        elif allequal([item[-1] for item in dataSet]):
            return Node(dataSet[0][-1])
        elif len(attributes) == 0:
            return Node(getDefault([item[-1] for item in dataSet]))
        else:
            #选取最大信息增益
            best = self.choose_attr(attributes, dataSet)
            if best == 0:
                return Node(getDefault([item[-1] for item in dataSet]))
            print best
            tree.val = best[0]
            #离散值的情况
            idx = self.attributes.index(best[0])
            if best[1] == 'nom':
                attributes.remove(best[0])
                for v in unique(item[idx] for item in dataSet):
                    subDataSet = [item for item in dataSet if item[idx] == v]
                    #选取条件熵后的子数据集递归构造树
                    subTree = self.train(subDataSet, Node(None, []), list(attributes), getDefault(item[-1] for item in dataSet))
                    branch = Node([best[0], '==', v], [subTree])
                    tree.child.append(branch)
            else:#连续型变量
                subDataSet1 = [item for item in dataSet if float(item[idx]) > best[2]]
                default = getDefault(item[-1] for item in dataSet)
                if len(subDataSet1) == len(dataSet):
                    print '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
                    return default
                subTree1 = self.train(subDataSet1, Node(None), list(attributes), default)
                subTree1.condition = [best[0], '>', str(best[2])]
                tree.child.append(subTree1)

                subDataSet2 = [item for item in dataSet if float(item[idx]) <= best[2]]
                subTree2 = self.train(subDataSet2, Node(None), list(attributes), default)
                subTree2.condition=[best[0], '<=', str(best[2])]
                tree.child.append(subTree2)
            return tree

    #求最大信息增益比
    def choose_attr(self, attributes, dataSet):
        maxIGR = 0.0
        dataLen = float(len(dataSet))
        group = [item[-1] for item in dataSet]
        groupC = Counter(group).items()
        #sysGI 分类系统熵
        sysGI = entropy([vl/dataLen for k,vl in groupC])
        for attr in attributes:
            idx = self.attributes.index(attr)
            gain = sysGI
            h = 0.0 #信息裂度
            if self.format[idx] == 'nominal':
                #expN 把频率最大的填补缺失
                expN = getDefault([item[idx] for item in dataSet])
                for item in dataSet:
                    if item[idx] == '?':
                        item[idx] = expN
                for i in unique([item[idx] for item in dataSet]):
                    #expG:该attr的所有分类结果
                    expG = [item[-1] for item in dataSet if item[idx] == i]
                    expGC = Counter(expG).items()
                    split_len = float(len(expG))
                    gain -= split_len/dataLen * entropy([vl/split_len for k,vl in expGC])
                #计算信息裂度
                groupValueC = Counter([item[idx] for item in dataSet ]).items()
                h -=  entropy([vl/len(dataSet) for k,vl in groupValueC])
                if h == 0:
                    continue #不知道为什么会有0，郁闷
                igr = gain / h
                if igr > maxIGR:
                    maxIGR = gain
                    best = [attr, 'nom']
            else:
                num_lst = [float(item[idx]) for item in dataSet if item[idx] != '?']
                if len(num_lst) == 0:
                    print "Error!!!!"
                mean = sum(num_lst) / len(num_lst)
                exps = list(dataSet)
                for item in exps:
                    if item[idx] == '?':
                        item[idx] = mean
                    else:
                        item[idx] = float(item[idx])
                exps.sort(key = operator.itemgetter(idx))
                split_candidate = self.split(exps, idx)
                for thresh in split_candidate:
                    gain = sysGI
                     #expG:该attr的所有分类结果
                    expG1 = [item[-1] for item in exps if float(item[idx]) > thresh]
                    expG2 = [item[-1] for item in exps if float(item[idx]) <= thresh]
                    len1 = float(len(expG1))
                    len2 = float(len(expG2))
                    if len1 == 0 or len2 == 0:
                        gain = 0
                    else:
                        expGC1 = Counter(expG1).items()
                        expGC2 = Counter(expG2).items()
                        gain -= len1/dataLen * entropy([vl/len1 for k,vl in expGC1])
                        gain -= len1/dataLen * entropy([vl/len1 for k,vl in expGC1])
                    h -= entropy([len1/len(dataSet), len2/len(dataSet)])
                    igr = gain / h
                    if igr > maxIGR:
                        maxIGR = igr
                        best = [attr, 'num', thresh]
        #print max_gain
        if maxIGR <= 0:
            return 0
        return best

def entropy(lst):
    entrop = 0.0
    for p in lst:
        if p == 0:
            continue
        entrop -= p * math.log(p, 2)
    return entrop

def unique(seq):
    keys = {}
    for e in seq:
        keys[e] = 1
    return keys.keys()

def allequal(seq):
    flag = seq[0]
    for item in seq:
        if item != flag:
            return 0
    return 1

def readData(inputfile):
    data = []
    abspath = os.path.abspath(inputfile)
    with open(abspath,"r")as file:
        text = file.readlines()
    for line in text:
        items = line.split(',')[:-1]
        items.pop(26)
        items.pop(26)
        data.append(items)
    print data[0]
    print len(data[0])
    return data

#这个函数是选取频率最大的
def getDefault(lst):
    frequent = Counter(lst)
    mostfrequent = frequent.most_common(2)
    if mostfrequent[0][0] == '?':
        mostfrequent = mostfrequent[1:]
    return mostfrequent[0][0]

format = []
for i in range(28):
    format.append("nominal")
for i in [0,17,19,21,23,25]:
    format[i] = "numeric"

inputfile = "allbp"
trainSet = readData(inputfile)
classifier = C4_5(trainSet, format, [])
classifier.startTrain()

