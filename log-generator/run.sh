#!/bin/bash
LANG="en_US.UTF-8" LC_ALL="en_US.UTF-8" pipenv run python3 log-generator.py ${LOGS_PER_MINUTE} > /tmp/generated-logs.log