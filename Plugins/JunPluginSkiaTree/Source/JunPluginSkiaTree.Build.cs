using System.IO;

namespace UnrealBuildTool.Rules
{
	public class JunPluginSkiaTree : ModuleRules
	{
		public JunPluginSkiaTree(ReadOnlyTargetRules Target) : base(Target)
		{
            DefaultBuildSettings = BuildSettingsVersion.V2;
            IncludeOrderVersion = EngineIncludeOrderVersion.Unreal5_1;
            CppStandard = CppStandardVersion.Cpp17;
			string JdkExactName = "jdk-21";
			string JvmIncludeDirectory = Path.Combine(ModuleDirectory, "..", "..", JdkExactName, "include");
			string SkiaTreePath = Path.GetFullPath(Path.Combine(ModuleDirectory, "..", "skia-tree"));
			PublicRuntimeLibraryPaths.Add(Path.Combine(SkiaTreePath, "target", "debug"));
			PublicIncludePaths.AddRange(new string[]
			{
				JvmIncludeDirectory,
				Path.Combine(JvmIncludeDirectory, "win32"),
				Path.Combine(SkiaTreePath, "target", "include")
			});
			PublicDependencyModuleNames.AddRange(new string[]
			{
				"Core",
				"CoreUObject",
				"Engine",
				"InputCore",
				"JunPlugin",
				"RenderCore",
				"RHI",
				"Slate",
				"SlateCore"
			});
			PublicAdditionalLibraries.AddRange(new string[]
			{
				Path.Combine(SkiaTreePath, "target", "debug", "skiatree.dll.lib")
			});
			PublicDelayLoadDLLs.AddRange(new string[]
			{
				"skiatree.dll"
			});
		}
	}
}
