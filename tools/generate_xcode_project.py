import os
import uuid

def make_uuid():
    return uuid.uuid4().hex[:24].upper()

swift_files = [
    ("ChibiDollApp.swift", "ChibiDoll/App/ChibiDollApp.swift"),
    ("CatalogModel.swift", "ChibiDoll/Models/CatalogModel.swift"),
    ("CharacterModel.swift", "ChibiDoll/Models/CharacterModel.swift"),
    ("RoomModel.swift", "ChibiDoll/Models/RoomModel.swift"),
    ("SkinTonePalette.swift", "ChibiDoll/Services/SkinTonePalette.swift"),
    ("CatalogRepository.swift", "ChibiDoll/Services/CatalogRepository.swift"),
    ("CharacterLogicHelper.swift", "ChibiDoll/Services/CharacterLogicHelper.swift"),
    ("ImageCacheManager.swift", "ChibiDoll/Services/ImageCacheManager.swift"),
    ("RoomRepository.swift", "ChibiDoll/Services/RoomRepository.swift"),
    ("CuteSoundManager.swift", "ChibiDoll/Services/CuteSoundManager.swift"),
    ("ChibiDollRendererView.swift", "ChibiDoll/Views/Components/ChibiDollRendererView.swift"),
    ("EditorStudioBackgroundView.swift", "ChibiDoll/Views/Components/EditorStudioBackgroundView.swift"),
    ("PackTabsBarView.swift", "ChibiDoll/Views/Components/PackTabsBarView.swift"),
    ("CategoryTabsBarView.swift", "ChibiDoll/Views/Components/CategoryTabsBarView.swift"),
    ("ItemGridPanelView.swift", "ChibiDoll/Views/Components/ItemGridPanelView.swift"),
    ("SkinToneSliderView.swift", "ChibiDoll/Views/Components/SkinToneSliderView.swift"),
    ("RoomStageCanvasView.swift", "ChibiDoll/Views/Components/RoomStageCanvasView.swift"),
    ("HomeScreen.swift", "ChibiDoll/Views/Home/HomeScreen.swift"),
    ("RoomScreen.swift", "ChibiDoll/Views/Room/RoomScreen.swift"),
    ("CharacterEditorScreen.swift", "ChibiDoll/Views/Editor/CharacterEditorScreen.swift"),
    ("AlbumScreen.swift", "ChibiDoll/Views/Album/AlbumScreen.swift"),
    ("SettingsScreen.swift", "ChibiDoll/Views/Settings/SettingsScreen.swift"),
]

file_refs = {}
build_files = {}

for name, path in swift_files:
    f_id = make_uuid()
    b_id = make_uuid()
    file_refs[name] = (f_id, path)
    build_files[name] = (b_id, f_id)

assets_f_id = make_uuid()
assets_b_id = make_uuid()
info_plist_f_id = make_uuid()
product_f_id = make_uuid()

sources_phase_id = make_uuid()
frameworks_phase_id = make_uuid()
resources_phase_id = make_uuid()
copy_assets_phase_id = make_uuid()

target_id = make_uuid()
project_id = make_uuid()

main_group_id = make_uuid()
chibidoll_group_id = make_uuid()
products_group_id = make_uuid()

debug_proj_cfg = make_uuid()
release_proj_cfg = make_uuid()
proj_cfg_list = make_uuid()

debug_tgt_cfg = make_uuid()
release_tgt_cfg = make_uuid()
tgt_cfg_list = make_uuid()

pbx_build_file_lines = []
for name, (b_id, f_id) in build_files.items():
    pbx_build_file_lines.append(f"\t\t{b_id} /* {name} in Sources */ = {{isa = PBXBuildFile; fileRef = {f_id} /* {name} */; }};")
pbx_build_file_lines.append(f"\t\t{assets_b_id} /* Assets.xcassets in Resources */ = {{isa = PBXBuildFile; fileRef = {assets_f_id} /* Assets.xcassets */; }};")

pbx_file_ref_lines = []
for name, (f_id, path) in file_refs.items():
    pbx_file_ref_lines.append(f'\t\t{f_id} /* {name} */ = {{isa = PBXFileReference; lastKnownFileType = sourcecode.swift; name = "{name}"; path = "{path}"; sourceTree = "<group>"; }};')
