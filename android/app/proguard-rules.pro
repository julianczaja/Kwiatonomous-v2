-keepattributes Signature

#GSON
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep class com.google.gson.stream.** { *; }

-keep class sun.misc.Unsafe { *; }

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#-----------------------Used in GSON -----------------------------#
-keep class com.corrot.kwiatonomousapp.domain.model.** { *; }
-keep class com.corrot.kwiatonomousapp.data.remote.** { *; }
#-----------------------Used in GSON -----------------------------#

# For debugging prod
#-dontobfuscate
#-dontoptimize
-keepattributes SourceFile,LineNumberTable

