// Fill out your copyright notice in the Description page of Project Settings.

using UnrealBuildTool;
using System.Collections.Generic;

public class HypogeanTarget : TargetRules
{
	public HypogeanTarget(TargetInfo Target) : base(Target)
	{
		Type = TargetType.Game;
        bOverrideBuildEnvironment = true;
        bUseLoggingInShipping = true;
        ExtraModuleNames.AddRange( new string[] { "Hypogean" } );
	}
}
