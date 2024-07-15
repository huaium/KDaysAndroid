## Platform calls Class.forName on types which do not exist on Android to determine platform.
#-dontnote retrofit2.Platform
## Platform used when running on RoboVM on iOS. Will not be used at runtime.
#-dontnote retrofit2.Platform$IOS$MainThreadExecutor
## Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
## Retain generic type information for use by reflection by converters and adapters.
#-keepattributes Signature
## Retain declared checked exceptions for use by a Proxy instance.
#-keepattributes Exceptions
## Necessary addition for Retrofit
#-dontwarn retrofit2.**
#-keep class retrofit2.** { *; }
#-keepattributes *Annotation*
#-keepattributes RuntimeVisibleAnnotations
#-keepattributes RuntimeInvisibleAnnotations
#-keepattributes RuntimeVisibleParameterAnnotations
#-keepattributes RuntimeInvisibleParameterAnnotations
#-keepattributes EnclosingMethod
## Retain custom models
#-keep class com.kdays.android.logic.model.** { *; }
## Retain Gson type parameter
#-keep class com.google.gson.** { *; }
#-keep class * implements com.google.gson.TypeAdapterFactory
## Editor combines traditional View & Compose
#-keep class com.kdays.android.ui.editor.** { *; }
## Retain JavaScript Interface within Webview
#-keepclassmembers class * {
#    @android.webkit.JavascriptInterface <methods>;
#}
#-keepclassmembers class com.kdays.android.ui.webview.WebViewActivity {
#  public *;
#}
#-keepattributes *JavascriptInterface*