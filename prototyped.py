#!/usr/bin/env python
# coding=utf-8
#
# Author: Arnau Orriols

from daemon import runner
import prototype
from os.path import expanduser
from time import sleep

class App():
    
    def __init__(self):
        self.stdin_path = '/dev/null'
        self.stdout_path = expanduser('~/earthquake_tracing/earthquakes.log')
        self.stderr_path = expanduser('~/earthquake_tracing/error.log')
        self.pidfile_path = expanduser('~/earthquake_tracing/prototyped.pid')
        self.pidfile_timeout = 5

    def run(self):
        former_latest = None
        while True:
            latest_eq = prototype.get_earthquakes_list(1)[0]
            if latest_eq:
                if compare_latest(former_latest, latest_eq):
                    print latest_eq
            former_latest = latest_eq
        sleep(60*5)


def compare_latest(former_last, last):
    
    if former_last != last:
        return last


app = App()
daemon_runner = runner.DaemonRunner(app)
daemon_runner.do_action()


