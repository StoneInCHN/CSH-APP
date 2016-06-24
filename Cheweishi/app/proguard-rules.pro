# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*
-keepattributes Signature
-keep public class * extends com.cheweishi.android.activity
-keep public class * extends com.cheweishi.android.fragement
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-dontwarn com.alipay.android.phone.mrpc.core**
-keep class com.alipay.android.phone.mrpc.core.**{*;}

-dontwarn com.alipay.apmobilesecuritysdk.face**
-keep class com.alipay.apmobilesecuritysdk.face.**{*;}

#-dontwarn com.baidu.navisdk.comapi.tts.ttsplayer**
#-keep class com.baidu.navisdk.**{*;}
--keep class com.lidroid.**{}
-dontwarn com.baidu**
-keep class com.baidu.** { *; }

-dontwarn demo.Pinyin4jAppletDemo**
-keep class demo.Pinyin4jAppletDemo{*;}

-dontwarn com.android.volley.toolbox**
-keep class com.android.volley.toolbox{*;}

-dontwarn com.google.gson**
-keep class com.google.gson.**{*;}

-dontwarn org.apache.http**
-keep class org.apache.http.**{*;}

-dontwarn com.handmark.pulltorefresh**
-keep class com.handmark.pulltorefresh.**{*;}

-dontwarn com.squareup.picasso**
-keep class com.squareup.picasso.**{*;}

-dontwarn com.cheweishi.android.entity**
-keep class com.cheweishi.android.entity.**{*;}

-keep class com.cheweishi.android.response.BaseResponse

-keep public class com.cheweishi.android.R$*{
    public static final int *;
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}