pbx_file_ref_lines.append(f'\t\t{assets_f_id} /* Assets.xcassets */ = {{isa = PBXFileReference; lastKnownFileType = folder.assetcatalog; name = "Assets.xcassets"; path = "ChibiDoll/Resources/Assets.xcassets"; sourceTree = "<group>"; }};')
pbx_file_ref_lines.append(f'\t\t{info_plist_f_id} /* Info.plist */ = {{isa = PBXFileReference; lastKnownFileType = text.plist.xml; name = "Info.plist"; path = "ChibiDoll/Resources/Info.plist"; sourceTree = "<group>"; }};')
pbx_file_ref_lines.append(f'\t\t{product_f_id} /* ChibiDoll.app */ = {{isa = PBXFileReference; explicitFileType = wrapper.application; includeInIndex = 0; path = "ChibiDoll.app"; sourceTree = BUILT_PRODUCTS_DIR; }};')

chibidoll_children = [f"\t\t\t\t{f_id} /* {name} */," for name, (f_id, _) in file_refs.items()]
chibidoll_children.append(f"\t\t\t\t{assets_f_id} /* Assets.xcassets */,")
chibidoll_children.append(f"\t\t\t\t{info_plist_f_id} /* Info.plist */,")

sources_files = [f"\t\t\t\t{b_id} /* {name} in Sources */," for name, (b_id, _) in build_files.items()]

