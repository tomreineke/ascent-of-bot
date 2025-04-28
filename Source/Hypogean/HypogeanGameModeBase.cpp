// Fill out your copyright notice in the Description page of Project Settings.


#include "HypogeanGameModeBase.h"

#include "JunPawn.h"
#include "JunPlayerController.h"

AHypogeanGameModeBase::AHypogeanGameModeBase(const FObjectInitializer& ObjectInitializer) : AGameModeBase(ObjectInitializer)
{
	DefaultPawnClass = AJunPawn::StaticClass();
	PlayerControllerClass = AJunPlayerController::StaticClass();
}