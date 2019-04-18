FROM openjdk:7u181-jdk as play-builder-full
MAINTAINER mOuLiNeX & eesprit
 
# Install Python, Ant for Play! Viareport.
RUN apt-get -y update && apt-get install -y python ant locales && rm -rf /var/lib/apt/lists/*
ENV PATH /opt/play:$PATH

ADD ivysettings.xml /root/.ivy2/ivysettings.xml

ADD . /usr/local/src/play

WORKDIR /usr/local/src/play

ARG PLAY_VERSION
ENV PLAY_VERSION ${PLAY_VERSION}

RUN ant -f framework/build.xml package -Dversion=$PLAY_VERSION
RUN unzip framework/dist/play-${PLAY_VERSION}.zip -d /tmp && mv /tmp/play-${PLAY_VERSION} /opt/play && rm -rf /opt/play/samples-and-tests/ /opt/play/documentation

### Second stage / image de build un peu plus ligh (sans le code source)
FROM openjdk:7u181-jdk as play-builder
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

ADD ivysettings.xml /root/.ivy2/ivysettings.xml

# Pour permettre les builds avec Jenkins / docker
RUN /usr/sbin/useradd -u 109 -g 113 --create-home --home-dir /home/jenkins --shell /bin/bash jenkins

ARG PLAY_VERSION
ENV PLAY_VERSION ${PLAY_VERSION}

COPY --from=play-builder-full /opt/play /opt/play

### Third stage, notre image light
FROM openjdk:7u181-jre-slim
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

ARG PLAY_VERSION
ENV PLAY_VERSION ${PLAY_VERSION}

COPY --from=play-builder /opt/play /opt/play
