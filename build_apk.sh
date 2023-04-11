#!/bin/sh
# author zixie

#函数定义，检测执行结果
function checkResult() {
  result=$?
  echo "result : $result"
  if [ $result -eq 0 ]; then
    echo "checkResult: execCommand succ"
  else
    echo "checkResult: execCommand failed"
    exit $result
  fi
}

deleteempty() {
  find ${1:-.} -mindepth 1 -maxdepth 1 -type d | while read -r dir; do
    if [[ -z "$(find "$dir" -mindepth 1 -type f)" ]] >/dev/null; then
      echo "$dir"
      rm -rf ${dir} 2>&- && echo "Empty, Deleted!" || echo "Delete error"
    fi
    if [ -d ${dir} ]; then
      deleteempty "$dir"
    fi
  done
}
echo "********APK build init set env *******"
localPath=$(pwd)
echo "localPath:"$localPath

branchName=$(git symbolic-ref --short -q HEAD)
echo "branchName:"${branchName}
if [ x"$branchName" = x ]; then
  branchName=${SVN_BRANCH}
fi
echo "branchName:"${branchName}
git checkout --force ${branchName} && git pull
git status

git tag -l | xargs git tag -d
git fetch

echo "ANDROID_HOME:"$ANDROID_HOME
echo "JAVA_HOME:"$JAVA_HOME
echo "ANDROID_NDK_HOME:"$ANDROID_NDK_HOME
echo "********APK build define consts *******"

# 根据编译参数，确定最终版本是哪条线
appPrefix=${1}
echo "appPrefix:${appPrefix}"
if [ "$appPrefix"x = x ]; then
  appPrefix="ZAPK"
fi
echo "appPrefix:${appPrefix}"

appModule=${2}
echo "appModule:${appModule}"
if [ "$appModule"x = x ]; then
  appModule="PubGetAPKInfo"
fi
echo "appModule:${appModule}"

appPackageName=${3}
echo "appPackageName:${appPackageName}"
if [ "appPackageName"x = x ]; then
  appPackageName="com.bihe0832.getsignature"
fi

appApplicationName=${4}
echo "appApplicationName:${appApplicationName}"
if [ "appApplicationName"x = x ]; then
  appApplicationName="com.bihe0832.android.app.Application"
fi

echo "MajorVersion before:"$MajorVersion
echo "MinorVersion before:"$MinorVersion
echo "FixVersion before:"$FixVersion
echo "isOfficial:${isOfficial}"

timeinfo=$(date +'%m%d')
version_code=$(git rev-list HEAD --count)
version="${MajorVersion}.${MinorVersion}.${FixVersion}"
versionWithCode=${version}_${version_code}
tag=Tag_${appPrefix}_${versionWithCode}
commitId=$(git rev-parse HEAD)

echo "version:"$version
echo "timeinfo:"$timeinfo
echo "version_code:"$version_code
echo "versionWithCode:"$versionWithCode
echo "commitId:"$commitId
echo "tag:"$tag

echo "********APK build mkdir bin *******"
cd $localPath
#临时文件
if [ ! -d "$localPath/bin" ]; then
  mkdir "$localPath/bin"
fi
#rm -fr $localPath/bin/*
rm -fr $localPath/bin/temp
mkdir $localPath/bin/temp

echo "********APK build touch tag *******"
echo -e "${version} info：\n-----------------------------\n\nVERSION_NAME=${version}\nVERSION_CODE=${version_code}\nTAG=${tag}\nDATETIME=${timeinfo}\nCOMMIT=${commitId}\nisOfficial=${isOfficial}\n\n-----------------------------" >$localPath/bin/${tag}
echo -e "${version} info：\n-----------------------------\n\nVERSION_NAME=${version}\nVERSION_CODE=${version_code}\nTAG=${tag}\nDATETIME=${timeinfo}\nCOMMIT=${commitId}\nisOfficial=${isOfficial}\n\n-----------------------------" >$localPath/Application/src/main/assets/app.ini
echo "********APK build modify version *******"

# 修改版本号
src="versionCode *= *[0-9]*"
dst="versionCode = ${version_code}"
cat $localPath/config.gradle | sed "s/$src/$dst/g" >$localPath/bin/temp/config.gradle
mv -f $localPath/bin/temp/config.gradle $localPath/config.gradle

# 修改版本名称
src="versionName *= *\\\"[0-9]*\.[0-9]*\.[0-9]*\\\""
dst="versionName = \"${version}\""
cat $localPath/config.gradle | sed "s/$src/$dst/g" >$localPath/bin/temp/config.gradle
mv -f $localPath/bin/temp/config.gradle $localPath/config.gradle

#修改tag
src="VERSION_TAG *= *\\\"Tag_ZIXIE_[0-9]*\.[0-9]*\.[0-9]*_[0-9]*\\\""
dst="VERSION_TAG = \\\"$tag"\\\"
cat $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java | sed "s/$src/$dst/g" >$localPath/bin/temp/Application.java
mv -f $localPath/bin/temp/Application.java $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java

