#!/usr/bin/env python
# coding=utf-8
#
# Requires libraries: Requests, lxml
#
# Author: Arnau Orriols

from requests import get
from lxml.html import document_fromstring


BASE_URL = 'http://www.ign.es/ign/layoutIn/sismoListadoTerremotos.do'


def get_earthquakes_list(list_days=5):
    """
    Make a request to ign web and parse the table containing all the
    earthquakes' data.

    Returns a list, whose items are dictionaries with the following
    layout:

        {'date' : '<day_of_eq>', 'time' : '<time_of_eq>',
         'magnitude' : '<magnitude>', 'location' : '<location>'}

    """
    payload = {'zona' : '1', 'cantidad_dias' : list_days}
    web_content = document_fromstring(get(BASE_URL, params=payload).text)

    eq_table = web_content.cssselect('tr.filaNegra2')
    eq_list = []
    for row in eq_table:
        eq_list.append({'date' : row[1].text,
                        'time' : row[2].text,
                        'magnitude' : row[7].text,
                        'location' : row[9].text
        })

    return eq_list