pbxproj = f"""// !$*UTF8*$!
{{
\tarchiveVersion = 1;
\tclasses = {{
\t}};
\tobjectVersion = 56;
\tobjects = {{

/* Begin PBXBuildFile section */
{chr(10).join(pbx_build_file_lines)}
/* End PBXBuildFile section */

/* Begin PBXFileReference section */
{chr(10).join(pbx_file_ref_lines)}
/* End PBXFileReference section */

/* Begin PBXFrameworksBuildPhase section */
\t\t{frameworks_phase_id} /* Frameworks */ = {{
\t\t\tisa = PBXFrameworksBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t}};
/* End PBXFrameworksBuildPhase section */

/* Begin PBXGroup section */
\t\t{main_group_id} = {{
\t\t\tisa = PBXGroup;
\t\t\tchildren = (
\t\t\t\t{chibidoll_group_id} /* ChibiDoll */,
\t\t\t\t{products_group_id} /* Products */,
\t\t\t);
\t\t\tsourceTree = "<group>";
\t\t}};
\t\t{chibidoll_group_id} /* ChibiDoll */ = {{
\t\t\tisa = PBXGroup;
\t\t\tchildren = (
{chr(10).join(chibidoll_children)}
\t\t\t);
\t\t\tname = ChibiDoll;
\t\t\tsourceTree = "<group>";
\t\t}};
\t\t{products_group_id} /* Products */ = {{
\t\t\tisa = PBXGroup;
\t\t\tchildren = (
\t\t\t\t{product_f_id} /* ChibiDoll.app */,
\t\t\t);
\t\t\tname = Products;
\t\t\tsourceTree = "<group>";
\t\t}};
/* End PBXGroup section */

/* Begin PBXNativeTarget section */
\t\t{target_id} /* ChibiDoll */ = {{
\t\t\tisa = PBXNativeTarget;
\t\t\tbuildConfigurationList = {tgt_cfg_list} /* Build configuration list for PBXNativeTarget "ChibiDoll" */;
\t\t\tbuildPhases = (
\t\t\t\t{sources_phase_id} /* Sources */,
\t\t\t\t{frameworks_phase_id} /* Frameworks */,
\t\t\t\t{resources_phase_id} /* Resources */,
\t\t\t\t{copy_assets_phase_id} /* Copy Shared Assets */,
\t\t\t);
\t\t\tbuildRules = (
\t\t\t);
\t\t\tdependencies = (
\t\t\t);
\t\t\tname = ChibiDoll;
\t\t\tproductName = ChibiDoll;
\t\t\tproductReference = {product_f_id} /* ChibiDoll.app */;
\t\t\tproductType = "com.apple.product-type.application";
\t\t}};
/* End PBXNativeTarget section */

/* Begin PBXProject section */
\t\t{project_id} /* Project object */ = {{
\t\t\tisa = PBXProject;
\t\t\tattributes = {{
\t\t\t\tBuildIndependentTargetsInParallel = 1;
\t\t\t\tLastSwiftUpdateCheck = 1500;
\t\t\t\tLastUpgradeCheck = 1500;
\t\t\t}};
\t\t\tbuildConfigurationList = {proj_cfg_list} /* Build configuration list for PBXProject "ChibiDoll" */;
\t\t\tcompatibilityVersion = "Xcode 14.0";
\t\t\tdevelopmentRegion = en;
\t\t\thasScannedForEncodings = 0;
\t\t\tknownRegions = (
\t\t\t\ten,
\t\t\t\tBase,
\t\t\t);
\t\t\tmainGroup = {main_group_id};
\t\t\tproductRefGroup = {products_group_id} /* Products */;
\t\t\tprojectDirPath = "";
\t\t\tprojectRoot = "";
\t\t\ttargets = (
\t\t\t\t{target_id} /* ChibiDoll */,
\t\t\t);
\t\t}};
/* End PBXProject section */

/* Begin PBXResourcesBuildPhase section */
\t\t{resources_phase_id} /* Resources */ = {{
\t\t\tisa = PBXResourcesBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
\t\t\t\t{assets_b_id} /* Assets.xcassets in Resources */,
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t}};
/* End PBXResourcesBuildPhase section */

/* Begin PBXShellScriptBuildPhase section */
\t\t{copy_assets_phase_id} /* Copy Shared Assets */ = {{
\t\t\tisa = PBXShellScriptBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
\t\t\t);
\t\t\tinputPaths = (
\t\t\t);
\t\t\tname = "Copy Shared Assets";
\t\t\toutputPaths = (
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t\tshellPath = /bin/sh;
\t\t\tshellScript = "ASSETS_SRC=\\"${{SRCROOT}}/../app/src/main/assets\\"\\nBUNDLE_DST=\\"${{TARGET_BUILD_DIR}}/${{UNLOCALIZED_RESOURCES_FOLDER_PATH}}\\"\\nif [ -d \\"$ASSETS_SRC\\" ]; then\\n  echo \\"Copying ChibiDoll assets into $BUNDLE_DST\\"\\n  mkdir -p \\"$BUNDLE_DST\\"\\n  cp -R \\"$ASSETS_SRC/\\"* \\"$BUNDLE_DST/\\"\\nfi\\n";
\t\t}};
/* End PBXShellScriptBuildPhase section */

/* Begin PBXSourcesBuildPhase section */
\t\t{sources_phase_id} /* Sources */ = {{
\t\t\tisa = PBXSourcesBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
{chr(10).join(sources_files)}
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t}};
/* End PBXSourcesBuildPhase section */

/* Begin XCBuildConfiguration section */
\t\t{debug_proj_cfg} /* Debug */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tALWAYS_SEARCH_USER_PATHS = NO;
\t\t\t\tCLANG_ENABLE_MODULES = YES;
\t\t\t\tCLANG_ENABLE_OBJC_ARC = YES;
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 16.0;
\t\t\t\tSDKROOT = iphoneos;
\t\t\t\tSWIFT_VERSION = 5.0;
\t\t\t}};
\t\t\tname = Debug;
\t\t}};
\t\t{release_proj_cfg} /* Release */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tALWAYS_SEARCH_USER_PATHS = NO;
\t\t\t\tCLANG_ENABLE_MODULES = YES;
\t\t\t\tCLANG_ENABLE_OBJC_ARC = YES;
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 16.0;
\t\t\t\tSDKROOT = iphoneos;
\t\t\t\tSWIFT_VERSION = 5.0;
\t\t\t\tVALIDATE_PRODUCT = YES;
\t\t\t}};
\t\t\tname = Release;
\t\t}};
\t\t{debug_tgt_cfg} /* Debug */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tASSETCATALOG_COMPILER_APPICON_NAME = AppIcon;
\t\t\t\tCODE_SIGN_STYLE = Automatic;
\t\t\t\tCURRENT_PROJECT_VERSION = 1;
\t\t\t\tDEVELOPMENT_TEAM = "";
\t\t\t\tGENERATE_INFOPLIST_FILE = NO;
\t\t\t\tINFOPLIST_FILE = ChibiDoll/Resources/Info.plist;
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 16.0;
\t\t\t\tMARKETING_VERSION = 1.0;
\t\t\t\tPRODUCT_BUNDLE_IDENTIFIER = com.chibidolls.app;
\t\t\t\tPRODUCT_NAME = "$(TARGET_NAME)";
\t\t\t\tSUPPORTED_PLATFORMS = "iphoneos iphonesimulator";
\t\t\t\tSUPPORTS_MACCATALYST = NO;
\t\t\t\tSWIFT_EMIT_LOC_STRINGS = YES;
\t\t\t\tTARGETED_DEVICE_FAMILY = "1,2";
\t\t\t}};
\t\t\tname = Debug;
\t\t}};
\t\t{release_tgt_cfg} /* Release */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tASSETCATALOG_COMPILER_APPICON_NAME = AppIcon;
\t\t\t\tCODE_SIGN_STYLE = Automatic;
\t\t\t\tCURRENT_PROJECT_VERSION = 1;
\t\t\t\tDEVELOPMENT_TEAM = "";
\t\t\t\tGENERATE_INFOPLIST_FILE = NO;
\t\t\t\tINFOPLIST_FILE = ChibiDoll/Resources/Info.plist;
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 16.0;
\t\t\t\tMARKETING_VERSION = 1.0;
\t\t\t\tPRODUCT_BUNDLE_IDENTIFIER = com.chibidolls.app;
\t\t\t\tPRODUCT_NAME = "$(TARGET_NAME)";
\t\t\t\tSUPPORTED_PLATFORMS = "iphoneos iphonesimulator";
\t\t\t\tSUPPORTS_MACCATALYST = NO;
\t\t\t\tSWIFT_EMIT_LOC_STRINGS = YES;
\t\t\t\tTARGETED_DEVICE_FAMILY = "1,2";
\t\t\t}};
\t\t\tname = Release;
\t\t}};
/* End XCBuildConfiguration section */

/* Begin XCConfigurationList section */
\t\t{proj_cfg_list} /* Build configuration list for PBXProject "ChibiDoll" */ = {{
\t\t\tisa = XCConfigurationList;
\t\t\tbuildConfigurations = (
\t\t\t\t{debug_proj_cfg} /* Debug */,
\t\t\t\t{release_proj_cfg} /* Release */,
\t\t\t);
\t\t\tdefaultConfigurationIsVisible = 0;
\t\t\tdefaultConfigurationName = Release;
\t\t}};
\t\t{tgt_cfg_list} /* Build configuration list for PBXNativeTarget "ChibiDoll" */ = {{
\t\t\tisa = XCConfigurationList;
\t\t\tbuildConfigurations = (
\t\t\t\t{debug_tgt_cfg} /* Debug */,
\t\t\t\t{release_tgt_cfg} /* Release */,
\t\t\t);
\t\t\tdefaultConfigurationIsVisible = 0;
\t\t\tdefaultConfigurationName = Release;
\t\t}};
/* End XCConfigurationList section */
\t}};
\trootObject = {project_id} /* Project object */;
}}
"""

