import psd_tools
from pathlib import Path
import re

input_dir = Path("../Hypogean/Resources/PSD")
output_dir = Path("tmp_output")

forbidden_chars = re.compile("[^a-zA-Z0-9_-]+")
def sanitize_name(name):
    return forbidden_chars.sub("-", name)

output_index = 0
def extract_layer(layer, name_prefix):
    global output_index
    out_file = output_dir.joinpath(f"{output_index:04d}_{name_prefix}.png")
    pic = layer.composite()
    if pic:
        pic.save(str(out_file))
    output_index += 1
    print(f"Saving to {out_file}")
    if layer.is_group():
        for child in layer:
            extract_layer(child, f"{name_prefix}_{sanitize_name(child.name)}")

def extract_psd(psd_path, name_prefix):
    img = psd_tools.PSDImage.open(psd_path)
    extract_layer(img, name_prefix)

for child in input_dir.iterdir():
    if child.is_file() and child.suffix == ".psd":
        extract_psd(child, child.stem)

print("Done.")
