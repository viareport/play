FROM openjdk:7u131-jdk
 
MAINTAINER mOuLiNeX & eesprit
 
# Install Python, Ant for Play! Viareport.
RUN apt-get -y update && apt-get install -y python ant locales && rm -rf /var/lib/apt/lists/*
ENV PATH /opt/play:$PATH

RUN sed -i 's/#\ en_US\.UTF-8/en_US\.UTF-8/' /etc/locale.gen && locale-gen

# Gestion locale pour FS
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# Set the timezone.
RUN echo "Europe/Paris" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata

WORKDIR /opt

ARG PLAY_VERSION
ENV PLAY_VERSION ${PLAY_VERSION}

ADD ./framework/dist/play-${PLAY_VERSION}.tgz /opt/

# configure the "inativ" user
RUN /usr/sbin/useradd -u 1000 --create-home --home-dir /home/inativ --shell /bin/bash inativ

USER inativ
