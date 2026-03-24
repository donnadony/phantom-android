# Phantom Library - ProGuard Rules
# These rules apply when the library itself is minified.

# Keep public API
-keep class com.phantom.Phantom { *; }
-keep class com.phantom.PhantomOkHttpInterceptor { *; }

# Keep models (used by consumers and serialization)
-keep class com.phantom.model.** { *; }

# Keep theme (consumers may instantiate PhantomTheme)
-keep class com.phantom.theme.PhantomTheme { *; }
-keep class com.phantom.theme.PhantomTheme$Companion { *; }

# Keep Activity (referenced in AndroidManifest)
-keep class com.phantom.ui.PhantomActivity { *; }

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class com.phantom.model.** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-keepclasseswithmembers class com.phantom.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.phantom.model.**$$serializer { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
