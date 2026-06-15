using System.IO;

namespace UnrealBuildTool.Rules
{
	public class JunPluginRMC : ModuleRules
	{
		public JunPluginRMC(ReadOnlyTargetRules Target) : base(Target)
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
				"Core",
				"CoreUObject",
				"Engine",
				"JunPlugin",
				"RenderCore",
				"RuntimeMeshComponent"
			});
		}
	}
}