# ProGuard / R8 rules for Ziven Android.
-keepattributes *Annotation*
-keepclassmembers class * { @com.google.gson.annotations.SerializedName <fields>; }
