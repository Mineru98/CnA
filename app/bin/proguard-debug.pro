# Begin: Debug Proguard rules

-dontobfuscate                             #최적화를 한다. (용량이 줄어든다.)                       - #이 사라질 경우 최적화를 안한다.

-keepattributes SoureFile,LineNumberTable   #소스파일, 라인 정보 유지
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#-keep class

-printmapping map.txt
-printseeds seed.txt
-printusage usage.txt
-printconfiguration config.txt

-renamesourcefileattribute SourceFile

# End: Debug ProGuard rules