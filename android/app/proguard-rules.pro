-keepattributes Signature

#GSON
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep class com.google.gson.stream.** { *; }

-keep class sun.misc.Unsafe { *; }

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

#-----------------------Used in GSON -----------------------------#
-keep class com.corrot.kwiatonomousapp.domain.model.** { *; }
-keep class com.corrot.kwiatonomousapp.data.remote.** { *; }
#-----------------------Used in GSON -----------------------------#

# TODO: Waiting for new retrofit release to remove these rules (https://github.com/square/retrofit/issues/3751)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# For debugging prod
#-dontobfuscate
#-dontoptimize
-keepattributes SourceFile,LineNumberTable

