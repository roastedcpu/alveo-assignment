FROM debian:buster
USER root
RUN apt update && apt install -y python3 pipenv
COPY ./ /opt/log-generator
WORKDIR /opt/log-generator
RUN LANG="en_US.UTF-8" LC_ALL="en_US.UTF-8" LC_CTYPE="en_US.UTF-8" pipenv install -r requirements.txt
