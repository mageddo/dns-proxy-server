#!/bin/sh

set -e

CUR_DIR=`pwd`
APP_VERSION=$(cat VERSION)
REPO_URL=mageddo/dns-proxy-server


assemble(){
	echo "> Testing ..."
	go test -p 1 -cover -ldflags "-X github.com/mageddo/dns-proxy-server/flags.version=test" ./.../
	echo "> Tests completed"

	echo "> Building..."

	rm -rf build/
	mkdir -p build/

	cp -r /static build/
}

generateDocs(){
	rm -r ${TARGET} || echo "not exists ${TARGET}"
	hugo --baseURL=http://mageddo.github.io/dns-proxy-server/$1 \
	--destination $2 \
	--ignoreCache --source docs/
}

case $1 in

	setup-repository )
		git remote remove origin  && git remote add origin https://${REPO_TOKEN}@github.com/$REPO_URL.git
		git checkout -b build_branch ${CURRENT_BRANCH}
		echo "> Repository added, travisBranch=${CURRENT_BRANCH}"

	;;

	upload-release )

		DESC=$(cat RELEASE-NOTES.md | awk 'BEGIN {RS="|"} {print substr($0, 0, index(substr($0, 3), "###"))}' | sed ':a;N;$!ba;s/\n/\\r\\n/g')
		github-cli release mageddo dns-proxy-server $APP_VERSION $CURRENT_BRANCH "${DESC}" $PWD/build/*.tgz

	;;

	docs )

	VERSION=$(cat VERSION | awk -F '.' '{ print $1"."$2}');
	TARGET=$PWD/../dns-proxy-server-docs/${VERSION}
	generateDocs ${VERSION} ${TARGET}

	VERSION=latest
	TARGET=$PWD/../dns-proxy-server-docs/${VERSION}
	generateDocs ${VERSION} ${TARGET}

	;;

	apply-version )

		# updating files version
		sed -i -E "s/(dns-proxy-server.*)[0-9]+\.[0-9]+\.[0-9]+/\1$APP_VERSION/" docker-compose.yml
		sed -i -E "s/[0-9]+\.[0-9]+\.[0-9]+/$APP_VERSION/g" Dockerfile*.hub

	;;

	assemble )
		assemble
	;;

	build )

		assemble

		if [ ! -z "$2" ]
		then
			builder.bash compile $2 $3
			exit 0
		fi

		# ARM
		builder.bash compile linux arm
		builder.bash compile linux arm64

		# LINUX
		# INTEL / AMD
		builder.bash compile linux 386
		builder.bash compile linux amd64

		echo "> Build success"

	;;

	compile )
		export GOOS=$2
		export GOARCH=$3
		echo "> Compiling os=${GOOS}, arch=${GOARCH}"
		go build -o $PWD/build/dns-proxy-server -ldflags "-X github.com/mageddo/dns-proxy-server/flags.version=$APP_VERSION"
		TAR_FILE=dns-proxy-server-${GOOS}-${GOARCH}-${APP_VERSION}.tgz
		cd $PWD/build/
		tar --exclude=*.tgz -czf $TAR_FILE *
	;;

	validate-release )

		if git rev-parse "$APP_VERSION^{}" >/dev/null 2>&1; then
			echo "> Version already exists $APP_VERSION"
			exit 3
		fi

	;;

	deploy-ci )

	EC=0
	docker-compose up --force-recreate --abort-on-container-exit prod-ci-deploy || EC=$?
	if [ "$EC" = "3" ]; then
		exit 0
	elif [ "$EC" -ne "0" ]; then
		exit $EC
	fi
	docker-compose build prod-build-image-dps-arm7x86 prod-build-image-dps-arm8x64 &&\
	echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin &&\
	docker-compose push prod-build-image-dps-arm7x86 prod-build-image-dps-arm8x64

	;;

	release )

		echo "> build started, current branch=$CURRENT_BRANCH"
		if [ "$CURRENT_BRANCH" = "master" ]; then
			echo "> deploying new version"
			builder.bash validate-release && builder.bash apply-version && builder.bash build && builder.bash upload-release

		else
			echo "> building candidate"
			builder.bash validate-release
			builder.bash build
		fi

	;;

esac
