# --- Retrofit ---
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.sipandsavour.data.remote.dto.** { *; }
-keepclassmembers class com.sipandsavour.data.remote.dto.** { *; }

# --- Gson ---
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# --- OkHttp ---
-dontwarn okhttp3.**
-dontwarn okio.**

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- Glide ---
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}

# --- Lottie ---
-dontwarn com.airbnb.lottie.**

# --- Hilt ---
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }