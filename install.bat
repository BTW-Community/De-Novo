set MAPPINGS_VERSION=1.0.3
cd /d "%~dp0"
rd /s /q "build_BTW\BTW_dev"
rd /s /q "build_BTW\tmp\BTW_dev"
call gradlew.bat --no-daemon downloadAssets
mkdir build_BTW\BTW_dev
mkdir custom_mappings
mkdir build_BTW\tmp\BTW_dev
echo Please wait...
tar.exe -xf mavenRepo/btw/community/mappings/%MAPPINGS_VERSION%/mappings-%MAPPINGS_VERSION%.jar -C custom_mappings
del "%userprofile%\.gradle\caches\fabric-loom\1.6.4\minecraft-merged-intermediary.jar"
java -jar libs/tiny-remapper-0.8.6+local-fat.jar "%userprofile%/.gradle/caches/fabric-loom/1.6.4/minecraft-merged.jar" "%userprofile%/.gradle/caches/fabric-loom/1.6.4/minecraft-merged-intermediary.jar" "%userprofile%/.gradle/caches/fabric-loom/1.6.4/intermediary-v2.tiny" official intermediary >nul
tar.exe -xf "%~f1" -C build_BTW/tmp/BTW_dev
java -jar libs/tiny-remapper-0.8.6+local-fat.jar "build_BTW/tmp/BTW_dev/BTW-CE-Intermediary.zip" "build_BTW/BTW_dev/BTW-CE-Intermediary.zip" custom_mappings/mappings/mappings.tiny intermediary named "%userprofile%/.gradle/caches/fabric-loom/1.6.4/minecraft-merged-intermediary.jar" >nul
tar.exe -xf "%userprofile%/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged/1.6.4-btw.community.mappings.1_6_4.%MAPPINGS_VERSION%-v2/minecraft-merged-1.6.4-btw.community.mappings.1_6_4.%MAPPINGS_VERSION%-v2.jar" -C build_BTW/BTW_dev
tar.exe -xf "build_BTW/BTW_dev/BTW-CE-Intermediary.zip" -C build_BTW/BTW_dev
del build_BTW\BTW_dev\BTW-CE-Intermediary.zip
cd build_BTW\BTW_dev
tar.exe -a -cf ../BTW_dev.zip *
cd ..
rd /s /q "BTW_dev"
mkdir "BTW_dev"
move BTW_dev.zip BTW_dev\BTW_dev.zip
cd ..
move build_BTW\tmp\BTW_dev\BTW-CE-Intermediary-javadoc.jar build_BTW\BTW_dev\BTW_dev-javadoc.jar
rd /s /q "build_BTW\tmp\BTW_dev"
echo Done!
PAUSE