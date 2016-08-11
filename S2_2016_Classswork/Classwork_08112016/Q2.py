'''
Created on Aug 11, 2016

@author: shabbirhussain
'''
import nltk
import time

MAX_SIZE = 2000

nltk.data.path = ['/Users/shabbirhussain/Data/nltk_data']
posts = nltk.corpus.nps_chat.xml_posts()[:MAX_SIZE]

def punctuation_features(post):
    features={}
    for word in nltk.wordpunct_tokenize(post):
        features['contains({})'.format(word.lower())] = True
    return features
def dialogue_act_features(post):
    features={}
    for word in nltk.word_tokenize(post):
        features['contains({})'.format(word.lower())] = True
    return features
def sentense_features(post):
    features={}
    for word in nltk.sent_tokenize(post):
        features['contains({})'.format(word.lower())] = True
    return features


def extract_features(extractorFn):
    featuresets = [(extractorFn(post.text), post.get('class')) for post in posts]
    size = 1000
    train_set, test_set = featuresets[size:], featuresets[:size]
    print('Training size={}, Test size={}'.format(len(train_set), len(test_set)))
    
    print('For {}'.format(extractorFn))
    print(executeClassification(nltk.NaiveBayesClassifier  , train_set, test_set))
    
    return


def executeClassification(c, train_set, test_set):
    start = time.time()
    classifier = c.train(train_set)
    timeToTrain = time.time() - start
    
    start = time.time()
    accuracy = nltk.classify.accuracy(classifier, test_set)
    timeToTest = time.time() - start
    
    
    stats = ('{} took {}s to train and {}s to test with accuracy {}'
          .format(c, timeToTrain, timeToTest, accuracy))
    return stats


if __name__ == '__main__':
    extract_features(dialogue_act_features)
    extract_features(punctuation_features)
    extract_features(sentense_features)
    
    pass