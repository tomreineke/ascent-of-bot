import json
import math
import os
from gimpfu import *


def printx(s):
    pdb.gimp_message(s)
    
def clamp(value, lo, hi):
    return max(min(value, hi), lo)
    
def process(output_directory, name, icon_path, input_obj, gui_sizes):
    black_img = pdb.file_png_load("tmp/black_x.png", "black_x")
    black_lay = black_img.layers[0]
    diff_img = pdb.file_png_load("tmp/diff.png", "diff")
    diff_lay = diff_img.layers[0]
    icon_img = pdb.file_png_load(icon_path, "icon")
    one_pixel_img = pdb.gimp_image_duplicate(icon_img)
    pdb.gimp_image_scale_full(one_pixel_img, 1.0, 1.0, INTERPOLATION_CUBIC)
    one_pixel_lay = one_pixel_img.layers[0]
    color_factors = pdb.gimp_drawable_get_pixel(one_pixel_lay, 0, 0)[1]
    width = black_img.width
    height = black_img.height
    
    for y in range(0, height):
        for x in range(0, width):
            color = pdb.gimp_drawable_get_pixel(black_lay, x, y)[1]
            diff = pdb.gimp_drawable_get_pixel(diff_lay, x, y)[1]
            components = list()
            for c in range(0, 3):
                components.append(clamp(color[c] + math.ceil(diff[c] * (color_factors[c] / 255.0)), 0, 255))
            components.append(255)
            pdb.gimp_drawable_set_pixel(black_lay, x, y, 4, tuple(components))
    pdb.gimp_drawable_update(black_lay, 0, 0, width, height)
    pdb.file_png_save(black_img, black_img.layers[0], "tmp/combined.png", "?", 0, 9, 0, 0, 0, 0, 0)
    printx(color)
    pass
    
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
        icon_path = os.path.abspath(input_obj["icon"])
        name = input_obj.get("name")
        if name is None:
            name = os.path.splitext(os.path.basename(icon_path))[0]
        printx("Processing %s from %s..." % (name, icon_path))
        process(output_directory, name, icon_path, input_obj, gui_sizes)