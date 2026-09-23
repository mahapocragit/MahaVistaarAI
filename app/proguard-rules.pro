#############################################
# BASIC ANDROID RULES
#############################################
# Keep R classes
-keep class **.R$* { *; }

# Keep BuildConfig
-keep class **.BuildConfig { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep annotations (important for Retrofit, Gson, etc.)
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes EnclosingMethod,InnerClasses

-keep class com.mahakrushi.mahilashetkari.data.remote.dto.** { *; }

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken

-keep interface com.mahakrushi.mahilashetkari.data.remote.api.** { *; }
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**
-dontwarn coil.**
#############################################
# GSON (CRITICAL FOR YOUR APP)
#############################################

# Keep Gson library
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.internal.$Gson$Types

# Keep your model classes (VERY IMPORTANT)
-keep class in.gov.mahapocra.mahavistaarai.data.model.** { *; }

#############################################
# RETROFIT
#############################################

-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

#############################################
# OKHTTP
#############################################

-dontwarn okhttp3.**
-dontwarn okio.**

#############################################
# FIREBASE
#############################################

-dontwarn com.google.firebase.**

#############################################
# GLIDE
#############################################

-keep public class * implements com.bumptech.glide.module.GlideModule

#############################################
# PICASSO
#############################################

-dontwarn com.squareup.picasso.**

#############################################
# CAMERAX
#############################################

-dontwarn androidx.camera.**

#############################################
# OSM DROID (MAPS)
#############################################

-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

#############################################
# RXJAVA
#############################################

-dontwarn io.reactivex.**

#############################################
# FAST ANDROID NETWORKING
#############################################

-keep class com.androidnetworking.** { *; }

#############################################
# CALLIGRAPHY (FONTS)
#############################################

-keep class uk.co.chrisjenx.calligraphy.** { *; }

#############################################
# DATABINDING / VIEWBINDING
#############################################

-keep class androidx.databinding.** { *; }

#############################################
# OPTIONAL SAFETY (KEEP IF USING REFLECTION)
#############################################

#tensor flow
-keep class org.tensorflow.** { *; }
-dontwarn org.tensorflow.**

# Keep enums (safe)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class io.ktor.client.** { *; }
-dontwarn io.ktor.client.**