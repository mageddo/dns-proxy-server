#!/bin/sh

set -e

CUR_DIR=`pwd`
APP_VERSION=$(cat VERSION)
REPO_URL=mageddo/dns-proxy-server

create_release(){
	# release notes
	DESC=$(cat RELEASE-NOTES.md | awk 'BEGIN {RS="|"} {print substr($0, 0, index(substr($0, 3), "###"))}' | sed ':a;N;$!ba;s/\n/\\r\\n/g') && \
	PAYLOAD='{
		"tag_name": "%s",
		"target_commitish": "%s",
		"name": "%s",
		"body": "%s",
		"draft": false,
		"prerelease": true
	}'
	PAYLOAD=$(printf "$PAYLOAD" $APP_VERSION $CURRENT_BRANCH $APP_VERSION "$DESC")
	TAG_ID=$(curl -i -s -f -X POST "https://api.github.com/repos/$REPO_URL/releases?access_token=$REPO_TOKEN" \
--data "$PAYLOAD" | grep -o -E 'id": [0-9]+'| awk '{print $2}' | head -n 1)
}

upload_file(){
	curl --data-binary "@$SOURCE_FILE" -i -w '\n' -f -s -X POST -H 'Content-Type: application/octet-stream' \
"https://uploads.github.com/repos/$REPO_URL/releases/$TAG_ID/assets?name=$TARGET_FILE&access_token=$REPO_TOKEN"
}

case $1 in

	setup-repository )
		git remote remove origin  && git remote add origin https://${REPO_TOKEN}@github.com/$REPO_URL.git
		git checkout -b build_branch ${CURRENT_BRANCH}
		echo "> Repository added, travisBranch=${CURRENT_BRANCH}"

	;;

	upload-release )

		if [ "$REPO_TOKEN" = "" ] ; then echo "REPO_TOKEN cannot be empty"; exit 1; fi

		if [ "`git config user.email || echo ''`" = "" ]; then
			echo '> custom config'
			git config user.name `git config user.name || echo 'CI BOT'`
			git config user.email `git config user.email || echo 'ci-bot@mageddo.com'`
		fi
		echo '> config'
		git config -l
		echo ''

		REMOTE="https://${REPO_TOKEN}@github.com/${REPO_URL}.git"

		git checkout -b build_branch ${CURRENT_BRANCH}
		echo "> Repository added, currentBranch=${CURRENT_BRANCH}"

		git commit -am "Releasing ${APP_VERSION}" || true
		git tag ${APP_VERSION}
		git push "$REMOTE" "build_branch:${CURRENT_BRANCH}"
		git status
		echo "> Branch pushed - Branch $CURRENT_BRANCH"

		create_release
		echo "> Release created with id $TAG_ID"

		for TARGET_FILE in $PWD/build/*.tgz; do
			SOURCE_FILE="$PWD/build/$TARGET_FILE"
			echo "> Source file hash"
			md5sum $SOURCE_FILE && ls -lha $SOURCE_FILE
			upload_file
		done

	;;

	apply-version )

		# updating files version
		sed -i -E "s/(dns-proxy-server.*)[0-9]+\.[0-9]+\.[0-9]+/\1$APP_VERSION/" docker-compose.yml
		sed -i -E "s/[0-9]+\.[0-9]+\.[0-9]+/$APP_VERSION/g" Dockerfile.hub

	;;

	test )
		echo "> Testing ..."
		go test -race -cover -ldflags "-X github.com/mageddo/dns-proxy-server/flags.version=test" ./.../
		echo "> Tests completed"
	;;

	build )

		echo "> Starting build os=${GOOS}, arch=${GOARCH}"

		rm -rf build/
		mkdir -p build/

		echo "> Building..."
		cp -r static build/
		
		export GOOS=linux && export GOARCH=386 && builder.bash compile
		export GOOS=linux && export GOARCH=amd64 && builder.bash compile
		export GOOS=linux && export GOARCH=arm && builder.bash compile
		export GOOS=linux && export GOARCH=arm64 && builder.bash compile

		echo "> Build success"

	;;
	
	compile )
		go build -v -o $PWD/build/dns-proxy-server -ldflags "-X github.com/mageddo/dns-proxy-server/flags.version=$APP_VERSION"
		export TAR_FILE=$PWD/build/dns-proxy-server-${GOOS}-${GOARCH}-${APP_VERSION}.tgz
		tar --exclude=*.tgz -czvf $TAR_FILE *
	;;

	validate-release )

		if git rev-parse "$APP_VERSION^{}" >/dev/null 2>&1; then
			echo "> Version already exists $APP_VERSION"
			exit 3
		fi

	;;

	deploy-ci )

		echo "> build started, current branch=$CURRENT_BRANCH"
		if [ "$CURRENT_BRANCH" = "master" ]; then
			echo "> deploying new version"
			builder.bash validate-release || exit 0 && builder.bash apply-version
			builder.bash build
			builder.bash upload-release
		else
			echo "> building candidate"
			builder.bash build
		fi

	;;

esac