#打开本地测试
src="IS_TEST_VERSION *= *false"
dst="IS_TEST_VERSION = true"
cat $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java | sed "s/$src/$dst/g" >$localPath/bin/temp/Application.java
mv -f $localPath/bin/temp/Application.java $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java

#重置发布标示
src="IS_OFFICIAL_VERSION *= *true"
dst="IS_OFFICIAL_VERSION = false"
cat $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java | sed "s/$src/$dst/g" >$localPath/bin/temp/Application.java
mv -f $localPath/bin/temp/Application.java $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java

# 关闭代码混淆
src="minifyEnabled *= *true"
dst="minifyEnabled = false"
cat $localPath/config.gradle | sed "s/$src/$dst/g" >$localPath/bin/temp/config.gradle
mv -f $localPath/bin/temp/config.gradle $localPath/config.gradle

echo "********APK build *******"

#调试包构建
cd $localPath
echo "********APK build debug start gradlew *******"
src=" *ext.mainProject *= *\\\""
dst="ext.mainProject = \\\"APPTest,${appModule}\\\""
cat $localPath/dependencies.gradle | sed "/$src/s/.*/$dst/" >$localPath/bin/dependencies.gradle
mv -f $localPath/bin/dependencies.gradle $localPath/dependencies.gradle

src=" *ext.developModule *= *\\\""
dst="ext.developModule = \\\"Application\\\""
cat $localPath/dependencies.gradle | sed "/$src/s/.*/$dst/" >$localPath/bin/dependencies.gradle
mv -f $localPath/bin/dependencies.gradle $localPath/dependencies.gradle

#如果需要修改包名，修改APPTest的包名
echo "appPackageName:${appPackageName}"
echo "appPrefix:${appPrefix}"

if [ -n "$appPackageName" ]; then
  echo "change appPackageName"
  src="project.ext.applicationID *= *\\\""
  dst="project.ext.applicationID = \\\"$appPackageName"\\\"
  cat $localPath/APPTest/build.gradle | sed "/$src/s/.*/$dst/" >$localPath/bin/temp/build.gradle
  mv -f $localPath/bin/temp/build.gradle $localPath/APPTest/build.gradle
fi

if [ -n "$appPrefix" ]; then
  echo "change appPrefix"
  src="project.ext.applicationPrefix *= *\\\""
  dst="project.ext.applicationPrefix = \\\"$appPrefix"\\\"
  cat $localPath/APPTest/build.gradle | sed "/$src/s/.*/$dst/" >$localPath/bin/temp/build.gradle
  mv -f $localPath/bin/temp/build.gradle $localPath/APPTest/build.gradle
fi

# 切换Application类名
dst="android:name=\\\"$appApplicationName\\\""
cat $localPath/APPTest/src/main/AndroidManifest.xml | sed "/<application/,/android:name/s/android:name.*/$dst/" >$localPath/bin/temp/AndroidManifest.xml
mv -f $localPath/bin/temp/AndroidManifest.xml $localPath/APPTest/src/main/AndroidManifest.xml

# 将 Pub 切换为Library模式
src=" *ext.pubModuleIsApplication *="
dst="ext.pubModuleIsApplication = false"
cat $localPath/dependencies.gradle | sed "/$src/s/.*/$dst/" >$localPath/bin/dependencies.gradle
mv -f $localPath/bin/dependencies.gradle $localPath/dependencies.gradle

# 将 Pub 添加到APPTest的依赖
dst="\\\"apidependenciesList\\\" : [ \\\"${appModule}\\\","
cat $localPath/dependencies.gradle | sed "/ *\\\"APPTest\\\" *: */,/\\\"apidependenciesList\\\"/s/\\\"apidependenciesList\\\".*/$dst/" >$localPath/bin/temp/dependencies.gradle
mv -f $localPath/bin/temp/dependencies.gradle $localPath/dependencies.gradle

#返回上层目录启动构建
chmod +x $localPath/gradlew
cd $localPath && ./gradlew clean
cd $localPath && ./gradlew :APPTest:assembleDebug -x lint -x verifyReleaseResources -x lintVitalRelease
checkResult

# 签名完整包
$ANDROID_HOME/build-tools/32.0.0/zipalign -p -v 4 $localPath/APPTest/build/outputs/apk/debug/${appPrefix}_V${versionWithCode}_debug.apk $localPath/bin/temp/${appPrefix}_V${versionWithCode}_${timeinfo}_beta_unsigned_zipalign.apk
$ANDROID_HOME/build-tools/32.0.0/apksigner sign --ks $localPath/debug.keystore --out $localPath/bin/temp/${appPrefix}_V${versionWithCode}_${timeinfo}_beta.apk --ks-pass pass:android $localPath/bin/temp/${appPrefix}_V${versionWithCode}_${timeinfo}_beta_unsigned_zipalign.apk
cp -fr $localPath/bin/temp/${appPrefix}_V${versionWithCode}_${timeinfo}_beta.apk $localPath/bin/${appPrefix}_V${versionWithCode}_${timeinfo}_beta.apk
checkResult

