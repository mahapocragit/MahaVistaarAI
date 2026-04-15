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

# Keep enums (safe)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}