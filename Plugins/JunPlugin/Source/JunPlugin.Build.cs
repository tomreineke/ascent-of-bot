using System.IO;

namespace UnrealBuildTool.Rules
{
	public class JunPlugin : ModuleRules
	{
		public JunPlugin(ReadOnlyTargetRules Target) : base(Target)
		{
            DefaultBuildSettings = BuildSettingsVersion.V2;
            IncludeOrderVersion = EngineIncludeOrderVersion.Unreal5_1;
            CppStandard = CppStandardVersion.Cpp17;
			string JdkExactName = "jdk-21";
			string JvmIncludeDirectory = Path.Combine(ModuleDirectory, "..", "..", JdkExactName, "include");
			PublicIncludePaths.AddRange(new string[]
			{
				JvmIncludeDirectory,
				Path.Combine(JvmIncludeDirectory, "win32")
			});
			PublicDependencyModuleNames.AddRange(new string[]
			{
				"CinematicCamera",
				"Core",
				"CoreUObject",
				"Engine",
				"HeadMountedDisplay",
				"InputCore",
				"Niagara",
				"Projects",
				"RenderCore",
				"RHI",
				"Slate",
				"SlateCore",
				"UMG",
				"XmlParser"
			});
        }
	}
}