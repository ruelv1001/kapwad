# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.google.** { *; }
-keep class com.android.** { *; }
-keep class android.support.v7.** { *; }
-keep class java.lang.** { *; }

-dontwarn android.support.v7.**

-keepattributes Signature
-keepattributes *Annotation*

# Remove Logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int e(...);
    public static int w(...);
    public static int wtf(...);
    public static int d(...);
    public static int v(...);
    public static int i(...);
}

#Encrpted Shared Pref
-keep class com.google.crypto.** { *; }
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
  <fields>;
}

-keep class com.lionscare.app.data.local.* { *; }

-keep class com.lionscare.app.data.model.* { *; }

-keep class com.lionscare.app.data.repositories.address.request.* { *; }
-keep class com.lionscare.app.data.repositories.address.response.* { *; }

-keep class com.lionscare.app.data.repositories.admin.request.* { *; }

-keep class com.lionscare.app.data.repositories.auth.request.* { *; }
-keep class com.lionscare.app.data.repositories.auth.response.* { *; }

-keep class com.lionscare.app.data.repositories.assistance.request.* { *; }
-keep class com.lionscare.app.data.repositories.assistance.response.* { *; }

-keep class com.lionscare.app.data.repositories.baseresponse.* { *; }

-keep class com.lionscare.app.data.repositories.group.request.* { *; }
-keep class com.lionscare.app.data.repositories.group.response.* { *; }

-keep class com.lionscare.app.data.repositories.generalsetting.response.* { *; }

-keep class com.lionscare.app.data.repositories.member.request.* { *; }
-keep class com.lionscare.app.data.repositories.member.response.* { *; }

-keep class com.lionscare.app.data.repositories.profile.request.* { *; }
-keep class com.lionscare.app.data.repositories.profile.response.* { *; }

-keep class com.lionscare.app.data.repositories.registration.request.* { *; }

-keep class com.lionscare.app.data.repositories.wallet.request.* { *; }
-keep class com.lionscare.app.data.repositories.wallet.response.* { *; }

-keep,allowobfuscation,allowshrinking class kotlinx.coroutines.flow.*

-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }