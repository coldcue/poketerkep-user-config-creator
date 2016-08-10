#!/usr/bin/python
# -*- coding: utf-8 -*-

"""tos.py - Accept PokemonGo ToS for multiple accounts using file."""

from pgoapi import PGoApi
from pgoapi.utilities import f2i
from pgoapi import utilities as util
from pgoapi.exceptions import AuthException
from pgoapi.exceptions import ServerSideRequestThrottlingException
from pgoapi.exceptions import NotLoggedInException
import pprint
import time
import threading
import sys, getopt

def accept_tos(username, password):
        api = PGoApi()
        api.set_position(40.7127837, -74.005941, 0.0)
        api.login('ptc', username, password)
        time.sleep(2)
        req = api.create_request()
        req.mark_tutorial_complete(tutorials_completed = 0, send_marketing_emails = False, send_push_notifications = False)
        response = req.call()
        print('Accepted Terms of Service for {}'.format(username))

with open(str(sys.argv[1])) as f:
        credentials = [x.strip().split(':') for x in f.readlines()]

for username,password in credentials:
        try:
                accept_tos(username, password)
        except ServerSideRequestThrottlingException as e:
                print('Server side throttling, Waiting 10 seconds.')
                time.sleep(10)
                accept_tos(username, password)
        except NotLoggedInException as e1:
                print('Could not login, Waiting for 10 seconds')
                time.sleep(10)
                accept_tos(username, password)
