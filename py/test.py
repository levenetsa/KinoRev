# -*- coding: utf-8 -*-
from pymystem3 import Mystem
from pyquery   import PyQuery
import urllib2

#gathering rwiews
film_id = 944708
url = 'https://www.kinopoisk.ru/film/' + str(film_id) + '/ord/rating/perpage/100'
text = urllib2.urlopen(url).read()
print 'Readed!' + '\n'

pq = PyQuery(text)
tag = pq('a')
'''print(''.join(tag.text()))
print 'Review fetched!' + '\n'
'''
#getting callocations

'''
m = Mystem()
lemmas = m.lemmatize(text)
print(''.join(lemmas))

from nltk.collocations import *
import nltk

finder = BigramCollocationFinder.from_words(lemmas, window_size = 3)
finder.apply_freq_filter(4)
for k,v in finder.ngram_fd.items():
  print '"' + k[0] + '"  "' + k[1] + '" ' + str(v) + '\n'
'''


#file_object  = open("data1.txt", "w")