os.makedirs("iosApp/ChibiDoll.xcodeproj/xcshareddata/xcschemes", exist_ok=True)
with open("iosApp/ChibiDoll.xcodeproj/project.pbxproj", "w", encoding="utf-8", newline="\n") as f:
    f.write(pbxproj)

xcscheme = f"""<?xml version="1.0" encoding="UTF-8"?>
<Scheme
   LastUpgradeVersion = "1500"
   version = "1.7">
   <BuildAction
      parallelizeBuildables = "YES"
      buildImplicitDependencies = "YES">
      <BuildActionEntries>
         <BuildActionEntry
            buildForTesting = "YES"
            buildForRunning = "YES"
            buildForProfiling = "YES"
            buildForArchiving = "YES"
            buildForAnalyzing = "YES">
            <BuildableReference
               BuildableIdentifier = "primary"
               BlueprintIdentifier = "{target_id}"
               BuildableName = "ChibiDoll.app"
               BlueprintName = "ChibiDoll"
               ReferencedContainer = "container:ChibiDoll.xcodeproj">
            </BuildableReference>
         </BuildActionEntry>
      </BuildActionEntries>
   </BuildAction>
   <LaunchAction
      buildConfiguration = "Debug"
      selectedDebuggerIdentifier = "Xcode.DebuggerFoundation.Debugger.LLDB"
      selectedLauncherIdentifier = "Xcode.DebuggerFoundation.Launcher.LLDB"
      launchStyle = "0"
      useCustomWorkingDirectory = "NO"
      ignoresPersistentStateOnLaunch = "NO"
      debugDocumentVersioning = "YES"
      debugServiceExtension = "internal"
      allowLocationSimulation = "YES">
      <BuildableProductRunnable
         runnableDebuggingMode = "0">
         <BuildableReference
            BuildableIdentifier = "primary"
            BlueprintIdentifier = "{target_id}"
            BuildableName = "ChibiDoll.app"
            BlueprintName = "ChibiDoll"
            ReferencedContainer = "container:ChibiDoll.xcodeproj">
         </BuildableReference>
      </BuildableProductRunnable>
   </LaunchAction>
   <ArchiveAction
      buildConfiguration = "Release"
      revealArchiveInOrganizer = "YES">
   </ArchiveAction>
</Scheme>
"""

with open("iosApp/ChibiDoll.xcodeproj/xcshareddata/xcschemes/ChibiDoll.xcscheme", "w", encoding="utf-8", newline="\n") as f:
    f.write(xcscheme)

print("Generated Xcode project and scheme successfully!")
