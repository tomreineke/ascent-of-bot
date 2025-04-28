import json
from gimpfu import *
import os

def printx(s):
    pdb.gimp_message(s)
    
def load_mask_layer(target_img, target_layer, svg_path, svg_size, shift):
    target_width = target_img.width
    target_height = target_img.height
    svg_img = pdb.file_svg_load(svg_path, "mask_svg", 90.0, svg_size, svg_size, 0)
    svg_layer = svg_img.layers[0]
    channel = pdb.gimp_channel_new_from_component(svg_img, CHANNEL_BLUE, "mask")
    svg_img.insert_channel(channel)
    pdb.gimp_image_select_item(svg_img, CHANNEL_OP_REPLACE, channel)
    pdb.gimp_selection_invert(svg_img)
    pdb.gimp_drawable_edit_clear(svg_layer)
    mask_layer = pdb.gimp_layer_new_from_drawable(svg_layer, target_img)
    target_img.insert_layer(mask_layer)
    pdb.gimp_image_select_item(target_img, CHANNEL_OP_REPLACE, mask_layer)
    target_img.remove_layer(mask_layer)
    pdb.gimp_selection_translate(
        target_img,
        (target_width - svg_size) / 2 + shift[0],
        (target_height - svg_size) / 2 + shift[1]
    )
    pdb.gimp_selection_invert(target_img)
    pdb.gimp_drawable_edit_clear(target_layer)

def process(output_directory, name, svg_path, input_obj, global_gui_sizes, is_selected, is_active, is_hovered):
    svg_size = input_obj.get("size")
    if svg_size is None:
        svg_size = 380
    shift = input_obj.get("shift")
    if shift is None:
        shift = [0, 0]
        
    gui_sizes = input_obj.get("gui_sizes") or global_gui_sizes

    metal_img = pdb.file_png_load("extracted/metalBase.png", "metalbase")
    metal_layer = metal_img.layers[0]
    load_mask_layer(metal_img, metal_layer, svg_path, svg_size, shift)
    pdb.gimp_selection_invert(metal_img)
    
    if not is_selected:
        pdb.python_layerfx_bevel_emboss(
            metal_img,
            metal_layer,
            2,             # Emboss
            18,            # depth 
            0,             # Up
            12,            # size
            0,             # soften
            135.0,         # angle
            30,            # altitude
            0,             # gloss contour Linear
            "White",       # highlight color
            SCREEN_MODE,   # highlight mode
            75.0,          # highlight opacity
            "Black",       # shadow_color
            MULTIPLY_MODE, # shadow mode
            75.0,          # shadow opacity
            0,             # surface contour Linear
            False,         # use texture
            "Dried mud",   # pattern
            150,           # scale
            -30,           # tex depth
            False,         # invert
            True           # merge
        )
    else:
        pdb.script_fu_neon_logo_alpha(
            metal_img,
            metal_layer,
            30.0,
            gimpcolor.RGB(0, 0, 0),
            gimpcolor.RGB(255, 128, 0),
            False
        )
        metal_img.remove_layer(metal_img.layers[2])
        metal_img.merge_visible_layers(CLIP_TO_IMAGE)
        
    metal_layer = metal_img.layers[0]
    
    master_img = pdb.gimp_file_load(
        "extracted/actionButton-selected.xcf" if is_selected else "extracted/actionButton.xcf",
        "actionButton"
    )
    master_size = [master_img.width, master_img.height]
    
    
    ins_layer = pdb.gimp_layer_new_from_drawable(metal_layer, master_img)
    master_img.insert_layer(ins_layer)
    
    translation = [15, 17]
    if is_selected:
        translation = [translation[0] - 8, translation[1] - 6]
    if is_active:
        translation = [translation[0] + 6, translation[1] + 6]
    ins_layer.translate(translation[0], translation[1])
    
    pdb.gimp_image_select_ellipse(master_img, CHANNEL_OP_REPLACE, 37, 37, 460, 460)
    pdb.gimp_selection_invert(master_img)
    pdb.gimp_drawable_edit_clear(ins_layer)
    pdb.gimp_selection_none(master_img)
    
    if is_active:
        dark_layer = master_img.layers[2]
        pdb.gimp_drawable_brightness_contrast(dark_layer, -0.5, 0.0)
    
    master_img.merge_visible_layers(CLIP_TO_IMAGE)
    
    if is_hovered:
        pdb.plug_in_softglow(
            master_img,
            master_img.layers[0],
            10.0,
            0.7,
            1.0
        )
    
    name_prefix = input_obj.get("prefix") or "actionButton"
    for size_suffix, scale in gui_sizes.items():
        size_name = "size%s" % size_suffix
        out_file_name = "%s-%s-%s" % (name_prefix, size_name, name)
        if is_selected:
            out_file_name += "-selected"
        if is_active:
            out_file_name += "-active"
        if is_hovered:
            out_file_name += "-hover"
        out_file_name += ".png"
        out_dir = os.path.join(output_directory, size_name)
        if not os.path.exists(out_dir):
            os.makedirs(out_dir)
        out_file = os.path.join(out_dir, out_file_name)
        
        scaled_image = pdb.gimp_image_duplicate(master_img)
        if scale:
            pdb.gimp_image_scale_full(scaled_image, scale[0], scale[1], INTERPOLATION_CUBIC)
            pdb.gimp_image_crop(scaled_image, scale[0], scale[1], 0, 0)
        else:
            pdb.gimp_image_crop(scaled_image, master_size[0], master_size[1], 0, 0)
        
        pdb.file_png_save(scaled_image, scaled_image.layers[0], out_file, "?", 0, 9, 0, 0, 0, 0, 0)

def main(args):
    if len(args) != 1:
        raise ValueError("Expecting one argument with path to JSON config.")
    config = None
    with open(args[0], "r") as f:
        config = json.load(f)
        
    output_directory = os.path.abspath(config["output_path"])
    gui_sizes = config["gui_sizes"]
    printx(output_directory)
    
    for input_obj in config["input"]:
        svg_path = os.path.abspath(input_obj["svg"])
        name = input_obj.get("name")
        if name is None:
            name = os.path.splitext(os.path.basename(svg_path))[0]
        printx("Processing %s from %s..." % (name, svg_path))
            
        process(output_directory, name, svg_path, input_obj, gui_sizes, False, False, False)
        process(output_directory, name, svg_path, input_obj, gui_sizes, False, False, True)
        process(output_directory, name, svg_path, input_obj, gui_sizes, False, True, False)
        process(output_directory, name, svg_path, input_obj, gui_sizes, False, True, True)
        if input_obj.get("selectable", True):
            process(output_directory, name, svg_path, input_obj, gui_sizes, True, False, False)
            process(output_directory, name, svg_path, input_obj, gui_sizes, True, False, True)
            process(output_directory, name, svg_path, input_obj, gui_sizes, True, True, False)
            process(output_directory, name, svg_path, input_obj, gui_sizes, True, True, True)
    