FROM openjdk:7u131-jdk
 
MAINTAINER mOuLiNeX & eesprit
 
WORKDIR /opt

# Install Python, Ant for Play! Viareport.
RUN apt-get -y update && apt-get install -y python ant locales && rm -rf /var/lib/apt/lists/*
ENV PATH /opt/play:$PATH

ADD ./framework/dist/play /opt/play

# Gestion locale pour FS
RUN locale-gen en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# configure the "inativ_dev" user
RUN /usr/sbin/useradd -u 1000 --create-home --home-dir /home/inativ_dev --shell /bin/bash inativ_dev
USER inativ_dev

# Set the timezone.
RUN echo "Europe/Paris" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata
