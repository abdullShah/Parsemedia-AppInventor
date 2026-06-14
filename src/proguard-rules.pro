# Add any ProGuard configurations specific to this
# extension here.

-keep public class parsemedia.Parsemedia {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'parsemedia/repack'
-flattenpackagehierarchy
-dontpreverify
