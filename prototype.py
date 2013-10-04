#!/usr/bin/env python
# coding=utf-8
#
# Author: Arnau Orriols

from requests import get
from lxml.html import document_fromstring

BASE_URL = 'http://www.ign.es/ign/layoutIn/sismoListadoTerremotos.do'

def get_earthquakes_list():

    payload = {'zona' : '1', 'cantidad_dias' : 5}

    web_content = document_fromstring(get(BASE_URL, params=payload).text)

    eq_table = web_content.cssselect('tr.filaNegra2')

    eq_list = []
    for row in eq_list:
        eq_list.appned({'date' : row[1].text,
                        'hour' : row[2].text,
                        'magnitude' : row[7].text,
                        'location' : row[9].text
        })

    return eq_list


if __name__ == '__main__':
    get_earthquakes_list()
