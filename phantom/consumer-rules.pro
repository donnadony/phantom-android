# Phantom Library - Consumer ProGuard Rules
# These rules are automatically applied to apps that depend on this library.

# Keep public API
-keep class com.phantom.Phantom { *; }
-keep class com.phantom.PhantomOkHttpInterceptor { *; }

# Keep models (data classes used by consumers)
-keep class com.phantom.model.** { *; }

# Keep theme
-keep class com.phantom.theme.PhantomTheme { *; }
-keep class com.phantom.theme.PhantomTheme$Companion { *; }

# Keep Activity (launched via Intent)
-keep class com.phantom.ui.PhantomActivity { *; }

# kotlinx.serialization (mock rules persistence)
-keepattributes *Annotation*, InnerClasses

-keepclassmembers @kotlinx.serialization.Serializable class com.phantom.model.** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-keepclasseswithmembers class com.phantom.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.phantom.model.**$$serializer { *; }
