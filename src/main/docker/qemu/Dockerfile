FROM multiarch/qemu-user-static:latest@sha256:fe60359c92e86a43cc87b3d906006245f77bfc0565676b80004cc666e4feb9f0

# see https://snapshot.debian.org/binary/qemu-user-static/ or https://snapshot.debian.org/
ARG DEB_MIRROR1="https://snapshot.debian.org/archive/debian/20240701T023644Z/pool/main/q/qemu/qemu-user-static_8.2.5%2Bds-2_amd64.deb"
ARG DEB_MIRROR2="http://ftp.us.debian.org/debian/pool/main/q/qemu/qemu-user-static_9.0.2+ds-1~bpo12+1_amd64.deb"
RUN wget ${DEB_MIRROR1} || wget ${DEB_MIRROR2}

RUN mkdir releases &&\
  dpkg-deb -X qemu-user-static_*.deb releases &&\
  echo "copying " &&\
  tree releases/usr/bin &&\
  cp -f releases/usr/bin/* /usr/bin/