echo "********APK build debug with end*******"

echo "********APK build official *******"
echo "isOfficial:${isOfficial}"
#是否对外版本
if [ "$isOfficial"x = "true"x ]; then
  echo "********APK build change official *******"
  #关闭本地测试
  src="IS_TEST_VERSION *= *true"
  dst="IS_TEST_VERSION = false"
 cat $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java | sed "s/$src/$dst/g" >$localPath/bin/temp/Application.java
 mv -f $localPath/bin/temp/Application.java $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java

  #设置发布标示
  src="IS_OFFICIAL_VERSION *= *false"
  dst="IS_OFFICIAL_VERSION = true"
 cat $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java | sed "s/$src/$dst/g" >$localPath/bin/temp/Application.java
 mv -f $localPath/bin/temp/Application.java $localPath/Application/src/main/java/com/bihe0832/android/app/Application.java

  # 将主工程修改为当前工程
  src=" *ext.mainProject *= *\\\""
  dst="ext.mainProject = \\\"${appModule}\\\""
  cat $localPath/dependencies.gradle | sed "/$src/s/.*/$dst/" >$localPath/bin/dependencies.gradle
  mv -f $localPath/bin/dependencies.gradle $localPath/dependencies.gradle

  # 将 Pub 切换为Library模式
  src=" *ext.pubModuleIsApplication *="
  dst="ext.pubModuleIsApplication = true"
  cat $localPath/dependencies.gradle | sed "/$src/s/.*/$dst/" >$localPath/bin/dependencies.gradle
  mv -f $localPath/bin/dependencies.gradle $localPath/dependencies.gradle

  # 关闭代码混淆
  src="minifyEnabled *= *false"
  dst="minifyEnabled = true"
  cat $localPath/config.gradle | sed "s/$src/$dst/g" >$localPath/bin/temp/config.gradle
  mv -f $localPath/bin/temp/config.gradle $localPath/config.gradle


  echo "********APK build official start gradlew *******"
  #返回上层目录启动构建
  chmod +x $localPath/gradlew
  cd $localPath && ./gradlew clean
  cd $localPath && ./gradlew :${appModule}:assembleRelease -x lint
  checkResult
  mkdir $localPath/bin/temp/official
  rm -fr $localPath/bin/temp/official/*
  #添加资源混淆
  java -jar $localPath/AndResGuard-cli-1.2.0.jar $localPath/${appModule}/build/outputs/apk/release/${appPrefix}_V${versionWithCode}_release.apk -config $localPath/proguard-rules-resource.xml -out $localPath/bin/temp/official
  # 签名完整包
  $ANDROID_HOME/build-tools/32.0.0/zipalign -p -v 4 $localPath/bin/temp/official/${appPrefix}_V${versionWithCode}_release_unsigned.apk $localPath/bin/temp/official/${appPrefix}_V${versionWithCode}_official_unsigned_zipalign.apk
  $ANDROID_HOME/build-tools/32.0.0/apksigner sign --ks $localPath/debug.keystore --out $localPath/bin/temp/${appPrefix}_V${versionWithCode}_${timeinfo}_official.apk --ks-pass pass:android $localPath/bin/temp/official/${appPrefix}_V${versionWithCode}_official_unsigned_zipalign.apk
  cp -fr $localPath/bin/temp/${appPrefix}_V${versionWithCode}_${timeinfo}_official.apk $localPath/bin/${appPrefix}_V${versionWithCode}_${timeinfo}_official.apk
  checkResult

  echo "********APK build start add mapping and hash*******"
  mkdir $localPath/bin/temp/official/mapping
  cp -r $localPath/bin/temp/official/resource_mapping_${appPrefix}_V${versionWithCode}_release.txt $localPath/bin/temp/official/mapping/${tag}_mapping_resource.txt
  checkResult
  cp -r $localPath/${appModule}/build/outputs/mapping/release/* $localPath/bin/temp/official/mapping/
  checkResult
  cp -r $localPath/${appModule}/build/outputs/mapping/release/mapping.txt $localPath/bin/temp/official/mapping/${tag}_mapping.txt
  checkResult
  cd $localPath/bin/temp/official/mapping/
  zip -rq $localPath/bin/${tag}_mapping.zip ./
  checkResult
fi

if [ "$isOfficial"x = "true"x ]; then
  echo "********APK build start add tag*******"
  git tag -a $tag --file=$localPath/bin/${tag}
  checkResult
  git status
  git show $tag | head
  checkResult
  echo "********APK build start push code*******"
  git config push.default simple
  git push
  checkResult
  echo "********APK build start push tag*******"
  git push origin $tag
  checkResult
fi
git status
rm -fr $localPath/bin/temp
checkResult