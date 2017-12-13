FROM openjdk:7u151-jdk as play-builder
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

# configure the "inativ" user
RUN /usr/sbin/useradd -u 1000 --create-home --home-dir /home/inativ --shell /bin/bash inativ

ADD . /opt/app

WORKDIR /opt/app

ARG PLAY_VERSION
ENV PLAY_VERSION ${PLAY_VERSION}

RUN ant -f framework/build.xml package -Dversion=$PLAY_VERSION
# TODO : voir si y a pas du ménage à faire (javadoc, samples, etc.)

FROM openjdk:7u151-jre-slim as play-extracted
ARG PLAY_VERSION
ENV PLAY_VERSION ${PLAY_VERSION}
COPY --from=play-builder /opt/app/framework/dist/play-$PLAY_VERSION.zip /tmp
RUN unzip /tmp/play-${PLAY_VERSION}.zip -d /tmp && mv /tmp/play-${PLAY_VERSION} /opt/play && rm -rf /opt/play/samples-and-tests/ /opt/play/documentation

# Second stage

FROM openjdk:7u151-jre-slim
MAINTAINER mOuLiNeX & eesprit

RUN apt-get -y update && apt-get install -y python locales && rm -rf /var/lib/apt/lists/*
ENV PATH /opt/play:$PATH

RUN sed -i 's/#\ en_US\.UTF-8/en_US\.UTF-8/' /etc/locale.gen && locale-gen

# Gestion locale pour FS
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# Set the timezone.
RUN echo "Europe/Paris" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata

# configure the "inativ" user
RUN /usr/sbin/useradd -u 1000 --create-home --home-dir /home/inativ --shell /bin/bash inativ
COPY --from=play-extracted /opt/play /opt/play
