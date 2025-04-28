#pragma once

//struct Surface;

//#include "skiatree.h"

namespace skiatree {
	
struct SkiaTreeLibrary;
struct Forest;
struct Surface;

template<typename T>
struct RefCell;
	
extern "C" {
	
void skiatree_set_log_fn(void(*LogFn)(const char* Message));

Surface* skiatree_surface_new(SkiaTreeLibrary* Library, uint32 Width, uint32 Height);
void skiatree_surface_delete(Surface* Surface);
void skiatree_surface_flush_and_submit(Surface* Surface);
uint8 skiatree_surface_read_pixels(Surface* Surface, uint8* BufferPtr, uintptr_t BufferSize);

uint8 skiatree_forest_draw_on_surface(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest, Surface* Surface);

const RefCell<Forest>* skiatree_forest_clone(const RefCell<Forest>* Forest);
void skiatree_forest_tick(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest);

bool skiatree_input_key_down(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest, uint32 KeyIndex, uint8 Modifiers);
bool skiatree_input_key_up(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest, uint32 KeyIndex, uint8 Modifiers);

bool skiatree_input_double_click(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest, FIntPoint Position, uint32 ButtonIndex, uint8 Modifiers);
bool skiatree_input_mouse_button_down(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest, FIntPoint Position, uint32 ButtonIndex, uint8 Modifiers);
bool skiatree_input_mouse_button_up(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest, FIntPoint Position, uint32 ButtonIndex, uint8 Modifiers);
void skiatree_input_mouse_leave(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest);
void skiatree_input_mouse_move(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest, FIntPoint Position, uint8 Modifiers);
void skiatree_input_mouse_wheel(SkiaTreeLibrary* Library, const RefCell<Forest>* Forest, FIntPoint Position, float WheelDelta, uint8 Modifiers);

const uint8 MODIFIER_SHIFT = 1;
const uint8 MODIFIER_CTRL = 2;
const uint8 MODIFIER_ALT = 4;
const uint8 MODIFIER_COMMAND = 8;

}

}