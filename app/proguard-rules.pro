# ProGuard rules for Travel Companion
# Optimized for production release with R8

# ===== Keep debugging info =====
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===== Kotlin =====
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# ===== Room Database =====
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Database class *
-keep class com.travelcompanion.data.db.entities.** { *; }
-keep interface com.travelcompanion.data.db.dao.** { *; }

# ===== Hilt / Dagger =====
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class dagger.hilt.** { *; }
-keep class **_HiltModules { *; }
-keep class **_Factory { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-dontwarn com.google.errorprone.annotations.**

# ===== ViewModels =====
-keep class * extends androidx.lifecycle.ViewModel { <init>(...); }

# ===== Moshi (used by Room Converters) =====
-keepattributes Signature, *Annotation*
-keep class com.squareup.moshi.** { *; }
-keep class **JsonAdapter { <init>(...); <fields>; }

# ===== Google Play Services =====
-keep class com.google.android.gms.location.** { *; }
-dontwarn com.google.android.gms.**

# ===== Glide =====
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder { *** rewind(); }
-dontwarn com.bumptech.glide.**

# ===== OSMDroid =====
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# ===== MPAndroidChart =====
-keep class com.github.mikephil.charting.** { *; }

# ===== Timber =====
-assumenosideeffects class timber.log.Timber* {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ===== Shimmer =====
-keep class com.facebook.shimmer.** { *; }

# ===== WorkManager =====
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker { public <init>(...); }

# ===== Domain Layer =====
-keep class com.travelcompanion.domain.model.** { *; }
-keep interface com.travelcompanion.domain.repository.** { *; }
-keep class com.travelcompanion.domain.usecase.** { *; }

# ===== Services & Receivers =====
-keep class com.travelcompanion.service.** { *; }
-keep class * extends android.content.BroadcastReceiver { <init>(...); }

# ===== DataStore =====
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { <fields>; }

# ===== Remove Logging in Release =====
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# ===== General =====
-keepattributes RuntimeVisibleAnnotations, Exceptions, InnerClasses
-keepclasseswithmembernames class * { native <methods>; }
-keepclassmembers class * { void set*(***); *** get*(); }
-dontwarn java.lang.invoke.**
