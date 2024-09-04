FROM multiarch/qemu-user-static:latest@sha256:fe60359c92e86a43cc87b3d906006245f77bfc0565676b80004cc666e4feb9f0

ARG DEB_MIRROR1="http://archive.ubuntu.com/ubuntu/pool/universe/q/qemu/qemu-user-static_8.2.2+ds-0ubuntu1.2_amd64.deb"
ARG DEB_MIRROR2="http://ftp.de.debian.org/debian/pool/main/q/qemu/qemu-user-static_8.2.5+ds-2_amd64.deb"
ARG DEB_MIRROR3="http://download.unesp.br/ubuntu/pool/universe/q/qemu/qemu-user-static_8.2.2+ds-0ubuntu1_amd64.deb"
ARG DEB_MIRROR4="https://deb.sipwise.com/debian/pool/main/q/qemu/qemu-user-static_8.2.1+ds-1~bpo12+1_amd64.deb"
RUN wget ${DEB_MIRROR1} || wget ${DEB_MIRROR2} || wget ${DEB_MIRROR3} || wget ${DEB_MIRROR4}

RUN mkdir releases &&\
    dpkg-deb -X qemu-user-static_*.deb releases &&\
    echo "copying " &&\
    tree releases/usr/bin &&\
    cp -f releases/usr/bin/* /usr/bin/

