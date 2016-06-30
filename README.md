

# 导入配置 (根目录下有快速入门文档)
###请注意配置jcenter库 腾讯内部是自己的maven
![maventojcenter](http://raw.github.com/zhaoyang21cn/Android_Suixinbo/master/raw/settings.png)



###配置自己的版本号
![version](http://raw.github.com/zhaoyang21cn/Android_Suixinbo/master/raw/settings2.png)


###如果你的项目中使用proguard等工具做了代码混淆，请保留以下选项。
<pre>
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**
</pre>


##最新版本App可从应用宝上下载安装（应用名：随心播）

http://android.myapp.com/myapp/detail.htm?apkName=com.tencent.qcloud.suixinbo







###优化配置项

1.退后台恢复 ：android后台进程不被杀掉，可正常恢复，画面/声音正常；

2.闹钟 ：android直播过程中有闹钟，关闭闹钟返回直播，观众和主播画面/声音正常

3.播放音频中断：使用QQ音乐后台播放，再进入，声音正常

4.后台播放视频中断：优酷播放视频，然后再进随心播，正常

5.视频通话中断 ：中断后可正常恢复直播

6.语音电话中断：ＱＱ语音电话，进入直播正常

7.电话中断: 手机通话中断，前台挂接电话，后台挂接电话均正常

8.android杀掉进程后，<90S,再创建直播，在直播间的观众可以正常恢复声音/画面

9.android杀掉进程后，>90S，后台自动关闭房间

###随心播的Spear的配置
因随心播的参数配置较高，因此对主播上行带宽有要求
![Spear配置](https://raw.githubusercontent.com/zhaoyang21cn/Android_Suixinbo/master/QQ%E6%88%AA%E5%9B%BE20160520170326.jpg)
