FROM multiarch/qemu-user-static:latest@sha256:fe60359c92e86a43cc87b3d906006245f77bfc0565676b80004cc666e4feb9f0

RUN wget "http://ftp.de.debian.org/debian/pool/main/q/qemu/qemu-user-static_8.2.5+ds-2_amd64.deb"
RUN mkdir releases &&\
    dpkg-deb -X qemu-user-static_*.deb releases &&\
    echo "copying " &&\
    tree releases/usr/bin &&\
    cp -f releases/usr/bin/* /usr/bin/

