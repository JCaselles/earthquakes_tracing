#!/usr/bin/env python
# coding=utf-8
#
# Author: Arnau Orriols

from daemon import runner
import prototype
from os.path import expanduser
from time import sleep
from smtplib import SMTP
from sys import argv

class App():

    MAIL_MSG = 'A new earthquake has taken place: \n\n'
    WORKING_PATH = expanduser('~/earthquake_tracing/')

    def __init__(self):
        self.stdin_path = '/dev/null'
        self.stdout_path = '%searthquakes.log' % self.WORKING_PATH
        self.stderr_path = '%serror.log' % self.WORKING_PATH
        self.pidfile_path = '%sprototyped.pid' % self.WORKING_PATH
        self.pidfile_timeout = 5

    def run(self):
        former_latest = None
        while True:
            latest_eq = prototype.get_earthquakes_list(1)[0]
            if former_latest:
                if former_latest != latest_eq:
                    if len(argv) >= 4:
                        server = SMTP('smtp.gmail.com:587')
                        server.starttls()
                        server.login(argv[2], argv[3])
                        to_addrs = [argv[2]]
                        if len(argv) > 4:
                            for email_addrs in argv[4:]:
                                to_addrs.append(email_addrs)
                        server.sendmail(argv[2], to_addrs,
                                compose_mail(latest_eq).encode('ascii',
                                                               'replace'))
                                # FIXME: this is a workaroud, fix encoding
                                # issues
                        server.quit()
                    else:
                        with open('%searthquakes.log', 'a') as file_log:
                            file_log.write(latest_eq)

            former_latest = latest_eq
            sleep(60*5)


def compose_mail(data_dict):

    return """
    A new earthquake has taken place:
        Date: %s
        Time: %s
        Magnitude: %s
        Location: %s



    (Auto email by prototyped)
    """ % (data_dict['date'], data_dict['time'],
           data_dict['magnitude'].strip(), data_dict['location'])


app = App()
daemon_runner = runner.DaemonRunner(app)
daemon_runner.do_action()


