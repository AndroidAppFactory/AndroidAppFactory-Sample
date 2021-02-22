#!/bin/sh
# author hardyshi

#函数定义，检测执行结果
function checkResult() {
   result=$?
   echo "result : $result"
   if [ $result -eq 0 ];then
      echo "checkResult: execCommand succ"
   else
    echo "checkResult: execCommand failed"
    exit $result
   fi
}

echo "********APK build init set env *******"
localPath=`pwd`
echo "localPath:"$localPath
chmod +x ./build_apk.sh
echo "appNo:${appNo}"

#获取应用基本信息的APP
if [ "$appNo"x = "1"x ];then
  /bin/bash ./build_apk.sh ZAPK PubGetAPKInfo com.bihe0832.getsignature com.bihe0832.android.app.Application
  checkResult
fi


#拼图APP
if [ "$appNo"x = "2"x ];then
  /bin/bash ./build_apk.sh ZPUZZLE PubPuzzleGame com.bihe0832.puzzle com.bihe0832.android.app.Application
  checkResult
fi

#M3u8 APP
if [ "$appNo"x = "3"x ];then
  /bin/bash ./build_apk.sh ZM3U8 PubM3U8 com.bihe0832.m3u8 com.bihe0832.android.app.Application
  checkResult
fi
