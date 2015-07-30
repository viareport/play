FROM ubuntu:14.04
 
# make sure the package repository is up to date
RUN echo "deb http://archive.ubuntu.com/ubuntu trusty main universe" > /etc/apt/sources.list
RUN apt-get -y update

# install python-software-properties (so you can do add-apt-repository)
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y -q software-properties-common python-software-properties curl

# clean openJDKs and install JavaSE 6
RUN apt-get purge openjdk* && add-apt-repository ppa:webupd8team/java && apt-get -y update && echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections && apt-get install -y oracle-java6-installer

WORKDIR /opt

# Install Python, Ant and Play! Viareport.
RUN apt-get install -y python ant
ENV PATH /opt/play:$PATH

ADD ./framework/dist/play /opt/play
